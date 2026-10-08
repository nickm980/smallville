[CmdletBinding()]
param(
  [string]$RepoRoot = (Join-Path $PSScriptRoot '..'),
  [int]$Port = 8091,
  [ValidateRange(0, 999)]
  [int]$Ticks = 3,
  [string]$RunId = ("{0:yyyyMMdd-HHmmss}-two-house-garden-v1-reviewed-llama3.2-3b-16k" -f (Get-Date)),
  [string]$MavenRepoLocal,
  [string]$ApprovedProposalAgent = '',
  [string]$ApprovedProposalParentLocation = '',
  [string]$ApprovedProposalName = '',
  [string]$ApprovedProposalState = ''
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

$currentStep = 'initialization'
$startupSoftTimeoutSec = 180
$startupHardTimeoutSec = 300
$tickRequestTimeoutSec = 1800
$focusedSuites = @(
  'SimulationServiceTest',
  'EndpointsTest',
  'EndpointExceptionsTest'
)
$startupProcess = $null
$restartProcess = $null
$startupScenarioLog = $null
$restartScenarioLog = $null
$startupShutdownState = $null
$restartShutdownState = $null
$configuredModel = ''
$frozenModel = ''

function Resolve-AbsolutePath {
  param(
    [Parameter(Mandatory = $true)]
    [string]$Path
  )

  return (Resolve-Path -LiteralPath $Path).Path
}

function Assert-PathExists {
  param(
    [Parameter(Mandatory = $true)]
    [string]$Path,

    [Parameter(Mandatory = $true)]
    [string]$Description
  )

  if (-not (Test-Path -LiteralPath $Path)) {
    throw "$Description was not found at $Path"
  }
}

function Invoke-CapturedCommand {
  param(
    [Parameter(Mandatory = $true)]
    [scriptblock]$Command,

    [Parameter(Mandatory = $true)]
    [string]$OutputPath,

    [Parameter(Mandatory = $true)]
    [string]$FailureDescription
  )

  $outputLines = @(& $Command 2>&1)
  $exitCode = $LASTEXITCODE
  $textLines = @($outputLines | ForEach-Object { $_.ToString() })
  $directory = Split-Path -Path $OutputPath -Parent
  if ($directory -and -not (Test-Path -LiteralPath $directory)) {
    New-Item -ItemType Directory -Path $directory -Force | Out-Null
  }
  $textLines | Set-Content -Path $OutputPath -Encoding UTF8

  if ($exitCode -ne 0) {
    throw "$FailureDescription failed with exit code $exitCode. See $OutputPath"
  }

  return @($textLines)
}

function Invoke-ProcessCapture {
  param(
    [Parameter(Mandatory = $true)]
    [string]$FilePath,

    [string[]]$ArgumentList = @(),

    [Parameter(Mandatory = $true)]
    [string]$WorkingDirectory,

    [Parameter(Mandatory = $true)]
    [string]$StdoutPath,

    [Parameter(Mandatory = $true)]
    [string]$StderrPath,

    [Parameter(Mandatory = $true)]
    [string]$FailureDescription
  )

  $stdoutDirectory = Split-Path -Path $StdoutPath -Parent
  if ($stdoutDirectory -and -not (Test-Path -LiteralPath $stdoutDirectory)) {
    New-Item -ItemType Directory -Path $stdoutDirectory -Force | Out-Null
  }

  $stderrDirectory = Split-Path -Path $StderrPath -Parent
  if ($stderrDirectory -and -not (Test-Path -LiteralPath $stderrDirectory)) {
    New-Item -ItemType Directory -Path $stderrDirectory -Force | Out-Null
  }

  $process = Start-Process -FilePath $FilePath -ArgumentList $ArgumentList -WorkingDirectory $WorkingDirectory -RedirectStandardOutput $StdoutPath -RedirectStandardError $StderrPath -PassThru -Wait
  $outputLines = @()
  if (Test-Path -LiteralPath $StdoutPath) {
    $outputLines += @(Get-Content -Path $StdoutPath)
  }
  if (Test-Path -LiteralPath $StderrPath) {
    $outputLines += @(Get-Content -Path $StderrPath)
  }

  if ($process.ExitCode -ne 0) {
    throw "$FailureDescription failed with exit code $($process.ExitCode). See $StdoutPath and $StderrPath"
  }

  return @($outputLines | ForEach-Object { $_.ToString() })
}

function Test-TcpPortOpen {
  param(
    [Parameter(Mandatory = $true)]
    [int]$Port,

    [int]$TimeoutMs = 500
  )

  $client = New-Object System.Net.Sockets.TcpClient
  try {
    $asyncResult = $client.BeginConnect('127.0.0.1', $Port, $null, $null)
    if (-not $asyncResult.AsyncWaitHandle.WaitOne($TimeoutMs)) {
      return $false
    }

    $client.EndConnect($asyncResult)
    return $client.Connected
  } catch {
    return $false
  } finally {
    $client.Dispose()
  }
}

function Invoke-JsonRequest {
  param(
    [Parameter(Mandatory = $true)]
    [ValidateSet('GET', 'POST')]
    [string]$Method,

    [Parameter(Mandatory = $true)]
    [string]$BaseUri,

    [Parameter(Mandatory = $true)]
    [string]$Path,

    [Parameter(Mandatory = $true)]
    [int]$TickIndex,

    [Parameter(Mandatory = $true)]
    [string]$OutputPath,

    [int]$TimeoutSec = 60
  )

  $uri = '{0}{1}' -f $BaseUri.TrimEnd('/'), $Path

  try {
    if ($Method -eq 'POST') {
      $body = Invoke-RestMethod -Method Post -Uri $uri -TimeoutSec $TimeoutSec -Body '{}' -ContentType 'application/json'
    } else {
      $body = Invoke-RestMethod -Method Get -Uri $uri -TimeoutSec $TimeoutSec
    }
  } catch {
    $statusCode = ''
    $responseBody = ''
    if ($_.Exception.Response) {
      try {
        $statusCode = [int]$_.Exception.Response.StatusCode
      } catch {
        $statusCode = ''
      }

      try {
        $stream = $_.Exception.Response.GetResponseStream()
        if ($stream) {
          $reader = New-Object System.IO.StreamReader($stream)
          $responseBody = $reader.ReadToEnd()
          $reader.Dispose()
        }
      } catch {
        $responseBody = ''
      }
    }

    throw "$Method $Path failed. status=$statusCode body=$responseBody"
  }

  $capture = [ordered]@{
    run_id = $script:RunId
    captured_at_utc = (Get-Date).ToUniversalTime().ToString('o')
    tick_index = $TickIndex
    method = $Method
    path = $Path
    http_status = 200
    body = $body
  }

  $capture | ConvertTo-Json -Depth 100 | Set-Content -Path $OutputPath -Encoding UTF8
  return $body
}

function Invoke-JsonRequestSafely {
  param(
    [Parameter(Mandatory = $true)]
    [ValidateSet('GET', 'POST')]
    [string]$Method,

    [Parameter(Mandatory = $true)]
    [string]$BaseUri,

    [Parameter(Mandatory = $true)]
    [string]$Path,

    [Parameter(Mandatory = $true)]
    [int]$TickIndex,

    [Parameter(Mandatory = $true)]
    [string]$OutputPath,

    [int]$TimeoutSec = 60
  )

  $uri = '{0}{1}' -f $BaseUri.TrimEnd('/'), $Path
  $body = $null
  $statusCode = 200
  $errorMessage = ''
  $errorBody = ''
  $requestSucceeded = $true

  try {
    if ($Method -eq 'POST') {
      $body = Invoke-RestMethod -Method Post -Uri $uri -TimeoutSec $TimeoutSec -Body '{}' -ContentType 'application/json'
    } else {
      $body = Invoke-RestMethod -Method Get -Uri $uri -TimeoutSec $TimeoutSec
    }
  } catch {
    $requestSucceeded = $false
    $statusCode = ''
    $errorMessage = $_.Exception.Message

    if ($_.Exception.Response) {
      try {
        $statusCode = [int]$_.Exception.Response.StatusCode
      } catch {
        $statusCode = ''
      }

      try {
        $stream = $_.Exception.Response.GetResponseStream()
        if ($stream) {
          $reader = New-Object System.IO.StreamReader($stream)
          $errorBody = $reader.ReadToEnd()
          $reader.Dispose()
        }
      } catch {
        $errorBody = ''
      }
    }
  }

  $capture = [ordered]@{
    run_id = $script:RunId
    captured_at_utc = (Get-Date).ToUniversalTime().ToString('o')
    tick_index = $TickIndex
    method = $Method
    path = $Path
    http_status = $statusCode
    body = $body
    success = $requestSucceeded
    error_message = $errorMessage
    error_body = $errorBody
  }

  $capture | ConvertTo-Json -Depth 100 | Set-Content -Path $OutputPath -Encoding UTF8

  return [pscustomobject]@{
    success = $requestSucceeded
    http_status = $statusCode
    body = $body
    error_message = $errorMessage
    error_body = $errorBody
  }
}

function Get-ObjectPropertyValue {
  param(
    [AllowNull()]
    [object]$Object,

    [Parameter(Mandatory = $true)]
    [string]$Name
  )

  if ($null -eq $Object) {
    return $null
  }

  $property = $Object.PSObject.Properties[$Name]
  if ($null -eq $property) {
    return $null
  }

  return $property.Value
}

function Get-CollectionValues {
  param(
    [AllowNull()]
    [object]$Value
  )

  if ($null -eq $Value) {
    return @()
  }

  return @(@($Value) | Where-Object { $null -ne $_ })
}

function Get-TrimmedStringValue {
  param(
    [AllowNull()]
    [object]$Value
  )

  if ($null -eq $Value) {
    return ''
  }

  return ([string]$Value).Trim()
}

function Get-NormalizedOptionalStringValue {
  param(
    [AllowNull()]
    [object]$Value
  )

  return Get-TrimmedStringValue -Value $Value
}

function Get-TargetLocationName {
  param(
    [Parameter(Mandatory = $true)]
    [string]$ParentLocation,

    [Parameter(Mandatory = $true)]
    [string]$Name
  )

  if ([string]::IsNullOrWhiteSpace($ParentLocation)) {
    return $Name
  }

  return '{0}: {1}' -f $ParentLocation, $Name
}

function Get-ApprovalProposalNote {
  param(
    [Parameter(Mandatory = $true)]
    [string]$Agent,

    [Parameter(Mandatory = $true)]
    [string]$ParentLocation,

    [Parameter(Mandatory = $true)]
    [string]$Name,

    [AllowEmptyString()]
    [string]$State = ''
  )

  return '[{0}] add_location parent=[{1}] name=[{2}] state=[{3}]' -f $Agent, $ParentLocation, $Name, $State
}

function Test-ExactApprovalEvidence {
  param(
    [Parameter(Mandatory = $true)]
    [string]$MatchedProposalId,

    [Parameter(Mandatory = $true)]
    [string]$ExpectedAgent,

    [Parameter(Mandatory = $true)]
    [string]$ExpectedParentLocation,

    [Parameter(Mandatory = $true)]
    [string]$ExpectedName,

    [AllowEmptyString()]
    [string]$ExpectedState = '',

    [Parameter(Mandatory = $true)]
    [object]$ApprovalResult,

    [Parameter(Mandatory = $true)]
    [object]$PostApprovalWorldResult,

    [Parameter(Mandatory = $true)]
    [object]$PostApprovalProposalResult
  )

  $targetLocationName = Get-TargetLocationName -ParentLocation $ExpectedParentLocation -Name $ExpectedName
  $expectedStateValue = Get-NormalizedOptionalStringValue -Value $ExpectedState

  if (-not $ApprovalResult.success) {
    return [pscustomobject]@{
      observed = $false
      reason = 'approval_response_unsuccessful'
      target_location = $targetLocationName
      applied_consequence = ''
    }
  }

  $approvalBody = Get-ObjectPropertyValue -Object $ApprovalResult -Name 'body'
  $responseProposal = Get-ObjectPropertyValue -Object $approvalBody -Name 'proposal'
  $responseProposalId = Get-TrimmedStringValue -Value (Get-ObjectPropertyValue -Object $responseProposal -Name 'id')
  $responseStatus = Get-TrimmedStringValue -Value (Get-ObjectPropertyValue -Object $responseProposal -Name 'status')

  if ($responseProposalId -ne $MatchedProposalId) {
    return [pscustomobject]@{
      observed = $false
      reason = "approval_response_id_mismatch expected=[$MatchedProposalId] actual=[$responseProposalId]"
      target_location = $targetLocationName
      applied_consequence = ''
    }
  }

  if ($responseStatus -ne 'applied') {
    return [pscustomobject]@{
      observed = $false
      reason = "approval_response_status_not_applied actual=[$responseStatus]"
      target_location = $targetLocationName
      applied_consequence = ''
    }
  }

  if (-not $PostApprovalWorldResult.success) {
    return [pscustomobject]@{
      observed = $false
      reason = "post_approval_world_capture_failed status=[$($PostApprovalWorldResult.http_status)] error=[$($PostApprovalWorldResult.error_message)]"
      target_location = $targetLocationName
      applied_consequence = ''
    }
  }

  if (-not $PostApprovalProposalResult.success) {
    return [pscustomobject]@{
      observed = $false
      reason = "post_approval_proposals_capture_failed status=[$($PostApprovalProposalResult.http_status)] error=[$($PostApprovalProposalResult.error_message)]"
      target_location = $targetLocationName
      applied_consequence = ''
    }
  }

  $postWorldBody = Get-ObjectPropertyValue -Object $PostApprovalWorldResult -Name 'body'
  $postProposalBody = Get-ObjectPropertyValue -Object $PostApprovalProposalResult -Name 'body'

  $targetLocation = $null
  foreach ($location in @(Get-CollectionValues -Value (Get-ObjectPropertyValue -Object $postWorldBody -Name 'locations'))) {
    $locationName = Get-TrimmedStringValue -Value (Get-ObjectPropertyValue -Object $location -Name 'name')
    if ($locationName -eq $targetLocationName) {
      $targetLocation = $location
      break
    }
  }

  if ($null -eq $targetLocation) {
    return [pscustomobject]@{
      observed = $false
      reason = "post_approval_location_missing target=[$targetLocationName]"
      target_location = $targetLocationName
      applied_consequence = ''
    }
  }

  $locationParent = Get-TrimmedStringValue -Value (Get-ObjectPropertyValue -Object $targetLocation -Name 'parent')
  $locationState = Get-NormalizedOptionalStringValue -Value (Get-ObjectPropertyValue -Object $targetLocation -Name 'state')
  if ($locationParent -ne $ExpectedParentLocation -or $locationState -ne $expectedStateValue) {
    return [pscustomobject]@{
      observed = $false
      reason = "post_approval_location_mismatch parent=[$locationParent] state=[$locationState]"
      target_location = $targetLocationName
      applied_consequence = ''
    }
  }

  $remainingQueuedMatches = @(
    @(Get-CollectionValues -Value (Get-ObjectPropertyValue -Object $postProposalBody -Name 'proposals')) |
      Where-Object {
        $proposalId = Get-TrimmedStringValue -Value (Get-ObjectPropertyValue -Object $_ -Name 'id')
        $proposalStatus = Get-TrimmedStringValue -Value (Get-ObjectPropertyValue -Object $_ -Name 'status')
        $proposalId -eq $MatchedProposalId -and @('pending', 'approved') -contains $proposalStatus
      }
  )

  if ($remainingQueuedMatches.Count -gt 0) {
    return [pscustomobject]@{
      observed = $false
      reason = "post_approval_queue_still_contains_match proposal_id=[$MatchedProposalId]"
      target_location = $targetLocationName
      applied_consequence = ''
    }
  }

  $actionLog = @(Get-CollectionValues -Value (Get-ObjectPropertyValue -Object $postWorldBody -Name 'actionLog'))
  $hasApprovedAction = @(
    $actionLog | Where-Object {
      (Get-TrimmedStringValue -Value (Get-ObjectPropertyValue -Object $_ -Name 'type')) -eq 'proposal-approved' -and
      (Get-TrimmedStringValue -Value (Get-ObjectPropertyValue -Object $_ -Name 'toLocation')) -eq $targetLocationName
    }
  ).Count -gt 0
  $hasAppliedAction = @(
    $actionLog | Where-Object {
      (Get-TrimmedStringValue -Value (Get-ObjectPropertyValue -Object $_ -Name 'type')) -eq 'proposal-applied' -and
      (Get-TrimmedStringValue -Value (Get-ObjectPropertyValue -Object $_ -Name 'toLocation')) -eq $targetLocationName
    }
  ).Count -gt 0

  if (-not $hasApprovedAction -or -not $hasAppliedAction) {
    return [pscustomobject]@{
      observed = $false
      reason = "post_approval_action_log_missing approved=[$hasApprovedAction] applied=[$hasAppliedAction]"
      target_location = $targetLocationName
      applied_consequence = ''
    }
  }

  return [pscustomobject]@{
    observed = $true
    reason = ''
    target_location = $targetLocationName
    applied_consequence = ('{0} target=[{1}] proposal_id=[{2}]' -f `
      (Get-ApprovalProposalNote -Agent $ExpectedAgent -ParentLocation $ExpectedParentLocation -Name $ExpectedName -State $expectedStateValue),
      $targetLocationName,
      $MatchedProposalId)
  }
}

function Wait-ForServerHealthy {
  param(
    [Parameter(Mandatory = $true)]
    [System.Diagnostics.Process]$Process,

    [Parameter(Mandatory = $true)]
    [string]$BaseUri,

    [Parameter(Mandatory = $true)]
    [int]$SoftTimeoutSec,

    [Parameter(Mandatory = $true)]
    [int]$HardTimeoutSec
  )

  $startedAt = Get-Date
  $softWarned = $false

  while ($true) {
    if ($Process.HasExited) {
      throw "Server process exited before becoming healthy. Exit code: $($Process.ExitCode)"
    }

    try {
      $response = Invoke-RestMethod -Method Get -Uri "$BaseUri/ping" -TimeoutSec 5
      if ($response.success -eq $true -and $response.ping -eq 'pong') {
        $healthyAt = Get-Date
        return [pscustomobject]@{
          started_at = $startedAt
          healthy_at = $healthyAt
          elapsed_seconds = [math]::Round(($healthyAt - $startedAt).TotalSeconds, 1)
        }
      }
    } catch {
      # Keep polling until timeout.
    }

    $elapsedSec = (Get-Date) - $startedAt
    if (-not $softWarned -and $elapsedSec.TotalSeconds -gt $SoftTimeoutSec) {
      Write-Warning "Server health check exceeded soft timeout of $SoftTimeoutSec seconds."
      $softWarned = $true
    }

    if ($elapsedSec.TotalSeconds -gt $HardTimeoutSec) {
      throw "Server did not become healthy within $HardTimeoutSec seconds."
    }

    Start-Sleep -Seconds 2
  }
}

function Get-NewScenarioLogPath {
  param(
    [Parameter(Mandatory = $true)]
    [string]$LogDirectory,

    [Parameter(Mandatory = $true)]
    [string[]]$KnownPaths
  )

  $known = @{}
  foreach ($path in $KnownPaths) {
    $known[$path] = $true
  }

  $candidate = Get-ChildItem -Path $LogDirectory -Filter 'logfile_*.log' -File -ErrorAction SilentlyContinue |
    Where-Object { -not $known.ContainsKey($_.FullName) } |
    Sort-Object LastWriteTime -Descending |
    Select-Object -First 1

  if ($candidate) {
    return $candidate.FullName
  }

  $latest = Get-ChildItem -Path $LogDirectory -Filter 'logfile_*.log' -File -ErrorAction SilentlyContinue |
    Sort-Object LastWriteTime -Descending |
    Select-Object -First 1

  if ($latest) {
    return $latest.FullName
  }

  return $null
}

function Get-LogLineRange {
  param(
    [Parameter(Mandatory = $true)]
    [AllowEmptyString()]
    [string[]]$Lines,

    [string[]]$MatchTerms = @(),

    [int]$TailCount = 0
  )

  if ($Lines.Count -eq 0) {
    return [pscustomobject]@{
      start = 1
      end = 0
    }
  }

  if ($Lines.Count -eq 1 -and $Lines[0] -eq '') {
    return [pscustomobject]@{
      start = 1
      end = 0
    }
  }

  if ($TailCount -gt 0) {
    $start = [math]::Max(1, $Lines.Count - $TailCount + 1)
    return [pscustomobject]@{
      start = $start
      end = $Lines.Count
    }
  }

  $matchedIndexes = New-Object System.Collections.Generic.List[int]
  foreach ($term in $MatchTerms) {
    for ($index = 0; $index -lt $Lines.Count; $index++) {
      if ($Lines[$index] -like "*$term*") {
        $matchedIndexes.Add($index + 1)
      }
    }
  }

  if ($matchedIndexes.Count -eq 0) {
    return [pscustomobject]@{
      start = 1
      end = $Lines.Count
    }
  }

  return [pscustomobject]@{
    start = ($matchedIndexes | Measure-Object -Minimum).Minimum
    end = ($matchedIndexes | Measure-Object -Maximum).Maximum
  }
}

function Write-LogExcerpt {
  param(
    [Parameter(Mandatory = $true)]
    [string]$SourceLogPath,

    [Parameter(Mandatory = $true)]
    [string]$OutputPath,

    [Parameter(Mandatory = $true)]
    [string]$RunId,

    [Parameter(Mandatory = $true)]
    [string]$MatchTerms,

    [string[]]$SearchTerms = @(),

    [int]$TailCount = 0
  )

  if (-not (Test-Path -LiteralPath $SourceLogPath)) {
    return
  }

  $lines = @(Get-Content -Path $SourceLogPath)
  $range = Get-LogLineRange -Lines $lines -MatchTerms $SearchTerms -TailCount $TailCount
  $excerptLines = @()

  if ($range.end -ge $range.start -and $range.end -gt 0) {
    $excerptLines = $lines[($range.start - 1)..($range.end - 1)]
  }

  $payload = @(
    "run_id: $RunId"
    "source_log: $SourceLogPath"
    "captured_at_utc: $((Get-Date).ToUniversalTime().ToString('o'))"
    "match_terms: $MatchTerms"
    "excerpt_begin_line: $($range.start) excerpt_end_line: $($range.end) "
  ) + $excerptLines

  $payload | Set-Content -Path $OutputPath -Encoding UTF8
}

function Get-ProposalAttemptRecords {
  param(
    [Parameter(Mandatory = $true)]
    [string]$LogPath
  )

  if (-not (Test-Path -LiteralPath $LogPath)) {
    return @()
  }

  $records = @()
  $keys = @('agent', 'outcome', 'detail', 'answer', 'type', 'parentLocation', 'name', 'proposedState', 'reason')

  foreach ($line in Get-Content -Path $LogPath) {
    if ($line -notmatch [regex]::Escape('[ProposalReview]')) {
      continue
    }

    $record = [ordered]@{
      raw_line = $line
      agent = ''
      outcome = ''
      detail = ''
      answer = ''
      type = ''
      parentLocation = ''
      name = ''
      proposedState = ''
      reason = ''
    }

    foreach ($key in $keys) {
      $match = [regex]::Match($line, [regex]::Escape($key) + '=\[(.*?)\]')
      if ($match.Success) {
        $record[$key] = $match.Groups[1].Value
      }
    }

    $records += [pscustomobject]$record
  }

  return @($records)
}

function Get-TemporalWarningSummary {
  param(
    [Parameter(Mandatory = $true)]
    [string]$LogPath,

    [int]$ExampleLimit = 5
  )

  $examples = @()
  if (Test-Path -LiteralPath $LogPath) {
    $examples = @(Select-String -Path $LogPath -Pattern 'Temporal memory possibly missing a time\. ' |
      ForEach-Object {
        $_.Line -replace '^.*Temporal memory possibly missing a time\. ', ''
      })
  }

  return [pscustomobject]@{
    count = $examples.Count
    examples = @($examples | Select-Object -First $ExampleLimit)
  }
}

function Get-ActivityResolutionWarningSummary {
  param(
    [Parameter(Mandatory = $true)]
    [string]$LogPath,

    [int]$ExampleLimit = 5
  )

  $examples = @()
  if (Test-Path -LiteralPath $LogPath) {
    $examples = @(Select-String -Path $LogPath -Pattern '\[Activity\] Resolved ' |
      ForEach-Object {
        $_.Line -replace '^.*\[Activity\] Resolved ', ''
      })
  }

  return [pscustomobject]@{
    count = $examples.Count
    examples = @($examples | Select-Object -First $ExampleLimit)
  }
}

function Get-WorldStateDeltaSummary {
  param(
    [Parameter(Mandatory = $true)]
    [string]$ColdWorldPath,

    [Parameter(Mandatory = $true)]
    [string]$FinalWorldPath,

    [int]$ExampleLimit = 5
  )

  $coldBody = (Get-Content -Path $ColdWorldPath -Raw | ConvertFrom-Json).body
  $finalBody = (Get-Content -Path $FinalWorldPath -Raw | ConvertFrom-Json).body

  $coldLocations = @{}
  foreach ($location in @($coldBody.locations)) {
    $coldLocations[$location.name] = [pscustomobject]@{
      parent = [string]$location.parent
      state = [string]$location.state
    }
  }

  $finalLocations = @{}
  foreach ($location in @($finalBody.locations)) {
    $finalLocations[$location.name] = [pscustomobject]@{
      parent = [string]$location.parent
      state = [string]$location.state
    }
  }

  $names = @($coldLocations.Keys + $finalLocations.Keys | Sort-Object -Unique)
  $deltaExamples = @()

  foreach ($name in $names) {
    $coldLocation = $coldLocations[$name]
    $finalLocation = $finalLocations[$name]

    if (-not $coldLocation) {
      $deltaExamples += "added [$name] parent=[$($finalLocation.parent)] state=[$($finalLocation.state)]"
      continue
    }

    if (-not $finalLocation) {
      $deltaExamples += "removed [$name] parent=[$($coldLocation.parent)] state=[$($coldLocation.state)]"
      continue
    }

    if ($coldLocation.parent -ne $finalLocation.parent -or $coldLocation.state -ne $finalLocation.state) {
      $deltaExamples += "changed [$name] from parent=[$($coldLocation.parent)] state=[$($coldLocation.state)] to parent=[$($finalLocation.parent)] state=[$($finalLocation.state)]"
    }
  }

  return [pscustomobject]@{
    status = if ($deltaExamples.Count -eq 0) { 'zero' } else { 'non_zero' }
    count = $deltaExamples.Count
    examples = @($deltaExamples | Select-Object -First $ExampleLimit)
  }
}

function Get-SemanticClassification {
  param(
    [Parameter(Mandatory = $true)]
    [object[]]$ProposalAttempts,

    [Parameter(Mandatory = $true)]
    [pscustomobject]$TemporalSummary,

    [Parameter(Mandatory = $true)]
    [pscustomobject]$ActivitySummary,

    [Parameter(Mandatory = $true)]
    [pscustomobject]$WorldDeltaSummary
  )

  $outcomes = @($ProposalAttempts | ForEach-Object { $_.outcome })
  $hasSemanticDegradation = (
    $outcomes -contains 'malformed_response' -or
    $outcomes -contains 'parser_rejected' -or
    $outcomes -contains 'dropped_proposal' -or
    $TemporalSummary.count -gt 0 -or
    $ActivitySummary.count -gt 0
  )

  return [pscustomobject]@{
    structural_status = 'structurally_complete'
    semantic_status = if ($hasSemanticDegradation) { 'semantically_degraded' } else { 'semantically_clean' }
    rule_slice_status = if ($hasSemanticDegradation -or $WorldDeltaSummary.count -eq 0) { 'blocked_for_rule_slice_justification' } else { 'not_blocked_for_rule_slice_justification' }
  }
}

function Stop-ServerProcess {
  param(
    [Parameter(Mandatory = $true)]
    [AllowNull()]
    [System.Diagnostics.Process]$Process,

    [Parameter(Mandatory = $true)]
    [int]$Port
  )

  $processGone = $true
  if ($null -ne $Process) {
    try {
      if (-not $Process.HasExited) {
        Stop-Process -Id $Process.Id -ErrorAction SilentlyContinue
        Start-Sleep -Seconds 2
      }
      if (-not $Process.HasExited) {
        Stop-Process -Id $Process.Id -Force -ErrorAction SilentlyContinue
        Start-Sleep -Seconds 2
      }
      $Process.Refresh()
      $processGone = $Process.HasExited
    } catch {
      $processGone = -not (Get-Process -Id $Process.Id -ErrorAction SilentlyContinue)
    }
  }

  $portClosed = $true
  for ($attempt = 0; $attempt -lt 10; $attempt++) {
    if (-not (Test-TcpPortOpen -Port $Port -TimeoutMs 250)) {
      $portClosed = $true
      break
    }
    $portClosed = $false
    Start-Sleep -Seconds 1
  }

  return [pscustomobject]@{
    shutdown_port_closed = $portClosed
    shutdown_process_gone = $processGone
  }
}

function Get-ConfigScalarValue {
  param(
    [Parameter(Mandatory = $true)]
    [string]$Path,

    [Parameter(Mandatory = $true)]
    [string]$Key
  )

  $raw = Get-Content -Path $Path -Raw
  $match = [regex]::Match($raw, "(?m)^\s*" + [regex]::Escape($Key) + ":\s*(?<value>.+?)\s*$")
  if (-not $match.Success) {
    return ''
  }

  return $match.Groups['value'].Value.Trim().Trim('"')
}

function Get-FocusedTestSummary {
  param(
    [Parameter(Mandatory = $true)]
    [string]$ReportsDirectory,

    [Parameter(Mandatory = $true)]
    [string[]]$SuiteNames
  )

  $tests = 0
  $failures = 0
  $errors = 0
  $skipped = 0

  foreach ($suite in $SuiteNames) {
    $report = Get-ChildItem -Path $ReportsDirectory -Filter "TEST-*$suite.xml" -File -ErrorAction SilentlyContinue |
      Sort-Object Name |
      Select-Object -First 1

    if (-not $report) {
      throw "Surefire report for $suite was not found in $ReportsDirectory"
    }

    [xml]$reportXml = Get-Content -Path $report.FullName
    $tests += [int]$reportXml.testsuite.tests
    $failures += [int]$reportXml.testsuite.failures
    $errors += [int]$reportXml.testsuite.errors
    $skipped += [int]$reportXml.testsuite.skipped
  }

  return '{0} tests, {1} failures, {2} errors, {3} skipped, BUILD SUCCESS' -f $tests, $failures, $errors, $skipped
}

function Write-Manifest {
  param(
    [Parameter(Mandatory = $true)]
    [string]$Path,

    [Parameter(Mandatory = $true)]
    [System.Collections.Specialized.OrderedDictionary]$Values
  )

  $lines = foreach ($entry in $Values.GetEnumerator()) {
    '{0}: {1}' -f $entry.Key, $entry.Value
  }

  $lines | Set-Content -Path $Path -Encoding UTF8
}

function Write-OperatorNotes {
  param(
    [Parameter(Mandatory = $true)]
    [string]$Path,

    [Parameter(Mandatory = $true)]
    [System.Collections.Specialized.OrderedDictionary]$Values
  )

  $lines = foreach ($entry in $Values.GetEnumerator()) {
    '{0}: {1}' -f $entry.Key, $entry.Value
  }

  $lines | Set-Content -Path $Path -Encoding UTF8
}

function Write-FailureSummary {
  param(
    [Parameter(Mandatory = $true)]
    [string]$Path,

    [Parameter(Mandatory = $true)]
    [string]$Step,

    [Parameter(Mandatory = $true)]
    [string]$Message,

    [AllowNull()]
    [System.Diagnostics.Process]$ActiveProcess,

    [AllowNull()]
    [string]$ScenarioLogPath
  )

  $artifactLines = @()
  $artifactDirectory = Split-Path -Path $Path -Parent
  if (Test-Path -LiteralPath $artifactDirectory) {
    $artifactLines = Get-ChildItem -Path $artifactDirectory -File -ErrorAction SilentlyContinue |
      Sort-Object Name |
      ForEach-Object { '- ' + $_.Name }
  }

  $lines = @(
    "run_id: $RunId"
    "failed_step: $Step"
    "failure_recorded_at_utc: $((Get-Date).ToUniversalTime().ToString('o'))"
    "message: $Message"
    "active_process_id: $(if ($ActiveProcess) { $ActiveProcess.Id } else { '' })"
    "active_process_has_exited: $(if ($ActiveProcess) { $ActiveProcess.HasExited } else { '' })"
    "scenario_log_path: $(if ($ScenarioLogPath) { $ScenarioLogPath } else { '' })"
    'artifacts_present:'
  ) + $artifactLines

  $lines | Set-Content -Path $Path -Encoding UTF8
}

$resolvedRepoRoot = Resolve-AbsolutePath -Path $RepoRoot
$script:RunId = $RunId
$scenarioDir = Join-Path $resolvedRepoRoot 'scenarios\two-house-garden-v1'
$scenarioConfigPath = Join-Path $scenarioDir 'config.yaml'
$scenarioSimulationPath = Join-Path $scenarioDir 'simulation.yaml'
$outputRoot = Join-Path $resolvedRepoRoot ("runs\{0}" -f $RunId)
$failureSummaryPath = Join-Path $outputRoot 'failure_summary.md'
$manifestPath = Join-Path $outputRoot 'manifest.yaml'
$proposalReviewPath = Join-Path $outputRoot 'proposal_review.md'
$operatorNotesPath = Join-Path $outputRoot 'operator_notes.md'
$startupStdoutPath = Join-Path $outputRoot 'server_stdout.log'
$startupStderrPath = Join-Path $outputRoot 'server_stderr.log'
$restartStdoutPath = Join-Path $outputRoot 'server_restart_stdout.log'
$restartStderrPath = Join-Path $outputRoot 'server_restart_stderr.log'
$scenarioLogsDir = Join-Path $scenarioDir 'logs'
$focusedTestsLogPath = Join-Path $outputRoot 'focused_tests.log'
$focusedTestsStdoutPath = Join-Path $outputRoot 'focused_tests.stdout.log'
$focusedTestsStderrPath = Join-Path $outputRoot 'focused_tests.stderr.log'
$buildLogPath = Join-Path $outputRoot 'build_server.log'
$buildStdoutPath = Join-Path $outputRoot 'build_server.stdout.log'
$buildStderrPath = Join-Path $outputRoot 'build_server.stderr.log'
$coldWorldCapturePath = Join-Path $outputRoot 'endpoint_world_cold.json'
$preApprovalProposalCapturePath = Join-Path $outputRoot 'endpoint_world_proposals_pre_approval.json'
$approvalResponseCapturePath = Join-Path $outputRoot 'endpoint_world_approval_response.json'
$postApprovalWorldCapturePath = Join-Path $outputRoot 'endpoint_world_post_approval.json'
$postApprovalProposalCapturePath = Join-Path $outputRoot 'endpoint_world_proposals_post_approval.json'
$finalWorldCapturePath = ''
$finalWorldSnapshotSource = 'not_applicable'
$approvalAgentValue = Get-TrimmedStringValue -Value $ApprovedProposalAgent
$approvalParentLocationValue = Get-TrimmedStringValue -Value $ApprovedProposalParentLocation
$approvalNameValue = Get-TrimmedStringValue -Value $ApprovedProposalName
$approvalStateValue = Get-NormalizedOptionalStringValue -Value $ApprovedProposalState
$approvalStateSpecified = -not [string]::IsNullOrWhiteSpace($ApprovedProposalState)
$approvalRequested = @(
  $ApprovedProposalAgent,
  $ApprovedProposalParentLocation,
  $ApprovedProposalName,
  $ApprovedProposalState
) | Where-Object { -not [string]::IsNullOrWhiteSpace($_) }
$approvalRequested = @($approvalRequested).Count -gt 0
$approvalRequestedText = ([string]$approvalRequested).ToLowerInvariant()
$approvalStatus = 'not_requested'
$approvalUniqueMatchFound = $false
$approvalSucceeded = $false
$applicationEvidenceObserved = $false
$approvalTargetNote = ''
$approvalMatchNote = ''
$approvalBlockedNote = ''
$approvalResponseNote = ''
$appliedConsequenceNotes = @()
$approvedCount = 0
$appliedCount = 0
$worldStateDeltaSource = 'not_applicable'
$preApprovalProposalResponse = $null
$postApprovalProposalResult = $null
$approvalMatchedProposalId = ''
$approvalExpectedStateValue = ''

if ($approvalRequested) {
  $approvalTargetNote = Get-ApprovalProposalNote -Agent $approvalAgentValue -ParentLocation $approvalParentLocationValue -Name $approvalNameValue -State $approvalStateValue
}

try {
  $currentStep = 'path validation'
  Assert-PathExists -Path $resolvedRepoRoot -Description 'Repo root'
  Assert-PathExists -Path $scenarioDir -Description 'Two-house scenario directory'
  Assert-PathExists -Path $scenarioConfigPath -Description 'Two-house config.yaml'
  Assert-PathExists -Path $scenarioSimulationPath -Description 'Two-house simulation.yaml'
  Assert-PathExists -Path (Join-Path $resolvedRepoRoot 'scripts\Use-SmallvilleToolchain.ps1') -Description 'Use-SmallvilleToolchain.ps1'
  Assert-PathExists -Path (Join-Path $resolvedRepoRoot 'scripts\Write-ProposalReview.ps1') -Description 'Write-ProposalReview.ps1'

  if (Test-Path -LiteralPath $outputRoot) {
    throw "Run output directory already exists: $outputRoot"
  }

  New-Item -ItemType Directory -Path $outputRoot -Force | Out-Null
  New-Item -ItemType Directory -Path $scenarioLogsDir -Force | Out-Null

  if ($MavenRepoLocal) {
    if (-not (Test-Path -LiteralPath $MavenRepoLocal)) {
      New-Item -ItemType Directory -Path $MavenRepoLocal -Force | Out-Null
    }
    $MavenRepoLocal = Resolve-AbsolutePath -Path $MavenRepoLocal
  }

  if (Test-TcpPortOpen -Port $Port) {
    throw "Port $Port is already in use."
  }

  $currentStep = 'load toolchain'
  $toolchain = & (Join-Path $resolvedRepoRoot 'scripts\Use-SmallvilleToolchain.ps1')
  $smallvilleDir = Join-Path $resolvedRepoRoot 'smallville'

  $currentStep = 'verify repo state'
  Push-Location $resolvedRepoRoot
  try {
    $gitBranch = (& git branch --show-current).Trim()
    $gitHead = (& git rev-parse HEAD).Trim()
    $gitStatusShort = @(& git status --short --untracked-files=all 2>&1)
    if ($LASTEXITCODE -ne 0) {
      throw 'git status failed'
    }
    $gitClean = (@($gitStatusShort | Where-Object { -not [string]::IsNullOrWhiteSpace($_) }).Count -eq 0)
  } finally {
    Pop-Location
  }

  $currentStep = 'capture tool versions'
  $javaVersionLines = Invoke-ProcessCapture -FilePath $toolchain.Java -ArgumentList @('-version') -WorkingDirectory $resolvedRepoRoot -StdoutPath (Join-Path $outputRoot 'java_version.stdout.log') -StderrPath (Join-Path $outputRoot 'java_version.stderr.log') -FailureDescription 'Java version command'
  $javaVersion = ($javaVersionLines | Where-Object { -not [string]::IsNullOrWhiteSpace($_) } | Select-Object -First 1).Trim()
  $mavenVersionLines = Invoke-ProcessCapture -FilePath $toolchain.Maven -ArgumentList @('-version') -WorkingDirectory $smallvilleDir -StdoutPath (Join-Path $outputRoot 'maven_version.stdout.log') -StderrPath (Join-Path $outputRoot 'maven_version.stderr.log') -FailureDescription 'Maven version command'
  $mavenVersion = ($mavenVersionLines | Where-Object { -not [string]::IsNullOrWhiteSpace($_) } | Select-Object -First 1).Trim()

  $reportsDir = Join-Path $smallvilleDir 'target\surefire-reports'
  $mavenPrefix = @()
  if ($MavenRepoLocal) {
    $mavenPrefix += "-Dmaven.repo.local=$MavenRepoLocal"
  }
  $mavenPrefix += '-q'

  $currentStep = 'run focused tests'
  $testArguments = @($mavenPrefix + @("-Dtest=$($focusedSuites -join ',')", 'test'))
  $focusedTestLines = Invoke-ProcessCapture -FilePath $toolchain.Maven -ArgumentList $testArguments -WorkingDirectory $smallvilleDir -StdoutPath $focusedTestsStdoutPath -StderrPath $focusedTestsStderrPath -FailureDescription 'Focused test command'
  $focusedTestLines | Set-Content -Path $focusedTestsLogPath -Encoding UTF8
  $targetedTestResult = Get-FocusedTestSummary -ReportsDirectory $reportsDir -SuiteNames $focusedSuites
  $targetedTestCommand = 'mvn -q "-Dtest=SimulationServiceTest,EndpointsTest,EndpointExceptionsTest" test'
  if ($MavenRepoLocal) {
    $targetedTestCommand = 'mvn -q "-Dmaven.repo.local={0}" "-Dtest=SimulationServiceTest,EndpointsTest,EndpointExceptionsTest" test' -f $MavenRepoLocal
  }

  $currentStep = 'build server'
  $buildArguments = @($mavenPrefix + @('package', '-DskipTests'))
  $buildLines = Invoke-ProcessCapture -FilePath $toolchain.Maven -ArgumentList $buildArguments -WorkingDirectory $smallvilleDir -StdoutPath $buildStdoutPath -StderrPath $buildStderrPath -FailureDescription 'Build command'
  $buildLines | Set-Content -Path $buildLogPath -Encoding UTF8
  $buildCommand = 'mvn -q package -DskipTests'
  if ($MavenRepoLocal) {
    $buildCommand = 'mvn -q "-Dmaven.repo.local={0}" package -DskipTests' -f $MavenRepoLocal
  }

  $jarPath = Get-ChildItem -Path (Join-Path $smallvilleDir 'target') -Filter '*.jar' -File -ErrorAction SilentlyContinue |
    Where-Object { $_.Name -notlike 'original-*' } |
    Sort-Object LastWriteTime -Descending |
    Select-Object -First 1

  if (-not $jarPath) {
    throw 'No Smallville server jar was found after package.'
  }

  $jarHash = (Get-FileHash -Path $jarPath.FullName -Algorithm SHA256).Hash
  $apiPath = Get-ConfigScalarValue -Path $scenarioConfigPath -Key 'apiPath'
  $configuredModel = Get-ConfigScalarValue -Path $scenarioConfigPath -Key 'model'
  $launchCommand = 'Push-Location "{0}"; & "{1}" -jar "{2}" --port {3}' -f $scenarioDir, $toolchain.Java, $jarPath.FullName, $Port
  $baseUri = "http://127.0.0.1:$Port"

  $currentStep = 'start primary server'
  $knownLogsBeforeStart = @(Get-ChildItem -Path $scenarioLogsDir -Filter 'logfile_*.log' -File -ErrorAction SilentlyContinue | Select-Object -ExpandProperty FullName)
  $startupProcess = Start-Process -FilePath $toolchain.Java -ArgumentList @('-jar', $jarPath.FullName, '--port', "$Port") -WorkingDirectory $scenarioDir -RedirectStandardOutput $startupStdoutPath -RedirectStandardError $startupStderrPath -PassThru
  $startupHealth = Wait-ForServerHealthy -Process $startupProcess -BaseUri $baseUri -SoftTimeoutSec $startupSoftTimeoutSec -HardTimeoutSec $startupHardTimeoutSec
  Start-Sleep -Seconds 1
  $startupScenarioLog = Get-NewScenarioLogPath -LogDirectory $scenarioLogsDir -KnownPaths $knownLogsBeforeStart

  $currentStep = 'capture cold endpoints'
  Invoke-JsonRequest -Method GET -BaseUri $baseUri -Path '/ping' -TickIndex 0 -OutputPath (Join-Path $outputRoot 'endpoint_ping_cold.json') | Out-Null
  $modelsBody = Invoke-JsonRequest -Method GET -BaseUri $baseUri -Path '/models' -TickIndex 0 -OutputPath (Join-Path $outputRoot 'endpoint_models_cold.json')
  Invoke-JsonRequest -Method GET -BaseUri $baseUri -Path '/world' -TickIndex 0 -OutputPath $coldWorldCapturePath | Out-Null
  Invoke-JsonRequest -Method GET -BaseUri $baseUri -Path '/state' -TickIndex 0 -OutputPath (Join-Path $outputRoot 'endpoint_state_cold.json') | Out-Null
  Invoke-JsonRequest -Method GET -BaseUri $baseUri -Path '/info' -TickIndex 0 -OutputPath (Join-Path $outputRoot 'endpoint_info_cold.json') | Out-Null
  Invoke-JsonRequest -Method GET -BaseUri $baseUri -Path '/agents' -TickIndex 0 -OutputPath (Join-Path $outputRoot 'endpoint_agents_cold.json') | Out-Null
  Invoke-JsonRequest -Method GET -BaseUri $baseUri -Path '/world/proposals' -TickIndex 0 -OutputPath (Join-Path $outputRoot 'endpoint_world_proposals_cold.json') | Out-Null
  Invoke-JsonRequestSafely -Method GET -BaseUri $baseUri -Path '/world/ledger/export' -TickIndex 0 -OutputPath (Join-Path $outputRoot 'endpoint_ledger_export_cold.json') | Out-Null

  $frozenModel = if ($modelsBody.defaultModel) { [string]$modelsBody.defaultModel } else { $configuredModel }
  $providerMode = if ($modelsBody.providerMode) { [string]$modelsBody.providerMode } else { 'unknown' }

  $currentStep = 'write startup artifacts'
  $manifestValues = [ordered]@{
    run_id = $RunId
    scenario_name = 'two-house-garden-v1'
    launch_mode = 'host-native'
    smallville_port = $Port
    frozen_model = $frozenModel
    launch_command = $launchCommand
    jar_path = $jarPath.FullName
    startup_started_at_utc = $startupHealth.started_at.ToUniversalTime().ToString('o')
    startup_healthy_at_utc = $startupHealth.healthy_at.ToUniversalTime().ToString('o')
    startup_elapsed_seconds = $startupHealth.elapsed_seconds
    launch_script = Join-Path $resolvedRepoRoot 'scripts\Invoke-TwoHouseReviewedRun.ps1'
    working_directory = $scenarioDir
    provider_mode_expected = $providerMode
    api_path = $apiPath
    java_version = $javaVersion
    maven_version = $mavenVersion
    jar_name = $jarPath.Name
    jar_sha256 = $jarHash
    git_clean = $gitClean.ToString().ToLowerInvariant()
    git_branch = $gitBranch
    git_head = $gitHead
    targeted_test_command = $targetedTestCommand
    targeted_test_result = $targetedTestResult
    full_suite_test_command = 'not run by this harness'
    full_suite_test_result = 'not run by this harness'
    build_command = $buildCommand
    build_result = 'package succeeded'
    startup_soft_timeout_sec = $startupSoftTimeoutSec
    startup_hard_timeout_sec = $startupHardTimeoutSec
    approval_scope = 'exact-match-optional-reviewed-harness'
    approval_requested = $approvalRequestedText
    approval_status = $approvalStatus
    approval_unique_match_found = ([string]$approvalUniqueMatchFound).ToLowerInvariant()
    approval_succeeded = ([string]$approvalSucceeded).ToLowerInvariant()
    application_evidence_observed = ([string]$applicationEvidenceObserved).ToLowerInvariant()
    approval_target = $approvalTargetNote
    final_world_snapshot_source = $finalWorldSnapshotSource
    intended_tick_count = $Ticks
  }
  Write-Manifest -Path $manifestPath -Values $manifestValues

  if ($startupScenarioLog) {
    Write-LogExcerpt -SourceLogPath $startupScenarioLog -OutputPath (Join-Path $outputRoot 'log_excerpt_startup.txt') -RunId $RunId -MatchTerms "Starting server..., Smallville server started on port $Port" -SearchTerms @('Starting server...', "Smallville server started on port $Port")
  }

  $currentStep = 'run tick captures'
  $lastTickLogExcerptPath = ''
  for ($tick = 1; $tick -le $Ticks; $tick++) {
    $tickName = '{0:D3}' -f $tick
    Invoke-JsonRequest -Method POST -BaseUri $baseUri -Path '/state' -TickIndex $tick -OutputPath (Join-Path $outputRoot "endpoint_state_post_tick_$tickName.json") -TimeoutSec $tickRequestTimeoutSec | Out-Null
    $finalWorldCapturePath = Join-Path $outputRoot "endpoint_world_tick_$tickName.json"
    Invoke-JsonRequest -Method GET -BaseUri $baseUri -Path '/world' -TickIndex $tick -OutputPath $finalWorldCapturePath -TimeoutSec 60 | Out-Null
    Invoke-JsonRequest -Method GET -BaseUri $baseUri -Path '/state' -TickIndex $tick -OutputPath (Join-Path $outputRoot "endpoint_state_tick_$tickName.json") -TimeoutSec 60 | Out-Null
    $lastTickLogExcerptPath = Join-Path $outputRoot "log_excerpt_tick_$tickName.txt"
  }

  if ($Ticks -gt 0 -and $startupScenarioLog) {
    Write-LogExcerpt -SourceLogPath $startupScenarioLog -OutputPath $lastTickLogExcerptPath -RunId $RunId -MatchTerms "tick-$Ticks-tail" -TailCount 80
  }

  if ($Ticks -gt 0 -and $finalWorldCapturePath) {
    $finalWorldSnapshotSource = Split-Path -Path $finalWorldCapturePath -Leaf
    $worldStateDeltaSource = $finalWorldSnapshotSource
  }

  $currentStep = 'capture final proposal state'
  $preApprovalProposalResponse = Invoke-JsonRequest -Method GET -BaseUri $baseUri -Path '/world/proposals' -TickIndex $Ticks -OutputPath $preApprovalProposalCapturePath -TimeoutSec 60
  Invoke-JsonRequestSafely -Method GET -BaseUri $baseUri -Path '/world/ledger/export' -TickIndex $Ticks -OutputPath (Join-Path $outputRoot 'endpoint_ledger_export_final.json') | Out-Null
  $finalProposalResponse = $preApprovalProposalResponse

  if ($approvalRequested) {
    $currentStep = 'process exact-match approval'

    if ([string]::IsNullOrWhiteSpace($approvalAgentValue) -or [string]::IsNullOrWhiteSpace($approvalParentLocationValue) -or [string]::IsNullOrWhiteSpace($approvalNameValue)) {
      $approvalStatus = 'invalid_request_missing_identity_fields'
      $approvalBlockedNote = 'exact-match approval requested without required agent, parentLocation, and name fields'
    } else {
      $matchingProposals = @(
        @(Get-CollectionValues -Value (Get-ObjectPropertyValue -Object $preApprovalProposalResponse -Name 'proposals')) |
          Where-Object {
            $proposalStatus = Get-TrimmedStringValue -Value (Get-ObjectPropertyValue -Object $_ -Name 'status')
            $proposalType = Get-TrimmedStringValue -Value (Get-ObjectPropertyValue -Object $_ -Name 'type')
            $proposalAgent = Get-TrimmedStringValue -Value (Get-ObjectPropertyValue -Object $_ -Name 'agent')
            $proposalParentLocation = Get-TrimmedStringValue -Value (Get-ObjectPropertyValue -Object $_ -Name 'parentLocation')
            $proposalName = Get-TrimmedStringValue -Value (Get-ObjectPropertyValue -Object $_ -Name 'name')
            $proposalState = Get-NormalizedOptionalStringValue -Value (Get-ObjectPropertyValue -Object $_ -Name 'proposedState')
            $stateMatches = if ($approvalStateSpecified) { $proposalState -eq $approvalStateValue } else { $true }

            $proposalStatus -eq 'pending' -and
            $proposalType -eq 'add_location' -and
            $proposalAgent -eq $approvalAgentValue -and
            $proposalParentLocation -eq $approvalParentLocationValue -and
            $proposalName -eq $approvalNameValue -and
            $stateMatches
          }
      )

      if ($matchingProposals.Count -eq 0) {
        $approvalStatus = 'requested_no_match'
        $approvalBlockedNote = ('no queued add_location proposal matched {0}' -f $approvalTargetNote)
      } elseif ($matchingProposals.Count -gt 1) {
        $approvalStatus = 'requested_multiple_matches'
        $approvalBlockedNote = ('multiple queued add_location proposals matched {0}' -f $approvalTargetNote)
      } else {
        $approvalUniqueMatchFound = $true
        $matchedProposal = $matchingProposals[0]
        $approvalMatchedProposalId = Get-TrimmedStringValue -Value (Get-ObjectPropertyValue -Object $matchedProposal -Name 'id')
        $matchedProposalState = Get-NormalizedOptionalStringValue -Value (Get-ObjectPropertyValue -Object $matchedProposal -Name 'proposedState')
        $approvalExpectedStateValue = if ($approvalStateSpecified) { $approvalStateValue } else { $matchedProposalState }
        $approvalMatchNote = ('proposal_id=[{0}] type=[add_location] parent=[{1}] name=[{2}] state=[{3}]' -f `
          $approvalMatchedProposalId,
          $approvalParentLocationValue,
          $approvalNameValue,
          $approvalExpectedStateValue)

        try {
          $approvalResult = Invoke-JsonRequestSafely -Method POST -BaseUri $baseUri -Path ('/world/proposals/{0}/approve' -f $approvalMatchedProposalId) -TickIndex $Ticks -OutputPath $approvalResponseCapturePath -TimeoutSec 60

          if (-not $approvalResult.success) {
            $approvalStatus = 'approve_endpoint_failed'
            $approvalBlockedNote = ('proposal_id=[{0}] http_status=[{1}] error=[{2}]' -f `
              $approvalMatchedProposalId,
              $approvalResult.http_status,
              $approvalResult.error_message)
          } else {
            $approvalResponseProposal = Get-ObjectPropertyValue -Object (Get-ObjectPropertyValue -Object $approvalResult -Name 'body') -Name 'proposal'
            $approvalResponseId = Get-TrimmedStringValue -Value (Get-ObjectPropertyValue -Object $approvalResponseProposal -Name 'id')
            $approvalResponseStatusValue = Get-TrimmedStringValue -Value (Get-ObjectPropertyValue -Object $approvalResponseProposal -Name 'status')
            $approvalResponseNote = ('proposal_id=[{0}] status=[{1}] http_status=[{2}]' -f `
              $approvalResponseId,
              $approvalResponseStatusValue,
              $approvalResult.http_status)

            if ($approvalResponseId -eq $approvalMatchedProposalId -and $approvalResponseStatusValue -eq 'applied') {
              $approvedCount = 1
            }

            $postApprovalWorldResult = Invoke-JsonRequestSafely -Method GET -BaseUri $baseUri -Path '/world' -TickIndex $Ticks -OutputPath $postApprovalWorldCapturePath -TimeoutSec 60
            $postApprovalProposalResult = Invoke-JsonRequestSafely -Method GET -BaseUri $baseUri -Path '/world/proposals' -TickIndex $Ticks -OutputPath $postApprovalProposalCapturePath -TimeoutSec 60

            if ($postApprovalProposalResult.success) {
              $finalProposalResponse = Get-ObjectPropertyValue -Object $postApprovalProposalResult -Name 'body'
            }

            $approvalEvidence = Test-ExactApprovalEvidence `
              -MatchedProposalId $approvalMatchedProposalId `
              -ExpectedAgent $approvalAgentValue `
              -ExpectedParentLocation $approvalParentLocationValue `
              -ExpectedName $approvalNameValue `
              -ExpectedState $approvalExpectedStateValue `
              -ApprovalResult $approvalResult `
              -PostApprovalWorldResult $postApprovalWorldResult `
              -PostApprovalProposalResult $postApprovalProposalResult

            if ($approvalEvidence.observed) {
              $approvalStatus = 'matched_and_applied'
              $approvalSucceeded = $true
              $applicationEvidenceObserved = $true
              $appliedCount = 1
              $appliedConsequenceNotes += $approvalEvidence.applied_consequence
              $finalWorldCapturePath = $postApprovalWorldCapturePath
              $finalWorldSnapshotSource = Split-Path -Path $postApprovalWorldCapturePath -Leaf
              $worldStateDeltaSource = $finalWorldSnapshotSource
            } else {
              $approvalStatus = 'matched_but_inconsistent_evidence'
              $approvalBlockedNote = $approvalEvidence.reason
            }
          }
        } catch {
          $approvalStatus = 'approve_endpoint_failed'
          $approvalBlockedNote = ('proposal_id=[{0}] error=[{1}]' -f $approvalMatchedProposalId, $_.Exception.Message)
        }
      }
    }
  }

  $currentStep = 'stop primary server'
  $startupShutdownState = Stop-ServerProcess -Process $startupProcess -Port $Port
  $startupProcess = $null

  $currentStep = 'restart server for cold capture'
  $knownLogsBeforeRestart = @(Get-ChildItem -Path $scenarioLogsDir -Filter 'logfile_*.log' -File -ErrorAction SilentlyContinue | Select-Object -ExpandProperty FullName)
  $restartProcess = Start-Process -FilePath $toolchain.Java -ArgumentList @('-jar', $jarPath.FullName, '--port', "$Port") -WorkingDirectory $scenarioDir -RedirectStandardOutput $restartStdoutPath -RedirectStandardError $restartStderrPath -PassThru
  $restartHealth = Wait-ForServerHealthy -Process $restartProcess -BaseUri $baseUri -SoftTimeoutSec $startupSoftTimeoutSec -HardTimeoutSec $startupHardTimeoutSec
  Start-Sleep -Seconds 1
  $restartScenarioLog = Get-NewScenarioLogPath -LogDirectory $scenarioLogsDir -KnownPaths $knownLogsBeforeRestart

  $currentStep = 'capture restart endpoints'
  Invoke-JsonRequest -Method GET -BaseUri $baseUri -Path '/world' -TickIndex $Ticks -OutputPath (Join-Path $outputRoot 'endpoint_world_restart_cold.json') -TimeoutSec 60 | Out-Null
  Invoke-JsonRequest -Method GET -BaseUri $baseUri -Path '/state' -TickIndex $Ticks -OutputPath (Join-Path $outputRoot 'endpoint_state_restart_cold.json') -TimeoutSec 60 | Out-Null
  Invoke-JsonRequestSafely -Method GET -BaseUri $baseUri -Path '/world/ledger/export' -TickIndex $Ticks -OutputPath (Join-Path $outputRoot 'endpoint_ledger_export_restart_cold.json') -TimeoutSec 60 | Out-Null

  if ($restartScenarioLog) {
    Write-LogExcerpt -SourceLogPath $restartScenarioLog -OutputPath (Join-Path $outputRoot 'log_excerpt_restart.txt') -RunId $RunId -MatchTerms "Starting server..., Smallville server started on port $Port" -SearchTerms @('Starting server...', "Smallville server started on port $Port")
  }

  $currentStep = 'stop restarted server'
  $restartShutdownState = Stop-ServerProcess -Process $restartProcess -Port $Port
  $restartProcess = $null

  $currentStep = 'summarize semantic review'
  $proposalAttempts = @(Get-ProposalAttemptRecords -LogPath $startupStdoutPath)
  $temporalSummary = Get-TemporalWarningSummary -LogPath $startupStdoutPath
  $activityResolutionSummary = Get-ActivityResolutionWarningSummary -LogPath $startupStdoutPath
  $worldDeltaSummary = if ($Ticks -gt 0 -and $finalWorldCapturePath) {
    Get-WorldStateDeltaSummary -ColdWorldPath $coldWorldCapturePath -FinalWorldPath $finalWorldCapturePath
  } else {
    [pscustomobject]@{
      status = 'not_applicable'
      count = 0
      examples = @()
    }
  }
  $semanticClassification = Get-SemanticClassification -ProposalAttempts $proposalAttempts -TemporalSummary $temporalSummary -ActivitySummary $activityResolutionSummary -WorldDeltaSummary $worldDeltaSummary
  $noProposalAttemptCount = @($proposalAttempts | Where-Object { $_.outcome -eq 'no_proposal_attempt' }).Count
  $malformedAttemptCount = @($proposalAttempts | Where-Object { $_.outcome -eq 'malformed_response' }).Count
  $parserRejectedAttemptCount = @($proposalAttempts | Where-Object { $_.outcome -eq 'parser_rejected' }).Count
  $droppedAttemptCount = @($proposalAttempts | Where-Object { $_.outcome -eq 'dropped_proposal' }).Count
  $queuedAttemptCount = @($proposalAttempts | Where-Object { $_.outcome -eq 'queued_proposal' }).Count
  $proposalReviewNotes = @()
  $approvalUniqueMatchFoundText = ([string]$approvalUniqueMatchFound).ToLowerInvariant()
  $approvalSucceededText = ([string]$approvalSucceeded).ToLowerInvariant()
  $applicationEvidenceObservedText = ([string]$applicationEvidenceObserved).ToLowerInvariant()

  if ([string]::IsNullOrWhiteSpace($finalWorldSnapshotSource)) {
    $finalWorldSnapshotSource = 'not_applicable'
  }
  if ([string]::IsNullOrWhiteSpace($worldStateDeltaSource)) {
    $worldStateDeltaSource = $finalWorldSnapshotSource
  }

  foreach ($attempt in $proposalAttempts) {
    $proposalReviewNotes += ('- proposal-attempt: [{0}] outcome=[{1}] detail=[{2}] answer=[{3}] type=[{4}] parentLocation=[{5}] name=[{6}] proposedState=[{7}] reason=[{8}]' -f `
      $attempt.agent,
      $attempt.outcome,
      $attempt.detail,
      $attempt.answer,
      $attempt.type,
      $attempt.parentLocation,
      $attempt.name,
      $attempt.proposedState,
      $attempt.reason)
  }

  $manifestValues['approval_status'] = $approvalStatus
  $manifestValues['approval_unique_match_found'] = $approvalUniqueMatchFoundText
  $manifestValues['approval_succeeded'] = $approvalSucceededText
  $manifestValues['application_evidence_observed'] = $applicationEvidenceObservedText
  $manifestValues['approval_target'] = $approvalTargetNote
  $manifestValues['final_world_snapshot_source'] = $finalWorldSnapshotSource
  Write-Manifest -Path $manifestPath -Values $manifestValues

  $currentStep = 'write proposal review'
  & (Join-Path $resolvedRepoRoot 'scripts\Write-ProposalReview.ps1') `
    -RunId $RunId `
    -TickObserved $Ticks `
    -OutputPath $proposalReviewPath `
    -Proposals (Get-ObjectPropertyValue -Object $finalProposalResponse -Name 'proposals') `
    -ReviewNotes $proposalReviewNotes `
    -ApprovedCount $approvedCount `
    -ApprovalStatus $approvalStatus `
    -AppliedCount $appliedCount `
    -RejectedCount 0 `
    -InvalidTargetCount 0 `
    -ProposalAttemptCount $proposalAttempts.Count `
    -NoProposalAttemptCount $noProposalAttemptCount `
    -MalformedAttemptCount $malformedAttemptCount `
    -ParserRejectedAttemptCount $parserRejectedAttemptCount `
    -DroppedAttemptCount $droppedAttemptCount `
    -QueuedAttemptCount $queuedAttemptCount `
    -TemporalWarningCount $temporalSummary.count `
    -TemporalWarningExamples $temporalSummary.examples `
    -ActivityResolutionWarningCount $activityResolutionSummary.count `
    -ActivityResolutionExamples $activityResolutionSummary.examples `
    -WorldStateDeltaStatus $worldDeltaSummary.status `
    -WorldStateDeltaCount $worldDeltaSummary.count `
    -WorldStateDeltaExamples $worldDeltaSummary.examples `
    -StructuralStatus $semanticClassification.structural_status `
    -SemanticStatus $semanticClassification.semantic_status `
    -RuleSliceStatus $semanticClassification.rule_slice_status `
    -ApplicationEvidenceObserved $applicationEvidenceObservedText `
    -FinalWorldSnapshotSource $finalWorldSnapshotSource `
    -WorldStateDeltaSource $worldStateDeltaSource `
    -ApprovalTargetNote $approvalTargetNote `
    -ApprovalMatchNote $approvalMatchNote `
    -ApprovalBlockedNote $approvalBlockedNote `
    -ApprovalResponseNote $approvalResponseNote `
    -AppliedConsequenceNotes $appliedConsequenceNotes

  $currentStep = 'write operator notes'
  $startupResult = if ($startupHealth.elapsed_seconds -le $startupSoftTimeoutSec) {
    "pass; server healthy on port $Port within approved timeout"
  } else {
    "pass_with_warning; server healthy on port $Port after soft timeout"
  }
  $operatorNotes = [ordered]@{
    run_id = $RunId
    scenario_name = 'two-house-garden-v1'
    frozen_model = $frozenModel
    startup_result = $startupResult
    structural_status = $semanticClassification.structural_status
    semantic_status = $semanticClassification.semantic_status
    rule_slice_status = $semanticClassification.rule_slice_status
    control_stability = 'not_applicable; protocol harness only'
    seed_integrity = 'pass; simulation seed loaded and cold endpoints captured'
    plan_integrity = if ($approvalStatus -eq 'matched_and_applied') { "reviewed_with_truth_surface; inspect proposal_review.md, $finalWorldSnapshotSource, $(Split-Path -Path $preApprovalProposalCapturePath -Leaf), $(Split-Path -Path $postApprovalProposalCapturePath -Leaf), and log_excerpt_tick_{0:D3}.txt" -f $Ticks } elseif ($Ticks -gt 0) { "reviewed_with_truth_surface; inspect proposal_review.md, endpoint_world_tick_{0:D3}.json, and log_excerpt_tick_{0:D3}.txt" -f $Ticks } else { 'reviewed_with_truth_surface; no ticks requested' }
    action_log_quality = if ($approvalRequested) { "reviewed_with_truth_surface; inspect proposal_review.md, $finalWorldSnapshotSource, and server_stdout.log for proposal-approved/proposal-applied evidence" } else { 'reviewed_with_truth_surface; inspect proposal_review.md and server_stdout.log for semantic diagnostics' }
    proposal_attempt_count = $proposalAttempts.Count
    approval_requested = $approvalRequestedText
    approval_status = $approvalStatus
    approval_unique_match_found = $approvalUniqueMatchFoundText
    approval_succeeded = $approvalSucceededText
    application_evidence_observed = $applicationEvidenceObservedText
    approval_target = $approvalTargetNote
    final_world_snapshot_source = $finalWorldSnapshotSource
    temporal_warning_count = $temporalSummary.count
    activity_resolution_warning_count = $activityResolutionSummary.count
    world_state_delta_status = $worldDeltaSummary.status
    world_state_delta_count = $worldDeltaSummary.count
    restart_integrity = 'pass; restart cold captures completed'
    shutdown_port_closed = $restartShutdownState.shutdown_port_closed
    shutdown_process_gone = $restartShutdownState.shutdown_process_gone
    next_decision = 'inspect proposal_review.md before judging rule-slice readiness'
  }
  Write-OperatorNotes -Path $operatorNotesPath -Values $operatorNotes
} catch {
  $message = $_.Exception.Message

  $activeProcess = if ($restartProcess) { $restartProcess } elseif ($startupProcess) { $startupProcess } else { $null }
  $activeScenarioLog = if ($restartScenarioLog) { $restartScenarioLog } elseif ($startupScenarioLog) { $startupScenarioLog } else { $null }

  try {
    if ($restartProcess) {
      $restartShutdownState = Stop-ServerProcess -Process $restartProcess -Port $Port
      $restartProcess = $null
    } elseif ($startupProcess) {
      $startupShutdownState = Stop-ServerProcess -Process $startupProcess -Port $Port
      $startupProcess = $null
    }
  } catch {
    # Preserve the original failure.
  }

  if (Test-Path -LiteralPath $outputRoot) {
    Write-FailureSummary -Path $failureSummaryPath -Step $currentStep -Message $message -ActiveProcess $activeProcess -ScenarioLogPath $activeScenarioLog

    $failureNotes = [ordered]@{
      run_id = $RunId
      scenario_name = 'two-house-garden-v1'
      frozen_model = if ($configuredModel) { $configuredModel } else { '' }
      startup_result = "failed; see failure_summary.md ($message)"
      structural_status = 'hard_reviewed_run_failure'
      semantic_status = 'not_available'
      rule_slice_status = 'blocked_for_rule_slice_justification'
      control_stability = 'not_applicable; protocol harness only'
      seed_integrity = 'blocked; run did not complete clean cold capture and tick sequence'
      plan_integrity = 'blocked; inspect failure_summary.md and runtime logs'
      action_log_quality = 'blocked; inspect failure_summary.md and runtime logs'
      approval_requested = $approvalRequestedText
      approval_status = $approvalStatus
      approval_unique_match_found = ([string]$approvalUniqueMatchFound).ToLowerInvariant()
      approval_succeeded = ([string]$approvalSucceeded).ToLowerInvariant()
      application_evidence_observed = ([string]$applicationEvidenceObserved).ToLowerInvariant()
      approval_target = $approvalTargetNote
      final_world_snapshot_source = $finalWorldSnapshotSource
      restart_integrity = 'blocked; restart sequence did not complete'
      shutdown_port_closed = if ($restartShutdownState) { $restartShutdownState.shutdown_port_closed } elseif ($startupShutdownState) { $startupShutdownState.shutdown_port_closed } else { $false }
      shutdown_process_gone = if ($restartShutdownState) { $restartShutdownState.shutdown_process_gone } elseif ($startupShutdownState) { $startupShutdownState.shutdown_process_gone } else { $false }
      next_decision = 'inspect failure_summary.md before rerun'
    }
    Write-OperatorNotes -Path $operatorNotesPath -Values $failureNotes
  }

  throw
}
