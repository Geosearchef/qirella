package rendering

import Qirella
import org.w3c.dom.events.Event

// cards also supports mipmapping: https://github.com/Geosearchef/cards/blob/master/src/jsMain/kotlin/framework/rendering/Image.kt

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