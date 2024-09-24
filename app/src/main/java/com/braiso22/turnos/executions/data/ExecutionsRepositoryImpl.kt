package com.braiso22.turnos.executions.data

import android.util.Log
import com.braiso22.turnos.common.EXECUTIONS_COLLECTION
import com.braiso22.turnos.common.Resource
import com.braiso22.turnos.executions.domain.Execution
import com.braiso22.turnos.executions.domain.ExecutionsRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map

class ExecutionsRepositoryImpl(
    private val firebaseDB: FirebaseFirestore,
) : ExecutionsRepository {

    private val TAG = "ExecutionsRepositoryImpl"

    override fun saveExecution(execution: Execution): Flow<Resource<Execution>> {
        val document = firebaseDB.collection(EXECUTIONS_COLLECTION).document()

        val executionWithId = execution.copy(id = document.id)
        return callbackFlow {
            trySend(Resource.Loading())

            document.set(executionWithId.toDto()).addOnSuccessListener {
                Log.i(TAG, "Execution saved successfully")
                trySend(Resource.Success(execution))
            }.addOnFailureListener {
                Log.i(TAG, "Execution save failed")
                trySend(Resource.Error(it))
            }
            awaitClose()
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