package com.info.smsforwarder.model

data class SmsRequest(
    val msisdn: String,
    val message: String,
    val id: String
)
