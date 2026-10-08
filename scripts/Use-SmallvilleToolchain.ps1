[CmdletBinding()]
param(
  [string]$MavenVersion = '3.9.12'
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

function Get-JavaHome {
  if ($env:JAVA_HOME) {
    $javaExe = Join-Path $env:JAVA_HOME 'bin\java.exe'
    if (Test-Path $javaExe) {
      return $env:JAVA_HOME
    }
  }

  $searchRoots = @(
    'C:\Program Files\Eclipse Adoptium',
    'C:\Program Files\Java'
  )

  foreach ($root in $searchRoots) {
    if (-not (Test-Path $root)) {
      continue
    }

    $match = Get-ChildItem $root -Directory -ErrorAction SilentlyContinue |
      Where-Object {
        $_.Name -like 'jdk-17*' -or $_.Name -like 'jdk-21*'
      } |
      Sort-Object Name -Descending |
      Select-Object -First 1

    if ($match) {
      return $match.FullName
    }
  }

  throw 'Java JDK not found. Install Temurin 17+ or set JAVA_HOME before running this script.'
}

function Get-MavenHome {
  param(
    [Parameter(Mandatory = $true)]
    [string]$RepoRoot,

    [Parameter(Mandatory = $true)]
    [string]$Version
  )

  $toolsDir = Join-Path $RepoRoot '.tools'
  $mavenHome = Join-Path $toolsDir "apache-maven-$Version"
  $mavenCmd = Join-Path $mavenHome 'bin\mvn.cmd'

  if (Test-Path $mavenCmd) {
    return $mavenHome
  }

  New-Item -ItemType Directory -Force -Path $toolsDir | Out-Null

  $archiveName = "apache-maven-$Version-bin.zip"
  $archivePath = Join-Path $toolsDir $archiveName
  $archiveUrl = "https://archive.apache.org/dist/maven/maven-3/$Version/binaries/$archiveName"

  Write-Host "Downloading Apache Maven $Version..."
  Invoke-WebRequest -Uri $archiveUrl -OutFile $archivePath

  if (Test-Path $mavenHome) {
    Remove-Item $mavenHome -Recurse -Force
  }

  Expand-Archive -Path $archivePath -DestinationPath $toolsDir -Force

  if (-not (Test-Path $mavenCmd)) {
    throw "Maven download completed, but $mavenCmd was not found."
  }

  return $mavenHome
}

$repoRoot = Split-Path -Parent $PSScriptRoot
$javaHome = Get-JavaHome
$mavenHome = Get-MavenHome -RepoRoot $repoRoot -Version $MavenVersion
$javaBin = Join-Path $javaHome 'bin'
$mavenBin = Join-Path $mavenHome 'bin'

$env:JAVA_HOME = $javaHome
$prefix = "$javaBin;$mavenBin"
if (-not $env:Path.StartsWith($prefix, [System.StringComparison]::OrdinalIgnoreCase)) {
  $env:Path = "$prefix;$env:Path"
}

[PSCustomObject]@{
  RepoRoot  = $repoRoot
  JavaHome  = $javaHome
  MavenHome = $mavenHome
  Java      = Join-Path $javaBin 'java.exe'
  Maven     = Join-Path $mavenBin 'mvn.cmd'
}
