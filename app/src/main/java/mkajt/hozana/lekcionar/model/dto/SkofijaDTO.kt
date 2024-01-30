package mkajt.hozana.lekcionar.model.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SkofijaDTO(
    val id: String,
    val skofija: String
)