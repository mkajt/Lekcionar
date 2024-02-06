package mkajt.hozana.lekcionar.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class LekcionarDTO(
        val map: List<MapDTO>, //JsonOBject
        val data: List<PodatkiDTO>, //JsonObject
        val skofije: List<SkofijaDTO>,
        val redovi: List<RedDTO>
)