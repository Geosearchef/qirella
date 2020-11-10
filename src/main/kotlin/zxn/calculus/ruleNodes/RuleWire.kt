package zxn.calculus.ruleNodes

import zxn.network.Wire
import zxn.network.ZXNode

class RuleWire(first: ZXNode, second: ZXNode, val multiplicity: Int = 1) : Wire(first, second) {

    companion object {
        val ANY_MULTIPLICITY = 255
    }


}