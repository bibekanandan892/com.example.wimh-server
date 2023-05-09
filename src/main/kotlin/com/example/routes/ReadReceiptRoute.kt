package com.example.routes

import com.example.data.remote.ReadReceiptService
import com.example.util.Constants
import com.example.util.Constants.WIMH
import com.example.util.MemberAlreadyExistsException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach

fun Route.readReceiptRoute(app : Application,readReceiptService: ReadReceiptService) {
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
                    app.log.info("$WIMH :::::: recieve the read resipet ")
                    incoming.consumeEach { frame->
                        if (frame is Frame.Text) {
                            val messageIdResponseString = frame.readText()
                            app.log.info(WIMH, frame.toString())
                                readReceiptService.sendReceipt(
                                    messageIdResponseString = messageIdResponseString,
                                    recipientHeartId = recipientHeartId
                                )
                        }else{
                            app.log.info("$WIMH :::::: going to else part in read resipt ")

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
