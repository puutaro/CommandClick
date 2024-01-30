package com.puutaro.commandclick.util.list

object ListTool {

     fun switchList(
        list: MutableList<String>,
        from: Int,
        to: Int,
    ){
        val fromEl = list[from]
        val toEl = list[to]
        list[to] = fromEl
        list[from] = toEl
    }
}