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
    constructor({ name, agent, text, activity, location, memories }) {
        this.name = name
        this.agent = agent
        this.text = text
        this.emoji = '?'
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
        if (emoji === undefined || emoji == null){
            this.emoji = "?"
            this.say(this.activity)
            return
        }
        
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

var nMesh

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
    moveAgent({ scene: nMesh, name: name, location: location })
    agent.setEmoji(emoji)
    agent.setActivity(activity)
}

function createAgent({ scene, name, location, activity, memories }) {
    if (nMesh == undefined) {
        nMesh = scene
    }

    updateHTMLElement({ name, location, activity })
    var player = scene.add.sprite(0, 0, 'player')
    player.setFrame(28)

    var group = scene.add.container()

    var dialog = scene.add.sprite(25, -30, 'dialog')

    var emoji = scene.add.text(0, -42, name[0] + ': ?', {
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

    player.on('pointerover ', function () {
        agent.reset()
    })

    player.on('pointerout', function () {
        agent.visible = false
    })

    dialog.setScale(0.75)
    group.add(dialog)
    group.add(activityText)
    group.add(emoji)
    group.add(player)

    scene.physics.world.enable(group)

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
    //-300, -140
    console.log('[Agent] Created a new agent ' + name)
    const coords = getCoordinates(location)
    agent.agent.x = coords.x
    agent.agent.y = coords.y
    moveAgent({ scene: scene, name: name, location: location })
}

function moveAgent({ scene, name, location }) {
    const agent = agents.find((agent) => {
        return name == agent.name
    })

    const coords = getCoordinates(location)

    if (!agent) {
        console.error(`No agent found with name ${name}`)
        return
    }

    const path = scene.navMesh.findPath(
        { x: agent.agent.x, y: agent.agent.y },
        { x: coords.x, y: coords.y }
    )

    let navMeshPolys = scene.navMesh.navMesh.getPolygons()
    let newPath = []

    if (path == undefined) {
        console.error('no path to object')
        return
    }

    path.forEach((point) => {
        navMeshPolys.forEach((poly) => {
            if (poly.contains(point)) {
                newPath.push(poly.centroid)
            }
        })
    })

    newPath = path
    let i = 1 // Initialize the index at 0

    const moveNext = () => {
        const item = newPath[i]
        const x = item.x
        const y = item.y
        var deltaX = x - agent.agent.x
        var deltaY = y - agent.agent.y

        let direction
        if (Math.abs(deltaX) > Math.abs(deltaY)) {
            direction = deltaX > 0 ? 'right' : 'left'
        } else {
            direction = deltaY > 0 ? 'down' : 'up'
        }

        const sprite = agent.agent.list[3]
        sprite.play(`walk-${direction}`)

        // Calculate the distance to the target
        const distance = Phaser.Math.Distance.Between(
            agent.agent.x,
            agent.agent.y,
            x,
            y
        )

        // Calculate the duration based on the distance (you can adjust the speed as needed)
        const duration = distance * 20

        // Create the tween
        scene.tweens.add({
            targets: agent.agent,
            x: x,
            y: y,
            duration: duration,
            onComplete: function () {
                i++
                if (i >= newPath.length) {
                    sprite.play(`idle`, true)
                } else {
                    moveNext() // Move to the next location recursively
                }
            },
        })
    }

    moveNext() // Start moving to the first location
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
            start: 66,
            end: 71,
        }),
        frameRate: 10,
        repeat: -1,
    })

    scene.anims.create({
        key: 'walk-left',
        frames: scene.anims.generateFrameNumbers('player', {
            start: 60,
            end: 65,
        }),
        frameRate: 10,
        repeat: -1,
    })
    scene.anims.create({
        //find correct sprite sheet frames
        key: 'walk-right',
        frames: scene.anims.generateFrameNumbers('player', {
            start: 48,
            end: 53,
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
