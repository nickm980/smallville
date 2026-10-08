[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)]
    [string]$BundlePath,

    [Parameter(Mandatory = $true)]
    [string]$OutputDirectory,

    [string]$ChecklistPath = (Join-Path (Resolve-Path (Join-Path $PSScriptRoot "..\\..")) "docs\\evals\\two-house-garden-v1-review-checklist.md")
)

Import-Module (Join-Path $PSScriptRoot "OperatorSurfaceExports.psm1") -Force
Export-ChecklistOutcomeCsv -BundlePath $BundlePath -OutputDirectory $OutputDirectory -ChecklistPath $ChecklistPath | ConvertTo-Json -Depth 8
