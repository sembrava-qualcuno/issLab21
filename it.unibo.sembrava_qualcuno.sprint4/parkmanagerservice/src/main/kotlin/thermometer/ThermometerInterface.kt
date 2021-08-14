package thermometer

interface ThermometerInterface {
    fun getTemperature(): Int
    fun addObserver( lmbd: (temperature: Int) -> Unit)
}
