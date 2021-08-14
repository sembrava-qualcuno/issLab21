package sonar

class SonarController(sonarInterface: SonarInterface) {
    private val sonar: SonarInterface = sonarInterface

    fun isOutdoorFree(): Boolean {
        return !sonar.isEngaged()
    }
}
