package mkajt.hozana.lekcionar.model.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LekcionarDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE) //TODO did this because of the error in logs
    suspend fun insertMap(mapEntities: List<MapEntity>)

    @Insert
    suspend fun insertPodatki(podatkiEntities: List<PodatkiEntity>)

    @Insert
    suspend fun insertRed(redEntities: List<RedEntity>)

    @Insert
    suspend fun insertSkofija(skofijeEntities: List<SkofijaEntity>)

    @Query("DELETE FROM map")
    suspend fun deleteAllFromMap()

    @Query("DELETE FROM podatki")
    suspend fun deleteAllFromPodatki()

    @Query("DELETE FROM red")
    suspend fun deleteAllFromRed()

    @Query("DELETE FROM skofija")
    suspend fun deleteAllFromSkofija()

}