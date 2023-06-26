package com.example.tport.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.tport.network.RestService
import com.example.tport.network.dto.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDateTime

class PathViewModel(
    private val restService: RestService
): ViewModel() {
    private val _pathList = MutableStateFlow<List<Path?>>(listOf())
    val pathList: StateFlow<List<Path?>> = _pathList

    suspend fun searchPath(origin: String, destination: String, time: String) {
        try {
            val response: List<Path> = restService.searchPath(origin, destination, time)
            _pathList.value = response
        } catch (e: Exception) {
            Log.d("Error", "Error is occurred. Error: $e")
        }
    }

    suspend fun reservePath(id: Int, busStop: String, time: String){
        try {
            val response: ReservationResponse = restService.reservation(ReservationRequest(id, busStop, time))
        } catch (e: Exception) {
            Log.d("Error", "Error is occurred. Error: $e")
        }
    }
}