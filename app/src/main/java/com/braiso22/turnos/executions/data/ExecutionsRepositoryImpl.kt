package com.braiso22.turnos.executions.data

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.braiso22.turnos.common.EXECUTIONS_COLLECTION
import com.braiso22.turnos.common.Resource
import com.braiso22.turnos.common.USER_ID_PREFERENCE
import com.braiso22.turnos.executions.data.local.ExecutionDao
import com.braiso22.turnos.executions.data.local.toEntity
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
    private val executionDao: ExecutionDao,
) : ExecutionsRepository {

    private val tag = "ExecutionsRepositoryImpl"

    override fun saveExecution(execution: Execution): Flow<Resource<Execution>> = callbackFlow {
        trySend(Resource.Loading())
        Log.i(tag, "Loading save execution")
        val userId = getUserId()
        val document = firebaseDB.collection(EXECUTIONS_COLLECTION).document()
        val executionWithId = execution.copy(id = document.id, userId = userId)

        document.set(executionWithId.toDto()).addOnCompleteListener {
            if (it.isSuccessful) {
                Log.i(tag, "Save execution isSuccessful")
                trySend(Resource.Success(executionWithId))
            } else {
                Log.i(tag, "Save execution is not successful")
                trySend(Resource.Error(it.exception ?: Exception("Error saving execution")))
            }
        }
        awaitClose()
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
        return firebaseDB.collection(EXECUTIONS_COLLECTION).snapshots().map {
            it.toObjects(ExecutionDto::class.java).map { taskDto ->
                taskDto.toDomain()
            }.sortedByDescending {
                it.dateTime
            }
        }
    }

    override fun getExecutionsByTaskIds(taskId: String): Flow<List<Execution>> {
        return executionDao.getByTaskId(taskId).map { executions ->
            executions.map { it.toDomain() }
        }
    }

    override fun listenOnlineExecutions(): Flow<List<Execution>> {
        return firebaseDB.collection(EXECUTIONS_COLLECTION)
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(ExecutionDto::class.java)
                    .map { dto ->
                        dto.toDomain()
                    }.sortedByDescending {
                        it.dateTime
                    }
            }
    }

    override suspend fun syncExecutions(executions: List<Execution>) {
        if (executions.isEmpty())
            return executionDao.deleteAll()

        executionDao.insertAll(
            executions.map {
                it.toEntity()
            }
        )
    }

    override fun getExecutionsByOtherUsers(excludeUserId: String): Flow<List<Execution>> {
        return executionDao.getExecutionsByOtherUsers(excludeUserId).map {
            it.map { executionEntity ->
                executionEntity.toDomain()
            }
        }
    }

    override fun confirmExecution(executionId: String): Flow<Resource<Unit>> {
        Log.i(tag, "Loading confirm execution")
        return firebaseApi.confirmExecution(executionId)
    }
}
