import { Smallville } from '../../javascript-client/dist/index.js'
import { agents } from './mechanics/agents.js'
import { updateAgent } from './mechanics/index.js'
import { updateLocations } from './mechanics/locations.js'
import { showLoadingCursor, stopShowingLoadingCursor } from './dom.js'

const smallville = new Smallville({
    host: 'http://localhost:8080',
    stateHandler: (state) => {
        console.log(state)
        if (state.agents == undefined) {
            console.log('No connection to server')
            return
        }
        state.agents.forEach((agent) => {
            updateAgent({
                name: agent.name,
                location: agent.location + ': ' + agent.object,
                activity: agent.action,
                emoji: agent.emoji,
            })
        })
        updateLocations(state.location_states)
    },
})

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
        name: 'Green House',
    })

    await smallville.createLocation({
        name: 'Red House',
    })

    await smallville.createLocation({
        name: 'Forest',
    })

    await smallville.createObject({
        parent: 'Green House',
        name: 'Kitchen',
        state: 'empty',
    })

    await smallville.createObject({
        parent: 'Red House',
        name: 'Bedroom',
        state: 'empty',
    })

    await smallville.createObject({
        parent: 'Forest',
        name: 'Campfire',
        state: 'on',
    })

    await smallville.createObject({
        parent: 'Forest',
        name: 'Branches',
        state: 'broken',
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
            location: agent.location,
            activity: agent.activity,
            memories: agent.memories,
        })
    })
}

await smallville.sync()

document
    .getElementById('smallville--next')
    .addEventListener('click', async function () {
        showLoadingCursor()
        console.log('Updating the game state')
        this.innerHTML = 'Loading...'
        this.disabled = true

        try {
            await smallville.updateState()
        } catch (err) {
            console.error(err)
        }

        stopShowingLoadingCursor()
        this.innerHTML = 'Update State'
        this.disabled = false
    })

let shouldAutoUpdate = false
let updateIntervalId
let secondsIndicatorIntervalId
let secondsIndicator = document.getElementById('seconds-indicator')
let secondsRemaining = 300

async function update() {
    showLoadingCursor()
    try {
        console.log('auto-updating')
        await smallville.updateState()
    } catch (err) {
        console.error(err)
    } finally {
        stopShowingLoadingCursor()
    }
}

document
    .getElementById('auto-update')
    .addEventListener('click', async function () {
        if (shouldAutoUpdate === false) {
            this.setAttribute('class', 'nes-btn is-success')
            this.innerText = 'Auto-Updating'
            update()
            updateIntervalId = setInterval(async function (e) {
                update()
            }, secondsRemaining * 1000)
            secondsIndicatorIntervalId = setInterval((e) => {
                secondsRemaining--
                secondsIndicator.innerText = secondsRemaining

                if (secondsRemaining === 0) secondsRemaining = 300
            }, 1000)
            shouldAutoUpdate = true
        } else if (shouldAutoUpdate === true) {
            this.setAttribute('class', 'nes-btn')
            this.innerText = 'Auto-Update?'
            clearInterval(updateIntervalId)
            secondsRemaining = 300
            console.log('cleared auto-update')
            shouldAutoUpdate = false
        }
    })
