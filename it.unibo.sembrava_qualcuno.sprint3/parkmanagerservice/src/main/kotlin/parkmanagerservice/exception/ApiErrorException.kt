package parkmanagerservice.exception

import it.unibo.parkmanagerservice.model.ApiError
import org.springframework.http.HttpStatus
import java.lang.RuntimeException

data class ApiErrorException(val status: HttpStatus, val apiError: ApiError) : RuntimeException()
