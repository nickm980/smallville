const locations: any = {
    'Red House': {
        x: -100,
        y: 100,
    },
    'Green House': {
        x: -296,
        y: -140,
    },
    'Campfire': {
        x: 590,
        y: 380,
    },
    'Forest': {
        x: 490,
        y: 500,
    },
    'Branches': {
        x: 810,
        y: 355,
    },
    'Main Island': {
        x: 330,
        y: 110,
    },
}

function getCoordinates(name: string) {
    var location = getLeafLocation(name)
    if (locations[location] == undefined) {
        location = name.split(':')[0]
    }

    return { x: locations[location].x, y: locations[location].y }
}

function getLeafLocation(location: string) {
    const parts = location.split(':')
    return parts[parts.length - 1].trim().replace(':', '')
}

function getRootLocation(location: string) {
    const parts = location.split(':')
    return parts[parts.length - 1].trim().replace(':', '')
}

function updateLocations(locations: any) {
    const element = document.getElementById('locations')
    var str
    element.innerHTML = ''

    for (const location of locations) {
        element.innerHTML += `
            <p><b>${location.name}:</b> ${location.state}</p>`
    }
}

export { getCoordinates, updateLocations }
