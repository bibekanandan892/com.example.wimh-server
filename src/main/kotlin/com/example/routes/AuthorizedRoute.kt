package com.example.routes

import com.example.data.model.ApiResponse
import com.example.data.model.endpoint.Endpoint
import com.example.util.Constants.HEART_ID_KEY
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authorizedRoute() {
    authenticate("jwt-auth") {
        get(Endpoint.Authorized.path) {
            val principal = call.authentication.principal<JWTPrincipal>()
            val userId = principal?.payload?.getClaim(HEART_ID_KEY)?.asString()
            call.respond(
                message = ApiResponse<String>(success = true, message = userId),
                status = HttpStatusCode.OK
            )
        }
    }
}