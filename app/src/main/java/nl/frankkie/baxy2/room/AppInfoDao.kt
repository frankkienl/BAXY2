package nl.frankkie.baxy2.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import nl.frankkie.baxy2.room.model.AppInfo

@Dao
interface AppInfoDao {
    @Query("SELECT * FROM AppInfo")
    fun getAll(): List<AppInfo>

    @Query("SELECT * FROM AppInfo WHERE packagename LIKE :packagename LIMIT 1")
    fun findByPackagename(packagename: String): AppInfo

    @Insert
    fun insertAll(vararg appInfos: AppInfo)

    @Delete
    fun delete(appInfo: AppInfo)
}