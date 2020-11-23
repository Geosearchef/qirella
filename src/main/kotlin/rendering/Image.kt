package rendering

import Qirella
import org.w3c.dom.events.Event

class Image(val imageSrc: String) {

    var loaded = false
        private set

    var wrappedImage = org.w3c.dom.Image()

    init {
        wrappedImage.src = imageSrc
        wrappedImage.onload = this::onImageLoaded
    }

    fun onImageLoaded(event: Event) {
        console.log("Image loaded: $imageSrc")
        loaded = true

        Qirella.requestRender()
    }
}