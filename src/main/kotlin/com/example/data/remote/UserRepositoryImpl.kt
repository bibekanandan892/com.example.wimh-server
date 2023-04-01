package com.example.data.remote

import com.example.data.model.user_details.ConnectionRequest
import com.example.data.model.user_details.User
import com.example.domain.UserRepository
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.setValue

class UserRepositoryImpl(dataBase: CoroutineDatabase) : UserRepository {


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

    override suspend fun sendConnectionRequest(heartId: String, senderUser: ConnectionRequest): Boolean {
        val findUser = users.findOne(filter = User::userHeartId eq heartId)
        return if (findUser != null) {
            if (findUser.connectedHeardId != null) {
                false
            } else {
                findUser.listOfConnectRequest?.toMutableList()?.add(senderUser)
                users.updateOne(
                    filter = User::userHeartId eq heartId,
                    update = setValue(
                        property = User::listOfConnectRequest,
                        value = findUser.listOfConnectRequest
                    )
                ).wasAcknowledged()
            }
        } else {
            false
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
                    value = null
                )
            ).wasAcknowledged()
            val removeAcceptorRequestList = users.updateOne(
                filter = User::userHeartId eq acceptorHeartId,
                update = setValue(
                    property = User::listOfConnectRequest,
                    value = null
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

    override suspend fun disconnectHeart(user: User): Boolean {
        val findUser1HeartId = user.userHeartId
        val findUser2HeartId = user.connectedHeardId
        val findUser1 = users.findOne(filter = User::userHeartId eq user.connectedHeardId)
        val findUser2 = users.findOne(filter = User::userHeartId eq user.userHeartId)
       return if(findUser1 == null || findUser2 == null){
            false
        }else{
            val removeUser1 = users.updateOne(filter = User::userHeartId eq findUser1HeartId, update = setValue(
                property = User::connectedHeardId,
                value = null
            )).wasAcknowledged()
           val removeUser2 = users.updateOne(filter = User::userHeartId eq findUser2HeartId, update = setValue(
               property = User::connectedHeardId,
               value = null
           )).wasAcknowledged()
           (removeUser1 && removeUser2)
       }
    }

    override suspend fun getUserHeartId(subId: String): String? {
        return users.findOne(filter = User::subId eq subId)?.userHeartId
    }
}