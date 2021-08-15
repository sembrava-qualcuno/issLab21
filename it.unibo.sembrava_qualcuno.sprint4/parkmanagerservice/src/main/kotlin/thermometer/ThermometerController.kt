package thermometer

class ThermometerController(private val thermometer: ThermometerInterface, private val temperatureThreshold: Int) {
    lateinit var observer: () -> Unit
    private var lastTemperature: Int

    init {
        lastTemperature = getTemperature()
    }

    fun getTemperature(): Int {
        return thermometer.getTemperature()
    }

    fun addObserver(lmbd: () -> Unit) {
        lastTemperature = getTemperature()
        observer = lmbd
        thermometer.addObserver(::checkHighTemperature)
    }

    private fun checkHighTemperature(temperature: Int) {
        println("Acutal temperature: $temperature")
        println("Last temperature: $lastTemperature")
        if(temperatureThreshold in lastTemperature until temperature) {
            observer()
            lastTemperature = temperature
        }
        else if(temperatureThreshold in temperature until lastTemperature) {
            observer()
            lastTemperature = temperature
        }
    }
}
