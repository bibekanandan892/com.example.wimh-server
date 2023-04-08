package com.example.data.remote

import com.example.data.model.Status
import com.example.data.model.user_details.ConnectionRequest
import com.example.data.model.user_details.User
import com.example.domain.UserRepository
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.setValue

class UserRepositoryImpl constructor(private val dataBase: CoroutineDatabase) : UserRepository {


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
                val isContain = newListOfConnectRequest.contains(
                    element = ConnectionRequest(
                        subId = findFormUser.subId,
                        name = findFormUser.name,
                        emailAddress = findFormUser.emailAddress,
                        userHeartId = findFormUser.userHeartId,
                        profilePhoto = findFormUser.profilePhoto
                    )
                )
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
        return if (senderUser?.connectedHeardId != null) {
            Status(success = false, message = "Heart is Already Connect")
        } else if (acceptorUser?.connectedHeardId != null) {
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
            val connectSenderToAcceptor = users.updateOne(
                filter = User::userHeartId eq senderHeartId, update = setValue(
                    property = User::connectedHeardId,
                    value = acceptorHeartId
                )
            ).wasAcknowledged()
            val connectAcceptorToSender = users.updateOne(
                filter = User::userHeartId eq acceptorHeartId, update = setValue(
                    property = User::connectedHeardId,
                    value = senderHeartId
                )
            ).wasAcknowledged()
            val isSuccess =
                (removeSenderRequestList && removeAcceptorRequestList && connectAcceptorToSender && connectSenderToAcceptor)
            Status(success = isSuccess, message = if (isSuccess) "Connect Success" else "Connect Problem")
        }
    }

    override suspend fun disconnectHeart(userHeartId: String, connectedHeardId: String): Status {
        val findUser1 = users.findOne(filter = User::userHeartId eq connectedHeardId)
        val findUser2 = users.findOne(filter = User::userHeartId eq userHeartId)
        return if (findUser1 == null || findUser2 == null) {
            Status(success = false, message = "User not found")
        } else {
            val removeUser1 = users.updateOne(
                filter = User::userHeartId eq userHeartId,
                update = setValue(
                    property = User::connectedHeardId,
                    value = null
                )
            ).wasAcknowledged()
            val removeUser2 = users.updateOne(
                filter = User::userHeartId eq connectedHeardId, update = setValue(
                    property = User::connectedHeardId,
                    value = null
                )
            ).wasAcknowledged()
            val isSuccess = (removeUser1 && removeUser2)
            Status(success = isSuccess, message = if (isSuccess) "disconnect Successfully" else "SomeThing Went wrong")
        }
    }

    override suspend fun getHeartIdBySubId(subId: String): String? {
        return users.findOne(filter = User::subId eq subId)?.userHeartId
    }

    override suspend fun getUserByHeartId(heartId: String): User? {
        return users.findOne(filter = User::userHeartId eq heartId)
    }
}