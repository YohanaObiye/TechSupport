package com.example.techsupport

import com.example.techsupport.models.UnsplashPhotoResponse
import retrofit2.http.GET
import retrofit2.http.Query

// Unsplash API interface
interface UnsplashApiService {
    @GET("search/photos")
    suspend fun searchPhotos(
        @Query("client_id") clientId: String,
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 1 // Only fetch one image
    ): UnsplashPhotoResponse
}