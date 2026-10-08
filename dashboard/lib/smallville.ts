const SMALLVILLE_URL =
  process.env.NEXT_PUBLIC_SMALLVILLE_URL ?? 'http://localhost:8080';

export { SMALLVILLE_URL };

export interface SmallvilleAgent {
  name: string;
  location: string;
  action: string;
  emoji: string;
}

export interface SmallvillePrompt {
  prompt?: string;
  response?: string;
  responseTime: number;
}

export interface SmallvilleAnalytics {
  step: number;
  time: string;
  locationVisits: { name: string; value: number }[];
  prompts: SmallvillePrompt[];
}

export interface SmallvilleLocation {
  name: string;
  state: string | null;
}

export interface SmallvilleWorldRules {
  summary: string;
  rules: string[];
  allowedLocationStates: string[];
}

export interface SmallvilleDailyRhythm {
  breakfast: string;
  lunch: string;
  dinner: string;
  morningTea: string;
  afternoonTea: string;
  snack: string;
}

export interface SmallvilleWorldLocation {
  name: string;
  parent: string | null;
  state: string | null;
  depth: number;
  agents: string[];
}

export interface SmallvilleWorldAgent {
  name: string;
  location: string | null;
  action: string;
  emoji: string | null;
  traits: string | null;
  model: string | null;
  socialPreference: string;
  canProposeWorldChanges: boolean;
  persona: string[];
  workingMemories: string[];
  recentMemories: string[];
  shortPlans: string[];
  longPlans: string[];
  goals: string[];
  rituals: string[];
}

export interface SmallvilleActionLogEntry {
  tick: number;
  time: string;
  actor: string;
  type: string;
  summary: string;
  fromLocation: string | null;
  toLocation: string | null;
}

export interface SmallvilleWorldProposal {
  id: string;
  agent: string;
  type: string;
  parentLocation: string | null;
  name: string;
  proposedState: string | null;
  reason: string;
  status: string;
  createdAtTick: number;
}

export interface SmallvilleWorldSnapshot {
  step: number;
  tick: number;
  time: string;
  worldBuilding: SmallvilleWorldRules;
  dailyRhythm: SmallvilleDailyRhythm;
  locations: SmallvilleWorldLocation[];
  agents: SmallvilleWorldAgent[];
  conversations: { name: string; message: string }[];
  actionLog: SmallvilleActionLogEntry[];
  pendingProposals: SmallvilleWorldProposal[];
}

export interface SmallvilleModelsResponse {
  providerMode: string;
  defaultModel: string;
  availableModels: string[];
  agentModelOverrides: Record<string, string>;
}

export interface SmallvilleAgentImportPreview {
  name: string;
  location: string;
  activity: string;
  model: string;
  issues: string[];
}

export interface SmallvilleImportAgentsResponse {
  success: boolean;
  preview: boolean;
  errors: string[];
  createdAgents: string[];
  agents: SmallvilleAgentImportPreview[];
}

const defaultAnalytics: SmallvilleAnalytics = {
  step: 0,
  time: 'Unavailable',
  locationVisits: [],
  prompts: []
};

const defaultWorldSnapshot: SmallvilleWorldSnapshot = {
  step: 0,
  tick: 0,
  time: 'Unavailable',
  worldBuilding: {
    summary: '',
    rules: [],
    allowedLocationStates: []
  },
  dailyRhythm: {
    breakfast: '',
    lunch: '',
    dinner: '',
    morningTea: '',
    afternoonTea: '',
    snack: ''
  },
  locations: [],
  agents: [],
  conversations: [],
  actionLog: [],
  pendingProposals: []
};

const defaultModelsResponse: SmallvilleModelsResponse = {
  providerMode: 'local-ollama',
  defaultModel: '',
  availableModels: [],
  agentModelOverrides: {}
};

const REQUEST_TIMEOUT_MS = 10000;
const LLM_REQUEST_TIMEOUT_MS = 180000;

async function requestJson<T>(path: string, init?: RequestInit, timeoutMs = REQUEST_TIMEOUT_MS): Promise<T> {
  const controller = new AbortController();
  const timeoutId = setTimeout(() => controller.abort(), timeoutMs);

  try {
    const response = await fetch(`${SMALLVILLE_URL}${path}`, {
      cache: 'no-store',
      ...init,
      signal: controller.signal
    });

    if (!response.ok) {
      throw new Error(`Smallville request failed: ${response.status}`);
    }

    return response.json() as Promise<T>;
  } finally {
    clearTimeout(timeoutId);
  }
}

function jsonRequest<T>(path: string, body: unknown, timeoutMs = REQUEST_TIMEOUT_MS) {
  return requestJson<T>(path, {
    method: 'POST',
    headers: {
      Accept: 'application/json',
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(body)
  }, timeoutMs);
}

function coerceWorldSnapshot(snapshot: Partial<SmallvilleWorldSnapshot> | null | undefined): SmallvilleWorldSnapshot {
  return {
    ...defaultWorldSnapshot,
    ...snapshot,
    worldBuilding: {
      ...defaultWorldSnapshot.worldBuilding,
      ...(snapshot?.worldBuilding ?? {})
    },
    dailyRhythm: {
      ...defaultWorldSnapshot.dailyRhythm,
      ...(snapshot?.dailyRhythm ?? {})
    },
    locations: (snapshot?.locations ?? []).map((location) => ({
      ...location,
      parent: location.parent ?? null,
      state: location.state ?? null,
      depth: location.depth ?? 0,
      agents: location.agents ?? []
    })),
    agents: (snapshot?.agents ?? []).map((agent) => ({
      ...agent,
      location: agent.location ?? null,
      action: agent.action ?? '',
      emoji: agent.emoji ?? null,
      traits: agent.traits ?? null,
      model: agent.model ?? null,
      socialPreference: agent.socialPreference ?? 'balanced',
      canProposeWorldChanges: agent.canProposeWorldChanges ?? false,
      persona: agent.persona ?? [],
      workingMemories: agent.workingMemories ?? [],
      recentMemories: agent.recentMemories ?? [],
      shortPlans: agent.shortPlans ?? [],
      longPlans: agent.longPlans ?? [],
      goals: agent.goals ?? [],
      rituals: agent.rituals ?? []
    })),
    conversations: snapshot?.conversations ?? [],
    actionLog: (snapshot?.actionLog ?? []).map((entry) => ({
      ...entry,
      tick: entry.tick ?? 0,
      fromLocation: entry.fromLocation ?? null,
      toLocation: entry.toLocation ?? null
    })),
    pendingProposals: (snapshot?.pendingProposals ?? []).map((proposal) => ({
      ...proposal,
      parentLocation: proposal.parentLocation ?? null,
      proposedState: proposal.proposedState ?? null,
      status: proposal.status ?? 'pending',
      createdAtTick: proposal.createdAtTick ?? 0
    }))
  };
}

function coerceModelsResponse(models: Partial<SmallvilleModelsResponse> | null | undefined): SmallvilleModelsResponse {
  return {
    ...defaultModelsResponse,
    ...models,
    availableModels: models?.availableModels ?? [],
    agentModelOverrides: models?.agentModelOverrides ?? {}
  };
}

export async function pingSmallville() {
  try {
    const response = await requestJson<{ success: boolean; ping: string }>('/ping');
    return response.ping === 'pong';
  } catch (error) {
    console.error('Error reaching Smallville server:', error);
    return false;
  }
}

export async function getAgents() {
  try {
    const response = await requestJson<{ agents: SmallvilleAgent[] }>('/agents');
    return response.agents;
  } catch (error) {
    console.error('Error fetching agents data:', error);
    return [];
  }
}

export async function getInfo() {
  try {
    return await requestJson<SmallvilleAnalytics>('/info');
  } catch (error) {
    console.error('Error fetching Smallville analytics:', error);
    return defaultAnalytics;
  }
}

export async function getAllLocations() {
  try {
    const response = await requestJson<{ locations: SmallvilleLocation[] }>('/locations');
    return response.locations;
  } catch (error) {
    console.error('Error fetching locations data:', error);
    return [];
  }
}

export async function getWorldSnapshot() {
  try {
    return coerceWorldSnapshot(await requestJson<SmallvilleWorldSnapshot>('/world'));
  } catch (error) {
    console.error('Error fetching world snapshot:', error);
    return defaultWorldSnapshot;
  }
}

export async function getModels() {
  try {
    return coerceModelsResponse(await requestJson<SmallvilleModelsResponse>('/models'));
  } catch (error) {
    console.error('Error fetching models:', error);
    return defaultModelsResponse;
  }
}

export async function setDefaultModel(model: string, providerMode?: string) {
  try {
    await jsonRequest('/models/default', { model, providerMode });
    return true;
  } catch (error) {
    console.error('Error updating default model:', error);
    return false;
  }
}

export async function setAgentModel(agentName: string, model: string | null) {
  try {
    await jsonRequest(`/agents/${encodeURIComponent(agentName)}/model`, { model });
    return true;
  } catch (error) {
    console.error('Error updating agent model override:', error);
    return false;
  }
}

export async function previewImportAgents(yaml: string) {
  try {
    return await jsonRequest<SmallvilleImportAgentsResponse>('/agents/import', {
      yaml,
      preview: true
    }, LLM_REQUEST_TIMEOUT_MS);
  } catch (error) {
    console.error('Error previewing agent import:', error);
    return {
      success: false,
      preview: true,
      errors: ['Could not preview the YAML import.'],
      createdAgents: [],
      agents: []
    };
  }
}

export async function importAgents(yaml: string) {
  try {
    return await jsonRequest<SmallvilleImportAgentsResponse>('/agents/import', {
      yaml,
      preview: false
    }, LLM_REQUEST_TIMEOUT_MS);
  } catch (error) {
    console.error('Error importing agents:', error);
    return {
      success: false,
      preview: false,
      errors: ['Could not import agents.'],
      createdAgents: [],
      agents: []
    };
  }
}

export async function getWorldProposals() {
  try {
    const response = await requestJson<{ proposals: SmallvilleWorldProposal[] }>('/world/proposals');
    return response.proposals;
  } catch (error) {
    console.error('Error fetching world proposals:', error);
    return [];
  }
}

export async function approveWorldProposal(id: string) {
  try {
    await jsonRequest(`/world/proposals/${encodeURIComponent(id)}/approve`, {});
    return true;
  } catch (error) {
    console.error('Error approving world proposal:', error);
    return false;
  }
}

export async function rejectWorldProposal(id: string) {
  try {
    await jsonRequest(`/world/proposals/${encodeURIComponent(id)}/reject`, {});
    return true;
  } catch (error) {
    console.error('Error rejecting world proposal:', error);
    return false;
  }
}

export async function interview(agent: string, question: string) {
  try {
    const response = await jsonRequest<{ answer: string }>(`/agents/${agent}/ask`, {
      question
    }, LLM_REQUEST_TIMEOUT_MS);
    return response.answer;
  } catch (error) {
    console.error('Error interviewing agent:', error);
    return 'The dashboard could not reach the Smallville server.';
  }
}

export async function updateLocation(name: string, state: string) {
  try {
    await jsonRequest(`/locations/${encodeURIComponent(name)}`, { state });
    return true;
  } catch (error) {
    console.error('Error updating location:', error);
    return false;
  }
}

export async function advanceSimulation() {
  try {
    await requestJson('/state', {
      method: 'POST'
    }, LLM_REQUEST_TIMEOUT_MS);
    return true;
  } catch (error) {
    console.error('Error advancing simulation:', error);
    return false;
  }
}
