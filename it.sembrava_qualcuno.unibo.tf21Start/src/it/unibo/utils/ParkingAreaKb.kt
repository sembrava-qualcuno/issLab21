package it.unibo.utils

object ParkingAreaKb {
	var trolleyStopped = false
	var indoorfree = true
	var outdoorfree = true
	var slotStateFree: BooleanArray = booleanArrayOf(false, false, false, false, true, false)
}