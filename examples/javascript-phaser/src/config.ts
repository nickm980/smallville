import { GameScene } from './scenes/game-scene'
import { UIScene } from './scenes/ui-scene'
import PhaserNavMeshPlugin from "phaser-navmesh";

export const GameConfig: Phaser.Types.Core.GameConfig = {
    type: Phaser.AUTO,
    physics: {
        default: 'arcade',
        arcade: {
            gravity: { y: 0 },
        },
        matter: {
            debug: false,
            gravity: { y: 0 },
        }
    },
    scale: {
        width: 800,
        height: 500,
    },
    plugins: {
        scene: [
            {
                key: 'PhaserNavMeshPlugin',
                plugin: PhaserNavMeshPlugin,
                mapping: 'navMeshPlugin',
                start: true,
            },
        ],
    },
    scene: [GameScene, UIScene],
    parent: 'phaser-container',
    antialias: true,
    pixelArt: true,
    dom: {
        createContainer: true,
    },
    roundPixels: true
}
