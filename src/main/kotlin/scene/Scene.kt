package scene

import Qirella
import qcn.QCComposer
import qcn.QCInput
import qcn.rendering.QCRenderer
import util.scenepicker.ScenePickerInput
import util.scenepicker.ScenePickerManager
import util.scenepicker.ScenePickerRenderer
import zxn.ZXComposer
import zxn.ZXInput
import zxn.ZXRenderer

enum class Scene(val input: SceneInput, /*val update: SceneUpdate,*/ val renderer: SceneRenderer, val uiManager: UIManager, val initFunction: () -> Unit) {
    PICKER(ScenePickerInput, ScenePickerRenderer, ScenePickerManager, initFunction = { ScenePickerManager.init() }),
    QCN(QCInput, QCRenderer, QCComposer, initFunction = { QCComposer.init() }),
    ZXN(ZXInput, ZXRenderer, ZXComposer, initFunction = { ZXComposer.init() });

    companion object {
        var currentScene: Scene = PICKER

        fun switchScene() {
            currentScene = when(currentScene) {
                QCN -> ZXN
                ZXN -> QCN
                PICKER -> QCN
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