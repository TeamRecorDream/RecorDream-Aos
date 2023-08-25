package com.team.recordream.mapper

import com.team.recordream.data.entity.remote.response.DetailRecordResponseDto
import com.team.recordream.domain.model.DetailRecord

fun DetailRecordResponseDto.toDomain(): DetailRecord = DetailRecord(
    id = data.id,
    title = data.title,
    date = data.date,
    content = data.content ?: "",
    emotion = data.emotion,
    genre = data.genre,
    note = data.note,
    voice = null,
)
