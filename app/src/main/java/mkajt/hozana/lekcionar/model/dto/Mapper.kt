package mkajt.hozana.lekcionar.model.dto

import mkajt.hozana.lekcionar.model.database.MapEntity
import mkajt.hozana.lekcionar.model.database.PodatkiEntity
import mkajt.hozana.lekcionar.model.database.RedEntity
import mkajt.hozana.lekcionar.model.database.SkofijaEntity

object Mapper {

    fun mapRedDtoToEntity(redoviDTO: List<RedDTO>) : List<RedEntity> {
        var redEntities = ArrayList<RedEntity>()
        redoviDTO.forEach { red ->
            var rEntity = RedEntity(red.id, red.red)
            redEntities.add(rEntity)
        }
        return redEntities
    }

    fun mapSkofijaDtoToEntity(skofijeDTO: List<SkofijaDTO>) : List<SkofijaEntity> {
        var skofijaEntities = ArrayList<SkofijaEntity>()
        skofijeDTO.forEach { skofija ->
            var sEntity = SkofijaEntity(skofija.id, skofija.skofija)
            skofijaEntities.add(sEntity)
        }
        return skofijaEntities

    }

    fun mapPodatkiDtoToEntity(podatkiDTO: List<PodatkiDTO>) : List<PodatkiEntity> {
        var podatkiEntities = ArrayList<PodatkiEntity>()
        podatkiDTO.forEach { podatki ->
            var pEntity = PodatkiEntity(
                podatki.id,
                podatki.opis,
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
            var mEntity = MapEntity(map.selektor, map.data_id)
            mapEntities.add(mEntity)
        }
        return mapEntities

    }
}