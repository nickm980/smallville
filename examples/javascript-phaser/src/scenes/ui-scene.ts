export class UIScene extends Phaser.Scene {
	constructor() {
		super({ key: 'UIScene', active: true })
	}

	create() {
		function setupButtons(scene: Phaser.Scene) {
			let updateStateButton = scene.add.dom(70, 30).createFromHTML(
				/*html*/
				`<button
                        class="nes-btn is-primary"
                        id="smallville--next"
                    >                    
                        Update State
                    </button>`
			)
			updateStateButton.setScrollFactor(0, 0)
			
			let moreButton = scene.add.dom(800 - 30, 30).createFromHTML(
				/*html*/
				`<button class="nes-btn is-secondary">⋮</button>`
			)



			scene.add
				.dom(0, 0)
				.createFromHTML(
					/*html*/
					`<div id="settings" class="modal-container display-none" style="width: ${800}px; height: ${600}px">
                        <div class="modal">
                            <div class="modal-header">
                                <h3>Settings</h3>
                                <button id="close-modal">✕</button>
                            </div>
                            <div class="modal-body">
                                Timestep (mins):
                                <input type="number" id="timestep" name="timestep"
                                min="1" value="1">
                                <button 
                                    id="timestep-submit"
                                   
                                >
                                Submit
                                </button>
                            </div>
                        </div>
                    </div>`
				)
				.setOrigin(0)
				.setPosition(0, 0)


			document
				.getElementById('timestep-submit')
				.addEventListener('click', (e) => {
					let timestepToSend = parseInt(document.getElementById('timestep').innerText)
					fetch('http://localhost:8080/timestep', {
						method: 'POST',
						mode: 'cors', // no-cors, *cors, same-origin
						cache: 'no-cache', // *default, no-cache, reload, force-cache, only-if-cached
						headers: {
							'Content-Type': 'application/json',
						},
						body: JSON.stringify({ numOfMinutes: timestepToSend }),
					})
				})

			let settings = document.getElementById('settings')


			moreButton.node.addEventListener('click', e => {
				settings.classList.remove('display-none')
			})

			document
				.getElementById('close-modal')
				.addEventListener('click', (e) => {
					settings.classList.add('display-none')
				})

			return [updateStateButton]
		}

		setupButtons(this)
	}
}