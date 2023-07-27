# Smallville [![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)]([https://opensource.org/licenses/MIT](https://github.com/nickm980/towny/blob/main/LICENSE)) [![contributions welcome](https://img.shields.io/badge/contributions-welcome-brightgreen.svg?style=flat)](https://github.com/nickm980/smallville/issues)

## Generative Agents for video games
Generative agents are virtual characters that can store memories and dynamically react to their environment. Using LLM models such as ChatGPT agents are able to observe their surroundings, store memories, and react to state changes in the world

Generative Agents save time programming interactions by hand and make NPC's more realistic / dynamic

## Getting Started
Create new agents (as in the example project) [example project](/example/javascript/smallville.js)

Supported Client Languages: Java, JavaScript (or use the http endpoints)

### Java
Use maven to use the project from the jitpack repository
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
### JavaScript
Start a new javascript project
```
npm init
```

```
npm i smallville
```

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

## Example
This example isn't finished yet but is a basic example of how to get started.
[example javascript project](/example)

## Configuration
Configure prompts and other options
### Running Locally
Start LocalAI and change the apiPath and model options to the correct values

## Info
Code based on Generative Agents: Interactive Simulacra of Human Behavior https://arxiv.org/pdf/2304.03442.pdf
Tileset made by LimeZu

## Getting Help
If you need help getting started with smallville or view project updates you can join our community discord https://discord.gg/APVSw2DrCX 
