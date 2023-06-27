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

    private val _path = MutableStateFlow<Path?>(null)
    val path: StateFlow<Path?> = _path

    suspend fun searchPath(origin: String, destination: String, time: String) {
        try {
            val response: List<Path> = restService.searchPathList(origin, destination, time)
            _pathList.value = response
        } catch (e: Exception) {
            Log.d("Error", "Error is occurred. Error: $e")
        }
    }

    suspend fun reservePath(path: Path, time:String){
        try {
            val id = path.metroBusDetail.busId
            val busStop = path.getOnBusStop
            val response = restService.reservation(id, busStop, time, ReservationRequest(id, busStop, time))
        } catch (e: Exception) {
            Log.d("Error", "Error is occurred. Error: $e")
        }
    }

    suspend fun getPath(path: Path, time: String){
        try {
            val response: Path = restService.getPath(path.id, time)
            _path.value = response
        } catch (e: Exception) {
            Log.d("Error", "Error is occurred. Error: $e")
        }
    }
}