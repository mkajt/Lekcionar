package si.hozana.lekcionar.model.dto

import kotlinx.serialization.Serializable

@Serializable
data class PodatkiDTO(
    val id: String = "",
    val opis: String = "",
    val opis_dolgi: String = "",
    val timestamp: Long = 0L,
    val datum: String = "",
    val mp3: String = "",

    val berilo1_napoved: String = "",
    val berilo1: String = "",
    val berilo1_naslov: String = "",
    val berilo1_vsebina: String = "",

    val odpev: String = "",
    val psalm: String = "",
    val psalm_vsebina: String = "",

    val berilo2_napoved: String = "",
    val berilo2: String = "",
    val berilo2_naslov: String = "",
    val berilo2_vsebina: String = "",

    val evangelij_napoved: String = "",
    val aleluja: String = "",
    val evangelij: String = "",
    val evangelij_naslov: String = "",
    val evangelij_vsebina: String = "",
    val vrstica: String = ""

)