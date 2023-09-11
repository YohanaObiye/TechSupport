package com.example.techsupport.models

import com.example.techsupport.Constants

data class User(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val createdAt: Long = 0L,
    val profile: String? = null,
    val category: String? = null,
    val role: String = Constants.USER_ROLE
)

data class Chat(
    val id: String = "",
    val userId: String = "", // Store the userId to link to the User
    val request: String = "", // The user's message
    val response: Map<String, Any> = emptyMap(), // The AI's response in JSON format
    val createdAt: Long = 0L, // Timestamp of when the chat occurred (defaults to current time)
    val duration: Long = 0L // Duration in seconds between request and response
)

data class Device(
    val id: String = "",
    val name: String = "",
    val imageUrl: String = "",
    val createdAt: Long = 0L
)

data class KB(
    val id: String = "",
    val title: String = "",
    val category: String = "",
    val content: String = "",
    val imageUrl: String = "",
    val createdAt: Long = 0L
)

// Model for Unsplash API response
data class UnsplashPhotoResponse(
    val results: List<UnsplashPhoto>
)

data class UnsplashPhoto(
    val urls: UnsplashPhotoUrls
)

data class UnsplashPhotoUrls(
    val regular: String
)
