package com.braiso22.turnos.common

/**
 * A generic class that holds a value with its status for the response from APIs or callbacks
 * @param data The type of the data, may be null on loading or error
 * @param exception The error, that caused this state
 */
sealed class Resource<T>(val data: T? = null, private val exception: Exception = Exception()) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(exception: Exception, data: T? = null) : Resource<T>(data, exception)
    class Loading<T>(data: T? = null) : Resource<T>(data)
}