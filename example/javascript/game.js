import { createAgent, enableCameraControlls, moveAgent, loadAnimations } from './mechanics/index.js'

var config = {
   type: Phaser.AUTO,
   physics: {
      default: 'arcade',
      arcade: {
         gravity: { y: 200 }
      }
   },
   scale: {
      width: 800,
      height: 500,
   },
   scene: {
      preload: preload,
      create: create,
   }
};

const game = new Phaser.Game(config);

function preload() {
   this.load.tilemapTiledJSON('my_map', 'assets/map.json');
   this.load.image('my_tileset', 'assets/sprites.png');
   this.load.spritesheet('player', 'assets/Adam_16x16.png', {
      frameConfig: {
      }, frameWidth: 16, frameHeight: 32
   });
}


function create() {
   function loadTilemap(scene) {
      const map = scene.make.tilemap({ key: 'my_map' });
      console.log(map)
      const tileset = map.addTilesetImage('my_tileset');
      console.log(tileset)
      map.createLayer('ground', tileset);
      map.createLayer('upperground', tileset);
      map.createLayer('structs2', tileset);
      map.createLayer('structs', tileset);
   }

   enableCameraControlls(game, this);
   loadTilemap(this);
   loadAnimations(this)
   createAgent({ scene: this, name: "Agent #1", location: "Red House", activity: "Gardening plants" })
   createAgent({ scene: this, name: "Agent #2", location: "Campfire", activity: "Sitting by campfire" })
   moveAgent({ name: "Agent #1", location: "Green House" })
}

export { moveAgent }