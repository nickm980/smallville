import { createAgent} from '../mechanics'
import { Player } from '../objects/player'
import { Flag } from '../objects/flag'
import { Pigeon } from '../objects/pigeon'
import { Trampoline } from '../objects/trampoline'
import { addLocation } from '../mechanics/locations'

var player: Player

export class GameScene extends Phaser.Scene {
    private player: Player
    private map: Phaser.Tilemaps.Tilemap
    private entities: Phaser.Types.Tilemaps.TiledObject[]

    constructor() {
        super({
            key: 'GameScene',
        })
    }

    preload(): void {
        this.load.html('chat', 'assets/chat.html')
        this.load.tilemapTiledJSON('my_map', 'assets/map.json')
        this.load.image('dialog', 'assets/dailog.png')
        this.load.image(
            'Modern_Exteriors_Complete_Tileset',
            'assets/tilesets/sprites.png'
        )

        this.load.image(
            '24_Ice_Cream_Shop',
            'assets/tilesets/24_Ice_Cream_Shop.png'
        )

        this.load.image(
            '5_Classroom_and_library_16x16',
            'assets/tilesets/5_Classroom_and_library_16x16.png'
        )

        this.load.image('Pigeon_16x16', 'assets/tilesets/Pigeon_16x16.png')

        this.load.image(
            '1_Generic_16x16',
            'assets/tilesets/1_Generic_16x16.png'
        )

        this.load.image(
            'Room_Builder_16x16',
            'assets/tilesets/Room_Builder_16x16.png'
        )

        this.load.image(
            '4_Bedroom_16x16',
            'assets/tilesets/4_Bedroom_16x16.png'
        )

        this.load.image(
            '12_Kitchen_16x16',
            'assets/tilesets/12_Kitchen_16x16.png'
        )

        this.load.image(
            '16_Grocery_store_16x16',
            'assets/tilesets/16_Grocery_store_16x16.png'
        )


        this.load.spritesheet(
            'flag',
            'assets/tilesets/School_Flag_1_16x16.png',
            {
                frameWidth: 48,
                frameHeight: 144,
            }
        )

        this.load.spritesheet(
            'trampoline',
            'assets/tilesets/Trampoline_1.png',
            {
                frameWidth: 48,
                frameHeight: 80,
            }
        )

        this.load.spritesheet('pigeon', 'assets/tilesets/Pigeon_16x16.png', {
            frameWidth: 16,
            frameHeight: 16,
        })

        this.load.spritesheet('adam', 'assets/tilesets/Adam_16x16.png', {
            frameWidth: 16,
            frameHeight: 32,
        })

        this.load.spritesheet('amelia', 'assets/tilesets/Amelia_16x16.png', {
            frameWidth: 16,
            frameHeight: 32,
        })
        this.load.spritesheet('bob', 'assets/tilesets/Bob_16x16.png', {
            frameWidth: 16,
            frameHeight: 32,
        })
        this.load.spritesheet('alex', 'assets/tilesets/Alex_16x16.png', {
            frameWidth: 16,
            frameHeight: 32,
        })
    }
    private active: boolean = false
    createChatBox(): void {
        const chat = this.add.dom(this.scale.width, this.scale.height).createFromCache('chat').setScrollFactor(0, 0)
        chat.setY(this.scale.height - chat.height)
        chat.setX(0)
        chat.setOrigin(0, 0)
        
        this.input.keyboard.on('keydown', function (event: any) {
            if (!this.active && (event.key == 't' || event.key == 'T')) {
                this.scene.input.keyboard.disableGlobalCapture()
                document.getElementById('chat').focus()
            }
        })
    }

    spawnAgents(): void {
        function createAgents(scene: Phaser.Scene) {
            createAgent({
                skin: 'bob',
                scene: scene,
                name: 'Medina',
                location: 'ice cream shop: counter',
                activity: 'sleeping',
                memories: [
                    'Medina is the mother of John',
                    'Medina loves to cook',
                    'Medina hates to be around campfires',
                ],
            })

            createAgent({
                skin: 'alex',
                scene: scene,
                name: 'John',
                location: 'central park: bench',
                activity: 'watching television',
                memories: [
                    'John is the son of Medina',
                    'John loves to be alone inside the Green House',
                ],
            })
        }

        createAgents(this)
    }

    init(): void { }

    loadTilemap(): void {
        function loadTiles(map: Phaser.Tilemaps.Tilemap) {
            let result = []
            result.push(
                map.addTilesetImage('Modern_Exteriors_Complete_Tileset')
            )
            result.push(map.addTilesetImage('Room_Builder_16x16'))
            result.push(map.addTilesetImage('24_Ice_Cream_Shop'))
            result.push(map.addTilesetImage('5_Classroom_and_library_16x16'))
            result.push(map.addTilesetImage('1_Generic_16x16'))
            result.push(map.addTilesetImage('4_Bedroom_16x16'))
            result.push(map.addTilesetImage('12_Kitchen_16x16'))
            result.push(map.addTilesetImage('16_Grocery_store_16x16'))

            return result
        }

        function loadTilemap(scene: any) {
            const map: Phaser.Tilemaps.Tilemap = scene.make.tilemap({
                key: 'my_map',
            })

            const tilesets = loadTiles(map)

            const layers = [
                'sidewalks',
                'parked_busses',
                'builds_under',
                'buildings',
                'sidewalk_objects',
                'school_objects_2',
                'sidewalk_signs',
                'building_objects',
                'cars',
                'interior_floors',
                'interior_walls',
                'interior_objects',
                'interior_decorations',
            ]

            let i = 0
            for (const layerName of layers) {
                i++
                const layer = map.createLayer(layerName, tilesets)
                layer.setCollisionByProperty({ collision: true })
                layer.setDepth(-99999 + i)
            }

            return map
        }

        this.map = loadTilemap(this)
    }

    spawnObjects(): void {
        var overlapObjects = this.map.getObjectLayer('walls')['objects'] //my Object layer was called Overlap
        let entities: Phaser.Types.Tilemaps.TiledObject[] = []

        function createObjects(name: string, callback: Function) {
            const objects = overlapObjects.filter((obj) => obj.name == name)

            for (const object of objects) {
                callback(object)
                entities.push(object)
            }
        }

        createObjects('pigeon', (obj: Phaser.Types.Tilemaps.TiledObject) => {
            new Pigeon({
                scene: this,
                x: obj.x,
                y: obj.y,
            })
        })


        createObjects('flag', (obj: Phaser.Types.Tilemaps.TiledObject) => {
            new Flag({
                scene: this,
                x: obj.x,
                y: obj.y,
            })
        })

        createObjects('trampoline', (obj: Phaser.Types.Tilemaps.TiledObject) => {
            const trampoline = new Trampoline({
                scene: this,
                x: obj.x,
                y: obj.y,
            })
        })

        this.entities = entities
    }

    create(): void {
        function createNavMesh(scene: any, map: Phaser.Tilemaps.Tilemap) {
            const objects = map.getObjectLayer('bounds')
            scene.navMesh = scene.navMeshPlugin.buildMeshFromTiled(
                'mesh',
                objects
            )
        }

        this.loadTilemap()
        this.createLocations()
        createNavMesh(this, this.map)
        this.spawnAgents()
        this.createChatBox()
        this.spawnObjects()

        player = (this.player = new Player({
            scene: this,
            texture: 'bob',
            x: 600,
            y: 390,
        }))

        this.initPhysics()
    }

    initPhysics(){
        function enableCollisions(layer: string, map: Phaser.Tilemaps.Tilemap, group: Phaser.Physics.Arcade.StaticGroup, offsetY: any = 0) {
            map.getObjectLayer(layer)['objects'].forEach((object: any) => {
                let obj = group.create(
                    object.x,
                    object.y,
                    null,
                    null,
                    false
                )

                obj.setScale(object.width / 32, object.height / 32)
                obj.setOrigin(0)
                obj.body.width = object.width
                obj.body.height = object.height - offsetY
                obj.data = object.properties
            })
        }

        let overlapObjectsGroup = this.physics.add.staticGroup({})
        let collisionObjectsGroup = this.physics.add.staticGroup({})

        enableCollisions('bounds', this.map, collisionObjectsGroup, 16)
        enableCollisions('walls', this.map, overlapObjectsGroup)

        overlapObjectsGroup.refresh()
        collisionObjectsGroup.refresh()

        this.physics.add.collider(
            this.player,
            collisionObjectsGroup
        )

        this.physics.add.overlap(
            this.player,
            overlapObjectsGroup,
            (player: any, box: any) => {
                const data = box.data
                if (data != undefined) {
                    data.forEach((item: any) => {
                        if (item.name == 'teleport') {
                            player.x = data[1].value
                            player.y = data[2].value
                            console.log('tele: ' + player.x + ', ' + player.y)
                        }
                    })
                }
            },
            null,
            this
        )

        const walls = this.map.getObjectLayer('walls')

        walls.objects.forEach((object: any) => {
            object.visible = false
        })
    }
    
    createLocations(){
        const locations = this.map.getObjectLayer('locations')

        for (const location of locations.objects){
            addLocation(location.name, location.x, location.y)   
            console.debug('adding location - ' + location.name)
        }
    }

    update(): void {
        this.player.update()
    }
}

export {player}