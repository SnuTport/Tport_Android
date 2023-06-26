package com.example.tport.network

import com.example.tport.network.dto.*
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface RestService {
    @POST("api/v1/signup")
    suspend fun signup(@Body request: SignupRequest): SignupResponse
    @POST("api/v1/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse
    @POST("api/v1/reservation")
    suspend fun reservation(@Body request: ReservationRequest): ReservationResponse
    @GET("api/v1/path/search")
    suspend fun searchPathList(
        @Query("originName") origin: String,
        @Query("destinationName") destination: String,
        @Query("departureTime") time: String,
    ): List<Path>

    @GET("api/v1/path/{pathGroupId}")
    suspend fun getPath(
        @retrofit2.http.Path("pathGroupId") pathGroupId: Int,
        @Query("departureTime") time: String,
    ): Path
}