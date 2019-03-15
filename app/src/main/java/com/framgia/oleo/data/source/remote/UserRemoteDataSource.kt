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
import com.google.android.gms.tasks.Task
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserRemoteDataSource : UserDataSource.Remote {
    override fun checkFriendByUserId(userId: String, friendId: String, valueEventListener: ValueEventListener) {
        firebaseDatabase.getReference(Constant.PATH_STRING_USER)
            .child(userId)
            .child(Constant.PATH_STRING_FRIEND)
            .child(friendId)
            .addValueEventListener(valueEventListener)
    }

    override fun deleteFriend(userId: String, friendId: String) {
        firebaseDatabase.getReference(Constant.PATH_STRING_USER)
            .child(userId)
            .child(Constant.PATH_STRING_FRIEND)
            .child(friendId)
            .removeValue()
            .addOnSuccessListener {
                firebaseDatabase.getReference(Constant.PATH_STRING_USER)
                    .child(friendId)
                    .child(Constant.PATH_STRING_FRIEND)
                    .child(userId)
                    .removeValue()
            }
    }

    override fun getFollowRequestSingleValueEvent(
        userCurrent: User,
        userFriend: User,
        valueEventListener: ValueEventListener
    ) {
        firebaseDatabase.getReference(Constant.PATH_STRING_FOLLOW)
            .child(userFriend.id)
            .child(Constant.PATH_STRING_FOLLOW_REQUEST)
            .child(userCurrent.id)
            .addListenerForSingleValueEvent(valueEventListener)
    }

    override fun deleteUserFollowed(id: String, followed: Followed) {
        firebaseDatabase.getReference(Constant.PATH_STRING_FOLLOW)
            .child(id)
            .child(Constant.PATH_STRING_FOLLOWED)
            .child(followed.id!!)
            .removeValue()
        firebaseDatabase.getReference(Constant.PATH_STRING_FOLLOW)
            .child(followed.id!!)
            .child(Constant.PATH_STRING_FOLLOW_REQUEST)
            .child(id)
            .removeValue()
    }

    override fun getFollowedsOfUser(id: String, childEventListener: ChildEventListener) {
        firebaseDatabase.getReference(Constant.PATH_STRING_FOLLOW)
            .child(id)
            .child(Constant.PATH_STRING_FOLLOWED)
            .addChildEventListener(childEventListener)
    }

    override fun addUserFollowed(idUser: String, userFollowed: User) {
        firebaseDatabase.getReference(Constant.PATH_STRING_FOLLOW)
            .child(idUser)
            .child(Constant.PATH_STRING_FOLLOWED)
            .child(userFollowed.id)
            .setValue(
                Followed(
                    userFollowed.id,
                    System.currentTimeMillis(),
                    userFollowed.userName,
                    userFollowed.image,
                    userFollowed.phoneNumber
                )
            )
    }

    override fun updatePassword(
        userId: String, password: String,
        onSuccessListener: OnSuccessListener<Void>,
        onFailureListener: OnFailureListener
    ) {
        firebaseDatabase.getReference(Constant.PATH_STRING_USER)
            .child(userId)
            .child(Constant.PATH_STRING_PASSWORD)
            .setValue(password)
            .addOnSuccessListener(onSuccessListener)
            .addOnFailureListener(onFailureListener)
    }

    override fun changeFollowStatus(userCurrent: User, followRequest: FollowRequest) {
        firebaseDatabase.getReference(Constant.PATH_STRING_FOLLOW)
            .child(userCurrent.id)
            .child(Constant.PATH_STRING_FOLLOW_REQUEST)
            .child(followRequest.id!!)
            .child(Constant.PATH_STRING_STATUS)
            .setValue(followRequest.status)
    }

    override fun getFollowRequestsOfUser(id: String, status: String, childEventListener: ChildEventListener) {
        firebaseDatabase.getReference(Constant.PATH_STRING_FOLLOW)
            .child(id)
            .child(Constant.PATH_STRING_FOLLOW_REQUEST)
            .orderByChild(Constant.PATH_STRING_STATUS)
            .equalTo(status)
            .addChildEventListener(childEventListener)
    }

    override fun getFollowRequestById(id: String, user: User, valueEventListener: ValueEventListener) {
        firebaseDatabase.getReference(Constant.PATH_STRING_FOLLOW)
            .child(id)
            .child(Constant.PATH_STRING_FOLLOW_REQUEST)
            .child(user.id)
            .addValueEventListener(valueEventListener)
    }

    override fun addFollowRequest(
        userCurrent: User,
        userFriend: User,
        onFailureListener: OnFailureListener,
        onSuccessListener: OnSuccessListener<Void>
    ) {
        firebaseDatabase.getReference(Constant.PATH_STRING_FOLLOW)
            .child(userFriend.id)
            .child(Constant.PATH_STRING_FOLLOW_REQUEST)
            .child(userCurrent.id)
            .setValue(
                FollowRequest(
                    userCurrent.id,
                    Constant.STATUS_WAITING,
                    System.currentTimeMillis(),
                    userCurrent.userName,
                    userCurrent.image,
                    userCurrent.phoneNumber
                )
            )
            .addOnFailureListener(onFailureListener)
            .addOnSuccessListener(onSuccessListener)
    }

    override fun addFriend(
        userId: String,
        friendRequestId: String
        , user: User, friend: User
    ) {
        firebaseDatabase.getReference(Constant.PATH_STRING_USER)
            .child(userId)
            .child(Constant.PATH_STRING_FRIEND)
            .child(friendRequestId)
            .setValue(Friend(friendRequestId, System.currentTimeMillis(), friend))
            .addOnSuccessListener {
                firebaseDatabase.getReference(Constant.PATH_STRING_USER)
                    .child(friendRequestId)
                    .child(Constant.PATH_STRING_FRIEND)
                    .child(userId)
                    .setValue(Friend(userId, System.currentTimeMillis(), user))
                firebaseDatabase.getReference(Constant.PATH_STRING_FRIEND_REQUEST)
                    .child(friendRequestId)
                    .child(userId)
                    .ref.removeValue()
            }
    }

    override fun confirmFriendRequest(
        user: User, friend: User,
        friendRequest: FriendRequest,
        onSuccessListener: OnSuccessListener<Void>,
        onFailureListener: OnFailureListener
    ) {
        addFriend(user.id, friendRequest.id!!, user, friend)
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

    override fun getContactsUser(userId: String, valueEventListener: ValueEventListener) {
        firebaseDatabase.reference
            .child(Constant.PATH_STRING_USER)
            .child(userId)
            .child(Constant.PATH_STRING_FRIEND)
            .addValueEventListener(valueEventListener)
    }

    override fun updateNameFriend(userId: String, friendId : String, newName: String,
        onSuccessListener: OnSuccessListener<Void>, onFailureListener: OnFailureListener) {
        firebaseDatabase.reference
            .child(Constant.PATH_STRING_USER)
            .child(userId)
            .child(Constant.PATH_STRING_FRIEND)
            .child(friendId)
            .child(Constant.PATH_STRING_USER_FRIEND)
            .child(Constant.PATH_STRING_USER_NAME_VALUE)
            .setValue(newName)
            .addOnSuccessListener {
                firebaseDatabase.reference.child(Constant.PATH_STRING_USER)
                    .child(userId)
                    .child(Constant.PATH_STRING_BOX)
                    .child(friendId)
                    .child(Constant.PATH_STRING_USER_FRIEND_VALUE)
                    .setValue(newName)
                    .addOnSuccessListener(onSuccessListener)
                    .addOnFailureListener(onFailureListener)
            }
    }

    override fun getFriendById(userId: String, friendId: String,
        onValueEventListener: ValueEventListener) {
        firebaseDatabase.reference
            .child(Constant.PATH_STRING_USER)
            .child(userId)
            .child(Constant.PATH_STRING_FRIEND)
            .child(friendId)
            .addValueEventListener(onValueEventListener)
    }
}
