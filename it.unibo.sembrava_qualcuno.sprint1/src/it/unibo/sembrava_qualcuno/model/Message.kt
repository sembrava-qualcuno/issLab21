package it.unibo.sembrava_qualcuno.model;

import kotlinx.serialization.Serializable;

@Serializable
data class Message(val code: Int, val message: String)
