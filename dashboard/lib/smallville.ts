import { User } from "../app/table";

export async function getAgents() {
  try {
    const response = await fetch('http://localhost:8080/agents', {cache: "no-store"}); // Replace with your actual server URL
    if (!response.ok) {
      throw new Error('Failed to fetch agents data.');
    }

    const agentsData = await response.json();
    const result: User[] = agentsData.agents

    return result
  } catch (error) {
    console.error('Error fetching agents data:');
    return []
  }
}

export interface SmallvilleAnalytics {
  step: Number;
  time: String;
  locationVisits: any;
  prompts: any[]
}

export async function getInfo() {
  try {
    const response = await fetch('http://localhost:8080/info', {cache: "no-store"}); // Replace with your actual server URL
    
    if (!response.ok) {
      throw new Error('Failed to fetch agents data.');
    }

    const agentsData = await response.json();
    const result: SmallvilleAnalytics = agentsData

    return result
  } catch (error) {
    console.error('Error fetching agents data');
    return []
  }
}

export async function getAllLocations() {
  try {
    const response = await fetch('http://localhost:8080/locations', {cache: "no-store"}); // Replace with your actual server URL
    
    if (!response.ok) {
      throw new Error('Failed to fetch location data.');
    }

    const result = await response.json();
    console.log("fetching new data")
    return result.locations
  } catch (error) {
    console.error('Error fetching locations data');
    return []
  }
}

export async function interview(agent: string, question: string) {
  try {
    const response = await fetch('http://localhost:8080/agents/' + agent + '/ask',{
      method: 'POST',
      headers: {
        Accept: 'application.json',
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        question: question
      }),
      cache: 'no-store'
    });

    if (!response.ok) {
      throw new Error('Failed to interivew agent.');
    }

    const responseJson = await response.json();
    const result = responseJson.answer

    return result
  } catch (error) {
    console.error('Error interviewing agent');
    return []
  }
}

export async function updateLocation(name: string, state: string) {
  try {
    const response = await fetch('http://localhost:8080/locations/' + name,{
      method: 'POST',
      headers: {
        Accept: 'application.json',
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        state: state
      }),
      cache: 'no-store'
    });

    if (!response.ok) {
      throw new Error('Failed to update location state.');
    }

    const responseJson = await response.json();
    const result = responseJson.answer

    return result
  } catch (error) {
    console.error('Error updating location');
    return []
  }
}