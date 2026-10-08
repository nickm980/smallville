[CmdletBinding()]
param()

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

Import-Module (Join-Path $PSScriptRoot "OperatorSurfaceExports.psm1") -Force

function Assert-True {
    param(
        [Parameter(Mandatory = $true)]
        [bool]$Condition,

        [Parameter(Mandatory = $true)]
        [string]$Message
    )

    if (-not $Condition) {
        throw $Message
    }
}

function Assert-HasColumns {
    param(
        [Parameter(Mandatory = $true)]
        [object]$Row,

        [Parameter(Mandatory = $true)]
        [string[]]$Columns
    )

    foreach ($column in $Columns) {
        if ($null -eq $Row.PSObject.Properties[$column]) {
            throw "Expected export column [$column] was not found."
        }
    }
}

function Test-PathOutsideEvidenceRoots {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Path,

        [Parameter(Mandatory = $true)]
        [string]$RepoRoot
    )

    $fullPath = [System.IO.Path]::GetFullPath($Path)
    $runsRoot = [System.IO.Path]::GetFullPath((Join-Path $RepoRoot "runs")).TrimEnd('\')
    $baselinesRoot = [System.IO.Path]::GetFullPath((Join-Path $RepoRoot "baselines")).TrimEnd('\')

    return (
        -not $fullPath.StartsWith($runsRoot + "\", [System.StringComparison]::OrdinalIgnoreCase) -and
        -not $fullPath.StartsWith($baselinesRoot + "\", [System.StringComparison]::OrdinalIgnoreCase) -and
        -not $fullPath.Equals($runsRoot, [System.StringComparison]::OrdinalIgnoreCase) -and
        -not $fullPath.Equals($baselinesRoot, [System.StringComparison]::OrdinalIgnoreCase)
    )
}

$repoRoot = (Resolve-Path (Join-Path $PSScriptRoot "..\\..")).Path
$tinyBundle = Join-Path $repoRoot "runs\\20260313-194619-tiny-world-smoke-control-llama3.2-3b-16k"
$twoHouseBundle = Join-Path $repoRoot "runs\\20260313-200242-two-house-garden-v1-reviewed-llama3.2-3b-16k"
$outputDirectory = Join-Path $env:TEMP ("smallville-operator-surface-slice4-proof-" + [guid]::NewGuid().ToString("N"))

try {
    $runSummaryTiny = Export-RunBundleSummaryCsv -BundlePath $tinyBundle -OutputDirectory $outputDirectory
    $proposalTiny = Export-ProposalSummaryCsv -BundlePath $tinyBundle -OutputDirectory $outputDirectory
    $checklistTiny = Export-ChecklistOutcomeCsv -BundlePath $tinyBundle -OutputDirectory $outputDirectory

    $runSummaryTwo = Export-RunBundleSummaryCsv -BundlePath $twoHouseBundle -OutputDirectory $outputDirectory
    $proposalTwo = Export-ProposalSummaryCsv -BundlePath $twoHouseBundle -OutputDirectory $outputDirectory
    $checklistTwo = Export-ChecklistOutcomeCsv -BundlePath $twoHouseBundle -OutputDirectory $outputDirectory
    $comparison = Export-RunComparisonSummaryCsv -BundlePathA $tinyBundle -BundlePathB $twoHouseBundle -OutputDirectory $outputDirectory

    foreach ($result in @($runSummaryTiny, $proposalTiny, $checklistTiny, $runSummaryTwo, $proposalTwo, $checklistTwo, $comparison)) {
        Assert-True -Condition (Test-Path -LiteralPath $result.export_path) -Message "Expected export file [$($result.export_path)] was not written."
        Assert-True -Condition (Test-PathOutsideEvidenceRoots -Path $result.export_path -RepoRoot $repoRoot) -Message "Export file [$($result.export_path)] was written inside a protected evidence root."
    }

    $tinyRunRows = Import-Csv -LiteralPath $runSummaryTiny.export_path
    $twoRunRows = Import-Csv -LiteralPath $runSummaryTwo.export_path
    $tinyProposalRows = Import-Csv -LiteralPath $proposalTiny.export_path
    $twoProposalRows = Import-Csv -LiteralPath $proposalTwo.export_path
    $tinyChecklistRows = Import-Csv -LiteralPath $checklistTiny.export_path
    $twoChecklistRows = Import-Csv -LiteralPath $checklistTwo.export_path
    $comparisonRows = Import-Csv -LiteralPath $comparison.export_path

    Assert-HasColumns -Row $tinyRunRows[0] -Columns @("bundle_warning_count", "bundle_warning_codes", "bundle_warning_messages")
    Assert-HasColumns -Row $tinyProposalRows[0] -Columns @("bundle_warning_count", "bundle_warning_codes", "bundle_warning_messages")
    Assert-HasColumns -Row $tinyChecklistRows[0] -Columns @("bundle_warning_count", "bundle_warning_codes", "category_warning_codes")
    Assert-HasColumns -Row $comparisonRows[0] -Columns @("warning_codes_a_only", "warning_codes_b_only", "warning_codes_shared")

    Assert-True -Condition ($twoRunRows[0].bundle_warning_codes -match "missing_operator_notes") -Message "Run summary export should preserve missing_operator_notes warning."
    Assert-True -Condition ($twoProposalRows[0].bundle_warning_codes -match "missing_operator_notes") -Message "Proposal summary export should preserve missing_operator_notes warning."
    Assert-True -Condition (@($twoChecklistRows | Where-Object { $_.category_warning_codes -match "missing_operator_notes" }).Count -gt 0) -Message "Checklist export should preserve missing_operator_notes warning on affected categories."
    Assert-True -Condition (@($comparisonRows | Where-Object { $_.warning_codes_b_only -match "missing_operator_notes" }).Count -gt 0) -Message "Comparison export should preserve missing_operator_notes warning difference."

    Write-Host "Export slice 4 accepted bundles:"
    Write-Host " - tiny-world smoke exports: PASS"
    Write-Host " - two-house-garden-v1 reviewed exports: PASS"
    Write-Host " - comparison summary export: PASS"
    Write-Host "Exports are CSV-first, written outside source bundles, and warnings are preserved without repair."
}
finally {
    if (Test-Path -LiteralPath $outputDirectory) {
        Remove-Item -LiteralPath $outputDirectory -Recurse -Force
    }
}
