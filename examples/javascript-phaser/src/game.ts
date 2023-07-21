import 'phaser';
import { GameConfig } from './config';
import { Smallville } from 'smallville'

export class Game extends Phaser.Game {
    constructor(config: Phaser.Types.Core.GameConfig) {
        super(config);
    }
}

window.addEventListener('load', async () => {
    const game = new Game(GameConfig);
    const successful = await smallville.init()

    if (successful) {
        createLocations()
    }

    async function createLocations() {
        /*****************************************
         *                                       *
         * Create locations and stateful objects *
         *                                       *
         *****************************************/
        await smallville.createLocation({
            name: 'Green House: Ball',
        })

        await smallville.createLocation({
            name: 'Red House',
        })

        await smallville.createLocation({
            name: 'Forest',
        })
    }
});


const smallville = new Smallville({
    host: 'http://localhost:8080',
    stateHandler: (state) => {
        console.log(state)
        if (state.agents == undefined) {
            console.log('No connection to server')
            return
        }
        console.log(state)
    },
})