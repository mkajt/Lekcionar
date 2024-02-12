package mkajt.hozana.lekcionar.model.database

import androidx.room.Entity


@Entity(tableName = "skofije")
data class SkofijeEntity(
    val slovenija: String,
    val ljubljana: String,
    val celje: String,
    val maribor: String,
    val murska_sobota: String,
    val novo_mesto: String,
    val koper: String
)
