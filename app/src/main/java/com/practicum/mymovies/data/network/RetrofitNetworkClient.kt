package com.practicum.mymovies.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.practicum.mymovies.data.NetworkClient
import com.practicum.mymovies.data.dto.MovieDetailsRequest
import com.practicum.mymovies.data.dto.MovieCastRequest
import com.practicum.mymovies.data.dto.MoviesSearchRequest
import com.practicum.mymovies.data.dto.NamesSearchRequest
import com.practicum.mymovies.data.dto.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RetrofitNetworkClient(
    private val imdbService: IMDbApiService,
    private val context: Context,
) : NetworkClient {

    override suspend fun doRequest(dto: Any): Response {
        if (!isConnected()) {
            return Response().apply { resultCode = -1 }
        }

        return when (dto) {
            is MoviesSearchRequest -> safeApiCall { imdbService.searchMovies(dto.expression) }
            is MovieDetailsRequest -> safeApiCall { imdbService.getMovieDetails(dto.movieId) }
            is MovieCastRequest -> safeApiCall { imdbService.getFullCast(dto.movieId) }
            is NamesSearchRequest -> safeApiCall { imdbService.searchNames(dto.expression) }
            else -> Response().apply { resultCode = 400 }
        }
    }

    private suspend fun <T> safeApiCall(apiCall: suspend () -> T): Response {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiCall() as Response
                response.apply { resultCode = 200 }
            } catch (e: Throwable) {
                Response().apply { resultCode = 500 }
            }
        }
    }

    private fun isConnected(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }
        return false
    }

}