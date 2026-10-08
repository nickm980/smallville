[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)]
    [string]$BundlePath
)

Import-Module (Join-Path $PSScriptRoot "OperatorSurfaceSlice1.psm1") -Force
Get-RunBundleSummaryView -BundlePath $BundlePath | ConvertTo-Json -Depth 16
