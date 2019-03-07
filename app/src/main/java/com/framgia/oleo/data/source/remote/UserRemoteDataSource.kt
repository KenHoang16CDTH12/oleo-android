package com.framgia.oleo.data.source.remote

import com.framgia.oleo.data.source.UserDataSource
import com.framgia.oleo.data.source.model.FollowRequest
import com.framgia.oleo.data.source.model.Friend
import com.framgia.oleo.data.source.model.FriendRequest
import com.framgia.oleo.data.source.model.Place
import com.framgia.oleo.data.source.model.User
import com.framgia.oleo.utils.Constant
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserRemoteDataSource : UserDataSource.Remote {
    override fun changeFollowStatus(userCurrent: User, userFriend: User, status: String) {
        firebaseDatabase.getReference(Constant.PATH_STRING_FOLLOW)
            .child(userCurrent.id)
            .child(Constant.PATH_STRING_FOLLOW_REQUEST)
            .child(userFriend.id)
            .child(Constant.PATH_STRING_STATUS)
            .setValue(status)
    }

    override fun getFollowRequestsOfUser(id: String, status: String, valueEventListener: ValueEventListener) {
        firebaseDatabase.getReference(Constant.PATH_STRING_FOLLOW)
            .child(id)
            .child(Constant.PATH_STRING_FOLLOW_REQUEST)
            .orderByChild(Constant.PATH_STRING_STATUS)
            .equalTo(status)
            .addValueEventListener(valueEventListener)
    }

    override fun getFollowRequestById(id: String, user: User, valueEventListener: ValueEventListener) {
        firebaseDatabase.getReference(Constant.PATH_STRING_FOLLOW)
            .child(id)
            .child(Constant.PATH_STRING_FOLLOW_REQUEST)
            .child(user.id)
            .addValueEventListener(valueEventListener)
    }

    override fun addFollowRequest(user: User, userFriend: User) {
        firebaseDatabase.getReference(Constant.PATH_STRING_FOLLOW)
            .child(userFriend.id)
            .child(Constant.PATH_STRING_FOLLOW_REQUEST)
            .child(user.id)
            .setValue(FollowRequest(user.id, Constant.STATUS_WAITING, System.currentTimeMillis()))
    }

    override fun addFriend(
        user: User,
        friendRequest: FriendRequest
    ) {
        firebaseDatabase.getReference(Constant.PATH_STRING_USER)
            .child(user.id)
            .child(Constant.PATH_STRING_FRIEND)
            .child(friendRequest.id!!)
            .setValue(Friend(friendRequest.id!!, System.currentTimeMillis()))
    }

    override fun changeStatusFriendRequest(
        user: User,
        friendRequest: FriendRequest,
        status: Int,
        onSuccessListener: OnSuccessListener<Void>,
        onFailureListener: OnFailureListener
    ) {
        friendRequest.status = status
        firebaseDatabase.getReference(Constant.PATH_STRING_USER)
            .child(user.id)
            .child(Constant.PATH_STRING_FRIEND_REQUEST)
            .child(friendRequest.id!!)
            .setValue(friendRequest)
            .addOnSuccessListener(onSuccessListener)
            .addOnFailureListener(onFailureListener)
    }

    override fun confirmFriendRequest(
        user: User,
        friendRequest: FriendRequest,
        onSuccessListener: OnSuccessListener<Void>,
        onFailureListener: OnFailureListener
    ) {
        changeStatusFriendRequest(
            user, friendRequest, Constant.CONFIRM_FRIEND_REQUEST,
            onSuccessListener, onFailureListener
        )
    }

    override fun deleteFriendRequest(
        user: User,
        friendRequest: FriendRequest,
        onSuccessListener: OnSuccessListener<Void>,
        onFailureListener: OnFailureListener
    ) {
        changeStatusFriendRequest(
            user, friendRequest, Constant.DELETE_FRIEND_REQUEST,
            onSuccessListener, onFailureListener
        )
    }

    override fun getFriendRequests(userId: String, valueEventListener: ValueEventListener) {
        firebaseDatabase.getReference(Constant.PATH_STRING_USER)
            .child(userId)
            .child(Constant.PATH_STRING_FRIEND_REQUEST)
            .addValueEventListener(valueEventListener)
    }

    override fun getUserById(userId: String, singleValueEventListener: ValueEventListener) {
        firebaseDatabase.getReference(Constant.PATH_STRING_USER)
            .child(userId)
            .addListenerForSingleValueEvent(singleValueEventListener)
    }

    private val firebaseDatabase = FirebaseDatabase.getInstance()

    override fun addFriendRequest(
        reciveId: String,
        user: User,
        message: String,
        onSuccessListener: OnSuccessListener<Void>,
        onFailureListener: OnFailureListener
    ) {
        firebaseDatabase.getReference(Constant.PATH_STRING_USER).child(reciveId)
            .child(Constant.PATH_STRING_FRIEND_REQUEST)
            .child(user.id).setValue(FriendRequest(user.id, message, 0, System.currentTimeMillis()))
            .addOnSuccessListener(onSuccessListener)
            .addOnFailureListener(onFailureListener)
    }

    override fun getUsers(valueEventListener: ValueEventListener) {
        firebaseDatabase.getReference(Constant.PATH_STRING_USER)
            .addValueEventListener(valueEventListener)
    }

    override fun pushUserLocation(idUser: String, place: Place) {
        firebaseDatabase.getReference(Constant.PATH_STRING_LOCATION)
            .child(idUser)
            .child(place.id.toString())
            .setValue(place)
    }

    override fun getUserByPhoneNumber(phoneNumber: String, valueEventListener: ValueEventListener) {
        firebaseDatabase.getReference(Constant.PATH_STRING_USER)
            .child(phoneNumber)
            .addValueEventListener(valueEventListener)
    }

    override fun registerUser(
        user: User,
        onCompleteListener: OnCompleteListener<Void>,
        onFailureListener: OnFailureListener
    ) {
        firebaseDatabase.getReference(Constant.PATH_STRING_USER)
            .child(user.phoneNumber)
            .setValue(user)
            .addOnCompleteListener(onCompleteListener)
            .addOnFailureListener(onFailureListener)
    }

    override fun getFriendLocation(id: String, childEventListener: ChildEventListener) {
        firebaseDatabase.getReference(Constant.PATH_STRING_LOCATION)
            .child(id)
            .addChildEventListener(childEventListener)
    }
}
