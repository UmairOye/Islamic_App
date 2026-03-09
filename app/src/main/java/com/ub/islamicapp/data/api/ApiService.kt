package com.ub.islamicapp.data.api

import retrofit2.http.GET

interface ApiService {
    @GET("endpoint")
    suspend fun getItems(): Any
}
