# Smallville [![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)]([https://opensource.org/licenses/MIT](https://github.com/nickm980/towny/blob/main/LICENSE)) [![contributions welcome](https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat)](https://github.com/nickm980/smallville/issues)

Create your own generative agent simulations for RPG games or research

## What are Generative Agents?
Generative agents are virtual characters that can learn and adapt to their environment. Using LLM models such as ChatGPT or StableLM, agents are able to observe their surroundings, store memories, and react to state changes in the world

Generative Agents save time programming interactions by hand and make NPC's more realistic / dynamic

## How to Use Generative Agents Server
1. Download and install the Java server.
2. Use the JavaScript SDK to create your own generative agent.
3. Connect your generative agent to the server.
4. Start the server and let your generative agent run in your game.

## Getting Started

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
Create new agents (as seen in the example project) [example project](/example/javascript/smallville.js)
```javascript
const sim = new Smallville({
        host: "http://localhost:8080", // host of the server
        stateHandler: function(state) {
                //in here you would update the location of the agent using your own pathfinding algorithm
                const currentLocation = state.agents[0].getCurrentLocation()
                const newLocation = state.agents[0].getNextLocation()
                const emoji = state.agents[0].getEmoji()
                const activity = state.agents[0].getCurrentActivity()
                
                const updatedLocations = state.locations;
                
                const conversations = state.conversations;
                
                console.log('[State Change]: The simulation has been updated')
    },
});

```
Create new location trees
```javascript
sim.createLocation({
   name: 'Barn',
   description: 'An empty barn'
})

sim.createLocation({
   name: 'Hay Pile',
   parent: 'Barn',
   description: 'A hay pile',
   state: 'Full'  
})
```

Add new agents and initialize their memory stream with starting memories
```javascript
sim.createAgent({
  name: 'John',
  location: 'Barn: Hay Pile',
  memories: [
    "John is a farmer at the Barn",
    "John is a nice and outgoing person"
  ]
})
```
Increment the time clock. The simulation will get the current time and update the agents state
```javascript
sim.next();
```
Keep calling sim.next whenever you want to update the simulation step

To add new observations to the agent which they will prompt a reaction to use the following method
```javascript
sim.addObservation({name: "Full Agent Name", observation: "memory description", reactable: true})
```
Such observations such as encountering another agent or discovering a location should make use of this

For observatonal purposes, you can also ask an agent a question which will use their relevant memories to answer the question
```
sim.askQuestion("John", "What do you do in your free time")
```

This will not store the question in the agents memory unless you call `sim.addObservation()`

Furthermore, locations should be given as a full tree. ex) 
```javascript
location: "Island: Red House: desk"
```

And the leaf node (in this example desk), should have a state, although it is not necessary. Adding states to leaf locations enables the agents to interact with the world. To get the leaf location and move the agent to the location you can use our utility function `getLeafLocation(location)` or make your own method.

## Running the server
Running the Server
The Smallville World Simulator comes with a Java 17 server that you can use to store the simulation data. 

Run the following command in the same directory as your jar file downloaded from releases
```
java -jar smallville-server.jar --api-key <OPEN_AI_KEY> --port 8080
```
The server will start on the default port 8080 unless specified otherwise

## Example
![image](https://user-images.githubusercontent.com/81270095/233894690-97aedf01-7b20-4c8d-a48c-e234fdc0f4bf.png)

The example is under the example directory. Start a node server on that port and run the java server to start the simulation. This example isn't finished yet but is a basic example of how to get started.
[example javascript project](/example)

## TODO:
- Reflections
- Improve conversations
- Finish example game
- Improve memory retrieval
- Improve token embeddings
- Add option to use StableLM
- Bug fixes on location state updates
- Work on a way around possible timeouts from long request wait times
- Improve test coverage

## Info
Code based on Generative Agents: Interactive Simulacra of Human Behavior https://arxiv.org/pdf/2304.03442.pdf

## Getting Help
If you need help getting started with smallville you can join our community discord https://discord.gg/ktXPsgbFp5
