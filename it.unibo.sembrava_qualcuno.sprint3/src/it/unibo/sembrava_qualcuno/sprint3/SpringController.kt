package it.unibo.sembrava_qualcuno.sprint3

import it.unibo.connQak.connQakBase
import it.unibo.connQak.connQakTcp
import it.unibo.kactor.MsgUtil
import it.unibo.sembrava_qualcuno.exception.ApiErrorException
import it.unibo.sembrava_qualcuno.model.ApiError
import it.unibo.sembrava_qualcuno.model.Message
import it.unibo.sembrava_qualcuno.model.ParkingSlot
import it.unibo.sembrava_qualcuno.model.TokenId
import it.unibo.sembrava_qualcuno.utils.ApplMessageUtil
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
class SpringController {
    final val connParkClientService: connQakBase = connQakTcp()

    init {
        connParkClientService.createConnection("localhost", 8023)
    }

    @GetMapping("/client/reqenter")
    fun reqenter(): ResponseEntity<ParkingSlot> {
        var request = MsgUtil.buildRequest("springcontroller", "reqenter", "reqenter(X)", "clientservice")
        val reply = ApplMessageUtil.messageFromString(connParkClientService.request(request))

        val message: Message = Json.decodeFromString(reply.msgContent)

        //Error
        if (message.code != 0)
            throw ApiErrorException(HttpStatus.FORBIDDEN, ApiError(message.code, message.message))

        return ResponseEntity.ok(ParkingSlot(message.message.toInt()))
    }

    @GetMapping("/client/carenter")
    fun carenter(@RequestParam slotnum: Int): ResponseEntity<TokenId> {
        var request = MsgUtil.buildRequest("springcontroller", "carenter", "carenter($slotnum)", "clientservice")
        val reply = ApplMessageUtil.messageFromString(connParkClientService.request(request))
        val message: Message = Json.decodeFromString(reply.msgContent)
        //Error
        if (message.code != 0) {
            if (message.code == 3)
                throw ApiErrorException(HttpStatus.BAD_REQUEST, ApiError(message.code, message.message))
            else
                throw ApiErrorException(HttpStatus.FORBIDDEN, ApiError(message.code, message.message))
        }

        return ResponseEntity.ok(TokenId(message.message))
    }

    @GetMapping("/client/reqexit")
    fun reqexit(@RequestParam tokenid: String) {
        var request = MsgUtil.buildRequest("springcontroller", "reqexit", "reqexit($tokenid)", "clientservice")
        val reply = ApplMessageUtil.messageFromString(connParkClientService.request(request))
        val message: Message = Json.decodeFromString(reply.msgContent)
        //Error
        if (message.code != 0) {
            if (message.code == 4)
                throw ApiErrorException(HttpStatus.BAD_REQUEST, ApiError(message.code, message.message))
            else
                throw ApiErrorException(HttpStatus.FORBIDDEN, ApiError(message.code, message.message))
        }

        return
    }
}
