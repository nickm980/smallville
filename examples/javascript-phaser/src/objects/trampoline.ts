import { IImageConstructor } from '../interfaces/image.interface'
import { loadAnimations } from '../mechanics'

export class Trampoline extends Phaser.GameObjects.Sprite {
    private isBeingUsed: boolean

    constructor(aParams: IImageConstructor) {
        super(aParams.scene, aParams.x, aParams.y, 'trampoline')
        this.initImage()
        this.loadAnimations(aParams.scene)
        this.setDepth(this.y)
        this.scene.add.existing(this)
        this.scene.physics.add.existing(this)
        this.playAnimation('trampoline-animation')
    }

    private loadAnimations(scene: Phaser.Scene) {
        scene.anims.create({
            key: 'trampoline-animation',
            frames: scene.anims.generateFrameNumbers('trampoline', {
                start: 1,
                end: 3,
            }),
            frameRate: 10,
            repeat: -1,
        })
        scene.anims.create({
            key: 'trampoline-idle',
            frames: scene.anims.generateFrameNumbers('trampoline', {
                start: 1,
                end: 1,
            }),
            frameRate: 10,
            repeat: -1,
        })
    }

    
    private initImage(): void {
        this.setOrigin(0.5, 0.5)
    }

    update(): void {
    }

    toggle(): void {
        if (this.isBeingUsed){
            this.isBeingUsed = false;
            this.playAnimation('trampoline-idle')
        }
        else {
            this.isBeingUsed = !this.isBeingUsed
            this.playAnimation('trampoline-animation')
        }
    }

    private playAnimation(anim: string): void {
        if (this.anims.getName() != anim) {
            this.play(anim)
        }
    }
}
