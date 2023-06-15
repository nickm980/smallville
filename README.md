# Smallville [![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)]([https://opensource.org/licenses/MIT](https://github.com/nickm980/towny/blob/main/LICENSE)) [![contributions welcome](https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat)](https://github.com/nickm980/smallville/issues)

## What are Generative Agents?
Generative agents are virtual characters that can store memories and dynamically react to their environment. Using LLM models such as ChatGPT or StableLM, agents are able to observe their surroundings, store memories, and react to state changes in the world

Generative Agents save time programming interactions by hand and make NPC's more realistic / dynamic
![walking](https://github.com/nickm980/smallville/assets/81270095/bc0bc20c-3b80-4c1d-906d-8ceabe7fa6a0)
![image](https://github.com/nickm980/smallville/assets/81270095/ffe3c8d4-4fab-42e5-aaac-a1d78f2c7c31)

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

Supported Client Languages: Java, JavaScript (or use the http endpoints)
#### Java
```xml
	<repositories>
		<repository>
			<id>jitpack.io</id>
			<url>https://jitpack.io</url>
		</repository>
	</repositories>
	<dependencies>
		<dependency>
			<groupId>com.github.nickm980</groupId>
			<artifactId>smallville</artifactId>
			<version>2b663b0</version>
		</dependency>
	</dependencies>
 ```

```java
   SmallvilleClient client = SmallvilleClient.create("http://localhost:8080", new AgentHandlerCallback() {
      public void handle(SimulationUpdateEvent event) {
          List<SmallvilleAgent> agents = event.getAgents();
          List<SmallvilleLocation> locations = event.getLocations();
        }
      });

      client.createLocation("Red House");
      client.createObject("Red House", "Kitchen", new ObjectState("occupied"));
  
      List<String> memories = new ArrayList<String>();
      memories.add("Memory1");
      client.createAgent("John", memories, "Red House: Kitchen", "Cooking");
  
      client.updateState();
```
#### JavaScript
```javascript
const client = new Smallville({
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

Asking an agent a question using ask will not create a new memory unless called with addObservation

## Running the server
Running the Server
The Smallville World Simulator comes with a Java 17 server that you can use to store the simulation data. 

Run the following command in the same directory as your jar file downloaded from releases
```
java -jar smallville-server.jar --api-key <OPEN_AI_KEY> --port 8080
```
The server will start on the default port 8080 unless specified otherwise. The dashboard which shows the memory stream, current activities, locations, and emojis of all available agents is found at http://localhost:8080/dashboard

## Dashboard
The dashboard can be accessed from http://localhost:8080/dashboard by default. The dashboard contains all the prompts sent to the LLM every update, information about agents, locations, and the current time, as well as memory streams of the agents. Through the dashboard you can also change the states of objects and interview agents.
![image](https://github.com/nickm980/smallville/assets/81270095/18912b25-059e-4ace-b390-f29de57b9615)

## Example
This example isn't finished yet but is a basic example of how to get started.
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

## Info
Code based on Generative Agents: Interactive Simulacra of Human Behavior https://arxiv.org/pdf/2304.03442.pdf
Tileset made by LimeZu

## Getting Help
If you need help getting started with smallville or view project updates you can join our community discord https://discord.gg/APVSw2DrCX 
