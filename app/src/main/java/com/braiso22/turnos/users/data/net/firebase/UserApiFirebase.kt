package com.braiso22.turnos.users.data.net.firebase

import com.braiso22.turnos.common.Resource
import com.braiso22.turnos.common.USERS_COLLECTION
import com.braiso22.turnos.users.data.net.api.UserApi
import com.braiso22.turnos.users.domain.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map

class UserApiFirebase(
    private val firestore: FirebaseFirestore,
) : UserApi {
    override fun registerUser(email: String, username: String): Flow<Resource<Unit>> =
        callbackFlow {
            trySend(Resource.Loading())
            val document = firestore.collection(USERS_COLLECTION).document()

            val user = UserFirebase(
                id = document.id,
                email = email,
                username = username,
            )

            document.set(user).addOnSuccessListener {
                trySend(Resource.Success(Unit))
            }.addOnFailureListener {
                trySend(Resource.Error(it))
            }

            awaitClose { }
        }

    override fun listenOnlineUsers(): Flow<List<User>> {
        return firestore.collection(USERS_COLLECTION).snapshots().map { snaps ->
            snaps.toObjects(UserFirebase::class.java).map { dto ->
                dto.toDomain()
            }
        }
    }
}