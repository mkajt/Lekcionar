package si.kapucini.lekcionar.model.dto

import si.kapucini.lekcionar.model.database.MapEntity
import si.kapucini.lekcionar.model.database.PodatkiEntity
import si.kapucini.lekcionar.model.database.RedEntity
import si.kapucini.lekcionar.model.database.SkofijaEntity

object Mapper {

    fun mapRedDtoToEntity(redoviDTO: List<RedDTO>) : List<RedEntity> {
        var redEntities = ArrayList<RedEntity>()
        redoviDTO.forEach { red ->
            val rEntity = RedEntity(red.id, red.red)
            redEntities.add(rEntity)
        }
        return redEntities
    }

    fun mapSkofijaDtoToEntity(skofijeDTO: List<SkofijaDTO>) : List<SkofijaEntity> {
        var skofijaEntities = ArrayList<SkofijaEntity>()
        skofijeDTO.forEach { skofija ->
            val sEntity = SkofijaEntity(skofija.id, skofija.skofija)
            skofijaEntities.add(sEntity)
        }
        return skofijaEntities

    }

    fun mapPodatkiDtoToEntity(podatkiDTO: List<PodatkiDTO>) : List<PodatkiEntity> {
        var podatkiEntities = ArrayList<PodatkiEntity>()
        podatkiDTO.forEach { podatki ->
            val pEntity = PodatkiEntity(
                podatki.id,
                if (podatki.opis_dolgi == "") podatki.opis else podatki.opis_dolgi,
                podatki.timestamp,
                podatki.datum,
                podatki.mp3,
                podatki.berilo1,
                podatki.berilo1_naslov,
                podatki.berilo1_vsebina,
                podatki.odpev,
                podatki.psalm,
                podatki.psalm_vsebina,
                podatki.berilo2,
                podatki.berilo2_naslov,
                podatki.berilo2_vsebina,
                podatki.aleluja,
                podatki.evangelij,
                podatki.evangelij_naslov,
                podatki.evangelij_vsebina,
                podatki.vrstica
            )
            podatkiEntities.add(pEntity)
        }
        return podatkiEntities

    }

    fun mapMapDtoToEntity(mapDTO: List<MapDTO>) : List<MapEntity> {
        var mapEntities = ArrayList<MapEntity>()
        mapDTO.forEach { map ->
            val mEntity = MapEntity(map.selektor, map.data_ids.joinToString(","))
            mapEntities.add(mEntity)
        }
        return mapEntities

    }
}