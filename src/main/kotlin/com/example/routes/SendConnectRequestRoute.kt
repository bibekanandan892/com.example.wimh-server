package com.example.routes

import com.example.data.model.ApiResponse
import com.example.data.model.connect_request.ConnectRequest
import com.example.data.model.endpoint.Endpoint
import com.example.domain.UserRepository
import com.example.util.Constants
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.sendConnectRequestRoute(app: Application, userRepository : UserRepository){
    authenticate("jwt-auth") {
        post (Endpoint.SendConnectRequest.path){
            val principal = call.authentication.principal<JWTPrincipal>()
            try {
                val fromHeartId = principal?.payload?.getClaim(Constants.HEART_ID_KEY)?.asString()
                val request = call.receive<ConnectRequest>()
                app.log.info("heartid = $fromHeartId")
                if(fromHeartId != null && request.toHeartId != null){
                    val status = userRepository.sendConnectionRequest(fromHeartId = fromHeartId, toHeartId = request.toHeartId)
                    if(status.success){
                        call.respond(message = ApiResponse<String>(success = true, message = status.message),status = HttpStatusCode.OK)
                    }else{
                        call.respond(message = ApiResponse<String>(success = false, message = status.message), status = HttpStatusCode.BadRequest)
                    }
                }else{
                    call.respond(message = ApiResponse<String>(success = false, message = "Heart Id Invalid"), status = HttpStatusCode.BadRequest)
                }
            }catch (e : Exception){
                call.respond(message = ApiResponse<String>(success = false, message = "Something Went Wrong"), status = HttpStatusCode.Unauthorized)
            }
        }
    }
}