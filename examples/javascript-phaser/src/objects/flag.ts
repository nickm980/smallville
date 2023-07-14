import { IImageConstructor } from '../interfaces/image.interface'
import { loadAnimations } from '../mechanics'

export class Flag extends Phaser.GameObjects.Sprite {
    constructor(aParams: IImageConstructor) {
        super(aParams.scene, aParams.x, aParams.y, 'flag')
        this.initImage()
        this.loadAnimations(aParams.scene)
        this.setDepth(this.y)
        this.scene.add.existing(this)
        this.scene.physics.add.existing(this)
        this.playAnimation('flag-animation')
    }

    private loadAnimations(scene: Phaser.Scene) {
        scene.anims.create({
            key: 'flag-animation',
            frames: scene.anims.generateFrameNumbers('flag', {
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
