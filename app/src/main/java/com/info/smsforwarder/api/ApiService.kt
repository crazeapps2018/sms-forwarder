package com.info.smsforwarder.api

import com.info.smsforwarder.model.SmsRequest
import com.info.smsforwarder.model.SmsStatus
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("api/poll")
    suspend fun poll(@Body creds: Map<String, String>): Response<SmsRequest?>

    @POST("api/status")
    suspend fun status(@Body status: SmsStatus): Response<Void>
}
