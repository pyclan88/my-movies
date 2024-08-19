package com.practicum.mymovies.data

import com.practicum.mymovies.data.dto.NamesSearchRequest
import com.practicum.mymovies.data.dto.NamesSearchResponse
import com.practicum.mymovies.domain.api.NamesRepository
import com.practicum.mymovies.domain.models.Person
import com.practicum.mymovies.util.Resource

class NamesRepositoryImpl(private val networkClient: NetworkClient) : NamesRepository {

    override fun searchNames(expression: String): Resource<List<Person>> {
        val response = networkClient.doRequest(NamesSearchRequest(expression))
        return when (response.resultCode) {
            -1 -> {
                Resource.Error("Проверьте подключение к интернету")
            }

            200 -> {
                with(response as NamesSearchResponse) {
                    Resource.Success(results.map {
                        Person(
                            description = it.description,
                            id = it.id,
                            photoUrl = it.image,
                            resultType = it.resultType,
                            title = it.title,
                        )
                    })
                }
            }

            else -> {
                Resource.Error("Ошибка сервера")
            }
        }
    }

}