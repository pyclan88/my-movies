package com.practicum.mymovies.di

import com.practicum.mymovies.data.HistoryRepositoryImpl
import com.practicum.mymovies.data.MoviesRepositoryImpl
import com.practicum.mymovies.data.NamesRepositoryImpl
import com.practicum.mymovies.data.SearchHistoryRepositoryImpl
import com.practicum.mymovies.data.converters.MovieCastConverter
import com.practicum.mymovies.data.converters.MovieDbConverter
import com.practicum.mymovies.domain.api.MoviesRepository
import com.practicum.mymovies.domain.api.NamesRepository
import com.practicum.mymovies.domain.api.SearchHistoryRepository
import com.practicum.mymovies.domain.db.HistoryRepository
import org.koin.dsl.module

val repositoryModule = module {

    factory { MovieCastConverter() }

    factory { MovieDbConverter() }

    single<MoviesRepository> {
        MoviesRepositoryImpl(
            networkClient = get(),
            movieCastConverter = get(),
            localStorage = get(),
            appDatabase = get(),
            movieDbConverter = get(),
        )
    }

    single<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl()
    }

    single<NamesRepository> {
        NamesRepositoryImpl(
            networkClient = get()
        )
    }

    single<HistoryRepository> {
        HistoryRepositoryImpl(
            appDatabase = get(),
            movieDbConvertor = get()
        )
    }

}