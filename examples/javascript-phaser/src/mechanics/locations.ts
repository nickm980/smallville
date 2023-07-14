let locations: Location[] = []

class Location {
    private name: string
    private x: integer
    private y: integer

    constructor(name: string, x: integer, y:integer){
        this.name = name
        this.x = x
        this.y = y
    }

    getName(): string{
        return this.name
    }

    getX(): integer {
        return this.x
    }

    getY(): integer {
        return this.y
    }
}

function addLocation(name: string, x: integer, y: integer){
    locations.push(new Location(name, x, y))
}

function getLocation(name: string){
    console.debug('looking for location: ' + name)
    const result = locations.find((location) => location.getName() == name)

    if (result === undefined){
        console.error(name + ' does not exist')
    }

    return result
}

function updateLocations(locations: any) {
    const element = document.getElementById('locations')
    element.innerHTML = ''

    for (const location of locations) {
        element.innerHTML += `
            <p><b>${location.name}:</b> ${location.state}</p>`
    }
}

export { addLocation, getLocation, updateLocations }
