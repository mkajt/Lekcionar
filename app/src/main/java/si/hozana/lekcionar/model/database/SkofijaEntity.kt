package si.hozana.lekcionar.model.database

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "skofija")
data class SkofijaEntity(

    @PrimaryKey
    val id: String,

    val skofija: String

)
