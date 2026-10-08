[CmdletBinding()]
param()

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$toolchain = & (Join-Path $PSScriptRoot 'Use-SmallvilleToolchain.ps1')

Push-Location (Join-Path $toolchain.RepoRoot 'smallville')
try {
  & $toolchain.Maven 'test'
}
finally {
  Pop-Location
}
