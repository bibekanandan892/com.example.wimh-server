package com.example.routes

import com.example.data.model.ApiResponse
import com.example.data.model.connect_request.ConnectRequest
import com.example.data.model.endpoint.Endpoint
import com.example.data.model.user_details.User
import com.example.domain.UserRepository
import com.example.util.Constants
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.acceptConnectionRequest(userRepository: UserRepository,app : Application){
    authenticate("jwt-auth") {
        post (Endpoint.AcceptConnectRequest.path){
            val principal = call.authentication.principal<JWTPrincipal>()
            try {
                val userHeartId = principal?.payload?.getClaim(Constants.HEART_ID_KEY)?.asString()
                val request = call.receive<ConnectRequest>()
                app.log.info("heartid = $userHeartId")
                if(userHeartId != null && request.toHeartId != null){
                    val status = userRepository.exceptConnectionRequest(senderHeartId = request.toHeartId, acceptorHeartId = userHeartId)
                    val user = userRepository.getUserByHeartId(heartId = userHeartId)
                    if(status.success && user != null){
                        call.respond(message = ApiResponse(success = true, message = status.message, response = user),status = HttpStatusCode.OK)
                    }else{
                        call.respond(message = ApiResponse<String>(success = false, message = status.message), status = HttpStatusCode.BadRequest)
                    }
                }else{
                    call.respond(message = ApiResponse<String>(success = false, message = "Heart Id Invalid"), status = HttpStatusCode.Unauthorized)
                }
            }catch (e : Exception){
                call.respond(message = ApiResponse<String>(success = false, message = "Something Went Wrong"), status = HttpStatusCode.Unauthorized)
            }
        }
    }
}