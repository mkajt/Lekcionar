package mkajt.hozana.lekcionar.model.database

import androidx.room.Entity

@Entity(tableName = "redovi")
data class RedoviEntity(
    val kapucini: String,
    val franciskani: String,
    val minoriti: String,
    val klarise: String,
    val tretjiRed: String,
    val ursulinke: String,
    val salezijanci: String,
    val hmp: String,
    val neo: String,
    val fmm: String,
    val cm: String,
    val hkl: String,
    val ms: String,
    val karmel: String,
    val jezuiti: String,
    val noben: String,
)
