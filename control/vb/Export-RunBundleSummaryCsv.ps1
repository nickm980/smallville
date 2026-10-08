[CmdletBinding()]
param(
    [Parameter(Mandatory = $true)]
    [string]$BundlePath,

    [Parameter(Mandatory = $true)]
    [string]$OutputDirectory
)

Import-Module (Join-Path $PSScriptRoot "OperatorSurfaceExports.psm1") -Force
Export-RunBundleSummaryCsv -BundlePath $BundlePath -OutputDirectory $OutputDirectory | ConvertTo-Json -Depth 8
