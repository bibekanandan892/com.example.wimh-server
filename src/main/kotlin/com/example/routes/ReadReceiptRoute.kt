package com.example.routes

import com.example.data.remote.ReadReceiptService
import com.example.util.Constants
import com.example.util.MemberAlreadyExistsException
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach

fun Route.readReceiptRoute(readReceiptService: ReadReceiptService) {
    authenticate("jwt-auth") {
        webSocket("/read_receipt") {
            val principal = call.authentication.principal<JWTPrincipal>()
            try {
                val recipientHeartId = principal?.payload?.getClaim(Constants.HEART_ID_KEY)?.asString()
                if (recipientHeartId == null) {
                    close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "Not authenticated"))
                    return@webSocket
                }
                readReceiptService.register(recipientHeartId, this)
                try {
                    // Handle incoming messages
                    incoming.consumeEach { frame->
                        if (frame is Frame.Text) {
                            val messageIdResponseString = frame.readText()
                                readReceiptService.sendReceipt(
                                    messageIdResponseString = messageIdResponseString,
                                    recipientHeartId = recipientHeartId
                                )
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    readReceiptService.unregister(recipientHeartId)
                }
            } catch (e: MemberAlreadyExistsException) {
                call.respond(HttpStatusCode.Conflict)
            } catch (e: Exception) {
                e.printStackTrace()
            }


        }
    }
}
