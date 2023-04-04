package com.example.data.remote

import com.example.data.model.user_details.ConnectionRequest
import com.example.data.model.user_details.User
import com.example.domain.UserRepository
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.setValue

class UserRepositoryImpl constructor(private val dataBase: CoroutineDatabase) : UserRepository {


    private val users = dataBase.getCollection<User>()//collection is a data holder for multiple fields

    override suspend fun saveUser(user: User): Boolean {
        val existingUser =
            users.findOne(filter = User::subId eq user.subId)//if I pass this user which I want to save I want to get existing user from database
        return if (existingUser == null) {
            users.insertOne(document = user).wasAcknowledged()
        } else {
            false
        }
    }

    override suspend fun sendConnectionRequest(fromHeartId: String, toHeartId: String): Boolean {
        val findFormUser = users.findOne(filter = User::userHeartId eq fromHeartId)
        val findToUser = users.findOne(filter = User::userHeartId eq toHeartId)
        return if (findFormUser == null || findToUser == null) {
            false
        } else {
            return if (findFormUser.connectedHeardId != null || findToUser.connectedHeardId != null) {
                false
            } else {
                var newListOfConnectRequest = findToUser.listOfConnectRequest
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
                users.updateOne(
                    filter = User::userHeartId eq toHeartId,
                    update = setValue(
                        property = User::listOfConnectRequest,
                        value = (newListOfConnectRequest.toList())
                    )
                ).wasAcknowledged()
            }
        }
    }

    override suspend fun exceptConnectionRequest(senderHeartId: String, acceptorHeartId: String): Boolean {
        val senderUser = users.findOne(filter = User::userHeartId eq senderHeartId)
        val acceptorUser = users.findOne(filter = User::userHeartId eq acceptorHeartId)
        return if (senderUser?.connectedHeardId != null) {
            false
        } else if (acceptorUser?.connectedHeardId != null) {
            false
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
            (removeSenderRequestList && removeAcceptorRequestList && connectAcceptorToSender && connectSenderToAcceptor)
        }
    }

    override suspend fun disconnectHeart(userHeartId: String,connectedHeardId: String): Boolean {
        val findUser1 = users.findOne(filter = User::userHeartId eq connectedHeardId)
        val findUser2 = users.findOne(filter = User::userHeartId eq userHeartId)
        return if (findUser1 == null || findUser2 == null) {
            false
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
            (removeUser1 && removeUser2)
        }
    }

    override suspend fun getHeartIdBySubId(subId: String): String? {
        return users.findOne(filter = User::subId eq subId)?.userHeartId
    }

    override suspend fun getUserByHeartId(heartId: String): User? {
        return users.findOne(filter = User::userHeartId eq heartId)
    }
}