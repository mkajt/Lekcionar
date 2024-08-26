package si.kapucini.lekcionar.model.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface LekcionarDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE) //did this because of the error in logs
    suspend fun insertMap(mapEntities: List<MapEntity>)

    @Insert
    suspend fun insertPodatki(podatkiEntities: List<PodatkiEntity>)

    @Insert
    suspend fun insertRed(redEntities: List<RedEntity>)

    @Insert
    suspend fun insertSkofija(skofijeEntities: List<SkofijaEntity>)

    @Query("SELECT id_podatek FROM map WHERE selektor = :selektor")
    suspend fun getIdPodatekFromMap(selektor: String): String

    @Query("SELECT * FROM podatki WHERE id = :id_podatek")
    suspend fun getPodatki(id_podatek: String): PodatkiEntity

    @Query("SELECT COUNT(*) FROM podatki")
    suspend fun countPodatki(): Int

    @Query("SELECT timestamp FROM podatki ORDER BY timestamp ASC LIMIT 1")
    suspend fun getFirstDataTimestamp(): Long

    @Query("SELECT timestamp FROM podatki ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastDataTimestamp(): Long

    @Query("SELECT * FROM red")
    suspend fun getRedList(): List<RedEntity>

    @Query("SELECT * FROM skofija")
    suspend fun getSkofijaList(): List<SkofijaEntity>

    @Query("DELETE FROM map")
    suspend fun deleteAllFromMap()

    @Query("DELETE FROM podatki")
    suspend fun deleteAllFromPodatki()

    @Query("DELETE FROM red")
    suspend fun deleteAllFromRed()

    @Query("DELETE FROM skofija")
    suspend fun deleteAllFromSkofija()

}