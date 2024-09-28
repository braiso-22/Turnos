package com.braiso22.turnos.executions.data

import com.braiso22.turnos.common.USERS_COLLECTION
import com.braiso22.turnos.users.data.UserDto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class FirebaseApiImpl(
    private val firestore: FirebaseFirestore,
) : FirebaseApi {
    override fun getUserByEmail(email: String): Flow<UserDto?> = callbackFlow {
        firestore.collection(USERS_COLLECTION)
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener {
                val documentUserId = it.documents.map {
                    it.toObject(UserDto::class.java)
                }.first()
                trySend(documentUserId)
            }
        awaitClose()
    }
}