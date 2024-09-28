package com.braiso22.turnos.executions.data

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.braiso22.turnos.common.EXECUTIONS_COLLECTION
import com.braiso22.turnos.common.Resource
import com.braiso22.turnos.common.USER_ID_PREFERENCE
import com.braiso22.turnos.executions.domain.Execution
import com.braiso22.turnos.executions.domain.ExecutionsRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore("settings")

class ExecutionsRepositoryImpl(
    private val firebaseDB: FirebaseFirestore,
    private val firebaseApi: FirebaseApi,
    private val auth: FirebaseAuth,
    private val context: Context,
) : ExecutionsRepository {

    private val tag = "ExecutionsRepositoryImpl"

    override fun saveExecution(execution: Execution): Flow<Resource<Execution>> {
        val document = firebaseDB.collection(EXECUTIONS_COLLECTION).document()
        return callbackFlow {
            trySend(Resource.Loading())

            val userId = getUserId()
            val executionWithId = execution.copy(id = document.id, userId = userId)

            document.set(executionWithId.toDto()).addOnSuccessListener {
                Log.i(tag, "Execution saved successfully")
                trySend(Resource.Success(execution))
            }.addOnFailureListener {
                Log.i(tag, "Execution save failed")
                trySend(Resource.Error(it))
            }
            awaitClose()
        }
    }

    private suspend fun getUserId(): String {
        val userId: String? = context.dataStore.data.map {
            it[stringPreferencesKey(USER_ID_PREFERENCE)]
        }.first()
        return if (userId != null) {
            userId
        } else {
            val email = auth.currentUser?.email ?: ""
            val userDto = firebaseApi.getUserByEmail(email).first()
            val extractedUserId = userDto?.id ?: ""
            context.dataStore.edit {
                it[stringPreferencesKey(USER_ID_PREFERENCE)] = extractedUserId
            }
            extractedUserId
        }
    }

    override fun getExecutions(): Flow<List<Execution>> {
        return firebaseDB.collection("executions").snapshots().map {
            it.toObjects(ExecutionDto::class.java).map { taskDto ->
                taskDto.toDomain()
            }.sortedByDescending {
                it.dateTime
            }
        }
    }

    override fun getExecutionsByTaskIds(taskId: String): Flow<List<Execution>> {
        return firebaseDB.collection("executions")
            .whereEqualTo("taskId", taskId)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(ExecutionDto::class.java).map { dto ->
                    dto.toDomain()
                }.sortedByDescending {
                    it.dateTime
                }
            }
    }
}