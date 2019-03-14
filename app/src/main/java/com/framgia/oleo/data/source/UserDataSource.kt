package com.framgia.oleo.data.source

import com.framgia.oleo.data.source.model.FollowRequest
import com.framgia.oleo.data.source.model.Followed
import com.framgia.oleo.data.source.model.FriendRequest
import com.framgia.oleo.data.source.model.Place
import com.framgia.oleo.data.source.model.User
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.ValueEventListener

interface UserDataSource {
    interface Local {
        fun getUser(): User?

        fun insertUser(vararg users: User)

        fun deleteUser()

        fun updateUser(vararg users: User)
    }

    interface Remote {
        fun getFollowRequestSingleValueEvent(
            userCurrent: User,
            userFriend: User,
            valueEventListener: ValueEventListener
        )

        fun deleteUserFollowed(id: String, followed: Followed)

        fun getFollowedsOfUser(id: String, childEventListener: ChildEventListener)

        fun addUserFollowed(idUser: String, userFollowed: User)

        fun pushUserLocation(idUser: String, place: Place)

        fun changeFollowStatus(userCurrent: User, followRequest: FollowRequest)

        fun getFollowRequestsOfUser(id: String, status: String, childEventListener: ChildEventListener)

        fun getFollowRequestById(id: String, user: User, valueEventListener: ValueEventListener)

        fun getUserByPhoneNumber(phoneNumber: String, valueEventListener: ValueEventListener)

        fun registerUser(
            user: User,
            onCompleteListener: OnCompleteListener<Void>,
            onFailureListener: OnFailureListener
        )

        fun getUsers(valueEventListener: ValueEventListener)

        fun getFriendLocation(id: String, childEventListener: ChildEventListener)

        fun addFriendRequest(
            reciveId: String,
            user: User,
            message: String,
            onSuccessListener: OnSuccessListener<Void>,
            onFailureListener: OnFailureListener
        )

        fun getUserById(userId: String, singleValueEventListener: ValueEventListener)

        fun getFriendRequests(userId: String, valueEventListener: ValueEventListener)

        fun confirmFriendRequest(
            user: User, friend: User,
            friendRequest: FriendRequest,
            onSuccessListener: OnSuccessListener<Void>,
            onFailureListener: OnFailureListener
        )

        fun deleteFriendRequest(
            user: User,
            friendRequest: FriendRequest,
            onSuccessListener: OnSuccessListener<Void>,
            onFailureListener: OnFailureListener
        )

        fun addFriend(userId: String, friendRequestId: String, user: User, friend: User)

        fun addFollowRequest(
            userCurrent: User,
            userFriend: User,
            onFailureListener: OnFailureListener,
            onSuccessListener: OnSuccessListener<Void>
        )

        fun getContactsUser(userId: String, valueEventListener: ValueEventListener)

        fun updatePassword(
            userId: String, password: String,
            onSuccessListener: OnSuccessListener<Void>,
            onFailureListener: OnFailureListener
        )

        fun deleteFriend(userId: String, friendId: String)

        fun checkFriendByUserId(userId: String, friendId: String, valueEventListener: ValueEventListener)
    }
}
