package com.example.routes

import com.example.data.model.endpoint.Endpoint
import com.example.domain.UserRepository
import com.example.util.Constants.HEART_ID_KEY
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.heartStatusRoute(userRepository: UserRepository){
    authenticate("jwt-auth") {
        get(Endpoint.HeartStatus.path){
            val principal = call.authentication.principal<JWTPrincipal>()
            val heartId = principal?.payload?.getClaim(HEART_ID_KEY)?.asString()
            call.respondRedirect(url = Endpoint.Unauthorized.path)
        }
    }
}