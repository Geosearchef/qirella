package zxn.calculus.ruleNodes

class RuleNodeSpider(val color: RuleColor, val anyPhase: Boolean, val phase: Double = 0.0) : RuleNode() {


    enum class RuleColor {
        ANY, WHITE, BLACK
    }
}