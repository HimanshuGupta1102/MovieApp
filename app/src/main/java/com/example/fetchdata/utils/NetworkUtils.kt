package com.example.fetchdata.utils

object NetworkUtils {

    fun getErrorMessage(exception: Exception): String {
        return when {
            exception.message?.contains("Unable to resolve host", ignoreCase = true) == true ->
                "No internet connection. Please check your network."
            exception.message?.contains("timeout", ignoreCase = true) == true ->
                "Request timed out. Please try again."
            exception.message?.contains("SSL", ignoreCase = true) == true ->
                "Secure connection failed. Please check your internet connection and try again."
            exception.message?.contains("SocketTimeoutException", ignoreCase = true) == true ->
                "Connection timed out. Please try again."
            exception.message?.contains("ConnectException", ignoreCase = true) == true ->
                "Unable to connect to server. Please try again."
            exception.message?.contains("401") == true || exception.message?.contains("403") == true ->
                "Authentication failed. Invalid API key."
            exception.message?.contains("404") == true ->
                "Resource not found."
            exception.message?.contains("500") == true ->
                "Server error. Please try again later."
            else -> "An error occurred: ${exception.message ?: "Unknown error"}"
        }
    }
}
