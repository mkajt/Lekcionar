package mkajt.hozana.lekcionar.model.dto

import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
data class MapDTO(
    val selektor: String = "",
    val data_ids: List<String>
)