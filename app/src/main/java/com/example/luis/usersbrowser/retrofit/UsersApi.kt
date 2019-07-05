package com.example.luis.usersbrowser.retrofit

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface UsersApi {

    @GET("/api/")
    fun getRates(@Query("page") page: Int,
                 @Query("results") results: Int,
                 @Query("seed") seed: String): Observable<UsersApiResponse>


}