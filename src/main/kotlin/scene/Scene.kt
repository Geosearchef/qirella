package scene

import Qirella
import org.w3c.dom.events.KeyboardEvent
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.events.WheelEvent
import qcn.QCComposer
import qcn.QCInput
import qcn.rendering.QCRenderer
import zxn.ZXInput
import zxn.ZXRenderer

enum class Scene(val input: SceneInput, /*val update: SceneUpdate,*/ val renderer: SceneRenderer, val initFunction: () -> Unit) {
    QCN(QCInput, QCRenderer, initFunction = { QCComposer.init() }),
    ZXN(ZXInput, ZXRenderer, initFunction = { zxn.ZXComposer.init() });

    companion object {
        var currentScene: Scene = ZXN

        fun switchScene() {
            currentScene = when(currentScene) {
                QCN -> ZXN
                ZXN -> QCN
            }
            Qirella.requestRender()
        }
    }



    interface SceneInput {
        fun onMouseMove(event: MouseEvent) {}
        fun onMouseDown(event: MouseEvent) {}
        fun onMouseUp(event: MouseEvent) {}
        fun onMouseWheel(event: WheelEvent) {}
        fun onKeyDown(event: KeyboardEvent) {}
        fun onKeyUp(event: KeyboardEvent) {}
    }
//    interface SceneUpdate {
//        fun update()
//    }
    interface SceneRenderer {
        fun render()
    }
}