package it.unibo.sembrava_qualcuno.sprint2.test

import it.unibo.connQak.connQakBase
import it.unibo.connQak.connQakTcp
import it.unibo.kactor.MsgUtil

fun main() {
    val connBasicRobot: connQakBase = connQakTcp()
    connBasicRobot.createConnection("localhost", 8020)

    while (true) {
        val command = readLine()

        when(command) {
            "w" -> connBasicRobot.forward(MsgUtil.buildRequest("basicrobottest", "step", "step(340)", "basicrobot"))
            "s" -> connBasicRobot.forward(MsgUtil.buildDispatch("basicrobottest", "cmd", "cmd(s)", "basicrobot"))
            "l" -> connBasicRobot.forward(MsgUtil.buildDispatch("basicrobottest", "cmd", "cmd(l)", "basicrobot"))
            "r" -> connBasicRobot.forward(MsgUtil.buildDispatch("basicrobottest", "cmd", "cmd(r)", "basicrobot"))
        }
    }
}
