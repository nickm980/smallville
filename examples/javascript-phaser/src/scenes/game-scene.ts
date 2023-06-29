import {
    createAgent,
    enableCameraControlls,
    moveAgent,
    loadAnimations,
} from '../mechanics'

export class GameScene extends Phaser.Scene {
    private background: Phaser.GameObjects.Image
    private coinsCollectedText: Phaser.GameObjects.Text
    private collectedCoins: number

    constructor() {
        super({
            key: 'GameScene',
        })
    }

    preload(): void {
        this.load.tilemapTiledJSON('my_map', 'assets/map.json')
        this.load.image('dialog', 'assets/dailog.png')
        this.load.image('my_tileset', 'assets/sprites.png')
        this.load.spritesheet('player', 'assets/Adam_16x16.png', {
            frameWidth: 16,
            frameHeight: 32,
        })
    }

    init(): void {
        this.collectedCoins = 0
    }

    create(): void {
        function loadTilemap(scene: any) {
            const map = scene.make.tilemap({ key: 'my_map' })
            const tileset = map.addTilesetImage('my_tileset')
            const ground = map.createLayer('ground', tileset)

            map.createLayer('upperground', tileset)
            map.createLayer('structs2', tileset)
            map.createLayer('structs', tileset)
            const wallLayer = map.createLayer('wall', tileset)
            const objectLayer = map.getObjectLayer('walls')
            wallLayer.setCollisionByProperty({ collides: true })

            scene.navMesh = scene.navMeshPlugin.buildMeshFromTiled(
                'mesh',
                objectLayer,
                [wallLayer]
            )

            scene.navMesh.debugDrawMesh({
                drawCentroid: true,
                drawBounds: true,
                drawNeighbors: true,
                drawPortals: true,
            })
        }

        enableCameraControlls(this.game, this)
        loadTilemap(this)
        loadAnimations(this)

        createAgent({
            scene: this,
            name: 'Medina',
            location: 'Red House: Bedroom',
            activity: 'sleeping',
            memories: [
                'Medina is the mother of John',
                'Medina loves to cook',
                'Medina hates to be around campfires',
            ],
        })

        createAgent({
            scene: this,
            name: 'John',
            location: 'Green House: Bedroom',
            activity: 'watching television',
            memories: [
                'John is the son of Medina',
                'John loves to be alone inside the Green House',
            ],
        })
    }

    update(): void {
        // update objects
        // this.player.update()
    }
}
