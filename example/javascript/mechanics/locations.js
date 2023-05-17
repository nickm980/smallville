/**
 * A collection of predefined locations and their coordinates.
 * @typedef {Object.<string, {x: number, y: number}>} Locations
 */

/**
 * An object containing predefined locations and their coordinates.
 * @type {Locations}
 */
const locations = {
    "Red House": {
        x: -100,
        y: 100
    },
    "Green House": {
        x: -296,
        y: -140
    },
    "Campfire": {
        x: 600,
        y: 380
    }
}

/**
 * Retrieves the coordinates of a location by name.
 * @param {string} name - The name of the location to retrieve coordinates for.
 * @returns {{x: number, y: number}} - The coordinates of the location.
 */
function getCoordinates(name) {
    return { x: locations[name].x, y: locations[name].y }
}

export { getCoordinates }


