package com.framgia.oleo.data.source

import com.framgia.oleo.data.source.model.Followed
import com.framgia.oleo.data.source.model.FriendRequest
import com.framgia.oleo.data.source.model.Place
import com.framgia.oleo.data.source.model.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.ValueEventListener

class UserRepository(
    private val local: UserDataSource.Local,
    private val remote: UserDataSource.Remote
) : UserDataSource.Local, UserDataSource.Remote {

    override fun deleteUserFollowed(id: String, userFriend: User) {
        remote.deleteUserFollowed(id, userFriend)
    }

    override fun getFollowedsOfUser(id: String, valueEventListener: ValueEventListener) {
        remote.getFollowedsOfUser(id, valueEventListener)
    }

    override fun addUserFollowed(idUser: String, userFollowed: User) {
        remote.addUserFollowed(idUser, userFollowed)
    }

    override fun changeFollowStatus(userCurrent: User, userFriend: User, status: String) {
        remote.changeFollowStatus(userCurrent, userFriend, status)
    }

    override fun getFollowRequestsOfUser(id: String, status: String, valueEventListener: ValueEventListener) {
        remote.getFollowRequestsOfUser(id, status, valueEventListener)
    }

    override fun getFollowRequestById(id: String, user: User, valueEventListener: ValueEventListener) {
        remote.getFollowRequestById(id, user, valueEventListener)
    }

    override fun addFollowRequest(userCurrent: User, userFriend: User) {
        remote.addFollowRequest(userCurrent, userFriend)
    }

    override fun confirmFriendRequest(
        user: User,
        friendRequest: FriendRequest,
        onSuccessListener: OnSuccessListener<Void>,
        onFailureListener: OnFailureListener
    ) {
        remote.confirmFriendRequest(user, friendRequest, onSuccessListener, onFailureListener)
    }

    override fun deleteFriendRequest(
        user: User,
        friendRequest: FriendRequest,
        onSuccessListener: OnSuccessListener<Void>,
        onFailureListener: OnFailureListener
    ) {
        remote.deleteFriendRequest(user, friendRequest, onSuccessListener, onFailureListener)
    }

    override fun addFriend(userId: String, friendRequestId: String) {
        remote.addFriend(userId, friendRequestId)
    }

    override fun getFriendRequests(userId: String, valueEventListener: ValueEventListener) {
        remote.getFriendRequests(userId, valueEventListener)
    }

    override fun getUserById(userId: String, singleValueEventListener: ValueEventListener) {
        remote.getUserById(userId, singleValueEventListener)
    }

    override fun addFriendRequest(
        reciveId: String,
        user: User,
        message: String,
        onSuccessListener: OnSuccessListener<Void>,
        onFailureListener: OnFailureListener
    ) {
        remote.addFriendRequest(
            reciveId, user, message,
            onSuccessListener, onFailureListener
        )
    }

    override fun getUsers(valueEventListener: ValueEventListener) {
        remote.getUsers(valueEventListener)
    }

    override fun pushUserLocation(idUser: String, place: Place) {
        remote.pushUserLocation(idUser, place)
    }

    override fun registerUser(
        user: User,
        onCompleteListener: OnCompleteListener<Void>,
        onFailureListener: OnFailureListener
    ) {
        remote.registerUser(user, onCompleteListener, onFailureListener)
    }

    override fun getUserByPhoneNumber(phoneNumber: String, valueEventListener: ValueEventListener) {
        remote.getUserByPhoneNumber(phoneNumber, valueEventListener)
    }

    override fun getUser(): User? {
        return local.getUser()
    }

    override fun insertUser(vararg users: User) {
        return local.insertUser(*users)
    }

    override fun deleteUser() {
        return local.deleteUser()
    }

    override fun updateUser(vararg users: User) {
        return local.updateUser(*users)
    }

    override fun getFriendLocation(id: String, childEventListener: ChildEventListener) {
        remote.getFriendLocation(id, childEventListener)
    }
}
