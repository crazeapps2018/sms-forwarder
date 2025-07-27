package com.info.smsforwarder.api

import com.info.smsforwarder.model.ApiResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @POST("Home/updatestatus")
    suspend fun updateAppStatus(
        @Query("userMobileNumber") mobileNumber: String,
        @Query("appStatus") appStatus: String = "Installed"
    ): Response<ApiResponse>


}
