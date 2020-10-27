package scene

import Qirella
import qcn.QCComposer
import qcn.QCInput
import qcn.rendering.QCRenderer
import zxn.ZXComposer
import zxn.ZXInput
import zxn.ZXRenderer

enum class Scene(val input: SceneInput, /*val update: SceneUpdate,*/ val renderer: SceneRenderer, val uiManager: UIManager, val initFunction: () -> Unit) {
    QCN(QCInput, QCRenderer, QCComposer, initFunction = { QCComposer.init() }),
    ZXN(ZXInput, ZXRenderer, ZXComposer, initFunction = { zxn.ZXComposer.init() });

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

//    interface SceneUpdate {
//        fun update()
//    }
    interface SceneRenderer {
        fun render()
    }
}