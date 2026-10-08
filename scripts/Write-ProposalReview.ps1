[CmdletBinding()]
param(
  [Parameter(Mandatory = $true)]
  [string]$RunId,

  [Parameter(Mandatory = $true)]
  [int]$TickObserved,

  [Parameter(Mandatory = $true)]
  [string]$OutputPath,

  [object]$Proposals,

  [string[]]$ReviewNotes = @(),

  [int]$ApprovedCount = 0,

  [int]$RejectedCount = 0,

  [int]$InvalidTargetCount = 0,

  [int]$ProposalAttemptCount = 0,

  [int]$NoProposalAttemptCount = 0,

  [int]$MalformedAttemptCount = 0,

  [int]$ParserRejectedAttemptCount = 0,

  [int]$DroppedAttemptCount = 0,

  [int]$QueuedAttemptCount = 0,

  [int]$TemporalWarningCount = 0,

  [string[]]$TemporalWarningExamples = @(),

  [int]$ActivityResolutionWarningCount = 0,

  [string[]]$ActivityResolutionExamples = @(),

  [string]$WorldStateDeltaStatus = '',

  [int]$WorldStateDeltaCount = 0,

  [string[]]$WorldStateDeltaExamples = @(),

  [string]$StructuralStatus = '',

  [string]$SemanticStatus = '',

  [string]$RuleSliceStatus = '',

  [string]$ApprovalStatus = '',

  [int]$AppliedCount = 0,

  [string]$ApplicationEvidenceObserved = '',

  [string]$FinalWorldSnapshotSource = '',

  [string]$WorldStateDeltaSource = '',

  [string]$ApprovalTargetNote = '',

  [string]$ApprovalMatchNote = '',

  [string]$ApprovalBlockedNote = '',

  [string]$ApprovalResponseNote = '',

  [string[]]$AppliedConsequenceNotes = @()
)

Set-StrictMode -Version Latest
$ErrorActionPreference = 'Stop'

function Get-ProposalList {
  param(
    [object]$ProposalInput
  )

  if ($null -eq $ProposalInput) {
    return @()
  }

  # Re-wrap after filtering so 1-item results stay arrays instead of collapsing to a scalar PSCustomObject.
  return @(@($ProposalInput) | Where-Object { $null -ne $_ })
}

function Get-ProposalValue {
  param(
    [object]$Proposal,
    [string]$Name
  )

  if ($null -eq $Proposal) {
    return ''
  }

  $property = $Proposal.PSObject.Properties[$Name]
  if ($null -eq $property -or $null -eq $property.Value) {
    return ''
  }

  return [string]$property.Value
}

$proposalList = @(Get-ProposalList -ProposalInput $Proposals)
$proposalCount = $proposalList.Count

$lines = @(
  "run_id: $RunId"
  "tick_observed: $TickObserved"
  "proposal_count: $proposalCount"
  "approval_status: $ApprovalStatus"
  "approved_count: $ApprovedCount"
  "applied_count: $AppliedCount"
  "rejected_count: $RejectedCount"
  "invalid_target_count: $InvalidTargetCount"
  "proposal_attempt_count: $ProposalAttemptCount"
  "no_proposal_attempt_count: $NoProposalAttemptCount"
  "malformed_attempt_count: $MalformedAttemptCount"
  "parser_rejected_attempt_count: $ParserRejectedAttemptCount"
  "dropped_attempt_count: $DroppedAttemptCount"
  "queued_attempt_count: $QueuedAttemptCount"
  "temporal_warning_count: $TemporalWarningCount"
  "activity_resolution_warning_count: $ActivityResolutionWarningCount"
  "world_state_delta_status: $WorldStateDeltaStatus"
  "world_state_delta_count: $WorldStateDeltaCount"
  "application_evidence_observed: $ApplicationEvidenceObserved"
  "final_world_snapshot_source: $FinalWorldSnapshotSource"
  "structural_status: $StructuralStatus"
  "semantic_status: $SemanticStatus"
  "rule_slice_status: $RuleSliceStatus"
  'review_notes:'
)

if ($proposalCount -gt 0) {
  foreach ($proposal in $proposalList) {
    $lines += (
      '- pending: [{0}] {1} parent=[{2}] name=[{3}] state=[{4}] reason=[{5}]' -f `
        (Get-ProposalValue -Proposal $proposal -Name 'agent'), `
        (Get-ProposalValue -Proposal $proposal -Name 'type'), `
        (Get-ProposalValue -Proposal $proposal -Name 'parentLocation'), `
        (Get-ProposalValue -Proposal $proposal -Name 'name'), `
        (Get-ProposalValue -Proposal $proposal -Name 'proposedState'), `
        (Get-ProposalValue -Proposal $proposal -Name 'reason')
    )
  }
} elseif ($ProposalAttemptCount -eq 0) {
  $lines += '- no proposal attempts were surfaced during review'
}

if (@($ReviewNotes).Count -gt 0) {
  $lines += $ReviewNotes
}

if (-not [string]::IsNullOrWhiteSpace($ApprovalTargetNote)) {
  $lines += ('- approval-target: ' + $ApprovalTargetNote)
}

if (-not [string]::IsNullOrWhiteSpace($ApprovalMatchNote)) {
  $lines += ('- approval-match: ' + $ApprovalMatchNote)
}

if (-not [string]::IsNullOrWhiteSpace($ApprovalBlockedNote)) {
  $lines += ('- approval-blocked: ' + $ApprovalBlockedNote)
}

if (-not [string]::IsNullOrWhiteSpace($ApprovalResponseNote)) {
  $lines += ('- approval-response: ' + $ApprovalResponseNote)
}

foreach ($appliedConsequenceNote in @($AppliedConsequenceNotes)) {
  if (-not [string]::IsNullOrWhiteSpace($appliedConsequenceNote)) {
    $lines += ('- applied-consequence: ' + $appliedConsequenceNote)
  }
}

if (-not [string]::IsNullOrWhiteSpace($WorldStateDeltaSource)) {
  $lines += ('- world-state-delta-source: ' + $WorldStateDeltaSource)
}

if ($TemporalWarningCount -eq 0) {
  $lines += '- temporal-warning: none observed'
} else {
  foreach ($example in @($TemporalWarningExamples)) {
    $lines += ('- temporal-warning: ' + $example)
  }
}

if ($ActivityResolutionWarningCount -eq 0) {
  $lines += '- activity-resolution-warning: none observed'
} else {
  foreach ($example in @($ActivityResolutionExamples)) {
    $lines += ('- activity-resolution-warning: ' + $example)
  }
}

if ($WorldStateDeltaCount -eq 0) {
  $lines += '- world-state-delta: none observed'
} else {
  foreach ($example in @($WorldStateDeltaExamples)) {
    $lines += ('- world-state-delta: ' + $example)
  }
}

$directory = Split-Path -Path $OutputPath -Parent
if ($directory -and -not (Test-Path $directory)) {
  New-Item -ItemType Directory -Path $directory -Force | Out-Null
}

$lines | Set-Content -Path $OutputPath
