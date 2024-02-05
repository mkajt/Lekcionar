package mkajt.hozana.lekcionar.apiService.dto

import kotlinx.serialization.Serializable

@Serializable
data class RedDTO(
    val id: String,
    val red: String
)