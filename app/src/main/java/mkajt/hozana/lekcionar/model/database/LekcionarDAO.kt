package mkajt.hozana.lekcionar.model.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LekcionarDAO {

    @Insert
    suspend fun addMap(vararg mapEntity: MapEntity)

    @Insert
    suspend fun addPodatki(vararg podatkiEntity: PodatkiEntity)

    @Insert
    suspend fun addRedovi(vararg redoviEntity: RedoviEntity)

    @Insert
    suspend fun addSkofije(vararg skofijeEntity: SkofijeEntity)

    @Query("DELETE FROM map")
    suspend fun deleteMap()

    @Query("DELETE FROM podatki")
    suspend fun deletePodatki()

    @Query("DELETE FROM redovi")
    suspend fun deleteRedovi()

    @Query("DELETE FROM skofije")
    suspend fun deleteSkofije()

}