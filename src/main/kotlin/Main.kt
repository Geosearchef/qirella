import kotlin.browser.document

object Main {

}

fun main() {
    console.log("Loading qirella")

    document.addEventListener("DOMContentLoaded", { Composer.init() })
//    Rendering.render()
}