package it.unibo.parkmanagerservice.model;

import kotlinx.serialization.Serializable;

@Serializable
data class Message(val code: Int, val message: String)
