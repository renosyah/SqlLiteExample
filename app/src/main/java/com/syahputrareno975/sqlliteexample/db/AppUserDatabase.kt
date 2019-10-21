package com.syahputrareno975.sqlliteexample.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.syahputrareno975.sqlliteexample.interfaces.UserDao
import com.syahputrareno975.sqlliteexample.model.UserModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = arrayOf(UserModel::class), version = 1, exportSchema = false)
abstract class AppUserDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    companion object{
        @Volatile
        private var INSTANCE : AppUserDatabase? =  null

        fun getDatabase(ctx : Context, scope: CoroutineScope) : AppUserDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null){
                return  tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    ctx.applicationContext,
                    AppUserDatabase::class.java,
                    "user"
                ).addCallback(AppUserDatabaseCallback(scope))
                    .build()

                INSTANCE = instance
                return instance
            }
        }
    }

    private class AppUserDatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {
        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch {
                    // add init data to database
                    // for testing
                    // but this currently not working

                    //populateDatabase(database.userDao())

                    // or just let this empty
                }
            }
        }
        suspend fun populateDatabase(userDao: UserDao) {
            userDao.insertAll(UserModel(Name = "reno",PhoneNumber = "08123113131"))
            userDao.insertAll(UserModel(Name = "reno",PhoneNumber = "08335343234"))
            userDao.insertAll(UserModel(Name = "reno",PhoneNumber = "08156564335"))
        }
    }
}
