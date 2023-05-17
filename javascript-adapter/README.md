Smallville is a javascript library to create custom generative agent simulations in web browsers. Generative Agents use LLM's to create realistic NPC charcters and are most useful for research and video games.

### Install the necessary files

Start a new javascript project
```
npm init
```

```
npm i smallville
```
Download the compiled jar from releases

### Example
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
})

sim.createObject({
   name: 'Hay Pile',
   state: 'Full',
   parent: 'Barn'    
})
```

Add new agents and initialize their memory stream with starting memories
```javascript
sim.createAgent({
  name: 'John',
  location: 'Barn',
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

This will not store the question in the agents memory unless you call `sim.addObservation()`

Furthermore, locations should be given as a full tree. ex) 
```javascript
location: "Island: Red House: desk"
```

And the leaf node (in this example desk), should have a state, although it is not necessary. Adding states to leaf locations enables the agents to interact with the world. To get the leaf location and move the agent to the location you can use our utility function `getLeafLocation(location)` or make your own method.
