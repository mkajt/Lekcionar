package si.hozana.lekcionar.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class SkofijaDTO(
    val id: String = "",
    val skofija: String = ""
)