package it.unibo.sembrava_qualcuno.model

import kotlinx.serialization.Serializable

@Serializable
data class ApiError(var code: Int, var message: String)
