package scene

import ui.SceneUI

interface UIManager {
    fun regenerateUI(width: Int, height: Int)
    fun getUI() : SceneUI
}