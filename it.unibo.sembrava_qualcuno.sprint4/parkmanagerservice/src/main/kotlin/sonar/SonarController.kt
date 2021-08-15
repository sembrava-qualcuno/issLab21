package sonar

import java.util.*

class SonarController(sonarInterface: SonarInterface, private val timerThreshold: Int) : TimerTask() {
    private val sonar: SonarInterface = sonarInterface
    lateinit var observer: () -> Unit
    private val timer = Timer()

    fun isOutdoorFree(): Boolean {
        return !sonar.isEngaged()
    }

    fun addObserver(lmbd: () -> Unit) {
        observer = lmbd
        sonar.addObserver(::checkSonarEngaged)
    }

    private fun checkSonarEngaged(engaged: Boolean) {
        if(engaged)
            timer.schedule(this, (timerThreshold * 1000).toLong())
        else
            timer.cancel()
    }

    override fun run() {
        observer()
    }
}
