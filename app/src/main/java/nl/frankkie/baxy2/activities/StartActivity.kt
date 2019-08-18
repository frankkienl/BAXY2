/*
 * Copyright (c) 2013. FrankkieNL
 */
package nl.frankkie.baxy2.activities

import android.app.Activity
import android.app.AlertDialog
import android.app.WallpaperManager
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.provider.Settings
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast

import java.io.File
import java.io.FileInputStream

import eu.chainfire.libsuperuser.Shell
import nl.frankkie.baxy2.*
import nl.frankkie.baxy2.util.Util
import tv.ouya.console.api.OuyaController

/**
 * Created by FrankkieNL on 6-7-13.
 */
class StartActivity : Activity() {
    var appWidgetHost: AppWidgetHost? = null
    var handler = Handler()
    var context: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        OuyaController.init(this)
        initUI()
        startImageCaching()
        initWidgets()

        val autoStartFavorites = false
        if (autoStartFavorites) {
            //Do same behaviour as when the favorites-button is pressed.
            val i = Intent()
            i.setClass(this@StartActivity, MainActivity::class.java)
            i.putExtra("type", MainActivity.APP_FAVORITES_ONLY)
            //Util.logGoToApplist(StartActivity.this, MainActivity.APP_FAVORITES_ONLY);
            startActivity(i)
        }
    }

    override fun onResume() {
        super.onResume()
        context = this
    }

    override fun onPause() {
        super.onPause()
        context = null //prevent leak
    }

    private fun initWidgets() {
        appWidgetHost = AppWidgetHost(this, appWidgetHostId)
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val firstRun = prefs.getBoolean("firstTimeWidgets", true)
        if (firstRun) {
            appWidgetHost?.deleteHost()
            prefs.edit().putBoolean("firstTimeWidgets", false).apply()
        }
    }

    fun addWidget() {
        //http://developer.android.com/guide/topics/appwidgets/host.html
        val appWidgetId = this.appWidgetHost?.allocateAppWidgetId()
        val pickIntent = Intent(AppWidgetManager.ACTION_APPWIDGET_PICK)
        pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        startActivityForResult(pickIntent, REQUEST_PICK_APPWIDGET)
    }

    internal fun addAppWidget(data: Intent) {
        val appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)

        //String customWidget = data.getStringExtra(EXTRA_CUSTOM_WIDGET);
        val appWidgetManager = AppWidgetManager.getInstance(this)
        val appWidget = appWidgetManager.getAppWidgetInfo(appWidgetId)

        if (appWidget.configure != null) {
            // Launch over to configure widget, if needed.
            val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE)
            intent.component = appWidget.configure
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            startActivityForResult(intent, REQUEST_CREATE_APPWIDGET)
            //Util.logAddWidgetConfigure(this, appWidget.provider.getPackageName());
        } else {
            // Otherwise, finish adding the widget.
            //            DatabaseOpenHelper helper = DatabaseOpenHelper.CreateInstance(this);
            //            DatabaseAppWidget databaseAppWidget = new DatabaseAppWidget();
            //            databaseAppWidget.info = appWidget;
            //            databaseAppWidget.appWidgetId = appWidgetId;
            //            databaseAppWidget.OnInsert();
            //            placeWidget(appWidgetId, appWidget);
            //Util.logAddWidget(this, appWidget.provider.getPackageName());
        }
    }

    fun placeWidget(appWidgetId: Int, appWidget: AppWidgetProviderInfo) {
        val v = appWidgetHost?.createView(this, appWidgetId, appWidget)
        //remove clock to make room for widgets
        findViewById<View>(R.id.clock_container).visibility = View.GONE
        val group = findViewById<View>(R.id.widgets_container) as ViewGroup
        //group.removeAllViews();
        group.addView(v)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        //        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        if (requestCode == REQUEST_PICK_APPWIDGET) {
            addAppWidget(data)
        } else if (requestCode == REQUEST_CREATE_APPWIDGET) {
            // Otherwise, finish adding the widget.
            Log.e("BAXY", "requestCode == REQUEST_CREATE_APPWIDGET")
            val appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)
            val appWidgetManager = AppWidgetManager.getInstance(this)
            val appWidget = appWidgetManager.getAppWidgetInfo(appWidgetId)
            //            DatabaseOpenHelper helper = DatabaseOpenHelper.CreateInstance(this);
            //            DatabaseAppWidget databaseAppWidget = new DatabaseAppWidget();
            //            databaseAppWidget.info = appWidget;
            //            databaseAppWidget.appWidgetId = appWidgetId;
            //            databaseAppWidget.OnInsert();
            placeWidget(appWidgetId, appWidget)
        }
    }

    private fun startImageCaching() {
        val task = MakeImageCache(this)
        task.execute()
    }

    private fun fixBackgroundOnFirstUse() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val isFirstRun = prefs.getBoolean("isFirstRun", true)
        if (isFirstRun) {
            prefs.edit().putBoolean("isFirstRun", false).commit()
            Util.getBackground(this) //place background files on SD
            val defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
            val path = defaultSharedPreferences.getString("backgroundFile", "/sdcard/BAXY/backgrounds/default.png")
            try {
                val fis = FileInputStream(File(path))
                setWallpaper(fis)
                WallpaperManager.getInstance(this).suggestDesiredDimensions(1920, 1080)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    private fun initUI() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        //        setContentView(R.layout.main);
        setContentView(R.layout.start)
        //Util.setBackground(this);
        //Fix Background on First Use (update)
        fixBackgroundOnFirstUse()
        ///
        //Util.setLogo(this); moved to refreshWidgets();
        //Util.setClock(this);
        val btnAll = findViewById<View>(R.id.start_all) as Button
        val btnGames = findViewById<View>(R.id.start_games) as Button
        val btnApps = findViewById<View>(R.id.start_apps) as Button
        val btnAndroid = findViewById<View>(R.id.start_android) as Button
        val btnFavorites = findViewById<View>(R.id.start_favorites) as Button
        val btnDiscover = findViewById<View>(R.id.start_discover) as Button
        val btnSettings = findViewById<View>(R.id.start_settings) as Button
        btnAll.setOnClickListener {
            val i = Intent()
            i.setClass(this@StartActivity, MainActivity::class.java)
            i.putExtra("type", MainActivity.APP_ALL)
            //Util.logGoToApplist(StartActivity.this, MainActivity.APP_ALL);
            startActivity(i)
        }
        btnGames.setOnClickListener {
            val i = Intent()
            i.setClass(this@StartActivity, MainActivity::class.java)
            i.putExtra("type", MainActivity.APP_OUYA_GAMES_ONLY)
            //Util.logGoToApplist(StartActivity.this, MainActivity.APP_OUYA_GAMES_ONLY);
            startActivity(i)
        }
        btnApps.setOnClickListener {
            val i = Intent()
            i.setClass(this@StartActivity, MainActivity::class.java)
            i.putExtra("type", MainActivity.APP_OUYA_APPS_ONLY)
            //Util.logGoToApplist(StartActivity.this, MainActivity.APP_OUYA_APPS_ONLY);
            startActivity(i)
        }
        btnAndroid.setOnClickListener {
            val i = Intent()
            i.setClass(this@StartActivity, MainActivity::class.java)
            i.putExtra("type", MainActivity.APP_ANDROID_APPS_ONLY)
            //Util.logGoToApplist(StartActivity.this, MainActivity.APP_ANDROID_APPS_ONLY);
            startActivity(i)
        }
        btnFavorites.setOnClickListener {
            val i = Intent()
            i.setClass(this@StartActivity, MainActivity::class.java)
            i.putExtra("type", MainActivity.APP_FAVORITES_ONLY)
            //Util.logGoToApplist(StartActivity.this, MainActivity.APP_FAVORITES_ONLY);
            startActivity(i)
        }
        btnDiscover.setOnClickListener { startDiscover() }
        btnSettings.setOnClickListener { goToSettings() }
    }

    private fun startDiscover() {
        val task = StartDiscoverRootAsyncTask()
        task.execute()
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        //Dont consume DPAD, and O
        val ignoreList = intArrayOf(
            OuyaController.BUTTON_DPAD_DOWN,
            OuyaController.BUTTON_DPAD_UP,
            OuyaController.BUTTON_DPAD_LEFT,
            OuyaController.BUTTON_DPAD_RIGHT,
            OuyaController.BUTTON_O
        )
        for (i in ignoreList) {
            if (event.keyCode == i) {
                return super.onKeyDown(keyCode, event) //let the OUYA take care of it.
            }
        }

        //Let the SDK take care of the rest
        val handled = OuyaController.onKeyUp(keyCode, event)
        return handled || super.onKeyUp(keyCode, event)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        //Dont consume DPAD
        val ignoreList = intArrayOf(
            OuyaController.BUTTON_DPAD_DOWN,
            OuyaController.BUTTON_DPAD_UP,
            OuyaController.BUTTON_DPAD_LEFT,
            OuyaController.BUTTON_DPAD_RIGHT,
            OuyaController.BUTTON_O
        )
        for (i in ignoreList) {
            if (event.keyCode == i) {
                return super.onKeyDown(keyCode, event) //let the OUYA take care of it.
            }
        }

        if (event.keyCode == OuyaController.BUTTON_Y) {
            turnOuyaOff()
            return true
        }

        if (event.keyCode == KeyEvent.KEYCODE_MENU || event.keyCode == OuyaController.BUTTON_MENU) {
            showMenuDialog()
            return true
        }

        //Let the SDK take care of the rest
        val handled = OuyaController.onKeyDown(keyCode, event)
        return handled || super.onKeyDown(keyCode, event)
    }

    private fun showMenuDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Menu")
        var items: Array<String>? = null
        items = arrayOf(
            "Launcher Settings",
            "Running Applications",
            "Advanced Settings",
            "Add Widget",
            "Remove all Widgets",
            "Pair Controller (OUYA)",
            "Manage (OUYA)",
            "Network Setup (OUYA)",
            "Webserver (Secret)",
            "Turn Off"
        ) //todo webserver

        builder.setItems(items) { dialogInterface, i ->
            when (i) {
                0 -> {
                    goToSettings()
                }
                1 -> {
                    goToRunningApps()
                }
                2 -> {
                    goToAdvancedSettings()
                }
                3 -> {
                    addWidget()
                }
                4 -> {
                    removeAllWidgets()
                }

                5 -> {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("ouya://launcher/manage/controllers/pairing")))
                }
                6 -> {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("ouya://launcher/manage")))
                }
                7 -> {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("ouya://launcher/manage/network")))
                }

                8 -> {
                    startWebserver()
                }
                9 -> {
                    turnOuyaOff()
                }
            }
        }
        builder.create().show()
    }

    private fun showMenuDialogOLD() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Menu")
        var items: Array<String>? = null
        if (Util.BETA) {
            items = arrayOf(
                "Launcher Settings",
                "Running Applications",
                "Advanced Settings",
                "Add Widget",
                "Remove all Widgets",
                "Turn Off"
            ) //todo webserver
        } else {
            items = arrayOf("Launcher Settings", "Running Applications", "Advanced Settings", "Turn Off")
        }
        builder.setItems(items) { dialogInterface, i ->
            when (i) {
                0 -> {
                    goToSettings()
                }
                1 -> {
                    goToRunningApps()
                }
                2 -> {
                    goToAdvancedSettings()
                }
                3 -> {
                    if (Util.BETA) {
                        addWidget()
                    } else {
                        turnOuyaOff()
                    }
                }
                4 -> {
                    removeAllWidgets()
                }
                5 -> {
                    turnOuyaOff()
                }
                6 -> {
                    startWebserver()
                }
            }
        }
        builder.create().show()
    }

    private fun startWebserver() {
        val i = Intent()
        i.setClass(this, WebserverActivity::class.java)
        startActivity(i)
    }

    private fun goToAdvancedSettings() {
        val i = Intent(Intent.ACTION_MAIN)
        i.action = Settings.ACTION_SETTINGS
        startActivity(i)
    }

    private fun goToRunningApps() {
        val i = Intent()
        i.setClass(this, RunningAppsActivity::class.java)
        startActivity(i)
    }

    private fun goToSettings() {
        val i = Intent()
        i.setClass(this, SettingsActivity::class.java)
        //Util.logGoToSettings(this);
        startActivity(i)
    }

    private fun turnOuyaOff() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Turn off console?")
        //        builder.setTitle("OUYA");
        builder.setNeutralButton("Yes (Standby)") { dialogInterface, i ->
            val task = TurnOffAsyncTask()
            task.execute("standby")
        }
        builder.setPositiveButton("Yes (Really off)") { dialogInterface, i ->
            val task = TurnOffAsyncTask()
            task.execute("off")
        }
        builder.setNegativeButton("No") { dialogInterface, i -> }
        builder.create().show()
    }

    override fun onGenericMotionEvent(event: MotionEvent): Boolean {
        //Dont consume LS, RS, L2, R2 events
        //boolean handled = OuyaController.onGenericMotionEvent(event);
        //return handled || super.onGenericMotionEvent(event);
        return super.onGenericMotionEvent(event)
    }

    override fun onStart() {
        super.onStart()
        //ANALYTICS
        //FlurryAgent.onStartSession(this, "MDHSMF65TV4JCSW3QN63");
        //
        Util.onStart(this)
        //Update Check
        val updater = Updater.getInstance(this)
        updater.startUpdateCheck()
        //Widgets
        appWidgetHost?.startListening()
        refreshWidgets()
    }

    fun removeAllWidgets() {
        val group = findViewById<View>(R.id.widgets_container) as ViewGroup
        group.removeAllViews()
        //Get Clock Back :P
        findViewById<View>(R.id.clock_container).visibility = View.VISIBLE
        //
        //        DatabaseOpenHelper helper = DatabaseOpenHelper.CreateInstance(this);
        //        Cursor cursor = helper.WriteableDatabase.rawQuery("SELECT id FROM appwidget", null);
        //        ArrayList<Integer> ids = new ArrayList<Integer>();
        //        while (cursor.moveToNext()) {
        //            int id = cursor.getInt(0);
        //            ids.add(id);
        //        }
        //        cursor.close();
        //        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        //        for (int id : ids) {
        //            DatabaseAppWidget databaseAppWidget = new DatabaseAppWidget(id);
        //            //databaseAppWidget.OnLoad();
        //            appWidgetHost.deleteAppWidgetId(id);
        //            databaseAppWidget.OnDelete();
        //        }
    }

    fun refreshWidgets() {
        Util.setClock(this)
        Util.setLogo(this)
        //check existing widgets!
        //        DatabaseOpenHelper helper = DatabaseOpenHelper.CreateInstance(this);
        //        Cursor cursor = helper.WriteableDatabase.rawQuery("SELECT id FROM appwidget", null);
        //        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        //        while (cursor.moveToNext()) {
        //            int id = cursor.getInt(0);
        //            DatabaseAppWidget databaseAppWidget = new DatabaseAppWidget(id);
        //            databaseAppWidget.OnLoad();
        //            AppWidgetProviderInfo appWidget = appWidgetManager.getAppWidgetInfo(databaseAppWidget.appWidgetId);
        //            databaseAppWidget.info = appWidget;
        //            placeWidget(databaseAppWidget.appWidgetId, appWidget);
        //        }
        //        cursor.close();
    }

    override fun onStop() {
        super.onStop()
        Util.onStop(this)
        //Widgets
        appWidgetHost?.stopListening()
        //ANALYTICS
        //FlurryAgent.onEndSession(this);
    }

    private inner class StartDiscoverRootAsyncTask : AsyncTask<Void, Void, Void>() {

        override fun doInBackground(vararg params: Void): Void? {
            // Let's do some SU stuff
            val suAvailable = Shell.SU.available()
            if (suAvailable) {
                //String suVersion = Shell.SU.version(false);
                //String suVersionInternal = Shell.SU.version(true);
                //Util.logStartDiscover(StartActivity.this);
                val suResult =
                    Shell.SU.run(arrayOf("am start --user 0 -n tv.ouya.console/tv.ouya.console.launcher.store.adapter.DiscoverActivity"))
            } else {
                toast(context!!, "Root is not Available.. Starting Stock Launcher")
                val i = Intent()
                i.setClassName("tv.ouya.console", "tv.ouya.console.launcher.OverlayMenuActivity")
                try {
                    context?.startActivity(i)
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
            return null
        }

    }

    private inner class TurnOffAsyncTask : AsyncTask<String, Void, Void>() {

        override fun doInBackground(vararg params: String): Void? {
            val reallyOff = params[0] == "off" //else standby
            // Let's do some SU stuff
            val suAvailable = Shell.SU.available()
            if (suAvailable) {
                //String suVersion = Shell.SU.version(false);
                //String suVersionInternal = Shell.SU.version(true);
                //Util.logTurnOff(StartActivity.this);
                if (!reallyOff) {
                    val suResult = Shell.SU.run(arrayOf("am broadcast --user 0 -a tv.ouya.console.action.TURN_OFF"))
                } else {
                    val suResult = Shell.SU.run(
                        arrayOf("reboot -p")/*
                             "input keyevent 26",
                             http://forum.xda-developers.com/showthread.php?t=2063741
                             //GETEVENT
                             "sendevent /dev/input/event1 0001 0074 00000001",
                             "sendevent /dev/input/event1 0000 0000 00000000",
                             "sleep 2",
                             "sendevent /dev/input/event1 0001 0074 00000000",
                             "sendevent /dev/input/event1 0000 0000 00000000",
                             ///XDA
                             "sendevent /dev/input/event0 0001 116 1",
                             "sendevent /dev/input/event0 0000 0000 00000000",
                             "sleep 2",
                             "sendevent /dev/input/event0 0001 116 00000000",
                             "sendevent /dev/input/event0 0000 0000 00000000"
                             */
                    )
                    val sb = StringBuilder()
                    if (suResult != null) {
                        for (line in suResult) {
                            sb.append(line).append(10.toChar())
                        }
                    }
                    Log.e("BAXY", "BAXY\n$sb")
                }
            } else {
                toast(context!!, "Root is not Available..")
            }
            return null
        }
    }

    fun toast(context: Context, s: String) {
        handler.post { Toast.makeText(context, s, Toast.LENGTH_LONG).show() }
    }

    companion object {

        val appWidgetHostId = 1337 + 9001 //Its Over 9000!!Xorz //just some random number
        private val REQUEST_CREATE_APPWIDGET = 5
        private val REQUEST_PICK_APPWIDGET = 9
    }
}
