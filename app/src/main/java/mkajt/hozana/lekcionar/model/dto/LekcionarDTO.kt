package mkajt.hozana.lekcionar.model.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LekcionarDTO(
    val map: List<MapDTO>, //JsonOBject
    val data: List<PodatkiDTO>, //JsonObject
    val skofije: List<SkofijaDTO>,
    val redovi: List<RedDTO>
)