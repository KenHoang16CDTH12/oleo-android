package com.framgia.oleo.data.source

import com.framgia.oleo.data.source.model.FriendRequest
import com.framgia.oleo.data.source.model.Place
import com.framgia.oleo.data.source.model.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.database.ChildEventListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.ValueEventListener

class UserRepository(
    private val local: UserDataSource.Local,
    private val remote: UserDataSource.Remote
) : UserDataSource.Local, UserDataSource.Remote {

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

    override fun changeStatusFriendRequest(
        user: User,
        friendRequest: FriendRequest,
        status: Int,
        onSuccessListener: OnSuccessListener<Void>,
        onFailureListener: OnFailureListener
    ) {
        remote.changeStatusFriendRequest(user, friendRequest, status, onSuccessListener, onFailureListener)
    }

    override fun addFriend(user: User, friendRequest: FriendRequest) {
        remote.addFriend(user, friendRequest)
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

    override fun pushUserLocation(idUser: String, idPlace: String, place: Place) {
        remote.pushUserLocation(idUser, idPlace, place)
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
