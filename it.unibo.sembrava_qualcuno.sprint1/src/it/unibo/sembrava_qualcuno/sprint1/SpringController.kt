package it.unibo.sembrava_qualcuno.sprint1

import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ResponseBody

@RestController
class SpringController {
	
	
	@GetMapping("/client/reqenter")
	@ResponseBody
	fun reqenter() : ResponseEntity<ParkingSlot> {
		//Request to the ParkClientService for the parking slot
		
		//Error
		throw NotAvailableException("errore")
		
		
		//return ResponseEntity.ok(ParkingSlot(1))
	}
}