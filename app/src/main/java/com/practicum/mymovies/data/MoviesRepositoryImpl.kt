package com.practicum.mymovies.data

import com.practicum.mymovies.data.converters.MovieCastConverter
import com.practicum.mymovies.data.converters.MovieDbConverter
import com.practicum.mymovies.data.db.AppDatabase
import com.practicum.mymovies.data.dto.MovieDetailsResponse
import com.practicum.mymovies.data.dto.MovieDetailsRequest
import com.practicum.mymovies.data.dto.MovieCastRequest
import com.practicum.mymovies.data.dto.MovieCastResponse
import com.practicum.mymovies.data.dto.MovieDto
import com.practicum.mymovies.data.dto.MoviesSearchRequest
import com.practicum.mymovies.data.dto.MoviesSearchResponse
import com.practicum.mymovies.domain.api.MoviesRepository
import com.practicum.mymovies.domain.models.Movie
import com.practicum.mymovies.domain.models.MovieDetails
import com.practicum.mymovies.domain.models.MovieCast
import com.practicum.mymovies.utils.LocalStorage
import com.practicum.mymovies.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MoviesRepositoryImpl(
    private val networkClient: NetworkClient,
    private val movieCastConverter: MovieCastConverter,
    private val localStorage: LocalStorage,
    private val appDatabase: AppDatabase,
    private val movieDbConverter: MovieDbConverter,
) : MoviesRepository {

    override fun searchMovies(expression: String): Flow<Resource<List<Movie>>> = flow {
        val response = networkClient.doRequest(MoviesSearchRequest(expression))
        when (response.resultCode) {
            -1 -> emit(Resource.Error("Проверьте подключение к интернету"))

            200 -> {
                val stored = localStorage.getSavedFavorites()
                with(response as MoviesSearchResponse) {
                    val data = results.map {
                        Movie(
                            id = it.id,
                            resultType = it.resultType,
                            image = it.image,
                            title = it.title,
                            description = it.description,
                            inFavorite = stored.contains(it.id),
                        )
                    }
                    saveMovie(results)
                    emit(Resource.Success(data))
                }
            }

            else -> emit(Resource.Error("Ошибка сервера"))
        }
    }

    override fun getMovieDetails(movieId: String): Flow<Resource<MovieDetails>> = flow {
        val response = networkClient.doRequest(MovieDetailsRequest(movieId))
        when (response.resultCode) {
            -1 -> emit(Resource.Error("Проверьте подключение к интернету"))
            200 -> {
                with(response as MovieDetailsResponse) {
                    val data = MovieDetails(
                        id, title, imDbRating, year,
                        countries, genres, directors, writers, stars, plot
                    )
                    emit(Resource.Success(data))
                }
            }

            else -> emit(Resource.Error("Ошибка сервера"))
        }
    }

    override fun getMovieCast(movieId: String): Flow<Resource<MovieCast>> = flow {
        val response = networkClient.doRequest(MovieCastRequest(movieId))
        when (response.resultCode) {
            -1 -> emit(Resource.Error("Проверьте подключение к интернету"))
            200 -> emit(Resource.Success(data = movieCastConverter.convert(response as MovieCastResponse)))
            else -> emit(Resource.Error("Ошибка сервера"))
        }
    }

    override fun addMovieToFavorites(movie: Movie) {
        localStorage.addToFavorites(movie.id)
    }

    override fun removeMovieFromFavorites(movie: Movie) {
        localStorage.removeFromFavorites(movie.id)
    }

    private suspend fun saveMovie(movies: List<MovieDto>) {
        val movieEntities = movies.map { movie -> movieDbConverter.map(movie) }
        appDatabase.movieDao().insertMovies(movieEntities)
    }

}