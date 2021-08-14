package fan

interface FanInterface {
    fun getState(): String
    fun updateResource(action: String)
}
