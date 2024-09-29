package com.braiso22.turnos.executions.data

import android.util.Log
import com.braiso22.turnos.common.EXECUTIONS_COLLECTION
import com.braiso22.turnos.common.Resource
import com.braiso22.turnos.common.USERS_COLLECTION
import com.braiso22.turnos.users.data.net.firebase.UserFirebase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class FirebaseApiImpl(
    private val firestore: FirebaseFirestore,
) : FirebaseApi {
    override fun getUserByEmail(email: String): Flow<UserFirebase?> = callbackFlow {
        firestore.collection(USERS_COLLECTION)
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener {
                val documentUserId = it.documents.map {
                    it.toObject(UserFirebase::class.java)
                }.first()
                trySend(documentUserId)
            }
        awaitClose()
    }

    override fun confirmExecution(executionId: String): Flow<Resource<Unit>> {
        val document = firestore.collection(EXECUTIONS_COLLECTION).document(executionId)
        return callbackFlow {
            trySend(Resource.Loading())
            Log.i("FirebaseApiImpl", "Loading confirm execution")
            document
                .update("confirmed", true)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        trySend(Resource.Success(Unit))
                    } else {
                        trySend(
                            Resource.Error(
                                it.exception ?: Exception("Error confirming execution")
                            )
                        )
                    }
                }
            awaitClose()
        }
    }
}