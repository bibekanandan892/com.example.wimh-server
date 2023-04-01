package com.example.routes

import com.example.data.model.ApiResponse
import com.example.data.model.endpoint.Endpoint
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.unauthorizedRoute(){
    get(Endpoint.Unauthorized.path){
        call.respond(
            message = ApiResponse(success = false, message = "unauthorized route"),
            status = HttpStatusCode.Unauthorized
        )
    }
}