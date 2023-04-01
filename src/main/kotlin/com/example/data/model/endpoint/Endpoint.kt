package com.example.data.model.endpoint

sealed class Endpoint(val path: String) {
    object Root: Endpoint(path = "/")
    object TokenVerification: Endpoint(path = "/token_verification")
    object GetUserInfo: Endpoint(path = "/get_user")
    object UpdateUserInfo: Endpoint(path = "/update_user")
    object DeleteUser: Endpoint(path = "/delete_user")
    object SignOut: Endpoint(path = "/sign_out")
    object Chat : Endpoint(path = "/chat-socket")
    object Unauthorized: Endpoint(path = "/unauthorized")
    object Authorized: Endpoint(path = "/authorized")
}
