[CmdletBinding()]
param()

Set-StrictMode -Version Latest
$ErrorActionPreference = "Stop"

Import-Module (Join-Path $PSScriptRoot "OperatorSurfaceSlice1.psm1") -Force

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

function Assert-Equal {
    param(
        [Parameter(Mandatory = $true)]
        $Actual,

        [Parameter(Mandatory = $true)]
        $Expected,

        [Parameter(Mandatory = $true)]
        [string]$Message
    )

    if ($Actual -ne $Expected) {
        throw "$Message. Expected [$Expected], got [$Actual]."
    }
}

function Get-SectionRow {
    param(
        [Parameter(Mandatory = $true)]
        [object[]]$Rows,

        [Parameter(Mandatory = $true)]
        [string]$Field
    )

    $match = @($Rows | Where-Object { $_.field -eq $Field })
    if ($match.Count -eq 0) {
        throw "Comparison field [$Field] was not found."
    }

    return $match[0]
}

function Get-ChecklistRow {
    param(
        [Parameter(Mandatory = $true)]
        [object[]]$Rows,

        [Parameter(Mandatory = $true)]
        [string]$Id
    )

    $match = @($Rows | Where-Object { $_.id -eq $Id })
    if ($match.Count -eq 0) {
        throw "Checklist comparison row [$Id] was not found."
    }

    return $match[0]
}

$repoRoot = Resolve-Path (Join-Path $PSScriptRoot "..\\..")
$tinyBundle = Join-Path $repoRoot "runs\\20260313-194619-tiny-world-smoke-control-llama3.2-3b-16k"
$preFixTwoBundle = Join-Path $repoRoot "runs\\20260313-173414-two-house-garden-v1-reviewed-llama3.2-3b-16k"
$postFixTwoBundle = Join-Path $repoRoot "runs\\20260313-200242-two-house-garden-v1-reviewed-llama3.2-3b-16k"

$sameRunComparison = Get-RunComparisonView -BundlePathA $tinyBundle -BundlePathB $tinyBundle
Assert-Equal -Actual (Get-SectionRow -Rows $sameRunComparison.comparison_sections.bundle_identity -Field "run_id").status -Expected "same" -Message "Same-bundle comparison should keep run_id as same"
Assert-Equal -Actual (Get-SectionRow -Rows $sameRunComparison.comparison_sections.bundle_completeness -Field "restart_evidence_present").status -Expected "same" -Message "Same-bundle completeness should compare restart evidence as same"
Assert-Equal -Actual $sameRunComparison.comparison_sections.warning_comparison.status -Expected "same" -Message "Same-bundle warning comparison should be same"
Assert-Equal -Actual (Get-ChecklistRow -Rows $sameRunComparison.comparison_sections.checklist_status -Id "plan-integrity").status -Expected "manual-review-required" -Message "Same-bundle checklist rows should preserve manual-review-required when both sides are manual"

$postVsPreComparison = Get-RunComparisonView -BundlePathA $postFixTwoBundle -BundlePathB $preFixTwoBundle
Assert-Equal -Actual (Get-SectionRow -Rows $postVsPreComparison.comparison_sections.bundle_identity -Field "scenario_name").status -Expected "same" -Message "Pre/post formal harness comparison should keep scenario name the same"
Assert-Equal -Actual (Get-SectionRow -Rows $postVsPreComparison.comparison_sections.proposal_review -Field "parsed_entry_count").status -Expected "different" -Message "Pre/post proposal parsing evidence should show different parsed entry counts"
Assert-True -Condition (@($postVsPreComparison.comparison_sections.warning_comparison.warning_codes_a_only | Where-Object { $_ -eq "missing_operator_notes" }).Count -eq 1) -Message "Post-fix reviewed run should carry missing_operator_notes in A-only warnings"
Assert-Equal -Actual (Get-ChecklistRow -Rows $postVsPreComparison.comparison_sections.checklist_status -Id "plan-integrity").status -Expected "warning-difference" -Message "Pre/post plan integrity comparison should show warning-driven difference"

$contrastComparison = Get-RunComparisonView -BundlePathA $tinyBundle -BundlePathB $postFixTwoBundle
Assert-Equal -Actual (Get-SectionRow -Rows $contrastComparison.comparison_sections.bundle_identity -Field "scenario_name").status -Expected "different" -Message "Control vs protocol comparison should show different scenario names"
Assert-Equal -Actual (Get-SectionRow -Rows $contrastComparison.comparison_sections.bundle_completeness -Field "operator_notes_present").status -Expected "different" -Message "Control vs protocol comparison should surface operator notes completeness difference"
Assert-Equal -Actual (Get-SectionRow -Rows $contrastComparison.comparison_sections.proposal_review -Field "proposal_count").status -Expected "different" -Message "Control vs protocol comparison should surface proposal count difference"
Assert-Equal -Actual $contrastComparison.comparison_sections.warning_comparison.status -Expected "warning-difference" -Message "Control vs protocol warning comparison should surface warning difference"

Write-Host "Comparison slice 3 accepted comparisons:"
Write-Host " - tiny-world smoke vs tiny-world smoke: PASS"
Write-Host " - two-house-garden-v1 post-fix vs pre-fix reviewed: PASS"
Write-Host " - tiny-world smoke vs two-house-garden-v1 reviewed: PASS"
Write-Host "Run comparison view preserves side-by-side evidence comparison without scoring or repair."
