import { Smallville } from '../../javascript-adapter/dist/index.js'
import { agents } from './mechanics/agents.js'
import { updateAgent } from './mechanics/index.js';

console.log(Smallville)
const smallville = new Smallville({
    host: 'http://localhost:8080',
    stateHandler: (state) => {
        state.agents.forEach(agent => {
            updateAgent(agent.name, agent.location, agent.currentActivity, agent.emoji)
        })
    }
});

const successful = await smallville.init()

if (successful){
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
            activity: agent.activity || "Cleaning the kitchen",
            memories: [
                "John is nice and he lives in the Red House",
            ]
        })
    })
}

document.getElementById("smallville--next").addEventListener('click', function () {
    console.log("Pressed update state button")
    smallville.updateState()
});

