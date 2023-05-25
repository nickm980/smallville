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

        innerHtml += createNewAgentBox({name, location, activity})

        container.innerHTML = innerHtml
    } else {
        element.innerHTML = `
            <h2>${name}</h2>
            <p><b>Current Activity:</b> ${activity}}</p>
            <p><b>Location:</b> ${location}</p>
            <p><b>Conversation:</b> None</p>`
    }
}

export {updateHTMLElement, createNewAgentBox}