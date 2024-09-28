package com.braiso22.turnos.users.data

import com.braiso22.turnos.common.Resource
import com.braiso22.turnos.common.USERS_COLLECTION
import com.braiso22.turnos.users.domain.User
import com.braiso22.turnos.users.domain.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class UserRepositoryImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : UserRepository {
    override fun emailSignIn(email: String, password: String): Flow<Resource<Unit>> = callbackFlow {
        trySend(Resource.Loading())

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                trySend(Resource.Success(Unit))
            } else {
                trySend(Resource.Error(it.exception ?: Exception("Error on sign in")))
            }
        }
        awaitClose { }
    }

    override fun emailLogin(email: String, password: String): Flow<Resource<Unit>> = callbackFlow {
        trySend(Resource.Loading())

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                trySend(Resource.Success(Unit))
            } else {
                trySend(Resource.Error(it.exception ?: Exception("Error on log in")))
            }
        }
        awaitClose { }
    }

    override fun registerUser(
        email: String,
        username: String,
    ): Flow<Resource<Unit>> = callbackFlow {
        trySend(Resource.Loading())
        val document = firestore.collection(USERS_COLLECTION).document()

        val user = UserDto(
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


    override suspend fun getUserById(id: String): User? {
        return firestore.collection(USERS_COLLECTION)
            .document(id)
            .get()
            .await()
            .toObject(
                UserDto::class.java
            )?.toDomain()
    }
}