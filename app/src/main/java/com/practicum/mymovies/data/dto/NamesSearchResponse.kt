package com.practicum.mymovies.data.dto

data class NamesSearchResponse(
    val expression: String,
    val results: List<PersonDto>,
    val searchType: String
) : Response()