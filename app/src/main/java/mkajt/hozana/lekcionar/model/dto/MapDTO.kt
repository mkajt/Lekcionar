package mkajt.hozana.lekcionar.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class MapDTO(
    val selektor: String,
    val data_id: String
)