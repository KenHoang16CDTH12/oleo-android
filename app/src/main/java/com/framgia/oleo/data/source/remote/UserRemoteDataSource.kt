package com.framgia.oleo.data.source.remote

import com.framgia.oleo.data.source.UserDataSource
import com.framgia.oleo.data.source.model.FollowRequest
import com.framgia.oleo.data.source.model.Followed
import com.framgia.oleo.data.source.model.Friend
import com.framgia.oleo.data.source.model.FriendRequest
import com.framgia.oleo.data.source.model.Place
import com.framgia.oleo.data.source.model.User
import com.framgia.oleo.utils.Constant
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserRemoteDataSource : UserDataSource.Remote {
    override fun addUserFollowed(idUser: String, userFollowed: User) {
        firebaseDatabase.getReference(Constant.PATH_STRING_FOLLOW)
            .child(idUser)
            .child(Constant.PATH_STRING_FOLLOWED)
            .child(idUser)
            .setValue(Followed(userFollowed.id,System.currentTimeMillis()))
    }

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

    override fun addFollowRequest(userCurrent: User, userFriend: User) {
        firebaseDatabase.getReference(Constant.PATH_STRING_FOLLOW)
            .child(userFriend.id)
            .child(Constant.PATH_STRING_FOLLOW_REQUEST)
            .child(userCurrent.id)
            .setValue(FollowRequest(userCurrent.id, Constant.STATUS_WAITING, System.currentTimeMillis()))
    }

    override fun addFriend(
        userId: String,
        friendRequestId: String
    ) {
        firebaseDatabase.getReference(Constant.PATH_STRING_USER)
            .child(userId)
            .child(Constant.PATH_STRING_FRIEND)
            .child(friendRequestId)
            .setValue(Friend(friendRequestId, System.currentTimeMillis()))
            .addOnSuccessListener {
                firebaseDatabase.getReference(Constant.PATH_STRING_USER)
                    .child(friendRequestId)
                    .child(Constant.PATH_STRING_FRIEND)
                    .child(userId)
                    .setValue(Friend(friendRequestId, System.currentTimeMillis()))
                firebaseDatabase.getReference(Constant.PATH_STRING_FRIEND_REQUEST)
                    .child(friendRequestId)
                    .child(userId)
                    .ref.removeValue()
            }
    }

    override fun confirmFriendRequest(
        user: User,
        friendRequest: FriendRequest,
        onSuccessListener: OnSuccessListener<Void>,
        onFailureListener: OnFailureListener
    ) {
        addFriend(user.id, friendRequest.id!!)
        firebaseDatabase.getReference(Constant.PATH_STRING_FRIEND_REQUEST)
            .child(user.id)
            .child(friendRequest.id!!)
            .ref.removeValue()
    }

    override fun deleteFriendRequest(
        user: User,
        friendRequest: FriendRequest,
        onSuccessListener: OnSuccessListener<Void>,
        onFailureListener: OnFailureListener
    ) {
        firebaseDatabase.getReference(Constant.PATH_STRING_FRIEND_REQUEST)
            .child(user.id)
            .child(friendRequest.id!!)
            .ref.removeValue()
    }

    override fun getFriendRequests(userId: String, valueEventListener: ValueEventListener) {
        firebaseDatabase.getReference(Constant.PATH_STRING_FRIEND_REQUEST)
            .child(userId)
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
        firebaseDatabase.getReference(Constant.PATH_STRING_USER)
            .child(user.id)
            .child(Constant.PATH_STRING_FRIEND)
            .child(reciveId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {}

                override fun onDataChange(data: DataSnapshot) {
                    if (!data.exists())
                        firebaseDatabase.getReference(Constant.PATH_STRING_FRIEND_REQUEST)
                            .child(reciveId)
                            .child(user.id)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError) {}

                                override fun onDataChange(snapShot: DataSnapshot) {
                                    if (!snapShot.exists())
                                        firebaseDatabase.getReference(Constant.PATH_STRING_FRIEND_REQUEST)
                                            .child(reciveId)
                                            .child(user.id)
                                            .setValue(
                                                FriendRequest(
                                                    user.id,
                                                    message,
                                                    Constant.STATUS_FRIEND_WAITING,
                                                    System.currentTimeMillis()
                                                )
                                            )
                                            .addOnSuccessListener(onSuccessListener)
                                            .addOnFailureListener(onFailureListener)
                                }
                            })
                }
            })
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
