package com.example.connect

import retrofit2.Response
import retrofit2.http.GET

interface MemeApi {
    @GET("gimme")
    suspend fun getMeme(): Response<MemeModel>
}
