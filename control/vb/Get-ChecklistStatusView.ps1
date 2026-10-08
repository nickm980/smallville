[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)]
    [string]$BundlePath,

    [string]$ChecklistPath = (Join-Path (Resolve-Path (Join-Path $PSScriptRoot "..\\..")) "docs\\evals\\two-house-garden-v1-review-checklist.md")
)

Import-Module (Join-Path $PSScriptRoot "OperatorSurfaceSlice1.psm1") -Force
Get-ChecklistStatusView -BundlePath $BundlePath -ChecklistPath $ChecklistPath | ConvertTo-Json -Depth 16
