import { getCoordinates } from "./locations.js"

/**
 * An array of Agent objects.
 * @type {Agent[]}
 */
var agents = []

/**
 * Represents an agent in the game.
 *
 * @class
 */
class Agent {
    /**
     * Creates a new Agent instance.
     *
     * @constructor
     * @param {string} name - The name of the agent.
     * @param {string} activity - The name of the agent.
     * @param {string[]} memories - The name of the agent.
     * @param {Phaser.GameObjects.Sprite} agent - The Phaser sprite representing the agent.
     * @param {Phaser.GameObjects.Text} text - The Phaser text object displaying the agent's name and text.
     */
    constructor(name, agent, text, activity, memories) {
        this.name = name
        this.agent = agent
        this.text = text
        this.emoji = "😀"
        this.activity = activity
        this.memories = memories
    }

    moveTo(locationName) {
        const coords = getCoordinates(locationName)

        this.agent.setX(coords.x)
        this.agent.setY(coords.y)
        this.text.setY(coords.y - 20)
        this.text.setX(coords.x)

        console.log(this.name + " moved to " + locationName)
    }


    say(message) {
        this.text.setText(this.name + ": " + message + " " + this.emoji)
    }

    setEmoji(emoji) {
        this.emoji = emoji
        this.say(activity + " " + emoji)
    }

    setActivity(activity) {
        this.activity = activity
        this.say(activity + " " + emoji)
    }

    getAgent() {
        return this.agent;
    }
}

/**
 * Update an agent's location, activity, and emoji based on their name.
 * @param {Object} options - The options for updating the agent.
 * @param {string} options.name - The name of the agent to update.
 * @param {string} options.location - The new location of the agent.
 * @param {string} options.activity - The new activity of the agent.
 * @param {string} options.emoji - The new emoji of the agent.
 * @returns {void}
 */
function updateAgent({ name, location, activity, emoji }) {
    agents.forEach(agent => {
        if (agent.name === name) {
            agent.moveTo(location)
            agent.setEmoji(emoji)
            agent.setActivity(activity)
            return
        }
    })
}

/**
 * Creates a new agent with a nametag text object and adds it to the agents array.
 * @param {Object} options - An object containing the following options:
 *   @param {Phaser.Scene} options.scene - The Phaser scene object.
 *   @param {string} options.name - The name of the agent.
 *   @param {string} options.location - The location of the agent.
 *   @param {string} options.activity - The activity of the agent.
 *   @param {string[]} options.memories - The memories of the agent.
 */
function createAgent({ scene, name, location, activity, memories }) {
    const coords = getCoordinates(location)
    const player = scene.add.sprite(coords.x, coords.y, 'player');
    player.frame = 28

    const text = scene.add.text(coords.x, coords.y - 20, "[" + name + "]: " + activity, { font: '12px Arial', fill: '#ffffff' });
    text.setStyle({ backgroundColor: '#111111' });
    text.setOrigin(.5);

    const agent = new Agent(name, player, text, activity, memories)
    agents.push(agent)
    player.play('idle');
    
    console.log("Created a new agent " + name)
}

function moveAgent({ name, location }) {
    const agent = agents.find(agent => {
        console.log(agent.name + " : " + name)
        return name == agent.name
    })

    if (!agent) {
        console.error(`No agent found with name ${name}`)
        return;
    }

    agent.moveTo(location)
}

function loadAnimations(scene) {
    scene.anims.create({
        key: 'idle',
        frames: scene.anims.generateFrameNumbers('player', { start: 42, end: 47 }),
        frameRate: 10,
        repeat: -1
    });

    scene.anims.create({
        key: 'walk-down',
        frames: scene.anims.generateFrameNumbers('player', { start: 48, end: 53 }),
        frameRate: 10,
        repeat: -1
    });

    scene.anims.create({
        key: 'walk-left',
        frames: scene.anims.generateFrameNumbers('player', { start: 61, end: 64 }),
        frameRate: 10,
        repeat: -1
    });

    scene.anims.create({
        key: 'walk-up',
        frames: scene.anims.generateFrameNumbers('player', { start: 54, end: 59 }),
        frameRate: 10,
        repeat: -1
    });
}

export { updateAgent, createAgent, agents, moveAgent, loadAnimations }