package zxn.calculus.ruleNodes

import util.math.Vector
import zxn.network.ZXNode

abstract class RuleNode : ZXNode(Vector()) {


    override fun isPosOnElement(v: Vector): Boolean {
        throw IllegalStateException("Can't check position, this is a rule node")
    }
}