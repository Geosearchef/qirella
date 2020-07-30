package rendering

import org.w3c.dom.HTMLCanvasElement

fun HTMLCanvasElement.resizeCanvas() {
    val displayWidth = this.offsetWidth
    val displayHeight = this.offsetHeight

    if(displayWidth != this.width || displayHeight != this.height) {
        this.style.margin = "0"
        this.style.border = "0"

        this.width = displayWidth
        this.height = displayHeight

        println("Resized canvas to $displayWidth x $displayHeight")
    }
}