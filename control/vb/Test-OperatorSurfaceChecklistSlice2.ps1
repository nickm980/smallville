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

function Get-CategoryById {
    param(
        [Parameter(Mandatory = $true)]
        [object[]]$Categories,

        [Parameter(Mandatory = $true)]
        [string]$Id
    )

    $match = @($Categories | Where-Object { $_.id -eq $Id })
    if ($match.Count -eq 0) {
        throw "Checklist category [$Id] was not found."
    }

    return $match[0]
}

$repoRoot = Resolve-Path (Join-Path $PSScriptRoot "..\\..")
$tinyBundle = Join-Path $repoRoot "runs\\20260313-194619-tiny-world-smoke-control-llama3.2-3b-16k"
$twoBundle = Join-Path $repoRoot "runs\\20260313-200242-two-house-garden-v1-reviewed-llama3.2-3b-16k"

$expectedOrder = @(
    "plan-integrity",
    "location-validity",
    "task-coherence",
    "water-garden-relevance",
    "bounded-proposals",
    "action-log-readability",
    "restart-reproducibility"
)

$tinyChecklist = Get-ChecklistStatusView -BundlePath $tinyBundle
Assert-Equal -Actual (@($tinyChecklist.categories.id) -join ",") -Expected ($expectedOrder -join ",") -Message "Tiny-world checklist categories should render in checklist source order"
Assert-Equal -Actual (Get-CategoryById -Categories $tinyChecklist.categories -Id "bounded-proposals").status -Expected "present" -Message "Tiny-world bounded proposals should show present when zero proposals are recorded"
Assert-Equal -Actual (Get-CategoryById -Categories $tinyChecklist.categories -Id "restart-reproducibility").status -Expected "warning" -Message "Tiny-world restart reproducibility should surface missing restart evidence as warning"
Assert-Equal -Actual (Get-CategoryById -Categories $tinyChecklist.categories -Id "plan-integrity").status -Expected "manual-review-required" -Message "Tiny-world plan integrity should remain manual review"
Assert-True -Condition (@($tinyChecklist.warnings | Where-Object { $_.code -eq "missing_restart_evidence" }).Count -eq 1) -Message "Tiny-world checklist should carry forward shared warnings"

$twoChecklist = Get-ChecklistStatusView -BundlePath $twoBundle
Assert-Equal -Actual (@($twoChecklist.categories.id) -join ",") -Expected ($expectedOrder -join ",") -Message "Two-house checklist categories should render in checklist source order"
Assert-Equal -Actual (Get-CategoryById -Categories $twoChecklist.categories -Id "plan-integrity").status -Expected "warning" -Message "Two-house plan integrity should surface missing operator notes as warning"
Assert-Equal -Actual (Get-CategoryById -Categories $twoChecklist.categories -Id "bounded-proposals").status -Expected "manual-review-required" -Message "Two-house bounded proposals should require manual review when proposals are present"
Assert-Equal -Actual (Get-CategoryById -Categories $twoChecklist.categories -Id "restart-reproducibility").status -Expected "warning" -Message "Two-house restart reproducibility should surface missing operator notes as warning"
Assert-True -Condition (@((Get-CategoryById -Categories $twoChecklist.categories -Id "plan-integrity").warnings | Where-Object { $_.code -eq "missing_operator_notes" }).Count -eq 1) -Message "Two-house plan integrity should attach missing operator notes warning"
Assert-True -Condition (@($twoChecklist.warnings | Where-Object { $_.code -eq "missing_operator_notes" }).Count -eq 1) -Message "Two-house checklist should carry forward missing operator notes warning"

Write-Host "Checklist slice 2 accepted bundles:"
Write-Host " - tiny-world smoke: PASS"
Write-Host " - two-house-garden-v1 reviewed: PASS"
Write-Host "Checklist status view preserves order, warnings, and manual-review-required states."
