package com.example.routes

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.data.model.auth.GoogleTokenRequest
import com.example.data.model.endpoint.Endpoint
import com.example.data.model.user_details.User
import com.example.domain.UserRepository
import com.example.util.Constants.AUDIENCE
import com.example.util.Constants.HEART_ID_KEY
import com.example.util.Constants.ISSUER
import com.example.util.Constants.JWT_AUDIENCE
import com.example.util.Constants.JWT_ISSUER
import com.example.util.Constants.SECRET_KEY
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.tokenVerificationRoute(app: Application, userRepository: UserRepository) {
    post(Endpoint.TokenVerification.path) {
        val request = call.receive<GoogleTokenRequest>()
        if (request.tokenId.isNotEmpty()) {
            val result = verifyGoogleTokenId(tokenId = request.tokenId)
            if (result != null) {
                val sub = result.payload["sub"].toString()
                val name = result.payload["name"].toString()
                val emailAddress = result.payload["email"].toString()
                val profilePhoto = result.payload["picture"].toString()
                val getHeartId = userRepository.getUserHeartId(sub)
                if (getHeartId == null) {
                    val heartId = UUID.randomUUID().toString()
                    val token = JWT.create()
                        .withAudience(JWT_AUDIENCE)
                        .withIssuer(JWT_ISSUER)
                        .withClaim(HEART_ID_KEY, heartId)
                        .withExpiresAt(Date(System.currentTimeMillis() + 25920000000000L))
                        .sign(Algorithm.HMAC256(SECRET_KEY))
                    userRepository.saveUser(
                        user = User(
                            subId = sub,
                            name = name,
                            emailAddress = emailAddress,
                            profilePhoto = profilePhoto,
                            userHeartId = heartId
                        )
                    )
                    call.respond(mapOf("token" to token))
                } else {
                    val token = JWT.create()
                        .withAudience(JWT_AUDIENCE)
                        .withIssuer(JWT_ISSUER)
                        .withClaim(HEART_ID_KEY, getHeartId)
                        .withExpiresAt(Date(System.currentTimeMillis() + 25920000000000L))
                        .sign(Algorithm.HMAC256(SECRET_KEY))
                    call.respond(mapOf("token" to token))
                }
            } else {
                call.respondRedirect(Endpoint.Unauthorized.path)
            }
        } else {
            call.respondRedirect(Endpoint.Unauthorized.path)
        }

    }
}

fun verifyGoogleTokenId(tokenId: String): GoogleIdToken? {
    return try {
        val verifier = GoogleIdTokenVerifier.Builder(NetHttpTransport(), GsonFactory())
            .setAudience(listOf(AUDIENCE))
            .setIssuer(ISSUER)
            .build()
        verifier.verify(tokenId)
    } catch (e: Exception) {
        null
    }
}
