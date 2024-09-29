package com.braiso22.turnos.users.data.net.firebase

import com.braiso22.turnos.common.Resource
import com.braiso22.turnos.users.data.net.api.AuthApi
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class AuthApiFirebase(
    private val auth: FirebaseAuth,
) : AuthApi {
    override fun signIn(
        email: String,
        password: String,
    ): Flow<Resource<Unit>> = callbackFlow {
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


    override fun login(email: String, password: String): Flow<Resource<Unit>> = callbackFlow {
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

    override suspend fun getCurrentUserEmail(): String? {
        return auth.currentUser?.email
    }
}
