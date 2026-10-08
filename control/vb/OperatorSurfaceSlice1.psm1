Set-StrictMode -Version Latest

function New-OperatorWarning {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Code,

        [Parameter(Mandatory = $true)]
        [string]$Message,

        [Parameter(Mandatory = $true)]
        [string]$Owner,

        [string[]]$Paths = @()
    )

    [pscustomobject][ordered]@{
        code = $Code
        severity = "warning"
        owner = $Owner
        message = $Message
        paths = @($Paths)
    }
}

function ConvertTo-NullableInt {
    param([AllowNull()][string]$Value)

    if ([string]::IsNullOrWhiteSpace($Value)) {
        return $null
    }

    $parsed = 0
    if ([int]::TryParse($Value.Trim(), [ref]$parsed)) {
        return $parsed
    }

    return $null
}

function ConvertTo-NullableBool {
    param([AllowNull()][string]$Value)

    if ([string]::IsNullOrWhiteSpace($Value)) {
        return $null
    }

    $parsed = $false
    if ([bool]::TryParse($Value.Trim(), [ref]$parsed)) {
        return $parsed
    }

    return $null
}

function Get-PropertyValue {
    param(
        [AllowNull()]$Object,
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

function ConvertFrom-JsonCompat {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Text
    )

    $command = Get-Command ConvertFrom-Json
    if ($command.Parameters.ContainsKey("Depth")) {
        return ($Text | ConvertFrom-Json -Depth 64)
    }

    return ($Text | ConvertFrom-Json)
}

function Read-OrderedKeyValueDocument {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Path,

        [Parameter(Mandatory = $true)]
        [string[]]$OrderedKeys
    )

    if (-not (Test-Path -LiteralPath $Path)) {
        return $null
    }

    $raw = Get-Content -LiteralPath $Path -Raw
    $data = [ordered]@{
        __path = (Resolve-Path -LiteralPath $Path).Path
        __raw = $raw
    }

    foreach ($key in $OrderedKeys) {
        $pattern = '(?ms)(?:^|\s)' + [regex]::Escape($key) + ':\s*(?<value>.*?)(?=(?:\s+[a-z_][a-z0-9_]*:)|$)'
        $match = [regex]::Match($raw, $pattern)
        if ($match.Success) {
            $data[$key] = $match.Groups["value"].Value.Trim()
        }
    }

    return [pscustomobject]$data
}

function Read-ProposalReviewDocument {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Path
    )

    if (-not (Test-Path -LiteralPath $Path)) {
        return $null
    }

    $lines = Get-Content -LiteralPath $Path
    $data = [ordered]@{
        __path = (Resolve-Path -LiteralPath $Path).Path
        review_notes = @()
    }
    $currentList = $null

    foreach ($line in $lines) {
        if ($line -match '^([A-Za-z0-9_]+):\s*(.*)$') {
            $key = $Matches[1]
            $value = $Matches[2]
            if ($value -eq "") {
                if (-not $data.Contains($key)) {
                    $data[$key] = @()
                }
                $currentList = $key
            }
            else {
                $data[$key] = $value.Trim()
                $currentList = $null
            }
            continue
        }

        if ($currentList -and $line -match '^\s*-\s*(.*)$') {
            $data[$currentList] = @($data[$currentList]) + $Matches[1]
        }
    }

    return [pscustomobject]$data
}

function Read-EndpointCapture {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Path
    )

    if (-not (Test-Path -LiteralPath $Path)) {
        return $null
    }

    $raw = Get-Content -LiteralPath $Path -Raw
    $doc = ConvertFrom-JsonCompat -Text $raw
    $body = Get-PropertyValue -Object $doc -Name "body"
    if ($body -is [string]) {
        $trimmed = $body.Trim()
        if ($trimmed.StartsWith("{") -or $trimmed.StartsWith("[")) {
            try {
                $body = ConvertFrom-JsonCompat -Text $trimmed
            }
            catch {
                $body = Get-PropertyValue -Object $doc -Name "body"
            }
        }
    }

    return [pscustomobject][ordered]@{
        __path = (Resolve-Path -LiteralPath $Path).Path
        run_id = Get-PropertyValue -Object $doc -Name "run_id"
        captured_at_utc = Get-PropertyValue -Object $doc -Name "captured_at_utc"
        tick_index = Get-PropertyValue -Object $doc -Name "tick_index"
        method = Get-PropertyValue -Object $doc -Name "method"
        path = Get-PropertyValue -Object $doc -Name "path"
        http_status = Get-PropertyValue -Object $doc -Name "http_status"
        body = $body
    }
}

function Get-BundleArtifacts {
    param(
        [Parameter(Mandatory = $true)]
        [string]$BundlePath
    )

    $resolved = (Resolve-Path -LiteralPath $BundlePath).Path
    $worldTickPaths = @(Get-ChildItem -LiteralPath $resolved -Filter "endpoint_world_tick_*.json" -File -ErrorAction SilentlyContinue | Sort-Object Name | Select-Object -ExpandProperty FullName)
    $stateTickPaths = @(Get-ChildItem -LiteralPath $resolved -Filter "endpoint_state_tick_*.json" -File -ErrorAction SilentlyContinue | Sort-Object Name | Select-Object -ExpandProperty FullName)
    $statePostTickPaths = @(Get-ChildItem -LiteralPath $resolved -Filter "endpoint_state_post_tick_*.json" -File -ErrorAction SilentlyContinue | Sort-Object Name | Select-Object -ExpandProperty FullName)

    [pscustomobject][ordered]@{
        bundle_path = $resolved
        manifest = Join-Path $resolved "manifest.yaml"
        proposal_review = Join-Path $resolved "proposal_review.md"
        operator_notes = Join-Path $resolved "operator_notes.md"
        endpoint_world_cold = Join-Path $resolved "endpoint_world_cold.json"
        endpoint_world_proposals_cold = Join-Path $resolved "endpoint_world_proposals_cold.json"
        endpoint_world_restart = Join-Path $resolved "endpoint_world_restart_cold.json"
        endpoint_state_restart = Join-Path $resolved "endpoint_state_restart_cold.json"
        log_startup = Join-Path $resolved "log_excerpt_startup.txt"
        log_restart = Join-Path $resolved "log_excerpt_restart.txt"
        world_tick_paths = $worldTickPaths
        state_tick_paths = $stateTickPaths
        state_post_tick_paths = $statePostTickPaths
    }
}

function Get-RunIdsFromBundleData {
    param(
        [Parameter(Mandatory = $true)]
        [pscustomobject]$BundleData
    )

    $sources = @()
    $manifestRunId = Get-PropertyValue -Object $BundleData.manifest -Name "run_id"
    if (-not [string]::IsNullOrWhiteSpace([string]$manifestRunId)) {
        $sources += [pscustomobject]@{ source = "manifest.yaml"; run_id = $manifestRunId; path = $BundleData.artifacts.manifest }
    }
    $proposalRunId = Get-PropertyValue -Object $BundleData.proposal_review -Name "run_id"
    if (-not [string]::IsNullOrWhiteSpace([string]$proposalRunId)) {
        $sources += [pscustomobject]@{ source = "proposal_review.md"; run_id = $proposalRunId; path = $BundleData.artifacts.proposal_review }
    }
    $operatorRunId = Get-PropertyValue -Object $BundleData.operator_notes -Name "run_id"
    if (-not [string]::IsNullOrWhiteSpace([string]$operatorRunId)) {
        $sources += [pscustomobject]@{ source = "operator_notes.md"; run_id = $operatorRunId; path = $BundleData.artifacts.operator_notes }
    }
    foreach ($endpoint in $BundleData.endpoints) {
        if ($endpoint.run_id) {
            $sources += [pscustomobject]@{ source = [System.IO.Path]::GetFileName($endpoint.__path); run_id = $endpoint.run_id; path = $endpoint.__path }
        }
    }

    return @($sources)
}

function Get-RunBundleWarnings {
    param(
        [Parameter(Mandatory = $true)]
        [pscustomobject]$BundleData
    )

    $warnings = [System.Collections.Generic.List[object]]::new()
    $artifacts = $BundleData.artifacts

    if (-not (Test-Path -LiteralPath $artifacts.manifest)) {
        $warnings.Add((New-OperatorWarning -Code "missing_manifest" -Message "manifest.yaml is missing from the run bundle." -Owner "runtime-harness" -Paths @($artifacts.manifest)))
    }

    if (-not (Test-Path -LiteralPath $artifacts.operator_notes)) {
        $warnings.Add((New-OperatorWarning -Code "missing_operator_notes" -Message "operator_notes.md is missing from the run bundle." -Owner "runtime-harness" -Paths @($artifacts.operator_notes)))
    }

    if (-not (Test-Path -LiteralPath $artifacts.proposal_review)) {
        $warnings.Add((New-OperatorWarning -Code "missing_proposal_review" -Message "proposal_review.md is missing from the run bundle." -Owner "runtime-harness" -Paths @($artifacts.proposal_review)))
    }

    if ($BundleData.proposal_review) {
        $proposalCountRaw = [string](Get-PropertyValue -Object $BundleData.proposal_review -Name "proposal_count")
        if ([string]::IsNullOrWhiteSpace($proposalCountRaw)) {
            $warnings.Add((New-OperatorWarning -Code "blank_proposal_count" -Message "proposal_review.md contains a blank proposal_count value." -Owner "run-bundle-writer" -Paths @($artifacts.proposal_review)))
        }
        else {
            $proposalCount = ConvertTo-NullableInt -Value $proposalCountRaw
            $reviewNotes = @(Get-PropertyValue -Object $BundleData.proposal_review -Name "review_notes")
            $pendingNoteCount = @($reviewNotes | Where-Object { $_ -match "^pending:" }).Count
            if ($null -ne $proposalCount -and $pendingNoteCount -gt 0 -and $proposalCount -ne $pendingNoteCount) {
                $warnings.Add((New-OperatorWarning -Code "proposal_count_note_mismatch" -Message "proposal_count does not match the number of pending proposal note lines." -Owner "run-bundle-writer" -Paths @($artifacts.proposal_review)))
            }
        }
    }

    $restartMissing = @()
    foreach ($restartPath in @($artifacts.endpoint_world_restart, $artifacts.endpoint_state_restart, $artifacts.log_restart)) {
        if (-not (Test-Path -LiteralPath $restartPath)) {
            $restartMissing += $restartPath
        }
    }
    if ($restartMissing.Count -gt 0) {
        $warnings.Add((New-OperatorWarning -Code "missing_restart_evidence" -Message "Restart evidence is incomplete for this run bundle." -Owner "runtime-harness" -Paths $restartMissing))
    }

    $runIds = @(Get-RunIdsFromBundleData -BundleData $BundleData)
    $uniqueRunIds = @($runIds.run_id | Where-Object { -not [string]::IsNullOrWhiteSpace($_) } | Sort-Object -Unique)
    if ($uniqueRunIds.Count -gt 1) {
        $warnings.Add((New-OperatorWarning -Code "inconsistent_run_id" -Message "Run identifiers are inconsistent across bundle files." -Owner "runtime-harness" -Paths @($runIds.path)))
    }

    return @($warnings)
}

function Get-RunBundleData {
    param(
        [Parameter(Mandatory = $true)]
        [string]$BundlePath
    )

    $artifacts = Get-BundleArtifacts -BundlePath $BundlePath
    $manifest = Read-OrderedKeyValueDocument -Path $artifacts.manifest -OrderedKeys @(
        "run_id",
        "scenario_name",
        "launch_mode",
        "smallville_port",
        "frozen_model",
        "launch_command",
        "jar_path",
        "startup_started_at_utc",
        "startup_healthy_at_utc",
        "startup_elapsed_seconds"
    )
    $proposalReview = Read-ProposalReviewDocument -Path $artifacts.proposal_review
    $operatorNotes = Read-OrderedKeyValueDocument -Path $artifacts.operator_notes -OrderedKeys @(
        "run_id",
        "scenario_name",
        "frozen_model",
        "startup_result",
        "control_stability",
        "seed_integrity",
        "plan_integrity",
        "action_log_quality",
        "restart_integrity",
        "shutdown_port_closed",
        "shutdown_process_gone",
        "next_decision"
    )

    $endpoints = @()
    foreach ($path in @(
            $artifacts.endpoint_world_cold,
            $artifacts.endpoint_world_proposals_cold,
            $artifacts.endpoint_world_restart,
            $artifacts.endpoint_state_restart
        ) + $artifacts.world_tick_paths + $artifacts.state_tick_paths + $artifacts.state_post_tick_paths) {
        $endpoint = Read-EndpointCapture -Path $path
        if ($endpoint) {
            $endpoints += $endpoint
        }
    }

    $bundleData = [pscustomobject][ordered]@{
        artifacts = $artifacts
        manifest = $manifest
        proposal_review = $proposalReview
        operator_notes = $operatorNotes
        endpoints = @($endpoints)
    }
    $bundleData | Add-Member -NotePropertyName warnings -NotePropertyValue @(Get-RunBundleWarnings -BundleData $bundleData)

    return $bundleData
}

function Get-ResolvedRunId {
    param(
        [Parameter(Mandatory = $true)]
        [pscustomobject]$BundleData
    )

    foreach ($candidate in @(
            (Get-PropertyValue -Object $BundleData.manifest -Name "run_id"),
            (Get-PropertyValue -Object $BundleData.proposal_review -Name "run_id"),
            (Get-PropertyValue -Object $BundleData.operator_notes -Name "run_id")
        )) {
        if (-not [string]::IsNullOrWhiteSpace([string]$candidate)) {
            return [string]$candidate
        }
    }

    foreach ($endpoint in $BundleData.endpoints) {
        if (-not [string]::IsNullOrWhiteSpace([string]$endpoint.run_id)) {
            return [string]$endpoint.run_id
        }
    }

    return $null
}

function Get-HighestTickFromPaths {
    param([string[]]$Paths)

    $values = @()
    foreach ($path in @($Paths)) {
        $fileName = [System.IO.Path]::GetFileNameWithoutExtension($path)
        if ($fileName -match '_(\d+)$') {
            $values += [int]$Matches[1]
        }
    }

    if ($values.Count -eq 0) {
        return $null
    }

    return ($values | Measure-Object -Maximum).Maximum
}

function Get-LatestWorldTickEndpoint {
    param(
        [Parameter(Mandatory = $true)]
        [pscustomobject]$BundleData
    )

    $latest = $null
    foreach ($endpoint in $BundleData.endpoints | Where-Object { $_.path -eq "/world" -and $null -ne $_.tick_index -and [int]$_.tick_index -gt 0 }) {
        if (-not $latest -or [int]$endpoint.tick_index -gt [int]$latest.tick_index) {
            $latest = $endpoint
        }
    }

    return $latest
}

function ConvertTo-ParsedProposalEntry {
    param([AllowNull()][string]$Note)

    if ([string]::IsNullOrWhiteSpace($Note)) {
        return $null
    }

    $pattern = '^pending:\s*\[(?<agent>[^\]]+)\]\s+(?<type>\S+)\s+parent=\[(?<parent>[^\]]*)\]\s+name=\[(?<name>[^\]]*)\]\s+state=\[(?<state>[^\]]*)\]\s+reason=\[(?<reason>.*)\]$'
    if ($Note -notmatch $pattern) {
        return $null
    }

    return [pscustomobject][ordered]@{
        agent = $Matches.agent
        type = $Matches.type
        parent = $Matches.parent
        name = $Matches.name
        proposed_state = $Matches.state
        reason = $Matches.reason
        source = "proposal_review.md"
    }
}

function ConvertTo-ChecklistCategoryId {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Name
    )

    $normalized = ($Name.ToLowerInvariant() -replace "[^a-z0-9]+", "-").Trim("-")
    switch ($normalized) {
        "plan-integrity" { return "plan-integrity" }
        "location-validity" { return "location-validity" }
        "task-coherence" { return "task-coherence" }
        "water-and-garden-relevance" { return "water-garden-relevance" }
        "bounded-proposals" { return "bounded-proposals" }
        "action-log-readability" { return "action-log-readability" }
        "restart-reproducibility" { return "restart-reproducibility" }
        default { return $normalized }
    }
}

function Get-ChecklistTemplate {
    param(
        [Parameter(Mandatory = $true)]
        [string]$ChecklistPath
    )

    if (-not (Test-Path -LiteralPath $ChecklistPath)) {
        return $null
    }

    $lines = Get-Content -LiteralPath $ChecklistPath
    $categories = @()
    $current = $null
    $inReviewItems = $false

    foreach ($line in $lines) {
        if ($line -match '^##\s+Review Items\s*$') {
            $inReviewItems = $true
            continue
        }

        if ($inReviewItems -and $line -match '^##\s+' -and $line -notmatch '^##\s+Review Items\s*$') {
            break
        }

        if (-not $inReviewItems) {
            continue
        }

        if ($line -match '^###\s+(.+)$') {
            if ($current) {
                $categories += [pscustomobject]$current
            }

            $name = $Matches[1].Trim()
            $current = [ordered]@{
                id = ConvertTo-ChecklistCategoryId -Name $name
                name = $name
                prompts = @()
            }
            continue
        }

        if ($current -and $line -match '^\s*-\s*(.+)$') {
            $current.prompts += $Matches[1].Trim()
        }
    }

    if ($current) {
        $categories += [pscustomobject]$current
    }

    return [pscustomobject][ordered]@{
        path = (Resolve-Path -LiteralPath $ChecklistPath).Path
        categories = @($categories)
    }
}

function Get-EndpointByPathAndTick {
    param(
        [Parameter(Mandatory = $true)]
        [pscustomobject]$BundleData,

        [Parameter(Mandatory = $true)]
        [string]$Path,

        [AllowNull()][int]$TickIndex
    )

    $matches = @($BundleData.endpoints | Where-Object {
            $_.path -eq $Path -and (
                ($null -eq $TickIndex -and $null -eq $_.tick_index) -or
                ($null -ne $TickIndex -and $null -ne $_.tick_index -and [int]$_.tick_index -eq $TickIndex)
            )
        })

    if ($matches.Count -eq 0) {
        return $null
    }

    return $matches[0]
}

function Get-LatestStatePostTickEndpoint {
    param(
        [Parameter(Mandatory = $true)]
        [pscustomobject]$BundleData
    )

    $latest = $null
    foreach ($endpoint in $BundleData.endpoints | Where-Object { $_.path -eq "/state" -and $_.method -eq "POST" -and $null -ne $_.tick_index }) {
        if (-not $latest -or [int]$endpoint.tick_index -gt [int]$latest.tick_index) {
            $latest = $endpoint
        }
    }

    return $latest
}

function Get-BundleWarningByCode {
    param(
        [Parameter(Mandatory = $true)]
        [AllowEmptyCollection()]
        [object[]]$Warnings,

        [Parameter(Mandatory = $true)]
        [string[]]$Codes
    )

    return @($Warnings | Where-Object { $Codes -contains $_.code })
}

function Get-WarningCodes {
    param(
        [AllowEmptyCollection()]
        [object[]]$Warnings
    )

    return @(
        @($Warnings | ForEach-Object {
                $code = Get-PropertyValue -Object $_ -Name "code"
                if (-not [string]::IsNullOrWhiteSpace([string]$code)) {
                    [string]$code
                }
            }) | Sort-Object -Unique
    )
}

function Test-IsMissingValue {
    param([AllowNull()]$Value)

    if ($null -eq $Value) {
        return $true
    }

    if ($Value -is [string]) {
        return [string]::IsNullOrWhiteSpace($Value)
    }

    if ($Value -is [System.Collections.IEnumerable] -and -not ($Value -is [string])) {
        return (@($Value).Count -eq 0)
    }

    return $false
}

function Get-ComparisonStatus {
    param(
        [AllowNull()]$ValueA,
        [AllowNull()]$ValueB
    )

    $missingA = Test-IsMissingValue -Value $ValueA
    $missingB = Test-IsMissingValue -Value $ValueB

    if ($missingA -and -not $missingB) {
        return "missing-in-a"
    }

    if ($missingB -and -not $missingA) {
        return "missing-in-b"
    }

    if ($missingA -and $missingB) {
        return "same"
    }

    if ([object]::Equals($ValueA, $ValueB)) {
        return "same"
    }

    return "different"
}

function New-ComparisonRow {
    param(
        [Parameter(Mandatory = $true)]
        [string]$Field,

        [AllowNull()]$ValueA,

        [AllowNull()]$ValueB,

        [Parameter(Mandatory = $true)]
        [string]$Status,

        [string]$Notes = ""
    )

    [pscustomobject][ordered]@{
        field = $Field
        status = $Status
        value_a = $ValueA
        value_b = $ValueB
        notes = $Notes
    }
}

function Get-ChecklistCategoryMap {
    param(
        [Parameter(Mandatory = $true)]
        [pscustomobject]$ChecklistView
    )

    $map = @{}
    foreach ($category in @($ChecklistView.categories)) {
        $map[$category.id] = $category
    }

    return $map
}

function Get-ChecklistComparisonRows {
    param(
        [Parameter(Mandatory = $true)]
        [pscustomobject]$ChecklistA,

        [Parameter(Mandatory = $true)]
        [pscustomobject]$ChecklistB
    )

    $mapA = Get-ChecklistCategoryMap -ChecklistView $ChecklistA
    $mapB = Get-ChecklistCategoryMap -ChecklistView $ChecklistB
    $orderedIds = @()
    foreach ($category in @($ChecklistA.categories)) {
        if ($orderedIds -notcontains $category.id) {
            $orderedIds += $category.id
        }
    }
    foreach ($category in @($ChecklistB.categories)) {
        if ($orderedIds -notcontains $category.id) {
            $orderedIds += $category.id
        }
    }

    $rows = @()
    foreach ($id in $orderedIds) {
        $categoryA = $mapA[$id]
        $categoryB = $mapB[$id]

        if ($null -eq $categoryA) {
            $rows += [pscustomobject][ordered]@{
                id = $id
                name = $categoryB.name
                status = "missing-in-a"
                status_a = $null
                status_b = $categoryB.status
                warnings_a = @()
                warnings_b = @(Get-WarningCodes -Warnings @($categoryB.warnings))
            }
            continue
        }

        if ($null -eq $categoryB) {
            $rows += [pscustomobject][ordered]@{
                id = $id
                name = $categoryA.name
                status = "missing-in-b"
                status_a = $categoryA.status
                status_b = $null
                warnings_a = @(Get-WarningCodes -Warnings @($categoryA.warnings))
                warnings_b = @()
            }
            continue
        }

        $warningCodesA = @(Get-WarningCodes -Warnings @($categoryA.warnings))
        $warningCodesB = @(Get-WarningCodes -Warnings @($categoryB.warnings))
        $status = ""

        if ($categoryA.status -eq "manual-review-required" -and $categoryB.status -eq "manual-review-required" -and [object]::Equals(($warningCodesA -join ","), ($warningCodesB -join ","))) {
            $status = "manual-review-required"
        }
        elseif ($categoryA.status -eq $categoryB.status -and [object]::Equals(($warningCodesA -join ","), ($warningCodesB -join ","))) {
            $status = "same"
        }
        elseif ($categoryA.status -eq "warning" -or $categoryB.status -eq "warning" -or -not [object]::Equals(($warningCodesA -join ","), ($warningCodesB -join ","))) {
            $status = "warning-difference"
        }
        else {
            $status = "different"
        }

        $rows += [pscustomobject][ordered]@{
            id = $id
            name = $categoryA.name
            status = $status
            status_a = $categoryA.status
            status_b = $categoryB.status
            warnings_a = $warningCodesA
            warnings_b = $warningCodesB
        }
    }

    return @($rows)
}

function Get-RunBundleCompletenessFlags {
    param(
        [Parameter(Mandatory = $true)]
        [pscustomobject]$SummaryView
    )

    [pscustomobject][ordered]@{
        startup_evidence_present = (
            -not (Test-IsMissingValue -Value $SummaryView.artifact_paths.manifest) -and
            -not (Test-IsMissingValue -Value $SummaryView.artifact_paths.endpoint_world_cold)
        )
        tick_evidence_present = (
            ($SummaryView.tick_count_captured -is [int] -and $SummaryView.tick_count_captured -gt 0) -and
            -not (Test-IsMissingValue -Value $SummaryView.artifact_paths.latest_world_tick)
        )
        restart_evidence_present = [bool]$SummaryView.restart_captured
        operator_notes_present = -not (Test-IsMissingValue -Value $SummaryView.artifact_paths.operator_notes) -and (Test-Path -LiteralPath $SummaryView.artifact_paths.operator_notes)
        proposal_review_present = -not (Test-IsMissingValue -Value $SummaryView.artifact_paths.proposal_review) -and (Test-Path -LiteralPath $SummaryView.artifact_paths.proposal_review)
    }
}

function Get-WarningComparison {
    param(
        [Parameter(Mandatory = $true)]
        [AllowEmptyCollection()]
        [object[]]$WarningsA,

        [Parameter(Mandatory = $true)]
        [AllowEmptyCollection()]
        [object[]]$WarningsB
    )

    $codesA = @(Get-WarningCodes -Warnings @($WarningsA))
    $codesB = @(Get-WarningCodes -Warnings @($WarningsB))
    $shared = @($codesA | Where-Object { $codesB -contains $_ })
    $aOnly = @($codesA | Where-Object { $codesB -notcontains $_ })
    $bOnly = @($codesB | Where-Object { $codesA -notcontains $_ })

    [pscustomobject][ordered]@{
        status = if ($aOnly.Count -eq 0 -and $bOnly.Count -eq 0) { "same" } else { "warning-difference" }
        warning_codes_a_only = $aOnly
        warning_codes_b_only = $bOnly
        warning_codes_shared = $shared
    }
}

function Get-RunComparisonView {
    [CmdletBinding()]
    param(
        [Parameter(Mandatory = $true)]
        [string]$BundlePathA,

        [Parameter(Mandatory = $true)]
        [string]$BundlePathB,

        [string]$ChecklistPath = (Join-Path (Resolve-Path (Join-Path $PSScriptRoot "..\\..")) "docs\\evals\\two-house-garden-v1-review-checklist.md")
    )

    $summaryA = Get-RunBundleSummaryView -BundlePath $BundlePathA
    $summaryB = Get-RunBundleSummaryView -BundlePath $BundlePathB
    $proposalA = Get-ProposalReviewView -BundlePath $BundlePathA
    $proposalB = Get-ProposalReviewView -BundlePath $BundlePathB
    $checklistA = Get-ChecklistStatusView -BundlePath $BundlePathA -ChecklistPath $ChecklistPath
    $checklistB = Get-ChecklistStatusView -BundlePath $BundlePathB -ChecklistPath $ChecklistPath

    $completenessA = Get-RunBundleCompletenessFlags -SummaryView $summaryA
    $completenessB = Get-RunBundleCompletenessFlags -SummaryView $summaryB

    $identityRows = @(
        (New-ComparisonRow -Field "run_id" -ValueA $summaryA.run_id -ValueB $summaryB.run_id -Status (Get-ComparisonStatus -ValueA $summaryA.run_id -ValueB $summaryB.run_id)),
        (New-ComparisonRow -Field "scenario_name" -ValueA $summaryA.scenario_name -ValueB $summaryB.scenario_name -Status (Get-ComparisonStatus -ValueA $summaryA.scenario_name -ValueB $summaryB.scenario_name)),
        (New-ComparisonRow -Field "model" -ValueA $summaryA.frozen_model -ValueB $summaryB.frozen_model -Status (Get-ComparisonStatus -ValueA $summaryA.frozen_model -ValueB $summaryB.frozen_model)),
        (New-ComparisonRow -Field "launch_mode" -ValueA $summaryA.launch_mode -ValueB $summaryB.launch_mode -Status (Get-ComparisonStatus -ValueA $summaryA.launch_mode -ValueB $summaryB.launch_mode))
    )

    $completenessRows = @(
        (New-ComparisonRow -Field "startup_evidence_present" -ValueA $completenessA.startup_evidence_present -ValueB $completenessB.startup_evidence_present -Status (Get-ComparisonStatus -ValueA $completenessA.startup_evidence_present -ValueB $completenessB.startup_evidence_present)),
        (New-ComparisonRow -Field "tick_evidence_present" -ValueA $completenessA.tick_evidence_present -ValueB $completenessB.tick_evidence_present -Status (Get-ComparisonStatus -ValueA $completenessA.tick_evidence_present -ValueB $completenessB.tick_evidence_present)),
        (New-ComparisonRow -Field "restart_evidence_present" -ValueA $completenessA.restart_evidence_present -ValueB $completenessB.restart_evidence_present -Status (Get-ComparisonStatus -ValueA $completenessA.restart_evidence_present -ValueB $completenessB.restart_evidence_present)),
        (New-ComparisonRow -Field "operator_notes_present" -ValueA $completenessA.operator_notes_present -ValueB $completenessB.operator_notes_present -Status (Get-ComparisonStatus -ValueA $completenessA.operator_notes_present -ValueB $completenessB.operator_notes_present)),
        (New-ComparisonRow -Field "proposal_review_present" -ValueA $completenessA.proposal_review_present -ValueB $completenessB.proposal_review_present -Status (Get-ComparisonStatus -ValueA $completenessA.proposal_review_present -ValueB $completenessB.proposal_review_present))
    )

    $proposalRows = @(
        (New-ComparisonRow -Field "proposal_count" -ValueA $proposalA.proposal_count -ValueB $proposalB.proposal_count -Status (Get-ComparisonStatus -ValueA $proposalA.proposal_count -ValueB $proposalB.proposal_count)),
        (New-ComparisonRow -Field "approved_count" -ValueA $proposalA.approved_count -ValueB $proposalB.approved_count -Status (Get-ComparisonStatus -ValueA $proposalA.approved_count -ValueB $proposalB.approved_count)),
        (New-ComparisonRow -Field "rejected_count" -ValueA $proposalA.rejected_count -ValueB $proposalB.rejected_count -Status (Get-ComparisonStatus -ValueA $proposalA.rejected_count -ValueB $proposalB.rejected_count)),
        (New-ComparisonRow -Field "invalid_target_count" -ValueA $proposalA.invalid_target_count -ValueB $proposalB.invalid_target_count -Status (Get-ComparisonStatus -ValueA $proposalA.invalid_target_count -ValueB $proposalB.invalid_target_count)),
        (New-ComparisonRow -Field "parsed_entry_count" -ValueA @($proposalA.parsed_entries).Count -ValueB @($proposalB.parsed_entries).Count -Status (Get-ComparisonStatus -ValueA @($proposalA.parsed_entries).Count -ValueB @($proposalB.parsed_entries).Count)),
        (New-ComparisonRow -Field "latest_pending_proposal_count" -ValueA @($proposalA.latest_pending_proposals).Count -ValueB @($proposalB.latest_pending_proposals).Count -Status (Get-ComparisonStatus -ValueA @($proposalA.latest_pending_proposals).Count -ValueB @($proposalB.latest_pending_proposals).Count))
    )

    [pscustomobject][ordered]@{
        bundle_a = [pscustomobject][ordered]@{
            bundle_path = $summaryA.bundle_path
            run_id = $summaryA.run_id
            scenario_name = $summaryA.scenario_name
        }
        bundle_b = [pscustomobject][ordered]@{
            bundle_path = $summaryB.bundle_path
            run_id = $summaryB.run_id
            scenario_name = $summaryB.scenario_name
        }
        comparison_sections = [pscustomobject][ordered]@{
            bundle_identity = @($identityRows)
            bundle_completeness = @($completenessRows)
            proposal_review = @($proposalRows)
            checklist_status = @(Get-ChecklistComparisonRows -ChecklistA $checklistA -ChecklistB $checklistB)
            warning_comparison = Get-WarningComparison -WarningsA @($summaryA.warnings) -WarningsB @($summaryB.warnings)
        }
    }
}

function New-ChecklistCategoryStatus {
    param(
        [Parameter(Mandatory = $true)]
        [pscustomobject]$Category,

        [Parameter(Mandatory = $true)]
        [string]$Status,

        [Parameter(Mandatory = $true)]
        [string]$Rationale,

        [string[]]$EvidencePaths = @(),

        [object[]]$Warnings = @()
    )

    [pscustomobject][ordered]@{
        id = $Category.id
        name = $Category.name
        status = $Status
        rationale = $Rationale
        prompts = @($Category.prompts)
        evidence_paths = @($EvidencePaths | Where-Object { -not [string]::IsNullOrWhiteSpace($_) -and (Test-Path -LiteralPath $_) } | Select-Object -Unique)
        warnings = @($Warnings)
    }
}

function Get-ChecklistCategoryStatus {
    param(
        [Parameter(Mandatory = $true)]
        [pscustomobject]$Category,

        [Parameter(Mandatory = $true)]
        [pscustomobject]$BundleData
    )

    $worldCold = Get-EndpointByPathAndTick -BundleData $BundleData -Path "/world" -TickIndex 0
    $latestWorldTick = Get-LatestWorldTickEndpoint -BundleData $BundleData
    $latestStatePostTick = Get-LatestStatePostTickEndpoint -BundleData $BundleData
    $proposalReview = $BundleData.proposal_review
    $bundleWarnings = @($BundleData.warnings)

    switch ($Category.id) {
        "plan-integrity" {
            $evidencePaths = @(
                $BundleData.artifacts.proposal_review
                $latestWorldTick.__path
            ) | Where-Object { -not [string]::IsNullOrWhiteSpace($_) }
            $categoryWarnings = @(
                Get-BundleWarningByCode -Warnings $bundleWarnings -Codes @("missing_operator_notes", "inconsistent_run_id", "missing_proposal_review")
            )
            if (-not $latestWorldTick) {
                return New-ChecklistCategoryStatus -Category $Category -Status "missing" -Rationale "No latest world tick capture is available for plan review." -EvidencePaths $evidencePaths -Warnings $categoryWarnings
            }
            if ($categoryWarnings.Count -gt 0) {
                return New-ChecklistCategoryStatus -Category $Category -Status "warning" -Rationale "Plan evidence exists, but bundle warnings reduce review completeness." -EvidencePaths $evidencePaths -Warnings $categoryWarnings
            }

            return New-ChecklistCategoryStatus -Category $Category -Status "manual-review-required" -Rationale "Plan evidence is present and must be reviewed by a human rather than auto-scored." -EvidencePaths $evidencePaths
        }
        "location-validity" {
            $evidencePaths = @(
                $BundleData.artifacts.endpoint_world_cold
                $latestWorldTick.__path
            ) | Where-Object { -not [string]::IsNullOrWhiteSpace($_) }
            $categoryWarnings = @(Get-BundleWarningByCode -Warnings $bundleWarnings -Codes @("missing_manifest", "inconsistent_run_id"))
            if (-not $worldCold -or -not $latestWorldTick) {
                return New-ChecklistCategoryStatus -Category $Category -Status "missing" -Rationale "Cold-start and latest world evidence are both required to review location validity." -EvidencePaths $evidencePaths -Warnings $categoryWarnings
            }
            if ($categoryWarnings.Count -gt 0) {
                return New-ChecklistCategoryStatus -Category $Category -Status "warning" -Rationale "Location evidence exists, but bundle consistency warnings affect confidence." -EvidencePaths $evidencePaths -Warnings $categoryWarnings
            }

            return New-ChecklistCategoryStatus -Category $Category -Status "manual-review-required" -Rationale "Location evidence is present and requires reviewer judgment." -EvidencePaths $evidencePaths
        }
        "task-coherence" {
            $evidencePaths = @(
                $latestWorldTick.__path
                $latestStatePostTick.__path
            ) | Where-Object { -not [string]::IsNullOrWhiteSpace($_) }
            $categoryWarnings = @(Get-BundleWarningByCode -Warnings $bundleWarnings -Codes @("missing_operator_notes", "inconsistent_run_id"))
            if (-not $latestWorldTick) {
                return New-ChecklistCategoryStatus -Category $Category -Status "missing" -Rationale "A latest world tick capture is required to review task coherence." -EvidencePaths $evidencePaths -Warnings $categoryWarnings
            }
            if ($categoryWarnings.Count -gt 0) {
                return New-ChecklistCategoryStatus -Category $Category -Status "warning" -Rationale "Task evidence exists, but bundle warnings reduce review completeness." -EvidencePaths $evidencePaths -Warnings $categoryWarnings
            }

            return New-ChecklistCategoryStatus -Category $Category -Status "manual-review-required" -Rationale "Task evidence is present and should be judged by a reviewer, not scored automatically." -EvidencePaths $evidencePaths
        }
        "water-garden-relevance" {
            $evidencePaths = @(
                $BundleData.artifacts.endpoint_world_cold
                $latestWorldTick.__path
            ) | Where-Object { -not [string]::IsNullOrWhiteSpace($_) }
            $categoryWarnings = @(Get-BundleWarningByCode -Warnings $bundleWarnings -Codes @("missing_operator_notes", "inconsistent_run_id"))
            if (-not $worldCold -or -not $latestWorldTick) {
                return New-ChecklistCategoryStatus -Category $Category -Status "missing" -Rationale "Cold-start and latest world evidence are required to review water/garden relevance." -EvidencePaths $evidencePaths -Warnings $categoryWarnings
            }
            if ($categoryWarnings.Count -gt 0) {
                return New-ChecklistCategoryStatus -Category $Category -Status "warning" -Rationale "Water/garden evidence exists, but bundle warnings reduce review completeness." -EvidencePaths $evidencePaths -Warnings $categoryWarnings
            }

            return New-ChecklistCategoryStatus -Category $Category -Status "manual-review-required" -Rationale "Water/garden evidence is present and requires human review against scenario intent." -EvidencePaths $evidencePaths
        }
        "bounded-proposals" {
            $proposalCount = ConvertTo-NullableInt -Value (Get-PropertyValue -Object $proposalReview -Name "proposal_count")
            $evidencePaths = @(
                $BundleData.artifacts.proposal_review
                $latestWorldTick.__path
                $BundleData.artifacts.endpoint_world_proposals_cold
            ) | Where-Object { -not [string]::IsNullOrWhiteSpace($_) }
            $categoryWarnings = @(Get-BundleWarningByCode -Warnings $bundleWarnings -Codes @("missing_proposal_review", "blank_proposal_count", "proposal_count_note_mismatch", "inconsistent_run_id"))
            if (-not $proposalReview) {
                return New-ChecklistCategoryStatus -Category $Category -Status "missing" -Rationale "proposal_review.md is required to review proposal boundedness." -EvidencePaths $evidencePaths -Warnings $categoryWarnings
            }
            if ($categoryWarnings.Count -gt 0) {
                return New-ChecklistCategoryStatus -Category $Category -Status "warning" -Rationale "Proposal review evidence exists, but proposal-specific warnings affect trust in the record." -EvidencePaths $evidencePaths -Warnings $categoryWarnings
            }
            if ($proposalCount -eq 0) {
                return New-ChecklistCategoryStatus -Category $Category -Status "present" -Rationale "Proposal review evidence is present and recorded zero pending proposals for this bundle." -EvidencePaths $evidencePaths
            }

            return New-ChecklistCategoryStatus -Category $Category -Status "manual-review-required" -Rationale "Proposal evidence is present and requires a human boundedness review." -EvidencePaths $evidencePaths
        }
        "action-log-readability" {
            $evidencePaths = @(
                $latestWorldTick.__path
            ) | Where-Object { -not [string]::IsNullOrWhiteSpace($_) }
            $categoryWarnings = @(
                Get-BundleWarningByCode -Warnings $bundleWarnings -Codes @("missing_operator_notes", "inconsistent_run_id")
            )
            if (-not $latestWorldTick) {
                return New-ChecklistCategoryStatus -Category $Category -Status "missing" -Rationale "A latest world tick capture is required to review action-log readability." -EvidencePaths $evidencePaths -Warnings $categoryWarnings
            }
            if ($categoryWarnings.Count -gt 0) {
                return New-ChecklistCategoryStatus -Category $Category -Status "warning" -Rationale "Action-log evidence exists, but bundle warnings reduce review completeness." -EvidencePaths $evidencePaths -Warnings $categoryWarnings
            }

            return New-ChecklistCategoryStatus -Category $Category -Status "manual-review-required" -Rationale "Action-log evidence is present and must be read by a reviewer." -EvidencePaths $evidencePaths
        }
        "restart-reproducibility" {
            $evidencePaths = @($BundleData.artifacts.endpoint_world_restart, $BundleData.artifacts.endpoint_state_restart, $BundleData.artifacts.log_restart, $BundleData.artifacts.operator_notes)
            $categoryWarnings = @(
                Get-BundleWarningByCode -Warnings $bundleWarnings -Codes @("missing_restart_evidence", "missing_operator_notes", "inconsistent_run_id")
            )
            if ($categoryWarnings.Count -gt 0) {
                return New-ChecklistCategoryStatus -Category $Category -Status "warning" -Rationale "Restart review is affected by missing restart evidence or missing review notes." -EvidencePaths $evidencePaths -Warnings $categoryWarnings
            }

            return New-ChecklistCategoryStatus -Category $Category -Status "manual-review-required" -Rationale "Restart evidence is present and requires structural comparison by a reviewer." -EvidencePaths $evidencePaths
        }
        default {
            return New-ChecklistCategoryStatus -Category $Category -Status "not-applicable" -Rationale "This category is not mapped in operator-surface slice 2." -EvidencePaths @()
        }
    }
}

function Get-ChecklistStatusView {
    [CmdletBinding()]
    param(
        [Parameter(Mandatory = $true)]
        [string]$BundlePath,

        [string]$ChecklistPath = (Join-Path (Resolve-Path (Join-Path $PSScriptRoot "..\\..")) "docs\\evals\\two-house-garden-v1-review-checklist.md")
    )

    $bundleData = Get-RunBundleData -BundlePath $BundlePath
    $checklist = Get-ChecklistTemplate -ChecklistPath $ChecklistPath
    if (-not $checklist) {
        throw "Checklist source not found at [$ChecklistPath]."
    }

    $categories = @()
    foreach ($category in $checklist.categories) {
        $categories += Get-ChecklistCategoryStatus -Category $category -BundleData $bundleData
    }

    return [pscustomobject][ordered]@{
        bundle_path = $bundleData.artifacts.bundle_path
        run_id = Get-ResolvedRunId -BundleData $bundleData
        scenario_name = Get-PropertyValue -Object $bundleData.manifest -Name "scenario_name"
        checklist_source_path = $checklist.path
        categories = @($categories)
        warnings = @($bundleData.warnings)
    }
}

function Get-RunBundleSummaryView {
    [CmdletBinding()]
    param(
        [Parameter(Mandatory = $true)]
        [string]$BundlePath
    )

    $bundleData = Get-RunBundleData -BundlePath $BundlePath
    $highestWorldTick = Get-HighestTickFromPaths -Paths $bundleData.artifacts.world_tick_paths
    $highestPostTick = Get-HighestTickFromPaths -Paths $bundleData.artifacts.state_post_tick_paths
    $tickCountCaptured = @($bundleData.artifacts.world_tick_paths | ForEach-Object {
            if ([System.IO.Path]::GetFileNameWithoutExtension($_) -match '_(\d+)$') { [int]$Matches[1] }
        } | Sort-Object -Unique).Count
    $restartCaptured = (Test-Path -LiteralPath $bundleData.artifacts.endpoint_world_restart) -and
        (Test-Path -LiteralPath $bundleData.artifacts.endpoint_state_restart) -and
        (Test-Path -LiteralPath $bundleData.artifacts.log_restart)
    $proposalCountRaw = $null
    if ($bundleData.proposal_review) {
        $proposalCountRaw = [string](Get-PropertyValue -Object $bundleData.proposal_review -Name "proposal_count")
    }

    return [pscustomobject][ordered]@{
        bundle_path = $bundleData.artifacts.bundle_path
        run_id = Get-ResolvedRunId -BundleData $bundleData
        scenario_name = Get-PropertyValue -Object $bundleData.manifest -Name "scenario_name"
        launch_mode = Get-PropertyValue -Object $bundleData.manifest -Name "launch_mode"
        frozen_model = Get-PropertyValue -Object $bundleData.manifest -Name "frozen_model"
        smallville_port = ConvertTo-NullableInt -Value (Get-PropertyValue -Object $bundleData.manifest -Name "smallville_port")
        launch_command = Get-PropertyValue -Object $bundleData.manifest -Name "launch_command"
        startup_result = Get-PropertyValue -Object $bundleData.operator_notes -Name "startup_result"
        startup_healthy_at_utc = Get-PropertyValue -Object $bundleData.manifest -Name "startup_healthy_at_utc"
        tick_count_captured = $tickCountCaptured
        highest_world_tick = $highestWorldTick
        highest_state_post_tick = $highestPostTick
        restart_captured = $restartCaptured
        shutdown_port_closed = ConvertTo-NullableBool -Value (Get-PropertyValue -Object $bundleData.operator_notes -Name "shutdown_port_closed")
        shutdown_process_gone = ConvertTo-NullableBool -Value (Get-PropertyValue -Object $bundleData.operator_notes -Name "shutdown_process_gone")
        proposal_count_raw = $proposalCountRaw
        proposal_count = ConvertTo-NullableInt -Value $proposalCountRaw
        artifact_paths = [pscustomobject][ordered]@{
            manifest = $bundleData.artifacts.manifest
            proposal_review = $bundleData.artifacts.proposal_review
            operator_notes = $bundleData.artifacts.operator_notes
            endpoint_world_cold = $bundleData.artifacts.endpoint_world_cold
            latest_world_tick = if ($highestWorldTick) { $bundleData.artifacts.world_tick_paths[-1] } else { $null }
            endpoint_world_restart = $bundleData.artifacts.endpoint_world_restart
        }
        warnings = @($bundleData.warnings)
    }
}

function Get-ProposalReviewView {
    [CmdletBinding()]
    param(
        [Parameter(Mandatory = $true)]
        [string]$BundlePath
    )

    $bundleData = Get-RunBundleData -BundlePath $BundlePath
    $proposalCountRaw = if ($bundleData.proposal_review) { [string](Get-PropertyValue -Object $bundleData.proposal_review -Name "proposal_count") } else { $null }
    $latestWorldTick = Get-LatestWorldTickEndpoint -BundleData $bundleData
    $latestPendingProposals = @()
    $latestWorldTickBody = Get-PropertyValue -Object $latestWorldTick -Name "body"
    if ($latestWorldTick -and $latestWorldTickBody -and (Get-PropertyValue -Object $latestWorldTickBody -Name "pendingProposals")) {
        $latestPendingProposals = @((Get-PropertyValue -Object $latestWorldTickBody -Name "pendingProposals"))
    }
    else {
        $proposalEndpoints = @($bundleData.endpoints | Where-Object { $_.path -eq "/world/proposals" })
        if ($proposalEndpoints.Count -gt 0) {
            $proposalEndpoint = $proposalEndpoints[0]
            $proposalEndpointBody = Get-PropertyValue -Object $proposalEndpoint -Name "body"
            if ($proposalEndpointBody -and (Get-PropertyValue -Object $proposalEndpointBody -Name "proposals")) {
                $latestPendingProposals = @((Get-PropertyValue -Object $proposalEndpointBody -Name "proposals"))
            }
        }
    }

    $parsedEntries = @()
    foreach ($note in @(Get-PropertyValue -Object $bundleData.proposal_review -Name "review_notes")) {
        $entry = ConvertTo-ParsedProposalEntry -Note $note
        if ($entry) {
            $parsedEntries += $entry
        }
    }

    return [pscustomobject][ordered]@{
        bundle_path = $bundleData.artifacts.bundle_path
        run_id = Get-ResolvedRunId -BundleData $bundleData
        scenario_name = Get-PropertyValue -Object $bundleData.manifest -Name "scenario_name"
        tick_observed = ConvertTo-NullableInt -Value (Get-PropertyValue -Object $bundleData.proposal_review -Name "tick_observed")
        proposal_count_raw = $proposalCountRaw
        proposal_count = ConvertTo-NullableInt -Value $proposalCountRaw
        approved_count = ConvertTo-NullableInt -Value (Get-PropertyValue -Object $bundleData.proposal_review -Name "approved_count")
        rejected_count = ConvertTo-NullableInt -Value (Get-PropertyValue -Object $bundleData.proposal_review -Name "rejected_count")
        invalid_target_count = ConvertTo-NullableInt -Value (Get-PropertyValue -Object $bundleData.proposal_review -Name "invalid_target_count")
        review_notes = @(Get-PropertyValue -Object $bundleData.proposal_review -Name "review_notes")
        parsed_entries = @($parsedEntries)
        latest_pending_proposals = @($latestPendingProposals)
        source_paths = [pscustomobject][ordered]@{
            proposal_review = $bundleData.artifacts.proposal_review
            endpoint_world_proposals_cold = $bundleData.artifacts.endpoint_world_proposals_cold
            latest_world_tick = if ($latestWorldTick) { $latestWorldTick.__path } else { $null }
        }
        warnings = @($bundleData.warnings)
    }
}

Export-ModuleMember -Function Get-RunBundleSummaryView, Get-ProposalReviewView, Get-ChecklistStatusView, Get-RunComparisonView
