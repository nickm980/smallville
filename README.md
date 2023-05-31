# Smallville [![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)]([https://opensource.org/licenses/MIT](https://github.com/nickm980/towny/blob/main/LICENSE)) [![contributions welcome](https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat)](https://github.com/nickm980/smallville/issues)

## What are Generative Agents?
Generative agents are virtual characters that can store memories and dynamically react to their environment. Using LLM models such as ChatGPT or StableLM, agents are able to observe their surroundings, store memories, and react to state changes in the world

Generative Agents save time programming interactions by hand and make NPC's more realistic / dynamic

## Getting Started
Visit the package on npm https://www.npmjs.com/package/smallville
### Install the necessary files

Start a new javascript project
```
npm init
```

```
npm i smallville
```
Download the compiled jar from releases

### Start writing code
Create new agents (as in the example project) [example project](/example/javascript/smallville.js)
```javascript
const sim = new Smallville({
        host: "http://localhost:8080", // host of the server
        stateHandler: function(state) {
                //in here you would update the location of the agent using your own pathfinding algorithm
                const agents = state.agents
                const objects = state.locations
                const conversations = state.conversations
                
                console.log('[State Change]: The simulation has been updated')
    },
});

```
Create new location trees
```javascript
sim.createLocation({
   name: 'Barn',
})

sim.createObject({
   name: 'Hay Pile',
   parent: 'Barn',
   state: 'Full'  
})
```

Add new agents and initialize their memory stream with starting memories
```javascript
sim.createAgent({
  name: 'John',
  location: 'Hay Pile',
  activity: 'Stacking hay on the hay pile',
  memories: [
    "John is a farmer at the Barn",
    "John is a nice and outgoing person"
  ]
})
```
Increment the time clock. The simulation will get the current time and update the agents state
```javascript
sim.updateState();
```
Keep calling sim.updateState whenever you want to update the simulation step

To add new observations to the agent which they will prompt a reaction to use the following method
```javascript
sim.addObservation({name: "Full Agent Name", observation: "memory description", reactable: true})
```
Such observations such as encountering another agent or discovering a location should make use of this

For observatonal purposes, you can also ask an agent a question which will use their relevant memories to answer the question
```
sim.askQuestion("John", "What do you do in your free time")
```

Asking an agent a question will not create a new memory unless called with `sim.addObservation()`

## Running the server
Running the Server
The Smallville World Simulator comes with a Java 17 server that you can use to store the simulation data. 

Run the following command in the same directory as your jar file downloaded from releases
```
java -jar smallville-server.jar --api-key <OPEN_AI_KEY> --port 8080
```
The server will start on the default port 8080 unless specified otherwise. The dashboard which shows the memory stream, current activities, locations, and emojis of all available agents is found at http://localhost:8080/dashboard

## Example
The example is under the example directory. This example isn't finished yet but is a basic example of how to get started.
[example javascript project](/example)

## Configuration
### Prompt Templates
| Template Variable | Description |
| --- | --- |
| agent.name | Full name of the agent |
| agent.memories | List of all the memories (has child of {{description}}) |
| agent.activity | What the agent is currently doing |
| agent.lastActivity | What the agent was last doing |
| agent.summary | Cached summary from prompts.yaml |
| agent.description | Comma delimited characteristics |
| agent.locationName | The current location |
| agent.locationChildren | List of objects contained by the current location |
| agent.plans | List of the two most recent plans |
| tenses | For example, x is no longer {past tense} and is now {current activity} |
| memories.unranked | Memories with a weight of 0 (except short term plans) |
| memories.characteristics | Initial memories added to the agent |
| memories.relevant | The three most relevant memories to an observation |
| date.full | Full date (as formatted in config.yaml) |
| date.time | Time of day (as formatted in config.yaml) |
| date.yesterday | Yesterday’s full date (as formatted in config.yaml) |
| world.description | World description (includes available locations) |
| question | Question asked to agent |
| tenses | For example, x is no longer {past tense} and is now {current activity} |
| memories.characteristics | Initial memories added to the agent |

### Running Locally
Start LocalAI and change the apiPath and model options to the correct values

## TODO:
- Improve conversations
- Improve memory retrieval
- Improve token embeddings
- Bug fixes on location state updates
- Improve test coverage

## Info
Code based on Generative Agents: Interactive Simulacra of Human Behavior https://arxiv.org/pdf/2304.03442.pdf
Tileset made by LimeZu

## Getting Help
If you need help getting started with smallville or view project updates you can join our community discord https://discord.gg/APVSw2DrCX 
