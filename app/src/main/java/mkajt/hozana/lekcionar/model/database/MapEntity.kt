package mkajt.hozana.lekcionar.model.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import mkajt.hozana.lekcionar.util.ListStringConverter

@Entity(tableName = "map")
data class MapEntity(

    @PrimaryKey
    val selektor: String,

    val id_podatek: List<String>

)
