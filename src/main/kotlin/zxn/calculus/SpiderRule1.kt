package zxn.calculus

import zxn.calculus.ruleNodes.ANY_NODE
import zxn.calculus.ruleNodes.RuleNodeSpider
import zxn.calculus.ruleNodes.RuleNodeSpider.RuleColor.WHITE
import zxn.calculus.ruleNodes.RuleWire
import zxn.calculus.ruleNodes.RuleWire.Companion.ANY_MULTIPLICITY

object SpiderRule1 : ZXRule("SpiderRule1", "S1") {

    init {
        with(ruleNetwork) {

            val spider1 = RuleNodeSpider(WHITE, true)
            val spider2 = RuleNodeSpider(WHITE, true)

            addNode(spider1)
            addNode(spider2)
            addWire(RuleWire(spider1, spider2, ANY_MULTIPLICITY))
            addWire(RuleWire(spider1, ANY_NODE, ANY_MULTIPLICITY))
            addWire(RuleWire(spider2, ANY_NODE, ANY_MULTIPLICITY))

        }
    }

}