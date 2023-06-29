import { getCoordinates } from './locations'
import { updateHTMLElement } from './dom'

var agents: Agent[] = []

class Agent {
    private name: string
    private agent: Phaser.GameObjects.Container
    private text: Phaser.GameObjects.Text
    private emoji: string
    private activity: string
    private memories: string[]
    private location: string

    constructor({
        name,
        agent,
        text,
        activity,
        location,
        memories,
    }: {
        name: string
        agent: Phaser.GameObjects.Container
        text: Phaser.GameObjects.Text
        activity: string
        location: string
        memories: string[]
    }) {
        this.name = name
        this.agent = agent
        this.text = text
        this.emoji = '?'
        this.activity = activity
        this.memories = memories
        this.location = location
    }

    moveTo(locationName: string) {
        const coords = getCoordinates(locationName)

        this.agent.setX(coords.x)
        this.agent.setY(coords.y)

        console.log(this.name + ' moved to ' + locationName)
    }

    say(message: string) {
        this.text.setText(this.name[0] + ': ' + this.emoji)
    }

    setEmoji(emoji: string) {
        if (emoji === undefined || emoji == null) {
            this.emoji = '?'
            this.say(this.activity)
            return
        }

        this.emoji = emoji
        this.say(this.activity)
    }

    setActivity(activity: string) {
        console.log(this.name + ' activity')
        this.activity = activity
        this.say(activity)
    }

    getAgent() {
        return this.agent
    }

    getName() {
        return this.name
    }

    getX() {
        return this.agent.x
    }

    getY() {
        return this.agent.y
    }
}

var nMesh: any

function updateAgent({
    name,
    location,
    activity,
    emoji,
}: {
    name: string
    location: string
    activity: string
    emoji: string
}) {
    const agent = agents.find((agent) => {
        return name == agent.getName()
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

function createAgent({
    scene,
    name,
    location,
    activity,
    memories,
}: {
    scene: Phaser.Scene
    name: string
    location: string
    activity: string
    memories: string[]
}) {
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
        backgroundColor: '#00000',
    })

    var activityText = scene.add.text(0, 0, "Game hasn't started!", {
        font: '16px Courier New',
        backgroundColor: '#00000',
    })
    // dialog.addChild(text)
    // player.addChild(dialog)
    activityText.visible = false
    player.setInteractive()

    player.on('pointerover ', function () {
        // agent.reset()
    })

    player.on('pointerout', function () {
        // agent.visible = false
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
    agent.getAgent().setX(coords.x)
    agent.getAgent().setY(coords.y)
    moveAgent({ scene: scene, name: name, location: location })
}

function moveAgent({
    scene,
    name,
    location,
}: {
    scene: any
    name: string
    location: string
}) {
    const agent = agents.find((agent) => name == agent.getName())
    const coords = getCoordinates(location)

    if (!agent) {
        console.error(`No agent found with name ${name}`)
        return
    }

    const path = scene.navMesh.findPath(
        { x: agent.getX(), y: agent.getY() },
        { x: coords.x, y: coords.y }
    )

    let navMeshPolys = scene.navMesh.navMesh.getPolygons()
    let newPath: any = []

    if (path == undefined) {
        console.error('no path to object')
        return
    }

    path.forEach((point: any) => {
        navMeshPolys.forEach((poly: any) => {
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
        var deltaX = x - agent.getX()
        var deltaY = y - agent.getY()

        let direction: string

        if (Math.abs(deltaX) > Math.abs(deltaY)) {
            direction = deltaX > 0 ? 'right' : 'left'
        } else {
            direction = deltaY > 0 ? 'down' : 'up'
        }

        const sprite: any = agent.getAgent().list[3]
        sprite.play(`walk-${direction}`)

        // Calculate the distance to the target
        const distance = Phaser.Math.Distance.Between(
            agent.getX(),
            agent.getY(),
            x,
            y
        )

        // Calculate the duration based on the distance (you can adjust the speed as needed)
        const duration = distance * 20

        // Create the tween
        scene.tweens.add({
            targets: agent.getAgent(),
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

function loadAnimations(scene: Phaser.Scene) {
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
