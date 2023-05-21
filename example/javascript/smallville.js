import { Smallville } from '../../javascript-adapter/dist/index.js'
import { agents } from './mechanics/agents.js'
import { updateAgent } from './mechanics/index.js';

const smallville = new Smallville({
    host: 'http://localhost:8080',
    stateHandler: (state) => {
        console.log(state)

        state.agents.forEach(agent => {
            updateAgent({
                name: agent.name,
                location: agent.location + ": " + agent.object,
                activity: agent.action,
                emoji: agent.emoji
            })
        })
    }
});

const successful = await smallville.init()

if (successful) {
    startSimulation()
}

async function startSimulation() {
    /*****************************************
     *                                       *
     * Create locations and stateful objects *
     *                                       *
     *****************************************/
    await smallville.createLocation({
        name: 'Main Island',
    })

    await smallville.createLocation({
        name: 'Forest',
    })

    await smallville.createObject({
        parent: 'Main Island',
        name: 'Green House',
        state: 'empty'
    })

    await smallville.createObject({
        parent: 'Main Island',
        name: 'Red House',
        state: 'empty'
    })

    await smallville.createObject({
        parent: 'Forest',
        name: 'Campfire',
        state: 'on'
    })
    
    await smallville.createObject({
        parent: 'Forest',
        name: 'Branches',
        state: 'broken'
    })
    /********************************
     *                              *
     * Add the agents to the server *
     *                              *
     ********************************/
    agents.forEach(async (agent) => {
        await smallville.createAgent({
            name: agent.name,
            location: agent.location,
            activity: agent.activity,
            memories: agent.memories
        })
    })
}

document.getElementById("smallville--next").addEventListener('click', async function () {
    console.log("Updating the game state")
    this.innerHTML = "Loading..."
    this.disabled = true
    await smallville.updateState()
    this.innerHTML = "Update State"
    this.disabled = false

});

await smallville.sync()