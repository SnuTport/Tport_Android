package com.example.tport.network.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
@Parcelize
data class User(
    val id: Int,
    val name: String,
//    val password: String,
//    val reservationInformation: ReservationInformation? = null
) : Parcelable

@Parcelize
data class Path(
    val id: Int,
    val getOnBusStop: String,
    val getOffBusStop: String,
    val bus: Bus,
    val fare: Int,
    val travelTime: Int
) : Parcelable

@Parcelize
data class Bus(
    val busId: Int,
    val busNum: String,
    val capacity: Int,
    val departureTime: LocalTime,
    val busStop: List<BusStopInDetail>
) : Parcelable

@Parcelize
data class BusStopInDetail(
    val name: String,
    val busArrivalTime: LocalTime,
    val forecastingBusStopData: ForecastingBusStopData,
    val actualBusStopData: ActualBusStopData
) : Parcelable

@Parcelize
data class ForecastingBusStopData(
    val demand: Int,
    val emptyNum: Int,
    val unreservedNum: Int,
    val reservedNum: Int,
    val reservationList: List<User>
) : Parcelable

@Parcelize
data class ActualBusStopData(
    val demand: Int,
    val emptyNum: Int,
    val unreservedNum: Int,
    val reservedNum: Int,
    val reservationList: List<User>
) : Parcelable

data class ReservationInformation(
    val busID: Int,
    val busStopID: Int,
    val seatNum: Int,
    val time: Int
)
@Parcelize
data class LocalTime(
    val hour: Int,
    val minute: Int,
    val second: Int,
    val nano: Int
) : Parcelable
