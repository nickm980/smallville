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
        `loading-cursor-frame-${currentCursorFrameNum}`,
        `loading-cursor-frame-${prevCursorFrameNum}`
    )
}

function createNewAgentBox({ name, location, activity }) {
    return `<div id="${name}" class="agent">
        <h2>${name}</h2>
        <p><b>Current Activity:</b> ${activity}}</p>
        <p><b>Location:</b> ${location}</p>
        <p><b>Conversation:</b> None</p>
    </div>`
}

function updateHTMLElement({ name, location, activity }) {
    const element = document.getElementById(name)
    if (element == undefined) {
        //create element
        var container = document.getElementById('characters')
        var innerHtml = container.innerHTML

        innerHtml += createNewAgentBox({ name, location, activity })

        container.innerHTML = innerHtml
    } else {
        element.innerHTML = `
            <h2>${name}</h2>
            <p><b>Current Activity:</b> ${activity}</p>
            <p><b>Location:</b> ${location}</p>
            <p><b>Conversation:</b> None</p>`
    }
}

export {
    updateHTMLElement,
    createNewAgentBox,
    showLoadingCursor,
    stopShowingLoadingCursor,
}
