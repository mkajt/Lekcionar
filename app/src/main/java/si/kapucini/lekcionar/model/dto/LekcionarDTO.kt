package si.kapucini.lekcionar.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class LekcionarDTO(
        val map: List<MapDTO>,
        val data: List<PodatkiDTO>,
        val skofije: List<SkofijaDTO>,
        val redovi: List<RedDTO>
)