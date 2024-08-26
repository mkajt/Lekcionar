package si.kapucini.lekcionar.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class MapDTO(
    val selektor: String = "",
    val data_ids: List<String>
)