package com.example.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.data.model.endpoint.Endpoint
import com.example.util.Constants.JWT_AUDIENCE
import com.example.util.Constants.JWT_ISSUER
import com.example.util.Constants.SECRET_KEY
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import java.time.Duration
import java.time.Instant

fun Application.configureAuth() {
    install(Authentication) {
        jwt("jwt-auth") {
            verifier(JWT.require(Algorithm.HMAC256(SECRET_KEY)).build())
            validate { credential ->
                val expiration = credential.payload.expiresAt?.toInstant()?.isAfter(Instant.now().plus(Duration.ofSeconds(30))) ?: true
                val issuer = credential.payload.issuer == JWT_ISSUER
                if (credential.payload.audience.contains(JWT_AUDIENCE) && expiration && issuer) {
                    JWTPrincipal(credential.payload)
                } else {
                    respondRedirect(Endpoint.Unauthorized.path)
                    null
                }
            }

        }
    }
}