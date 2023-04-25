package com.example.data.remote

import com.example.data.model.Status
import com.example.data.model.chat.MessageEntity
import com.example.data.model.endpoint.Endpoint
import com.example.data.model.fcm.FcmResponse
import com.example.data.model.fcm.req.Body
import com.example.data.model.fcm.req.Data
import com.example.data.model.fcm.req.FcmRequest
import com.example.data.model.fcm.req.Notification
import com.example.data.model.user_details.ConnectionRequest
import com.example.data.model.user_details.User
import com.example.domain.UserRepository

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.network.sockets.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.util.*
import kotlinx.coroutines.flow.collect
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.litote.kmongo.SetTo
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.setValue
import java.io.IOException

class UserRepositoryImpl constructor(private val dataBase: CoroutineDatabase, private val httpClient: HttpClient) :
    UserRepository {


    private val users = dataBase.getCollection<User>()//collection is a data holder for multiple fields

    override suspend fun saveUser(user: User): Status {
        val existingUser =
            users.findOne(filter = User::subId eq user.subId)//if I pass this user which I want to save I want to get existing user from database
        return if (existingUser == null) {
            val status = users.insertOne(document = user).wasAcknowledged()
            Status(success = status, message = if (status) "User Created" else "Problem in Create User")
        } else {
            Status(success = false, message = "User already Exist")
        }
    }

    override suspend fun sendConnectionRequest(fromHeartId: String, toHeartId: String): Status {
        val findFormUser = users.findOne(filter = User::userHeartId eq fromHeartId)
        val findToUser = users.findOne(filter = User::userHeartId eq toHeartId)
        return if (findFormUser == null || findToUser == null) {
            Status(success = false, "User not Found")
        } else {
            return if (findFormUser.connectedHeardId != null || findToUser.connectedHeardId != null) {
                Status(success = false, message = "Heart already Connected")
            } else if (fromHeartId == toHeartId) {
                Status(success = false, message = "Try With Different Heart Id")
            } else {
                var newListOfConnectRequest = findToUser.listOfConnectRequest
                var isContain: Boolean = false
                newListOfConnectRequest.forEach { connectionRequest ->
                    if (connectionRequest?.userHeartId == fromHeartId) {
                        isContain = true
                        return@forEach
                    }
                }
                return if (isContain) {
                    Status(success = false, message = "Request already Sent")
                } else {
                    newListOfConnectRequest = newListOfConnectRequest.toMutableList().apply {
                        add(
                            ConnectionRequest(
                                subId = findFormUser.subId,
                                name = findFormUser.name,
                                emailAddress = findFormUser.emailAddress,
                                userHeartId = findFormUser.userHeartId,
                                profilePhoto = findFormUser.profilePhoto
                            )
                        )
                    }
                    val isSuccess = users.updateOne(
                        filter = User::userHeartId eq toHeartId,
                        update = setValue(
                            property = User::listOfConnectRequest,
                            value = (newListOfConnectRequest.toList())
                        )
                    ).wasAcknowledged()
                    Status(success = isSuccess, message = if (isSuccess) "Request Sent" else "Unable to send Request")
                }
            }
        }
    }

    override suspend fun exceptConnectionRequest(senderHeartId: String, acceptorHeartId: String): Status {
        val senderUser = users.findOne(filter = User::userHeartId eq senderHeartId)
        val acceptorUser = users.findOne(filter = User::userHeartId eq acceptorHeartId)
        return if (senderUser == null || acceptorUser == null) {
            Status(success = false, message = "user not found")
        } else if (senderUser.connectedHeardId != null) {
            Status(success = false, message = "Heart is Already Connect")
        } else if (acceptorUser.connectedHeardId != null) {
            Status(success = false, message = "Heart is Already Connect")
        } else {
            val removeSenderRequestList = users.updateOne(
                filter = User::userHeartId eq senderHeartId,
                update = setValue(
                    property = User::listOfConnectRequest,
                    value = listOf()
                )
            ).wasAcknowledged()
            val removeAcceptorRequestList = users.updateOne(
                filter = User::userHeartId eq acceptorHeartId,
                update = setValue(
                    property = User::listOfConnectRequest,
                    value = listOf()
                )
            ).wasAcknowledged()

            val op1 = users.updateMany(
                filter = User::userHeartId eq senderHeartId,
                updates = arrayOf(
                    SetTo(
                        property = User::connectedHeardId,
                        value = acceptorUser.userHeartId
                    ),
                    SetTo(
                        property = User::connectedUserName,
                        value = acceptorUser.name
                    ),
                    SetTo(
                        property = User::connectedUserEmail,
                        value = acceptorUser.emailAddress
                    ),
                    SetTo(
                        property = User::connectUserPhoto,
                        value = acceptorUser.profilePhoto
                    )
                )
            ).wasAcknowledged()
            val op2 = users.updateMany(
                filter = User::userHeartId eq acceptorHeartId,
                updates = arrayOf(
                    SetTo(
                        property = User::connectedHeardId,
                        value = senderUser.userHeartId
                    ),
                    SetTo(
                        property = User::connectedUserName,
                        value = senderUser.name
                    ),
                    SetTo(
                        property = User::connectedUserEmail,
                        value = senderUser.emailAddress
                    ),
                    SetTo(
                        property = User::connectUserPhoto,
                        value = senderUser.profilePhoto
                    )
                )
            ).wasAcknowledged()

            val isSuccess =
                (removeSenderRequestList && removeAcceptorRequestList && op1 && op2)
            Status(success = isSuccess, message = if (isSuccess) "Connect Success" else "Connect Problem")
        }
    }


    override suspend fun disconnectHeart(userHeartId: String, connectedHeardId: String): Status {
        val senderUser = users.findOne(filter = User::userHeartId eq connectedHeardId)
        val acceptorUser = users.findOne(filter = User::userHeartId eq userHeartId)
        return if (senderUser == null || acceptorUser == null) {
            Status(success = false, message = "User not found")
        } else {
            val op1 = users.updateMany(
                filter = User::userHeartId eq userHeartId,
                updates = arrayOf(
                    SetTo(
                        property = User::connectedHeardId,
                        value = null
                    ),
                    SetTo(
                        property = User::connectedUserName,
                        value = null
                    ),
                    SetTo(
                        property = User::connectedUserEmail,
                        value = null
                    ),
                    SetTo(
                        property = User::connectUserPhoto,
                        value = null
                    ),SetTo(
                        property = User::fcmToken,
                        value = null
                    )
                )
            ).wasAcknowledged()
            val op2 = users.updateMany(
                filter = User::userHeartId eq connectedHeardId,
                updates = arrayOf(
                    SetTo(
                        property = User::connectedHeardId,
                        value = null
                    ),
                    SetTo(
                        property = User::connectedUserName,
                        value = null
                    ),
                    SetTo(
                        property = User::connectedUserEmail,
                        value = null
                    ),
                    SetTo(
                        property = User::connectUserPhoto,
                        value = null
                    ),
                    SetTo(
                        property = User::fcmToken,
                        value = null
                    )
                )
            ).wasAcknowledged()
            val isSuccess = (op1 && op2)
            Status(success = isSuccess, message = if (isSuccess) "disconnect Successfully" else "SomeThing Went wrong")
        }
    }

    override suspend fun getHeartIdBySubId(subId: String): String? {
        return users.findOne(filter = User::subId eq subId)?.userHeartId
    }

    override suspend fun getUserByHeartId(heartId: String): User? {
        return users.findOne(filter = User::userHeartId eq heartId)
    }

    override suspend fun updateFcmToken(fcmToken: String?, heartId: String): Status {
        val isSuccess = users.updateOne(
            filter = User::userHeartId eq heartId,
            update = setValue(property = User::fcmToken, value = fcmToken)
        ).wasAcknowledged()
        return if (isSuccess) Status(success = true, message = "Token Update Success") else Status(
            success = false,
            "something went wrong"
        )
    }

    override suspend fun sendMessageNotification(
        toHeartId: String, messageEntityString: String, fromUserHeartId: String
    ): Status {
        val toUser = getUserByHeartId(heartId = toHeartId)
        val fromUser = getUserByHeartId(heartId = fromUserHeartId)
        val body = Json.decodeFromString<MessageEntity>(messageEntityString)
        return try {
            val response = httpClient.post {
                url(Endpoint.SendNotification.path)
                setBody(
                    body = FcmRequest(
                        data = Data(
                            title = fromUser?.name,
                            body = Body(
                                currentTime = body.currentTime,
                                fromUserHeartId = body.fromUserHeartId,
                                toUserHeartId = body.toUserHeartId,
                                id = body.id,
                                image = body.image,
                                message = body.message,
                                isMine =  body.isMine,
                            ),
                        ),
                        to = toUser?.fcmToken
                    )
                )
            }
            Status(
                success = true,
                message = response.body<FcmResponse>().results?.get(0)?.message_id?: response.body<FcmResponse>().results?.get(0)?.error ?: "Unknown Error"
            )
        } catch (e: ClientRequestException) {
            Status(success = false, message = (e.response.status.description))
        } catch (e: ServerResponseException) {
            Status(success = false, message = (e.response.status.description))
        } catch (e: RedirectResponseException) {
            Status(success = false, message = (e.response.status.description))
        } catch (e: ConnectTimeoutException) {
            Status(success = false, message = (e.message ?: "Connection Timeout"))
        } catch (e: SocketTimeoutException) {
            Status(success = false, message = (e.message ?: "Socket Timeout"))
        } catch (e: IOException) {
            Status(success = false, message = (e.message ?: "Unknown IO Error"))
        } catch (e: Exception) {
            Status(success = false, message = (e.message ?: "Unknown Error"))
        }
    }
}