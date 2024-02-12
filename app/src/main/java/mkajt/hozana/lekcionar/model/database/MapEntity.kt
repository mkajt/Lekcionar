package mkajt.hozana.lekcionar.model.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "map")
data class MapEntity(

    @PrimaryKey
    val izbira: String,

    val id_podatek: String
)
