package sonar

import java.util.*
import kotlin.concurrent.thread

class SonarController(sonarInterface: SonarInterface, private val timerThreshold: Int) : TimerTask() {
    private val sonar: SonarInterface = sonarInterface
    lateinit var observer: () -> Unit
    private lateinit var timer: Timer
    fun isOutdoorFree(): Boolean {
        return !sonar.isEngaged()
    }

    fun addObserver(lmbd: () -> Unit) {
        observer = lmbd
        sonar.addObserver(::checkSonarEngaged)
    }

    private fun checkSonarEngaged(engaged: Boolean) {
        timer = Timer()
        if(engaged)
            timer.schedule(this, (timerThreshold * 1000).toLong())
        else
            timer.cancel()
    }

    override fun run() {
        thread { observer() }
    }
}
