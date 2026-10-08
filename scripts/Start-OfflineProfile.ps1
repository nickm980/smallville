<#
.SYNOPSIS
Start SmallVille in offline mode with loopback-only enforcement.

.DESCRIPTION
Configures the server for fully offline operation:
- offlineMode: true  — no external API calls permitted
- loopbackOnly: true — only loopback API paths accepted
- Optional runtime root override via $env:SMALLVILLE_RUNTIME_ROOT

.PARAMETER Port
Server port (default 8091).

.PARAMETER ScenarioDir
Scenario directory to run. Defaults to scenarios/two-house-garden-v1.
#>
[CmdletBinding()]
param(
    [int]$Port = 8091,
    [string]$ScenarioDir
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$repoRoot = Resolve-Path (Join-Path $PSScriptRoot '..')
$smallvilleDir = Join-Path $repoRoot 'smallville'

if (-not $ScenarioDir) {
    $ScenarioDir = Join-Path $repoRoot 'scenarios' 'two-house-garden-v1'
}

if (-not (Test-Path -LiteralPath $ScenarioDir)) {
    throw "Scenario directory not found: $ScenarioDir"
}

# Locate the built jar
$jarPath = Get-ChildItem -Path (Join-Path $smallvilleDir 'target') -Filter 'smallville-*.jar' -File |
    Where-Object { $_.Name -notmatch 'sources|javadoc' } |
    Sort-Object -Property LastWriteTime -Descending |
    Select-Object -First 1

if (-not $jarPath) {
    throw "No smallville jar found in $smallvilleDir/target. Run 'mvn package' first."
}

# Runtime root
$runtimeRoot = if ($env:SMALLVILLE_RUNTIME_ROOT) {
    $env:SMALLVILLE_RUNTIME_ROOT
} else {
    Join-Path $repoRoot '.smallville-runtime'
}

if (-not (Test-Path -LiteralPath $runtimeRoot)) {
    New-Item -ItemType Directory -Path $runtimeRoot -Force | Out-Null
}

# Write offline config overlay
$configOverlay = @{
    offlineMode = $true
    loopbackOnly = $true
} | ConvertTo-Json

$configPath = Join-Path $runtimeRoot 'offline-config.json'
$configOverlay | Set-Content -Path $configPath -Encoding UTF8

Write-Host "Starting SmallVille in offline mode"
Write-Host "  Port:         $Port"
Write-Host "  Scenario:     $ScenarioDir"
Write-Host "  Runtime root: $runtimeRoot"
Write-Host "  Config:       $configPath"
Write-Host ""

# Find java
$java = if ($env:JAVA_HOME) {
    Join-Path $env:JAVA_HOME 'bin' 'java.exe'
} else {
    'java'
}

& $java -jar $jarPath.FullName --port $Port
