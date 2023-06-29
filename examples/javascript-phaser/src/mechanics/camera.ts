var camera: any

function enableCameraControlls(
    game: Phaser.Game,
    scene: Phaser.Scene,
) {
    scene.cameras.main.setRoundPixels(true)
    camera = scene.cameras.main

    // create a variable to store the previous mouse x and y positions
    let prevX = 0
    let prevY = 0
    let isMouseDown = false

    scene.input.on('pointerdown', () => {
        prevX = scene.input.mousePointer.x
        prevY = scene.input.mousePointer.y
        isMouseDown = true
    })

    scene.input.on('pointerup', () => {
        isMouseDown = false
    })
    // add a pointer move listener
    scene.input.on('pointermove', () => {
        if (isMouseDown) {
            // only pan the camera when mouse is down
            const deltaX = scene.input.mousePointer.x - prevX
            const deltaY = scene.input.mousePointer.y - prevY

            scene.cameras.main.scrollX -= deltaX
            scene.cameras.main.scrollY -= deltaY

            prevX = scene.input.mousePointer.x
            prevY = scene.input.mousePointer.y
        }
    })

    const zoomSpeed = 0.002
    const minZoom = 0.5
    const maxZoom = 1
    let zoomLevel = 1

    scene.input.on(
        'wheel',
        (
            pointer: any,
            gameObjects: any,
            deltaX: any,
            deltaY: any,
            deltaZ: any
        ) => {
            zoomLevel -= deltaY * zoomSpeed
            zoomLevel = Phaser.Math.Clamp(zoomLevel, minZoom, maxZoom)

            camera.setZoom(zoomLevel)
        }
    )
}

export { enableCameraControlls }
