package it.unibo.sembrava_qualcuno.exception

import it.unibo.sembrava_qualcuno.model.ApiError
import org.springframework.http.HttpStatus
import java.lang.RuntimeException

data class ApiErrorException(val status: HttpStatus, val apiError: ApiError) : RuntimeException()
