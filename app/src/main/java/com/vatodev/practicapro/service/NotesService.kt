package com.vatodev.practicapro.service

import com.vatodev.practicapro.entitys.ApiNote
import com.vatodev.practicapro.model.CreateNoteRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface NotesService {

    @GET("notas")
    suspend fun getNotes(): List<ApiNote>

    @POST("notas")
    suspend fun createNote(@Body note: CreateNoteRequest) : ApiNote
}
