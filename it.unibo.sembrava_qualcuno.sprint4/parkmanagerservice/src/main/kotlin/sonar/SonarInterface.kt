package sonar

interface SonarInterface {
    fun isEngaged(): Boolean
    fun addObserver(lmbd: (engaged: Boolean) -> Unit)
}
