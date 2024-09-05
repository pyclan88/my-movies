package com.practicum.mymovies.data

import com.practicum.mymovies.data.dto.NamesSearchRequest
import com.practicum.mymovies.data.dto.NamesSearchResponse
import com.practicum.mymovies.domain.api.NamesRepository
import com.practicum.mymovies.domain.models.Person
import com.practicum.mymovies.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class NamesRepositoryImpl(private val networkClient: NetworkClient) : NamesRepository {

    override fun searchNames(expression: String): Flow<Resource<List<Person>>> = flow {
        val response = networkClient.doRequest(NamesSearchRequest(expression))
        when (response.resultCode) {
            -1 -> {
                emit(Resource.Error("Проверьте подключение к интернету"))
            }

            200 -> {
                with(response as NamesSearchResponse) {
                    val data = results.map {
                        Person(
                            description = it.description,
                            id = it.id,
                            photoUrl = it.image,
                            resultType = it.resultType,
                            title = it.title,
                        )
                    }
                    emit(Resource.Success(data))
                }
            }

            else -> {
                emit(Resource.Error("Ошибка сервера"))
            }
        }
    }

}