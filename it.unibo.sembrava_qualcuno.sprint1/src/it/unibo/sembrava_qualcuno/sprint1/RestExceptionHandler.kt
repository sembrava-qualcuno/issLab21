package it.unibo.sembrava_qualcuno.sprint1

import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.core.annotation.Order
import org.springframework.core.Ordered
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.http.ResponseEntity
import org.springframework.http.HttpStatus

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
class RestExceptionHandler : ResponseEntityExceptionHandler() {
	
	@ExceptionHandler(NotAvailableException::class)		 
	fun handleEntityNotFound(ex : NotAvailableException) : ResponseEntity<Any> {
       val apiError : ApiError = ApiError(1, ex.getLocalizedMessage())

       return buildResponseEntity(apiError)
   }
			
	fun buildResponseEntity(apiError : ApiError) : ResponseEntity<Any> {
       return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiError)
	}
}