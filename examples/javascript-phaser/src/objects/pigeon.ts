import { IImageConstructor } from '../interfaces/image.interface'
import { loadAnimations } from '../mechanics'

export class Pigeon extends Phaser.GameObjects.Sprite {
    constructor(aParams: IImageConstructor) {
        super(aParams.scene, aParams.x, aParams.y, 'flag')
        this.initImage()
        this.loadAnimations(aParams.scene)
        this.scene.add.existing(this)
        this.scene.physics.add.existing(this)
        this.playAnimation('pigeon-idle')
    }

    private loadAnimations(scene: Phaser.Scene) {
        scene.anims.create({
            key: 'pigeon-idle',
            frames: scene.anims.generateFrameNumbers('pigeon', {
                start: 1,
                end: 5,
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

    private playAnimation(anim: string): void {
        if (this.anims.getName() != anim) {
            this.play(anim)
        }
    }
}
