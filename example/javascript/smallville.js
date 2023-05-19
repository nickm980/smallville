import { Smallville } from '../../javascript-adapter/dist/index.js'
import { agents } from './mechanics/agents.js'
import { updateAgent } from './mechanics/index.js';

console.log(Smallville)
const smallville = new Smallville({
    host: 'http://localhost:8080',
    stateHandler: (state) => {
        console.log(state)
        
        if (state.agents == undefined){
            console.log("No agents found")
            return
        }

        state.agents.forEach(agent => {
            updateAgent(agent.name, agent.location, agent.currentActivity, agent.emoji)
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
        name: 'Kitchen',
    })

    await smallville.createObject({
        parent: 'Kitchen',
        name: 'Chair',
        state: 'occupied'
    })

    await smallville.createObject({
        parent: 'Kitchen',
        name: 'Stove',
        state: 'off'
    })

    /********************************
     *                              *
     * Add the agents to the server *
     *                              *
     ********************************/
    agents.forEach(async (agent) => {
        console.log(agent)
        await smallville.createAgent({
            name: agent.name,
            location: 'Kitchen',
            activity: agent.activity,
            memories: agent.memories
        })
    })
}

document.getElementById("smallville--next").addEventListener('click', function () {
    console.log("Pressed update state button")
    smallville.updateState()
});

