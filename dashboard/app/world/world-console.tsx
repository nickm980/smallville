'use client';

import { Badge, Button, Card, Text } from '@tremor/react';
import { useRouter } from 'next/navigation';
import { useEffect, useState, useTransition } from 'react';
import {
  SmallvilleImportAgentsResponse,
  SmallvilleModelsResponse,
  SmallvilleWorldAgent,
  SmallvilleWorldLocation,
  SmallvilleWorldProposal,
  SmallvilleWorldSnapshot,
  approveWorldProposal,
  importAgents,
  previewImportAgents,
  rejectWorldProposal,
  setAgentModel,
  setDefaultModel
} from '../../lib/smallville';

interface WorldConsoleProps {
  initialSnapshot: SmallvilleWorldSnapshot;
  initialModels: SmallvilleModelsResponse;
}

function renderItems(items: string[] | undefined, emptyLabel: string) {
  const safeItems = items ?? [];

  if (safeItems.length === 0) {
    return <p className="text-sm text-slate-500">{emptyLabel}</p>;
  }

  return (
    <ul className="space-y-2 text-sm text-slate-700">
      {safeItems.map((item) => (
        <li key={item} className="rounded-xl bg-slate-50 px-3 py-2">
          {item}
        </li>
      ))}
    </ul>
  );
}

function groupLocations(locations: SmallvilleWorldLocation[]) {
  return locations.reduce<Record<string, SmallvilleWorldLocation[]>>((groups, location) => {
    const root = location.name.split(':')[0].trim();
    groups[root] = groups[root] ?? [];
    groups[root].push(location);
    return groups;
  }, {});
}

function agentByName(agents: SmallvilleWorldAgent[], name: string) {
  return agents.find((agent) => agent.name === name);
}

function AgentChip(props: { agent: SmallvilleWorldAgent }) {
  return (
    <div className="rounded-2xl border border-emerald-200 bg-emerald-50 px-3 py-2">
      <div className="flex items-center gap-2">
        <span className="text-sm font-semibold text-emerald-950">
          {props.agent.emoji ? `${props.agent.emoji} ` : ''}
          {props.agent.name}
        </span>
        <Badge color="emerald">{props.agent.model ?? 'auto'}</Badge>
      </div>
      <p className="mt-1 text-xs text-emerald-900">{props.agent.action}</p>
    </div>
  );
}

function LocationZone(props: {
  root: string;
  locations: SmallvilleWorldLocation[];
  agents: SmallvilleWorldAgent[];
}) {
  const rootLocation = props.locations.find((location) => location.name === props.root);
  const children = props.locations
    .filter((location) => location.name !== props.root)
    .sort((first, second) => first.depth - second.depth || first.name.localeCompare(second.name));

  return (
    <Card className="h-full">
      <div className="space-y-4">
        <div className="flex items-start justify-between gap-4">
          <div>
            <p className="text-xl font-semibold text-slate-900">{props.root}</p>
            <p className="mt-1 text-sm text-slate-600">
              {rootLocation?.state ?? 'No root state defined yet.'}
            </p>
          </div>
          <Badge color="gray">{props.locations.length} node(s)</Badge>
        </div>

        <div className="grid gap-3">
          {(children.length > 0 ? children : props.locations).map((location) => (
            <div key={location.name} className="rounded-2xl border border-slate-200 bg-white p-4 shadow-sm">
              <div className="flex items-start justify-between gap-3">
                <div>
                  <p className="text-sm font-semibold text-slate-900">{location.name}</p>
                  <p className="mt-1 text-sm text-slate-600">
                    {location.state ?? 'No current state'}
                  </p>
                </div>
                <Badge color="gray">Depth {location.depth}</Badge>
              </div>
              <div className="mt-3 grid gap-2 sm:grid-cols-2">
                {location.agents.length > 0 ? (
                  location.agents.map((name) => {
                    const agent = agentByName(props.agents, name);
                    return agent ? <AgentChip key={agent.name} agent={agent} /> : null;
                  })
                ) : (
                  <p className="text-sm text-slate-500">No agents here right now.</p>
                )}
              </div>
            </div>
          ))}
        </div>
      </div>
    </Card>
  );
}

export default function WorldConsole(props: WorldConsoleProps) {
  const router = useRouter();
  const [isRefreshing, startRefresh] = useTransition();
  const [message, setMessage] = useState('');
  const [busy, setBusy] = useState<string | null>(null);
  const [defaultModelValue, setDefaultModelValue] = useState(props.initialModels.defaultModel);
  const [agentOverrides, setAgentOverrides] = useState<Record<string, string>>(
    props.initialModels.agentModelOverrides
  );
  const [yaml, setYaml] = useState('');
  const [preview, setPreview] = useState<SmallvilleImportAgentsResponse | null>(null);

  useEffect(() => {
    setDefaultModelValue(props.initialModels.defaultModel);
    setAgentOverrides(props.initialModels.agentModelOverrides);
  }, [props.initialModels]);

  function refreshWorld() {
    startRefresh(() => {
      router.refresh();
    });
  }

  async function runAction(key: string, action: () => Promise<boolean>, successMessage: string) {
    setBusy(key);
    setMessage('');
    const ok = await action();
    if (ok) {
      setMessage(successMessage);
      refreshWorld();
    } else {
      setMessage('The dashboard could not complete that action.');
    }
    setBusy(null);
  }

  const locationGroups = groupLocations(props.initialSnapshot.locations);
  const roots = Object.keys(locationGroups).sort((first, second) => first.localeCompare(second));

  return (
    <div className="mt-6 space-y-6">
      <div className="grid gap-6 xl:grid-cols-[1.15fr_0.85fr]">
        <Card>
          <div className="space-y-4">
            <div className="flex flex-wrap items-start justify-between gap-3">
              <div>
                <p className="text-lg font-semibold text-slate-900">Simulation Snapshot</p>
                <p className="mt-1 text-sm text-slate-600">
                  Tick {props.initialSnapshot.tick} at {props.initialSnapshot.time}
                </p>
              </div>
              <div className="flex flex-wrap gap-2">
                <Badge color="gray">{props.initialSnapshot.step} min step</Badge>
                <Badge color="emerald">{props.initialSnapshot.agents.length} agents</Badge>
                <Badge color="blue">{props.initialSnapshot.locations.length} locations</Badge>
              </div>
            </div>
            <p className="text-sm text-slate-700">
              {props.initialSnapshot.worldBuilding.summary ||
                'No world-building frame has been defined yet.'}
            </p>
            <div className="grid gap-4 md:grid-cols-2">
              <div>
                <p className="text-sm font-semibold text-slate-900">World Rules</p>
                <div className="mt-2">
                  {renderItems(
                    props.initialSnapshot.worldBuilding.rules,
                    'No explicit build rules yet.'
                  )}
                </div>
              </div>
              <div>
                <p className="text-sm font-semibold text-slate-900">Daily Rhythm</p>
                <div className="mt-2 rounded-2xl bg-slate-50 p-4 text-sm text-slate-700">
                  <p>Breakfast: {props.initialSnapshot.dailyRhythm.breakfast}</p>
                  <p>Lunch: {props.initialSnapshot.dailyRhythm.lunch}</p>
                  <p>Dinner: {props.initialSnapshot.dailyRhythm.dinner}</p>
                  <p>Morning tea: {props.initialSnapshot.dailyRhythm.morningTea}</p>
                  <p>Afternoon tea: {props.initialSnapshot.dailyRhythm.afternoonTea}</p>
                  <p>Snack: {props.initialSnapshot.dailyRhythm.snack}</p>
                </div>
              </div>
            </div>
          </div>
        </Card>

        <Card>
          <div className="space-y-4">
            <div className="flex items-start justify-between gap-3">
              <div>
                <p className="text-lg font-semibold text-slate-900">Model Access</p>
                <p className="mt-1 text-sm text-slate-600">
                  Provider mode: {props.initialModels.providerMode}
                </p>
              </div>
              <Badge color="emerald">
                {props.initialModels.availableModels.length} local model(s)
              </Badge>
            </div>

            <div className="rounded-2xl bg-slate-50 p-4">
              <label className="block text-sm font-semibold text-slate-900">Default Model</label>
              <select
                className="mt-2 w-full rounded-xl border border-slate-300 bg-white px-3 py-2 text-sm"
                value={defaultModelValue}
                onChange={(event) => setDefaultModelValue(event.target.value)}
              >
                {props.initialModels.availableModels.map((model) => (
                  <option key={model} value={model}>
                    {model}
                  </option>
                ))}
              </select>
              <Button
                className="mt-3"
                loading={busy === 'default-model' || isRefreshing}
                onClick={() =>
                  runAction(
                    'default-model',
                    () => setDefaultModel(defaultModelValue, props.initialModels.providerMode),
                    `Default model updated to ${defaultModelValue}.`
                  )
                }
              >
                Save Default Model
              </Button>
            </div>

            <div className="space-y-3">
              <p className="text-sm font-semibold text-slate-900">Per-Agent Overrides</p>
              {props.initialSnapshot.agents.map((agent) => (
                <div key={agent.name} className="rounded-2xl border border-slate-200 bg-white p-4">
                  <div className="flex flex-wrap items-start justify-between gap-3">
                    <div>
                      <p className="text-sm font-semibold text-slate-900">{agent.name}</p>
                      <p className="mt-1 text-sm text-slate-600">{agent.action}</p>
                    </div>
                    <Badge color="gray">{agent.model ?? 'auto'}</Badge>
                  </div>
                  <div className="mt-3 flex flex-col gap-3 md:flex-row">
                    <select
                      className="w-full rounded-xl border border-slate-300 bg-white px-3 py-2 text-sm"
                      value={agentOverrides[agent.name] ?? ''}
                      onChange={(event) =>
                        setAgentOverrides((current) => ({
                          ...current,
                          [agent.name]: event.target.value
                        }))
                      }
                    >
                      <option value="">Use default / persona preference</option>
                      {props.initialModels.availableModels.map((model) => (
                        <option key={`${agent.name}-${model}`} value={model}>
                          {model}
                        </option>
                      ))}
                    </select>
                    <Button
                      loading={busy === `agent-model-${agent.name}` || isRefreshing}
                      onClick={() =>
                        runAction(
                          `agent-model-${agent.name}`,
                          () => setAgentModel(agent.name, agentOverrides[agent.name] || null),
                          `Updated runtime model for ${agent.name}.`
                        )
                      }
                    >
                      Save
                    </Button>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </Card>
      </div>

      <div className="grid gap-6 xl:grid-cols-[1.2fr_0.8fr]">
        <div className="space-y-6">
          <div>
            <p className="text-lg font-semibold text-slate-900">World Canvas</p>
            <p className="mt-1 text-sm text-slate-600">
              Top-level zones group the live world so you can see where agents are standing and
              what each leaf location is currently doing.
            </p>
          </div>
          <div className="grid gap-6 lg:grid-cols-2">
            {roots.length > 0 ? (
              roots.map((root) => (
                <LocationZone
                  key={root}
                  root={root}
                  locations={locationGroups[root]}
                  agents={props.initialSnapshot.agents}
                />
              ))
            ) : (
              <Card>
                <p className="text-sm text-slate-500">No locations are loaded yet.</p>
              </Card>
            )}
          </div>
        </div>

        <div className="space-y-6">
          <Card>
            <div className="space-y-4">
              <div className="flex items-center justify-between gap-3">
                <p className="text-lg font-semibold text-slate-900">Proposal Queue</p>
                <Badge color="amber">{props.initialSnapshot.pendingProposals.length} pending</Badge>
              </div>
              {props.initialSnapshot.pendingProposals.length > 0 ? (
                props.initialSnapshot.pendingProposals.map((proposal) => (
                  <ProposalCard
                    key={proposal.id}
                    proposal={proposal}
                    busy={busy}
                    isRefreshing={isRefreshing}
                    onApprove={() =>
                      runAction(
                        `approve-${proposal.id}`,
                        () => approveWorldProposal(proposal.id),
                        `Approved ${proposal.type} from ${proposal.agent}.`
                      )
                    }
                    onReject={() =>
                      runAction(
                        `reject-${proposal.id}`,
                        () => rejectWorldProposal(proposal.id),
                        `Rejected proposal from ${proposal.agent}.`
                      )
                    }
                  />
                ))
              ) : (
                <p className="text-sm text-slate-500">No world-building proposals are pending.</p>
              )}
            </div>
          </Card>

          <Card>
            <div className="space-y-4">
              <p className="text-lg font-semibold text-slate-900">Tick Timeline</p>
              {props.initialSnapshot.actionLog.length > 0 ? (
                props.initialSnapshot.actionLog.map((entry, index) => (
                  <div key={`${entry.tick}-${entry.actor}-${index}`} className="rounded-2xl bg-slate-50 p-4">
                    <div className="flex flex-wrap items-center gap-2">
                      <Badge color="blue">Tick {entry.tick}</Badge>
                      <Badge color="gray">{entry.type}</Badge>
                      <span className="text-sm font-semibold text-slate-900">{entry.actor}</span>
                      <span className="text-sm text-slate-500">{entry.time}</span>
                    </div>
                    <p className="mt-2 text-sm text-slate-700">{entry.summary}</p>
                  </div>
                ))
              ) : (
                <p className="text-sm text-slate-500">Advance the sim to populate the timeline.</p>
              )}
            </div>
          </Card>
        </div>
      </div>

      <div className="grid gap-6 xl:grid-cols-[0.95fr_1.05fr]">
        <Card>
          <div className="space-y-4">
            <div>
              <p className="text-lg font-semibold text-slate-900">Persona Importer</p>
              <p className="mt-1 text-sm text-slate-600">
                Paste a scenario fragment with an <code>agents:</code> block to preview and import
                new personas.
              </p>
            </div>
            <textarea
              className="min-h-[260px] w-full rounded-2xl border border-slate-300 bg-white p-4 font-mono text-sm text-slate-800"
              placeholder={`agents:\n  - name: Rowan\n    location: "Green House: Potting Bench"\n    activity: checking seed trays\n    persona:\n      - Rowan keeps a notebook of quiet plant experiments.`}
              value={yaml}
              onChange={(event) => setYaml(event.target.value)}
            />
            <div className="flex flex-wrap gap-3">
              <Button
                loading={busy === 'preview-import' || isRefreshing}
                onClick={async () => {
                  setBusy('preview-import');
                  setMessage('');
                  const response = await previewImportAgents(yaml);
                  setPreview(response);
                  setMessage(response.success ? 'Preview passed validation.' : 'Preview found issues.');
                  setBusy(null);
                }}
              >
                Preview YAML
              </Button>
              <Button
                color="emerald"
                loading={busy === 'import-agents' || isRefreshing}
                onClick={async () => {
                  setBusy('import-agents');
                  setMessage('');
                  const response = await importAgents(yaml);
                  setPreview(response);
                  if (response.success) {
                    setMessage(`Imported ${response.createdAgents.length} persona(s).`);
                    refreshWorld();
                  } else {
                    setMessage('Import failed validation.');
                  }
                  setBusy(null);
                }}
              >
                Import Personas
              </Button>
            </div>

            {preview && (
              <div className="space-y-4 rounded-2xl bg-slate-50 p-4">
                <div>
                  <p className="text-sm font-semibold text-slate-900">Validation</p>
                  <div className="mt-2">
                    {renderItems(preview.errors, 'No validation errors.')}
                  </div>
                </div>
                <div>
                  <p className="text-sm font-semibold text-slate-900">Persona Preview</p>
                  <div className="mt-2 space-y-3">
                    {preview.agents.map((agent) => (
                      <div key={`${agent.name}-${agent.location}`} className="rounded-xl bg-white p-3">
                        <p className="text-sm font-semibold text-slate-900">{agent.name}</p>
                        <p className="mt-1 text-sm text-slate-600">
                          {agent.activity} at {agent.location}
                        </p>
                        <p className="mt-1 text-sm text-slate-500">Model: {agent.model}</p>
                        {agent.issues.length > 0 && (
                          <div className="mt-2">{renderItems(agent.issues, 'No issues.')}</div>
                        )}
                      </div>
                    ))}
                  </div>
                </div>
              </div>
            )}
          </div>
        </Card>

        <Card>
          <div className="space-y-4">
            <div>
              <p className="text-lg font-semibold text-slate-900">Agent Personas</p>
              <p className="mt-1 text-sm text-slate-600">
                Persona memories, working context, and rituals stay visible so you can tune
                autonomy without collapsing everyone into one shared voice.
              </p>
            </div>
            <div className="space-y-4">
              {props.initialSnapshot.agents.map((agent) => (
                <div key={agent.name} className="rounded-2xl border border-slate-200 bg-white p-4">
                  <div className="flex flex-wrap items-start justify-between gap-3">
                    <div>
                      <p className="text-lg font-semibold text-slate-900">{agent.name}</p>
                      <p className="mt-1 text-sm text-slate-600">
                        {agent.action} at {agent.location ?? 'Unknown location'}
                      </p>
                    </div>
                    <div className="flex flex-wrap gap-2">
                      <Badge color="emerald">{agent.model ?? 'auto'}</Badge>
                      <Badge color="gray">{agent.socialPreference}</Badge>
                      {agent.canProposeWorldChanges && <Badge color="amber">can propose</Badge>}
                    </div>
                  </div>
                  <div className="mt-4 grid gap-4 lg:grid-cols-2">
                    <div>
                      <p className="text-sm font-semibold text-slate-900">Goals</p>
                      <div className="mt-2">{renderItems(agent.goals, 'No goals set yet.')}</div>
                    </div>
                    <div>
                      <p className="text-sm font-semibold text-slate-900">Rituals</p>
                      <div className="mt-2">{renderItems(agent.rituals, 'No rituals set yet.')}</div>
                    </div>
                  </div>
                  <div className="mt-4 grid gap-4 lg:grid-cols-2">
                    <div>
                      <p className="text-sm font-semibold text-slate-900">Persona</p>
                      <div className="mt-2">{renderItems(agent.persona, 'No persona memories yet.')}</div>
                    </div>
                    <div>
                      <p className="text-sm font-semibold text-slate-900">Working Memory</p>
                      <div className="mt-2">
                        {renderItems(agent.workingMemories, 'No working-memory observations yet.')}
                      </div>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </Card>
      </div>

      <Card>
        <div className="flex flex-wrap items-center justify-between gap-3">
          <Text>{message}</Text>
          {isRefreshing && <Badge color="blue">Refreshing world...</Badge>}
        </div>
      </Card>
    </div>
  );
}

function ProposalCard(props: {
  proposal: SmallvilleWorldProposal;
  busy: string | null;
  isRefreshing: boolean;
  onApprove: () => void;
  onReject: () => void;
}) {
  return (
    <div className="rounded-2xl border border-amber-200 bg-amber-50 p-4">
      <div className="flex flex-wrap items-center gap-2">
        <Badge color="amber">{props.proposal.type}</Badge>
        <Badge color="gray">Tick {props.proposal.createdAtTick}</Badge>
        <span className="text-sm font-semibold text-slate-900">{props.proposal.agent}</span>
      </div>
      <p className="mt-2 text-sm text-slate-800">
        {props.proposal.parentLocation ? `${props.proposal.parentLocation} -> ` : ''}
        {props.proposal.name}
      </p>
      {props.proposal.proposedState && (
        <p className="mt-1 text-sm text-slate-600">Proposed state: {props.proposal.proposedState}</p>
      )}
      <p className="mt-2 text-sm text-slate-700">{props.proposal.reason}</p>
      <div className="mt-4 flex gap-3">
        <Button
          color="emerald"
          loading={props.busy === `approve-${props.proposal.id}` || props.isRefreshing}
          onClick={props.onApprove}
        >
          Approve
        </Button>
        <Button
          color="rose"
          variant="secondary"
          loading={props.busy === `reject-${props.proposal.id}` || props.isRefreshing}
          onClick={props.onReject}
        >
          Reject
        </Button>
      </div>
    </div>
  );
}
