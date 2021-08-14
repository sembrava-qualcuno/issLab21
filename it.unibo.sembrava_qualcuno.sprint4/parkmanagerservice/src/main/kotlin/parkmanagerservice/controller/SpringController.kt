package parkmanagerservice.controller

import connQak.connQakBase
import connQak.connQakTcp
import it.unibo.kactor.MsgUtil
import parkmanagerservice.exception.ApiErrorException
import parkmanagerservice.utils.ApplMessageUtil
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import parkmanagerservice.model.*

@RestController
class SpringController {
    private val connClientService: connQakBase = connQakTcp()
    private val connManagerService: connQakBase = connQakTcp()

    init {
        connClientService.createConnection("localhost", 8023)
        connManagerService.createConnection("localhost", 8023)
    }

    @GetMapping("/client/reqenter")
    fun reqenter(): ResponseEntity<ParkingSlot> {
        var request = MsgUtil.buildRequest("springcontroller", "reqenter", "reqenter(X)", "clientservice")
        val reply = ApplMessageUtil.messageFromString(connClientService.request(request))

        val message: Message = Json.decodeFromString(reply.msgContent)

        //Error
        if (message.code != 0)
            throw ApiErrorException(HttpStatus.FORBIDDEN, ApiError(message.code, message.message))

        return ResponseEntity.ok(ParkingSlot(message.message.toInt()))
    }

    @GetMapping("/client/carenter")
    fun carenter(@RequestParam slotnum: Int): ResponseEntity<TokenId> {
        var request = MsgUtil.buildRequest("springcontroller", "carenter", "carenter($slotnum)", "clientservice")
        val reply = ApplMessageUtil.messageFromString(connClientService.request(request))
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
        val reply = ApplMessageUtil.messageFromString(connClientService.request(request))
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

    @GetMapping("/parkingArea")
    fun parkingArea(): ResponseEntity<ParkingArea> {
        val request = MsgUtil.buildRequest("springcontroller", "getParkingArea", "getParkingArea()", "managerservice")
        val reply = ApplMessageUtil.messageFromString(connManagerService.request(request))
        return ResponseEntity.ok(Json.decodeFromString(reply.msgContent))
    }

    @PutMapping("/parkingArea/trolley")
    fun updateTrolley(@RequestBody trolley: Trolley) {
        val request = MsgUtil.buildRequest("springcontroller", "updateTrolley", "updateTrolley(${trolley.state})", "managerservice")
        val reply = ApplMessageUtil.messageFromString(connManagerService.request(request))
        val message: Message = Json.decodeFromString(reply.msgContent)

        //Error
        if(message.code != 0)
            throw ApiErrorException(HttpStatus.BAD_REQUEST, ApiError(message.code, message.message))

        return
    }
}
