import input.Input
import kotlinx.browser.document
import kotlinx.browser.window
import scene.Scene

fun main() {
    console.log("Loading qirella")

    document.addEventListener("DOMContentLoaded", {
        Scene.values().forEach { it.initFunction() }
        Input.init(Qirella.canvas)

        window.requestAnimationFrame(Qirella::animationFrame)
    })
}