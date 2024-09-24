package com.braiso22.turnos.tasks.data

import android.util.Log
import com.braiso22.turnos.common.Resource
import com.braiso22.turnos.common.TASKS_COLLECTION
import com.braiso22.turnos.tasks.domain.Task
import com.braiso22.turnos.tasks.domain.TasksRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map

class TaskRepositoryImpl(
    private val firebaseDB: FirebaseFirestore,
) : TasksRepository {

    private val TAG = "TaskRepositoryImpl"

    override fun saveTask(task: Task): Flow<Resource<Task>> {
        val document = firebaseDB.collection(TASKS_COLLECTION).document()

        val taskWithId = task.copy(id = document.id)
        return callbackFlow {
            trySend(Resource.Loading())

            document.set(taskWithId).addOnSuccessListener {
                Log.i(TAG, "Task saved successfully")
                trySend(Resource.Success(task))
            }.addOnFailureListener {
                Log.i(TAG, "Task save failed")
                trySend(Resource.Error(it))
            }
            awaitClose()
        }
    }

    override fun getTasks(): Flow<List<Task>> {
        return firebaseDB.collection(TASKS_COLLECTION).snapshots().map {
            it.toObjects(TaskDto::class.java).map { taskDto ->
                taskDto.toDomain()
            }
        }
    }
}