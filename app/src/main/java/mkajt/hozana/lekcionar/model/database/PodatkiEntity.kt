package mkajt.hozana.lekcionar.model.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Timestamp

@Entity(tableName = "podatki")
data class PodatkiEntity(

    @PrimaryKey
    val id: String,

    val opis: String,
    val timestamp: Timestamp, //check myb just Long
    val datum: String,
    val mp3: String,

    val berilo1: String,
    val berilo1_naslov: String,
    val berilo1_vsebina: String,

    val odpev: String,
    val psalm: String,
    val psalm_vsebina: String,

    val berilo2: String,
    val berilo2_naslov: String,
    val berilo2_vsebina: String,

    val aleluja: String,
    val evangelij: String,
    val evangelij_naslov: String,
    val evangelij_vsebina: String,
    val vrstica: String

)
