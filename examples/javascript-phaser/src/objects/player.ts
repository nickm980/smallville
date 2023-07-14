import { IPlayerConstructor } from '../interfaces/player.interface'
import { loadAnimations } from '../mechanics'
import { updateHTMLElement } from '../mechanics/dom'

export class Player extends Phaser.GameObjects.Sprite {
    private cursors: Phaser.Types.Input.Keyboard.CursorKeys
    private walkingSpeed: number
    private keyA: Phaser.Input.Keyboard.Key
    private keyS: Phaser.Input.Keyboard.Key
    private keyD: Phaser.Input.Keyboard.Key
    private keyW: Phaser.Input.Keyboard.Key
    private cameraDolly: any

    constructor(aParams: IPlayerConstructor) {
        super(aParams.scene, aParams.x, aParams.y, aParams.texture)

        this.initVariables()
        this.initImage()
        this.initInput()
        this.scene.add.existing(this)
        this.scene.physics.add.existing(this)
        this.cameraDolly = new Phaser.Geom.Point(this.x, this.y)
        this.scene.cameras.main.startFollow(this.cameraDolly)
    }

    private initVariables(): void {
        this.walkingSpeed = 100
    }

    private initImage(): void {
        this.setOrigin(0.5, 0.5)
        loadAnimations(this.scene, 'player', 'bob')
    }

    private initInput(): void {
        this.keyA = this.scene.input.keyboard.addKey(
            Phaser.Input.Keyboard.KeyCodes.A
        )
        this.keyS = this.scene.input.keyboard.addKey(
            Phaser.Input.Keyboard.KeyCodes.S
        )
        this.keyD = this.scene.input.keyboard.addKey(
            Phaser.Input.Keyboard.KeyCodes.D
        )
        this.keyW = this.scene.input.keyboard.addKey(
            Phaser.Input.Keyboard.KeyCodes.W
        )
        this.cursors = this.scene.input.keyboard.createCursorKeys()

        // enable agent teleportation

        for (const element of document.getElementsByClassName('teleport')) {
            console.log(element)
            element.addEventListener('onclick', (event)=>{
                console.log('CLICK')
            })
        }
    }

    update(): void {
        this.handleInput()
        updateHTMLElement({name: 'Medina', location: 'loc', activity: 'activity'})
        // Move the camera dolly in round pixels
        this.cameraDolly.x = Math.floor(this.x)
        this.cameraDolly.y = Math.floor(this.y)

        this.setDepth(this.y)
    }

    private previousDirection: string

    private handleInput(): void {
        this.body.velocity.x = 0
        this.body.velocity.y = 0

        if (this.cursors.right.isDown || this.keyD.isDown) {
            this.body.velocity.x = this.walkingSpeed
            this.playAnimation('player-walk-right')
            this.previousDirection = 'right'
        } else if (this.cursors.left.isDown || this.keyA.isDown) {
            this.body.velocity.x = -this.walkingSpeed
            this.playAnimation('player-walk-left')
            this.previousDirection = 'left'
        } else if (this.cursors.up.isDown || this.keyW.isDown) {
            this.body.velocity.y = -this.walkingSpeed
            this.playAnimation('player-walk-up')
            this.previousDirection = 'up'
        } else if (this.cursors.down.isDown || this.keyS.isDown) {
            this.body.velocity.y = this.walkingSpeed
            this.playAnimation('player-walk-down')
            this.previousDirection = 'down'
        } else {
            if (
                this.previousDirection == undefined ||
                this.previousDirection == 'down'
            ) {
                this.playAnimation('player-idle')
            }
            else {
                this.playAnimation(`player-idle-${this.previousDirection}`)
            }

            this.body.velocity.x = 0
            this.body.velocity.y = 0
        }
    }

    private playAnimation(anim: string): void {
        if (this.anims.getName() != anim) {
            this.play(anim)
        }
    }

    teleport(x: integer, y: integer){
        this.setX(x)
        this.setY(y)
    }
}
