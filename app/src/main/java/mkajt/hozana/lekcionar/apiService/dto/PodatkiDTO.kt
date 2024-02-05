package mkajt.hozana.lekcionar.apiService.dto

import kotlinx.serialization.Serializable
import java.sql.Timestamp

@Serializable
data class PodatkiDTO(
    val id: String,
    val opis: String,
    val timestamp: Long, //check myb just Long
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