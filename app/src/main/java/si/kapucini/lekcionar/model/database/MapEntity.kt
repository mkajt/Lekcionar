package si.kapucini.lekcionar.model.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "map")
data class MapEntity(

    @PrimaryKey
    val selektor: String,

    val id_podatek: String

)
