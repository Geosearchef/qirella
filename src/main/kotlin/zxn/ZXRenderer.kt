package zxn

import rendering.Rendering.ctx
import rendering.color
import rendering.fillRect
import rendering.setIdentityMatrix
import scene.Scene
import util.math.Vector

object ZXRenderer : Scene.SceneRenderer {



    override fun render() {
        ctx.scale(ZXComposer.scale, ZXComposer.scale)
        ctx.translate(ZXComposer.offset.x, ZXComposer.offset.y)

        ctx.color("gray")
        ctx.fillRect(Vector(100.0, 100.0), 300.0, 300.0)

        ctx.setIdentityMatrix()
    }
}