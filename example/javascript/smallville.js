import { Smallville } from '../../javascript-adapter/dist/index.js'
import { agents } from './mechanics/agents.js'
import { updateAgent } from './mechanics/index.js'

const smallville = new Smallville({
    host: 'http://localhost:8080',
    stateHandler: (state) => {
        console.log(state)
        stopShowingLoadingCursor()
        state.agents.forEach((agent) => {
            updateAgent({
                name: agent.name,
                location: agent.location + ': ' + agent.object,
                activity: agent.action,
                emoji: agent.emoji,
            })
        })
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
        name: 'Main Island',
    })

    await smallville.createLocation({
        name: 'Forest',
    })

    await smallville.createObject({
        parent: 'Main Island',
        name: 'Green House',
        state: 'empty',
    })

    await smallville.createObject({
        parent: 'Main Island',
        name: 'Red House',
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
        await smallville.createAgent({
            name: agent.name,
            location: agent.location,
            activity: agent.activity,
            memories: agent.memories,
        })
    })
}

let loadingCursorIntervalId
const finalFrame = 7
let currentCursorFrameNum
let prevCursorFrameNum
let phaserContainer = document.getElementById('phaser-container')
function showLoadingCursor() {
    
    
    prevCursorFrameNum = finalFrame
    currentCursorFrameNum = 0
    
    if (
        !phaserContainer.classList.contains(
            `loading-cursor-frame-${currentCursorFrameNum}`
        )
    ) {
        phaserContainer.classList.add(
            `loading-cursor-frame-${currentCursorFrameNum}`
        )
    }
    loadingCursorIntervalId = setInterval(() => {
        phaserContainer.classList.replace(
            `loading-cursor-frame-${prevCursorFrameNum}`,
            `loading-cursor-frame-${currentCursorFrameNum}`
        ) // Add the class for the current cursor
        currentCursorFrameNum =
            currentCursorFrameNum + 1 <= finalFrame
                ? currentCursorFrameNum + 1
                : 0

        prevCursorFrameNum =
            currentCursorFrameNum - 1 > -1
                ? currentCursorFrameNum - 1
                : finalFrame
    }, 160) // Change cursor every 160 milliseconds (0.16 seconds)
}

function stopShowingLoadingCursor() {
    clearInterval(loadingCursorIntervalId)
    phaserContainer.classList.remove(
        `loading-cursor-frame-${currentCursorFrameNum}`, `loading-cursor-frame-${prevCursorFrameNum}`
    )
}

document
    .getElementById('smallville--next')
    .addEventListener('click', function () {
        showLoadingCursor()
        console.log('Updating the game state')
        smallville.updateState()
    })

await smallville.sync()
