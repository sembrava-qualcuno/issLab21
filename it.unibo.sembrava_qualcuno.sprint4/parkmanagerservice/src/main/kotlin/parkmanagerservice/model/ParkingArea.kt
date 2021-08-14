package parkmanagerservice.model

import kotlinx.serialization.Serializable

@Serializable
data class ParkingArea(var fan: String, var temperature: Int, var trolley: String)
