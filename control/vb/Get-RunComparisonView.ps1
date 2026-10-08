[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)]
    [string]$BundlePathA,

    [Parameter(Mandatory = $true)]
    [string]$BundlePathB,

    [string]$ChecklistPath = (Join-Path (Resolve-Path (Join-Path $PSScriptRoot "..\\..")) "docs\\evals\\two-house-garden-v1-review-checklist.md")
)

Import-Module (Join-Path $PSScriptRoot "OperatorSurfaceSlice1.psm1") -Force
Get-RunComparisonView -BundlePathA $BundlePathA -BundlePathB $BundlePathB -ChecklistPath $ChecklistPath | ConvertTo-Json -Depth 20
