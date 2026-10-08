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

function Assert-ContainsWarning {
    param(
        [Parameter(Mandatory = $true)]
        [object[]]$Warnings,

        [Parameter(Mandatory = $true)]
        [string]$Code
    )

    Assert-True -Condition (@($Warnings | Where-Object { $_.code -eq $Code }).Count -gt 0) -Message "Expected warning [$Code] to be present"
}

function Assert-NotContainsWarning {
    param(
        [Parameter(Mandatory = $true)]
        [object[]]$Warnings,

        [Parameter(Mandatory = $true)]
        [string]$Code
    )

    Assert-True -Condition (@($Warnings | Where-Object { $_.code -eq $Code }).Count -eq 0) -Message "Did not expect warning [$Code]"
}

function New-TempBundleCopy {
    param(
        [Parameter(Mandatory = $true)]
        [string]$SourcePath,

        [Parameter(Mandatory = $true)]
        [string]$TempRoot,

        [Parameter(Mandatory = $true)]
        [string]$Name
    )

    $target = Join-Path $TempRoot $Name
    Copy-Item -LiteralPath $SourcePath -Destination $target -Recurse -Force
    return $target
}

$repoRoot = Resolve-Path (Join-Path $PSScriptRoot "..\\..")
$tinyBundle = Join-Path $repoRoot "runs\\20260313-194619-tiny-world-smoke-control-llama3.2-3b-16k"
$twoBundle = Join-Path $repoRoot "runs\\20260313-200242-two-house-garden-v1-reviewed-llama3.2-3b-16k"

$tinySummary = Get-RunBundleSummaryView -BundlePath $tinyBundle
$tinyProposal = Get-ProposalReviewView -BundlePath $tinyBundle

Assert-Equal -Actual $tinySummary.run_id -Expected "20260313-194619-tiny-world-smoke-control-llama3.2-3b-16k" -Message "Tiny-world smoke run id should resolve from the bundle"
Assert-Equal -Actual $tinySummary.scenario_name -Expected "tiny-world" -Message "Tiny-world smoke scenario should parse from manifest"
Assert-Equal -Actual $tinySummary.proposal_count -Expected 0 -Message "Tiny-world smoke proposal count should remain exactly as stored"
Assert-ContainsWarning -Warnings $tinySummary.warnings -Code "missing_restart_evidence"
Assert-NotContainsWarning -Warnings $tinySummary.warnings -Code "missing_operator_notes"
Assert-Equal -Actual $tinyProposal.review_notes[0] -Expected "none observed during review" -Message "Tiny-world zero-case proposal note should remain exact"
Assert-Equal -Actual @($tinyProposal.latest_pending_proposals).Count -Expected 0 -Message "Tiny-world smoke should show zero latest pending proposals"

$twoSummary = Get-RunBundleSummaryView -BundlePath $twoBundle
$twoProposal = Get-ProposalReviewView -BundlePath $twoBundle

Assert-Equal -Actual $twoSummary.run_id -Expected "20260313-200242-two-house-garden-v1-reviewed-llama3.2-3b-16k" -Message "Two-house reviewed run id should resolve from the bundle"
Assert-Equal -Actual $twoSummary.scenario_name -Expected "two-house-garden-v1" -Message "Two-house reviewed scenario should parse from manifest"
Assert-Equal -Actual $twoSummary.proposal_count -Expected 2 -Message "Two-house reviewed proposal count should remain exactly as stored"
Assert-ContainsWarning -Warnings $twoSummary.warnings -Code "missing_operator_notes"
Assert-NotContainsWarning -Warnings $twoSummary.warnings -Code "blank_proposal_count"
Assert-Equal -Actual @($twoProposal.review_notes).Count -Expected 2 -Message "Two-house reviewed notes should remain exact and complete"
Assert-Equal -Actual @($twoProposal.parsed_entries).Count -Expected 2 -Message "Two-house reviewed proposal notes should parse into two read-only entries"
Assert-Equal -Actual @($twoProposal.latest_pending_proposals).Count -Expected 2 -Message "Two-house reviewed latest pending proposals should stay visible"

$tempRoot = Join-Path ([System.IO.Path]::GetTempPath()) ("smallville-operator-surface-" + [guid]::NewGuid().ToString("N"))
New-Item -ItemType Directory -Path $tempRoot | Out-Null

try {
    $missingManifest = New-TempBundleCopy -SourcePath $tinyBundle -TempRoot $tempRoot -Name "missing-manifest"
    Remove-Item -LiteralPath (Join-Path $missingManifest "manifest.yaml")
    Assert-ContainsWarning -Warnings (Get-RunBundleSummaryView -BundlePath $missingManifest).warnings -Code "missing_manifest"

    $missingProposalReview = New-TempBundleCopy -SourcePath $tinyBundle -TempRoot $tempRoot -Name "missing-proposal-review"
    Remove-Item -LiteralPath (Join-Path $missingProposalReview "proposal_review.md")
    Assert-ContainsWarning -Warnings (Get-ProposalReviewView -BundlePath $missingProposalReview).warnings -Code "missing_proposal_review"

    $blankProposalCount = New-TempBundleCopy -SourcePath $tinyBundle -TempRoot $tempRoot -Name "blank-proposal-count"
    $blankReviewPath = Join-Path $blankProposalCount "proposal_review.md"
    $blankReviewRaw = Get-Content -LiteralPath $blankReviewPath -Raw
    $blankReviewRaw = [regex]::Replace($blankReviewRaw, '(?m)^proposal_count:\s*0\s*$', 'proposal_count: ')
    Set-Content -LiteralPath $blankReviewPath -Value $blankReviewRaw
    Assert-ContainsWarning -Warnings (Get-ProposalReviewView -BundlePath $blankProposalCount).warnings -Code "blank_proposal_count"

    $mismatchProposalCount = New-TempBundleCopy -SourcePath $twoBundle -TempRoot $tempRoot -Name "mismatch-proposal-count"
    $mismatchReviewPath = Join-Path $mismatchProposalCount "proposal_review.md"
    $mismatchReviewRaw = Get-Content -LiteralPath $mismatchReviewPath -Raw
    $mismatchReviewRaw = [regex]::Replace($mismatchReviewRaw, '(?m)^proposal_count:\s*2\s*$', 'proposal_count: 5')
    Set-Content -LiteralPath $mismatchReviewPath -Value $mismatchReviewRaw
    Assert-ContainsWarning -Warnings (Get-ProposalReviewView -BundlePath $mismatchProposalCount).warnings -Code "proposal_count_note_mismatch"

    $inconsistentRunId = New-TempBundleCopy -SourcePath $tinyBundle -TempRoot $tempRoot -Name "inconsistent-run-id"
    $inconsistentReviewPath = Join-Path $inconsistentRunId "proposal_review.md"
    $inconsistentReviewRaw = Get-Content -LiteralPath $inconsistentReviewPath -Raw
    $inconsistentReviewRaw = [regex]::Replace($inconsistentReviewRaw, '(?m)^run_id:\s*20260313-194619-tiny-world-smoke-control-llama3.2-3b-16k\s*$', 'run_id: wrong-run-id')
    Set-Content -LiteralPath $inconsistentReviewPath -Value $inconsistentReviewRaw
    Assert-ContainsWarning -Warnings (Get-ProposalReviewView -BundlePath $inconsistentRunId).warnings -Code "inconsistent_run_id"
}
finally {
    if (Test-Path -LiteralPath $tempRoot) {
        Remove-Item -LiteralPath $tempRoot -Recurse -Force
    }
}

Write-Host "Accepted bundles:"
Write-Host " - tiny-world smoke: PASS"
Write-Host " - two-house-garden-v1 reviewed: PASS"
Write-Host "Warning cases:"
Write-Host " - missing manifest.yaml: PASS"
Write-Host " - missing operator_notes.md: PASS"
Write-Host " - blank proposal_count: PASS"
Write-Host " - proposal_count mismatch with note lines: PASS"
Write-Host " - inconsistent run_id across files: PASS"
Write-Host " - missing restart evidence: PASS"
Write-Host " - missing proposal_review.md: PASS"
Write-Host "Operator surface slice 1 proof completed successfully."
