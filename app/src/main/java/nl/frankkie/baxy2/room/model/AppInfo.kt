package nl.frankkie.baxy2.room.model

import android.content.ComponentName
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AppInfo(
    @PrimaryKey val uid: Int,
    @ColumnInfo var title: String?,
    //true = icon has been resized
    @ColumnInfo var filtered: Boolean? = false,
    /*
     * OUYA compatible, so:
     * tv.ouya.intent.category.GAME
     * tv.ouya.intent.category.APP
     */
    @ColumnInfo var isOuya: Boolean? = false,
    /*
     * OUYA compatible, so:
     * tv.ouya.intent.category.GAME
     * && ! tv.ouya.intent.category.APP
     */
    @ColumnInfo var isOuyaGame: Boolean? = false,
    @ColumnInfo var packagename: String? = ""

) {

//    var icon: Drawable?=null
    /**
     * Creates the application intent based on a component name and various launch flags.
     *
     * @param className the class name of the component representing the intent
     * @param launchFlags the launch flags
     */
    fun getActivityIntent(className: ComponentName, launchFlags: Int): Intent {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        intent.component = className
        intent.flags = launchFlags
        return intent
    }
}
