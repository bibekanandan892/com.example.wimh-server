package com.example.routes

import com.example.data.model.ApiResponse
import com.example.data.model.endpoint.Endpoint
import com.example.domain.UserRepository
import com.example.util.Constants.HEART_ID_KEY
import com.google.protobuf.Api
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.heartStatusRoute(userRepository: UserRepository, app: Application){
    authenticate("jwt-auth") {
        get(Endpoint.HeartStatus.path){
            val principal = call.authentication.principal<JWTPrincipal>()
            try {
                val heartId = principal?.payload?.getClaim(HEART_ID_KEY)?.asString()
                app.log.info("heartid = $heartId")
                if(heartId != null){
                    val user = userRepository.getUserByHeartId(heartId = heartId)
                    app.log.info("user = ${user.toString()}")
                    call.respond(message = ApiResponse(success = true,response = user, message = if (user?.connectedHeardId == null) "Not Connect" else "Connect Successfully"), status = HttpStatusCode.OK)
                }else{
                    call.respond(message = ApiResponse<String>(success = false, message = "Invalid Heart Id"))
                }
            }catch (e : Exception){
                call.respond(message = ApiResponse<String>(success = false, message = "Something Went Wrong"), status = HttpStatusCode.Unauthorized)
            }
        }
    }
}