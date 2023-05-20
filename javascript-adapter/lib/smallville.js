import fetch from 'cross-fetch';

/**
 * @typedef {Object} SmallvilleAgent
 * @property {string} name - Unique name of the agent used as an identifier
 * @property {number} location - Agent's current location
 * @property {string} emoji - Unicode emoji representation of current activity
 * @property {string} currentActivity - Natural langugae description of what the agent is doing
*/

/**
 * @typedef {Object} SmallvilleLocation
 * @property {string} name - Unique name of the agent used as an identifier
 * @property {number} description - Agent's current location
 * @property {?SmallvilleLocation} parent - Unicode emoji representation of current activity
*/

/**
 * @typedef {Object} Dialog
 * @property {string} name - name of agent who spoke
 * @property {string} dialog - Unicode emoji representation of current activity
*/

/**
 * @typedef {Object} Conversation
 * @property {string} name - name of the agent who spoke first
 * @property {string} other - name of other agent involved in conversation
 * @property {Dialog[]} dialog - what the agents said to each other
*/

/**
 * @typedef {Object} SmallvilleState
 * @property {SmallvilleAgent[]} agents - All of the agents
 * @property {SmallvilleLocation[]} locations - Only the updated locations
 * @property {Conversation[]} conversations - Conversations automatically created between two agents
 */

/**
 * @callback agentHandlerCallback
 * @param {SmallvilleState} state - The current state of Smallville
 */

class Smallville {
    /**
     * @param {Object} options - Configure the host and state callback
     * @param {string} options.host - The host of the Smallville service. 
     * By default the server runs on localhost:8080
     * @param {agentHandlerCallback} options.stateHandler - The callback to handle agents
     * @param {Number} options.sessionId - Simulation session ID.
    */
    constructor({
        host: host,
        stateHandler: stateHandler,
    }) {
        if (!host) {
            throw new Error('Missing host option in initialization. If running locally with default options try http://localhost:8080/');
        }

        if (!stateHandler) {
            throw new Error('Missing agentsHandler');
        }

        this.host = host
        this.stateHandler = stateHandler
    }

    /**
     * Syncs the game with the server to preserve state when reloading the page
    */
    async sync(){
        const response = await fetch(this.host + "/state", {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json'
            },
        })

        const json = await response.json()
        this.stateHandler(json)
    }
    
    /**
     * Checks if server is alive and sends requests until a response is received or after 10 attempts
     * 
     * @returns {Promise<Boolean>} - Whether or not the simulation was initialized.
    */
    async init() {
        const MAX_RETRIES = 10;
        let retries = 0;
        let connected = false;

        while (!connected && retries < MAX_RETRIES) {
            try {
                const response = await fetch(this.host + "/ping", {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                })

                if (response.ok) {
                    connected = true
                    console.log('Connection successful');
                }

                else {
                    throw new Error(`Received ${response.status} ${response.statusText} from server`);
                }
            } catch (error) {
                console.log(`Connection attempt failed (retry ${retries + 1})`);
                retries++;
                await new Promise(resolve => setTimeout(resolve, 5000)); // Wait 5 seconds before retrying
            }
        }

        if (!connected) {
            console.error('Unable to establish a connection after maximum number of retries');
        }

        return connected
    }
    
    /**
     * Creates a new agent with the given name, description and initial location.
     * 
     * @param {Object} options - Configuration options of a new agent
     * @param {string} options.name - The unique name of the agent.
     *  @param {string} options.location - The unique name of the agent.
     * @param {string[]} options.memories - Initial memories and characteristics
     * @param {string} options.activity - Agent's current activity (what it is doing 
     * at the start of the simulation)
     * @returns {Promise<Boolean>} - Whether or not the creation was succesful. Will return
     * false if an agent by the name already exists or the location is invalid
    */
    async createAgent({ name, memories, location, activity }) {
        const url = `${this.host}/agents`
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                name: name,
                memories: memories,
                location: location,
                activity: activity
            })
        })

        const json = await response.json()
        console.log(json)
        return true
    }

    /**
     * Creates a new location with the given name, description and state.
     * 
     * @param {Object} options - Location information
     * @param {string} options.parent - Parent location
     * @param {string} options.name - The name of the location.
     * @param {?string} options.state - The state where the location is situated.
     * @returns {Promise<Boolean>} - Whether or not the creation was succesful
    */
    async createObject({ parent, name, state }) {
        const url = `${this.host}/objects`
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                parent: parent,
                name: name,
                state: state
            })
        })
        const json = await response.json()
        console.log(json)
        return true
    }

    /**
     * Creates a new location with the given name, description and state.
     * 
     * @param {Object} options - Location information
     * @param {string} options.name - The name of the location.
     * @returns {Promise<Boolean>} - Whether or not the creation was succesful
    */
    async createLocation({ name }) {
        const url = `${this.host}/locations`
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                name: name,
            })
        })

        const json = await response.json()
        console.log(json)

        return true
    }

    /**
     * Updates the simulation state including agent plans, ranking memories, and
     * new activities. Will send the result to the stateHandler callback function
     * and also return the new information in this request
     * 
     * @returns {Promise<SmallvilleState>} - A Promise that resolves to an object
     * containing the details of the current simulation state
    */
    async updateState() {
        const url = `${this.host}/state`

        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            }
        })

        const json = await response.json()

        this.stateHandler(json)

        return json
    }

    /**
     * Adds an observation to a given agent. 
     * 
     * @param {Object} options - Observation information
     * @param {string} options.name - Name of the agent to add an observation
     * @param {string} options.observation - The observation / memory to be added
     * @param {Boolean} options.reactable - Whether or not the agent can react to 
     * this observation. Results in higher API usage if true
     * @returns {Promise<Boolean>} - Whether or not the creation was succesful
    */
    async addObservation({ name, observation, reactable: reactable = false }) {
        const url = `${this.host}/react`
        await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                name: name,
                observation: observation,
                reactable: reactable
            })
        })

        return true
    }

    /**
     * Asks an agent a question without saving the question or response to
     * the agent's memory stream
     * 
     * @param {Object} options - Question information
     * @param {string} options.name - Name of the agent to ask
     * @param {string} options.question - Question to ask
     * @returns {Promise<string>} - Agent's response to the question
    */
    async ask({ name: name, question: question }) {
        const url = `${this.host}/agent/${name}/ask`

        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                question: question
            })
        })

        const result = await response.json()

        return result.answer
    }
}

/**
 * Usually on object or item in the simulation such as an expresso machine,
 * chair, or table.
 * 
 * @param {string} location 
 * @returns 
 */
function getLeafLocation(location) {
    const parts = location.split(':');
    return parts[parts.length - 1].trim();
}

export { Smallville, getLeafLocation };