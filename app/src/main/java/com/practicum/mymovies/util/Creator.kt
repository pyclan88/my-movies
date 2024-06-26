package com.practicum.mymovies.util

import android.content.Context
import com.practicum.mymovies.data.MoviesRepositoryImpl
import com.practicum.mymovies.data.network.RetrofitNetworkClient
import com.practicum.mymovies.domain.api.MoviesInteractor
import com.practicum.mymovies.domain.api.MoviesRepository
import com.practicum.mymovies.domain.impl.MoviesInteractorImpl
import com.practicum.mymovies.presentation.movies.MoviesSearchPresenter
import com.practicum.mymovies.presentation.poster.PosterPresenter
import com.practicum.mymovies.presentation.movies.MoviesView
import com.practicum.mymovies.presentation.poster.PosterView

object Creator {
    private fun getMoviesRepository(context: Context): MoviesRepository {
        return MoviesRepositoryImpl(RetrofitNetworkClient(context))
    }

    fun provideMoviesInteractor(context: Context): MoviesInteractor {
        return MoviesInteractorImpl(getMoviesRepository(context))
    }

    fun provideMoviesSearchPresenter(
        context: Context
    ): MoviesSearchPresenter {
        return MoviesSearchPresenter(
            context = context
        )
    }

    fun providePosterPresenter(
        posterView: PosterView,
        imageUrl: String
    ): PosterPresenter {
        return PosterPresenter(
            view = posterView,
            imageUrl = imageUrl
        )
    }
}