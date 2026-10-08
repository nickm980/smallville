[CmdletBinding()]
param(
  [switch]$RunTests
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$toolchain = & (Join-Path $PSScriptRoot 'Use-SmallvilleToolchain.ps1')
$arguments = @('package')
if (-not $RunTests) {
  $arguments += '-DskipTests'
}

Push-Location (Join-Path $toolchain.RepoRoot 'smallville')
try {
  & $toolchain.Maven @arguments
}
finally {
  Pop-Location
}
