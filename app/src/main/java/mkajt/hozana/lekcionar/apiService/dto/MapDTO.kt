package mkajt.hozana.lekcionar.apiService.dto

import kotlinx.serialization.Serializable

@Serializable
data class MapDTO(
    val selektor: String,
    val data_id: String
)