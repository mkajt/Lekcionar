package mkajt.hozana.lekcionar.model.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MapDTO(
    val selektor: String,
    val data_id: String
)