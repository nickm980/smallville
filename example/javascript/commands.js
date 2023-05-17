import {agents} from './mechanics/agents.js'

const commands = [
    "move",
    "ping",
    "ask",
    "say"
]

document.addEventListener('keydown', function (event) {
    if (event.keyCode === 13) {
        var inputValue = event.target.value;
        event.target.value = ''

        if (inputValue == 'ping'){
            alert('pong')
        }

        if (inputValue.startsWith("say")){
            const a = inputValue.split(" ")
            if (a.length < 1) {
                alert('usage: say <message>')
                return
            }
            agents[0].say(a[1])
        }

        if (inputValue.startsWith('move')){
            const a = inputValue.split(" ")
            if (a.length < 1) {
                alert('usage: move <location>')
                return
            }
            agents[0].moveTo(a[1])
        }
    }
})
