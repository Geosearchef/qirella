package util.scenepicker

import Qirella
import org.w3c.dom.events.MouseEvent
import rendering.Image
import rendering.Rendering.ctx
import rendering.color
import rendering.fillRect
import scene.Scene
import scene.SceneInput
import scene.UIManager
import ui.SceneUI
import ui.UIComponent
import util.math.Rectangle
import util.math.Vector


object ScenePickerInput : SceneInput() {
    override fun onMouseDown(event: MouseEvent, isOnUI: Boolean) {}
}

object ScenePickerRenderer : Scene.SceneRenderer {
    override fun render() {}
}

class ScenePickerUI(width: Int, height: Int): SceneUI(width, height) {
    override val TOP_BAR_SIZE: Double get() = height.toDouble()

    val buttonSize = 400.0

    // image loading is executed 2 times, as the image is created in the UI that is faked and then recreated at the correct size
    init {
        addComponent(PickerButton(Image("/picker/qcnPicker.png"), Rectangle((width.toDouble() / 2.0) - buttonSize - 100.0 , (height.toDouble() / 2.0) - buttonSize / 2.0, buttonSize, buttonSize)) {
            Scene.currentScene = Scene.QCN
            Qirella.requestRender()
        })
        addComponent(PickerButton(Image("/picker/zxnPicker.png"), Rectangle((width.toDouble() / 2.0) + 100.0, (height.toDouble() / 2.0) - buttonSize / 2.0 , buttonSize, buttonSize)) {
            Scene.currentScene = Scene.ZXN
            Qirella.requestRender()
        })
    }

    class PickerButton(val image: Image, val rect: Rectangle, val onPress: () -> Unit) : UIComponent(rect) {

        override fun render() {
            ctx.color("#ffffff")
            ctx.fillRect(rect)

            if(image.loaded) {
                ctx.drawImage(image.wrappedImage, rect.x + 50, rect.y + 50, rect.width - 100, rect.height - 100)
            }
        }

        override fun onPressed(mousePosition: Vector, event: MouseEvent) {
            onPress()
        }
    }

    override fun isMouseEventOnUI(mousePosition: Vector): Boolean {
        return true
    }

    override fun onUIPressed(mousePosition: Vector, event: MouseEvent) {
        super.onUIPressed(mousePosition, event)
    }
}

object ScenePickerManager : UIManager {

    var uiInstance = ScenePickerUI(300, 200)

    override fun regenerateUI(width: Int, height: Int) {
        uiInstance = ScenePickerUI(width, height)
    }

    override fun getUI(): SceneUI {
        return uiInstance
    }

    fun init() {}

}