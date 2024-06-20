package si.hozana.lekcionar.model.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "red")
data class RedEntity(

    @PrimaryKey
    val id: String,

    val red: String

)
