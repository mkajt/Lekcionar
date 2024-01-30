package mkajt.hozana.lekcionar.model.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RedDTO(
    val id: String,
    val red: String
)