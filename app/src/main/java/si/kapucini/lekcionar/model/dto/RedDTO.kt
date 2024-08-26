package si.kapucini.lekcionar.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class RedDTO(
    val id: String = "",
    val red: String = ""
)