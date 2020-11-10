package zxn.calculus

import zxn.network.ZXNetwork

abstract class ZXRule(val longName: String, val shortName:String) {

    val ruleNetwork = ZXNetwork(representsRule = true)

    //TODO: inverse?
    // TODO: HOW TO APPLY THIS?????? automatic transform? manual implementation of transform? Manual implementation of detection and transform?

    //TODO: presentation
    //TODO: specification
    //TODO: detection
    //TODO: application
}