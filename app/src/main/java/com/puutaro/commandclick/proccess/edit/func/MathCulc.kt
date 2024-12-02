package com.puutaro.commandclick.proccess.edit.func

import net.objecthunter.exp4j.ExpressionBuilder

object MathCulc {

    fun int(formulaStr: String): Int {
        return ExpressionBuilder(formulaStr).build().evaluate().toInt()
    }

    fun float(formulaStr: String): Float {
        return ExpressionBuilder(formulaStr).build().evaluate().toFloat()
    }
}