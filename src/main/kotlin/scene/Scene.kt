package scene

import org.w3c.dom.events.MouseEvent
import org.w3c.dom.events.WheelEvent
import qcn.QCComposer
import qcn.QCInput
import qcn.rendering.QCRendering

enum class Scene(val input: SceneInput, /*val update: SceneUpdate,*/ val renderer: SceneRenderer, val initFunction: () -> Unit) {
    QCN(QCInput, QCRendering, initFunction = { QCComposer.init() });

    companion object {
        var currentScene: Scene = QCN
    }





    interface SceneInput {
        fun onMouseMove(event: MouseEvent) {}
        fun onMouseDown(event: MouseEvent) {}
        fun onMouseUp(event: MouseEvent) {}
        fun onMouseWheel(event: WheelEvent) {}
    }
//    interface SceneUpdate {
//        fun update()
//    }
    interface SceneRenderer {
        fun render()
    }
}