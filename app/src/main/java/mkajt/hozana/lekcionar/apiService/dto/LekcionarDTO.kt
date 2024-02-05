package mkajt.hozana.lekcionar.apiService.dto

@Serialiizable
data class LekcionarDTO(
        val map: List<MapDTO>, //JsonOBject
        val data: List<PodatkiDTO>, //JsonObject
        val skofije: List<SkofijaDTO>,
        val redovi: List<RedDTO>
)