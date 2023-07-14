import { updateHTMLElement } from './dom'
import { getLocation } from './locations'

var agents: Agent[] = []

class Agent {
    private name: string
    private agent: Phaser.GameObjects.Container
    private text: Phaser.GameObjects.Text
    private emoji: string
    private activity: string
    private location: string
    private memories: string[]

    constructor({
        name,
        agent,
        text,
        activity,
        location,
        memories
    }: {
        name: string
        agent: Phaser.GameObjects.Container
        text: Phaser.GameObjects.Text
        activity: string
        location: string,
        memories: string[]
    }) {
        this.name = name
        this.agent = agent
        this.text = text
        this.emoji = '?'
        this.activity = activity
        this.location = location
        this.memories = memories
    }

    teleportTo(x: integer, y: integer) {
        this.agent.setX(x)
        this.agent.setY(y)

        console.log(this.name + ' moved to ' + x + ", " + y)
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
    moveAgent({ name: name, locationName: location })
    agent.setEmoji(emoji)
    agent.setActivity(activity)
}

function createAgent({
    scene,
    name,
    location,
    activity,
    skin,
    memories
}: {
    scene: Phaser.Scene
    name: string
    location: string
    activity: string
    memories: string[]
    skin: string
}) {
    if (nMesh == undefined) {
        nMesh = scene
    }
    loadAnimations(scene, name, skin)

    updateHTMLElement({ name, location, activity })
    var player = scene.add.sprite(0, 0, name)
    var group = scene.add.container()

    var dialog = scene.add.sprite(25, -30, 'dialog')
    dialog.setScale(0.75)

    var emoji = scene.add.text(0, -42, name[0] + ': ?', {
        font: '16px Courier New',
        backgroundColor: '#fffff',
    })

    var activityText = scene.add.text(0, 0, "Game hasn't started!", {
        font: '16px Courier New',
        backgroundColor: '#fffff',
    })
    // dialog.addChild(text)
    // player.addChild(dialog)
    activityText.visible = false

    const graphics = scene.add.graphics({ lineStyle: { color: 0xff0000 } });

    const circle = new Phaser.Geom.Circle(0, 0, 100);
    graphics.strokeCircleShape(circle);

    group.add(dialog)
    group.add(graphics)
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
        memories: memories
    })

    agents.push(agent)
    player.play(name + '-idle')

    console.log('[Agent] Created a new agent ' + name)
    moveAgent({ name: name, locationName: location })
}

function moveAgent({
    name,
    locationName,
}: {
    name: string
    locationName: string
}) {
    const agent = agents.find((agent) => name == agent.getName())
    const location = getLocation(locationName)

    if (agent === undefined) {
        console.error(`No agent found with name ${name}`)
        return
    }

    if (location === undefined) {
        console.error("location not found")
        return
    }

    console.log(location.getX(), location.getY())
    console.log(location.getName())
    agent.teleportTo(location.getX(), location.getY())
}

function loadAnimations(scene: Phaser.Scene, name: string, skin: string) {
    scene.anims.create({
        key: name + '-idle',
        frames: scene.anims.generateFrameNumbers(skin, {
            start: 42,
            end: 47,
        }),
        frameRate: 10,
        repeat: -1,
    })
    scene.anims.create({
        key: name + '-idle-left',
        frames: scene.anims.generateFrameNumbers(skin, {
            start: 36,
            end: 41,
        }),
        frameRate: 10,
        repeat: -1,
    })
    scene.anims.create({
        key: name + '-idle-up',
        frames: scene.anims.generateFrameNumbers(skin, {
            start: 30,
            end: 35,
        }),
        frameRate: 10,
        repeat: -1,
    })

    scene.anims.create({
        key: name + '-idle-right',
        frames: scene.anims.generateFrameNumbers(skin, {
            start: 24,
            end: 29,
        }),
        frameRate: 10,
        repeat: -1,
    })

    scene.anims.create({
        key: name + '-walk-down',
        frames: scene.anims.generateFrameNumbers(skin, {
            start: 66,
            end: 71,
        }),
        frameRate: 10,
        repeat: -1,
    })

    scene.anims.create({
        key: name + '-walk-left',
        frames: scene.anims.generateFrameNumbers(skin, {
            start: 60,
            end: 65,
        }),
        frameRate: 10,
        repeat: -1,
    })
    scene.anims.create({
        key: name + '-walk-right',
        frames: scene.anims.generateFrameNumbers(skin, {
            start: 48,
            end: 53,
        }),
        frameRate: 10,
        repeat: -1,
    })
    scene.anims.create({
        key: name + '-walk-up',
        frames: scene.anims.generateFrameNumbers(skin, {
            start: 54,
            end: 59,
        }),
        frameRate: 10,
        repeat: -1,
    })
}

export { updateAgent, createAgent, agents, moveAgent, loadAnimations }
