var camera;

function enableCameraControlls(game, scene) {
    scene.cameras.main.setRoundPixels(true)
    camera = scene.cameras.main;

    // create a variable to store the previous mouse x and y positions
    let prevX = 0;
    let prevY = 0;
    let isMouseDown = false;

    scene.input.on('pointerdown', () => {
        prevX = scene.input.mousePointer.x;
        prevY = scene.input.mousePointer.y;
        isMouseDown = true;
    });

    scene.input.on('pointerup', () => {
        isMouseDown = false;
    });
    // add a pointer move listener
    scene.input.on('pointermove', () => {
        const debug = document.getElementById("smallville--debug-mouse")
        debug.innerHTML = `(${Math.round(scene.input.mousePointer.worldX)}, ${Math.round(scene.input.mousePointer.worldY)})`;


        if (isMouseDown) { // only pan the camera when mouse is down
            const deltaX = scene.input.mousePointer.x - prevX;
            const deltaY = scene.input.mousePointer.y - prevY;

            scene.cameras.main.scrollX -= deltaX;
            scene.cameras.main.scrollY -= deltaY;

            prevX = scene.input.mousePointer.x;
            prevY = scene.input.mousePointer.y;
        }
    });

    const zoomSpeed = 0.001;
    const minZoom = .5;
    const maxZoom = 1;
    let zoomLevel = 1;

    scene.input.on('wheel', (pointer, gameObjects, deltaX, deltaY, deltaZ) => {

        zoomLevel -= deltaY * zoomSpeed;

        zoomLevel = Phaser.Math.Clamp(zoomLevel, minZoom, maxZoom);

        camera.setZoom(zoomLevel);
    });
}

export { enableCameraControlls }

