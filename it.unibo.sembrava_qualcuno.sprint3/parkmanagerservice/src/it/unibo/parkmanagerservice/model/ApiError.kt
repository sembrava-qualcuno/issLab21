package it.unibo.parkmanagerservice.model

import kotlinx.serialization.Serializable

@Serializable
data class ApiError(var code: Int, var message: String)
