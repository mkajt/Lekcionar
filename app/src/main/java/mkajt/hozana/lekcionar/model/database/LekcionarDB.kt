package mkajt.hozana.lekcionar.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PodatkiEntity::class, MapEntity::class, RedoviEntity::class, SkofijeEntity::class],
    version = 1, exportSchema = false)
abstract class LekcionarDB : RoomDatabase() {

    abstract fun lekcionarDao(): LekcionarDAO
    companion object {

        @Volatile
        private var INSTANCE: LekcionarDB? = null

        fun getInstance(context: Context): LekcionarDB {

            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        LekcionarDB::class.java,
                        "lekcionar_db"
                    ).build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}