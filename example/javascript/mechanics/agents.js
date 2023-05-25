import { getCoordinates } from './locations.js'
import { updateHTMLElement } from '../dom.js'
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
     * Represents an Agent instance.
     * @class
     * @classdesc Smallville Agent
     * @constructor
     * @param {Object} options - The options object.
     * @param {string} options.name - The name of the instance.
     * @param {string} options.agent - The agent of the instance.
     * @param {string} options.text - The text of the instance.
     * @param {string} options.activity - The activity of the instance.
     * @param {string} options.location - The location of the instance.
     * @param {Array} options.memories - The memories of the instance.
     */
    constructor({ name, agent, text, activity, location, memories }) {
        this.name = name
        this.agent = agent
        this.text = text
        this.emoji = '😀'
        this.activity = activity
        this.memories = memories
        this.location = location
    }

    moveTo(locationName) {
        const coords = getCoordinates(locationName)

        this.agent.setX(coords.x)
        this.agent.setY(coords.y)

        console.log(this.name + ' moved to ' + locationName)
    }

    say(message) {
        this.text.setText(this.name[0] + ': ' + this.emoji)
    }

    setEmoji(emoji) {
        this.emoji = emoji
        this.say(this.activity)
    }

    setActivity(activity) {
        console.log(this.name + ' activity')
        this.activity = activity
        this.say(activity)
    }

    getAgent() {
        return this.agent
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
    const agent = agents.find((agent) => {
        return name == agent.name
    })

    if (!agent) {
        console.error(`No agent found with name ${name}`)
        return
    }

    updateHTMLElement({ name, location, activity })
    agent.moveTo(location)
    agent.setEmoji(emoji)
    agent.setActivity(activity)
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
    updateHTMLElement({ name, location, activity })
    var player = scene.add.sprite(0, 0, 'player')
    player.setFrame(28)
    var group = scene.add.container()

    var dialog = scene.add.sprite(25, -30, 'dialog')
    var emoji = scene.add.text(0, -42, name[0] + ':😀', {
        font: '16px Courier New',
        fill: '#00000',
    })
    var activityText = scene.add.text(0, 0, "Game hasn't started!", {
        font: '16px Courier New',
        fill: '#00000',
    })
    // dialog.addChild(text)
    // player.addChild(dialog)
    activityText.visible = false
    player.setInteractive()
    player.on('click', function () {
        activity.visible = true
    })
    player.on('click', function () {
        activity.visible = false
    })

    dialog.setScale(0.75)
    group.add(dialog)
    group.add(activityText)
    group.add(emoji)
    group.add(player)

    const agent = new Agent({
        name: name,
        agent: group,
        text: emoji,
        activity: activity,
        location: location,
        memories: memories,
    })

    agents.push(agent)
    player.play('idle')

    moveAgent({ name: name, location: location })
    console.log('Created a new agent ' + name)
}

function moveAgent({ name, location }) {
    const agent = agents.find((agent) => {
        return name == agent.name
    })

    if (!agent) {
        console.error(`No agent found with name ${name}`)
        return
    }

    console.log(location)
    agent.moveTo(location)
}

function loadAnimations(scene) {
    scene.anims.create({
        key: 'idle',
        frames: scene.anims.generateFrameNumbers('player', {
            start: 42,
            end: 47,
        }),
        frameRate: 10,
        repeat: -1,
    })

    scene.anims.create({
        key: 'walk-down',
        frames: scene.anims.generateFrameNumbers('player', {
            start: 48,
            end: 53,
        }),
        frameRate: 10,
        repeat: -1,
    })

    scene.anims.create({
        key: 'walk-left',
        frames: scene.anims.generateFrameNumbers('player', {
            start: 61,
            end: 64,
        }),
        frameRate: 10,
        repeat: -1,
    })

    scene.anims.create({
        key: 'walk-up',
        frames: scene.anims.generateFrameNumbers('player', {
            start: 54,
            end: 59,
        }),
        frameRate: 10,
        repeat: -1,
    })
}

export { updateAgent, createAgent, agents, moveAgent, loadAnimations }
