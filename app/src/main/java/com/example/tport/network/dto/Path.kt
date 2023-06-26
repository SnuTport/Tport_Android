package com.example.tport.network.dto

import java.time.LocalDateTime

data class SearchRequest(
    val originName: String,
    val destinationName: String,
    val departureTime: String,
)

data class SearchResponse(
    val id: Int,
    val getOnBusStop: String,
    val getOffBusStop: String,
    val bus: Bus,
    val fare: Int,
    val travelTime: Int
)