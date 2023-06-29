import { Smallville } from 'smallville'
import { agents } from '../src/mechanics/agents.js'
import { updateAgent } from '../src/mechanics/index.js'
import { updateLocations } from '../src/mechanics/locations.js'
import { showLoadingCursor, stopShowingLoadingCursor } from '../src/mechanics/dom.js'

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
        state: 'occupied by John',
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
let intervalId
let secondsIndicator = document.getElementById('seconds-indicator')
let secondsBetweenUpdates = 300
let secondsLeft = secondsBetweenUpdates
let prevExecutionTime

async function update() {
    showLoadingCursor()
    try {
        console.log('auto-updating')
        const agent = agents[0]
        await smallville.addObservation({
            observation: 'John is at the red house',
            reactable: true,
        })
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
            prevExecutionTime = Date.now()
            intervalId = setInterval((e) => {
                let wholeSecondsSincePrevExecution = Math.floor(
                    (Date.now() - prevExecutionTime) / 1000
                )

                if (wholeSecondsSincePrevExecution > 0) {
                    secondsLeft -= wholeSecondsSincePrevExecution
                    prevExecutionTime = Date.now()
                }
                if (secondsLeft <= 0) {
                    update()
                    secondsLeft = secondsBetweenUpdates - secondsLeft
                }
                secondsIndicator.innerText = secondsLeft
            }, 1000)
            shouldAutoUpdate = true
        } else if (shouldAutoUpdate === true) {
            this.setAttribute('class', 'nes-btn')
            this.innerText = 'Auto-Update?'
            clearInterval(intervalId)
            secondsLeft = secondsBetweenUpdates
            console.log('cleared auto-update')
            shouldAutoUpdate = false
        }
    })
