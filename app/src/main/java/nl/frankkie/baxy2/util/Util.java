/*
 * Copyright (c) 2013. FrankkieNL
 */

package nl.frankkie.baxy2.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;


import java.io.*;
import java.util.HashMap;

import nl.frankkie.baxy2.BackgroundMusicService;
import nl.frankkie.baxy2.R;
//import nl.frankkie.baxy2.databaserows.DatabaseAppInfo;

/**
 * Created by FrankkieNL on 10-7-13.
 * http://stackoverflow.com/questions/5834221/android-drawable-from-file-path
 */
public class Util {

    public static final boolean BETA = true; //!!!

    public static final String PREFS_BETA_ENABLED = "betaEnabled";
    public static final String PREFS_MUSIC_FILE = "musicFile";

    public static String loadedBackgroundString;
    public static Drawable loadedBackground;
    public static String loadedLogoString;
    public static Drawable loadedLogo;

    public static final int THUMBNAIL_SMALL_WIDTH = 140;
    public static final int THUMBNAIL_SMALL_HEIGHT = 79;

    public static void setClock(Activity activity) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);
        try {
            String clockType = prefs.getString("clockType", "analog");
            if (clockType.equals("analog")) {
                activity.findViewById(R.id.analog_clock).setVisibility(View.VISIBLE);
                activity.findViewById(R.id.digital_clock).setVisibility(View.GONE);
            } else if (clockType.equals("digital")){
                activity.findViewById(R.id.analog_clock).setVisibility(View.GONE);
                activity.findViewById(R.id.digital_clock).setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            //If this happens, you have bigger problems than a missing background..
            //You have a missing layout-root.
            //The app should crash right about now :P
            e.printStackTrace();
        }
    }

    @Deprecated
    public static void setBackground(Activity activity) {
        if (true){return;}
        try {
            activity.findViewById(R.id.layout_background).setBackground(getBackground(activity));
        } catch (Exception e) {
            //If this happens, you have bigger problems than a missing background..
            //You have a missing layout-root.
            //The app should crash right about now :P
            e.printStackTrace();
        }
    }

    public static void setLogo(Activity activity){
        ImageView imageView = ((ImageView) activity.findViewById(R.id.logo));
        imageView.setImageResource(getLogo(activity));
    }

    @Deprecated
    public static void setLogoOLD(Activity activity) {
        if (true) {
            return;
        }
        try {
            ImageView imageView = ((ImageView) activity.findViewById(R.id.logo));
            imageView.setScaleType(ImageView.ScaleType.FIT_START);
            imageView.setMaxHeight(80);
            imageView.setAdjustViewBounds(true);
            //imageView.setImageDrawable(getLogo(activity));
            imageView.invalidate();
            //imageView.setBackground(getLogo(activity));
        } catch (Exception e) {
            //If this happens, you have bigger problems than a missing background..
            //You have a missing layout-root.
            //The app should crash right about now :P
            e.printStackTrace();
        }
    }

    @Deprecated
    public static Drawable getBackground(Context c) {
        //Check if default Background exists
        //Background file should always exist!
        File defaultFile = new File("/sdcard/BAXY/backgrounds/default.png");
        if (!defaultFile.exists()) {
            //make that file
            File folder = new File("/sdcard/BAXY/backgrounds/");
            folder.mkdirs();
            try {
                //add .nomedia
                File noMedia = new File("/sdcard/BAXY/backgrounds/.nomedia");
                noMedia.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                copyResourceToFile(c, R.raw.bg_color, new File("/sdcard/BAXY/backgrounds/default.png"));
                copyResourceToFile(c, R.raw.ouya_background, new File("/sdcard/BAXY/backgrounds/ouya_controller.png"));
                copyResourceToFile(c, R.raw.ouya_console_wallpaper, new File("/sdcard/BAXY/backgrounds/ouya_console.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            //Return Default !
            return c.getResources().getDrawable(R.drawable.bg_color);
        }

        //Check preference
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
        String fileString = defaultSharedPreferences.getString("backgroundFile", "/sdcard/BAXY/backgrounds/default.png");
        File file = new File(fileString);
        if (!file.exists()) {
            //The selected custom background does not exist..
            //Return Default !
            Log.e("BAXY", "Selected Background does not exist !! (return default)");
            return c.getResources().getDrawable(R.drawable.bg_color);
        }
        //File does exist
        //Check if already loaded
        if (loadedBackgroundString != null && loadedBackground != null) {
            if (loadedBackgroundString.equals(fileString)) {
                return loadedBackground;
            }
        }
        //Not loaded yet, load it, return it
        try {
            Drawable d = Drawable.createFromPath(fileString);
            loadedBackground = d;
            loadedBackgroundString = fileString;
            return d;
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Default
        return c.getResources().getDrawable(R.drawable.bg_color);
    }

    public static int getLogo(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String logoType = prefs.getString("logoType", "BAXY");
        if (logoType.equals("BAXY")){
            return R.drawable.logo_default;
        } else if(logoType.equals("OUYA")){
            return R.drawable.logo_ouya_red;
        }
        return R.drawable.logo_default_old;
    }

    @Deprecated
    public static Drawable getLogoOLD(Context c) {
        //Check if default Background exists
        //Background file should always exist!
        File defaultFile = new File("/sdcard/BAXY/logos/logo_default.png");
        if (!defaultFile.exists()) {
            //make that file
            File folder = new File("/sdcard/BAXY/logos/");
            folder.mkdirs();
            try {
                //add .nomedia
                File noMedia = new File("/sdcard/BAXY/logos/.nomedia");
                noMedia.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                copyResourceToFile(c, R.raw.logo_default, new File("/sdcard/BAXY/logos/logo_default.png"));
//                copyResourceToFile(c, R.raw.logo_baxy_white_shadow, new File("/sdcard/BAXY/logos/logo_baxy_white_shadow.pngshadow.png"));
                //copyResourceToFile(c, R.raw.logo_baxy_white, new File("/sdcard/BAXY/logos/logo_baxy_white.png"));
                //copyResourceToFile(c, R.raw.logo_ouya_black, new File("/sdcard/BAXY/logos/logo_ouya_black.png"));
                //copyResourceToFile(c, R.raw.logo_ouya_red, new File("/sdcard/BAXY/logos/logo_ouya_red.png"));
                //copyResourceToFile(c, R.raw.logo_ouya_white, new File("/sdcard/BAXY/logos/logo_ouya_white.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            //Return Default !
            return c.getResources().getDrawable(R.drawable.logo_default_old);
        }

        //Check preference
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
        String fileString = defaultSharedPreferences.getString("logoFile", "/sdcard/BAXY/logos/logo_default.png");
        File file = new File(fileString);
        if (!file.exists()) {
            //The selected custom background does not exist..
            //Return Default !
            Log.e("BAXY", "Selected Logo does not exist !! (return default)");
            return c.getResources().getDrawable(R.drawable.logo_default_old);
        }
        //File does exist
        //Check if already loaded
        if (loadedLogoString != null && loadedLogo != null) {
            if (loadedLogoString.equals(fileString)) {
                return loadedLogo;
            }
        }
        //Not loaded yet, load it, return it
        try {
            Drawable d = Drawable.createFromPath(fileString);
            loadedLogo = d;
            loadedLogoString = fileString;
            return d;
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Default
        return c.getResources().getDrawable(R.drawable.logo_default_old);
    }

    public static void copyResourceToFile(Context c, int resourceId, File file) throws IOException {
        //http://stackoverflow.com/questions/8664468/copying-raw-file-into-sdcard
        InputStream in = c.getResources().openRawResource(resourceId);
        FileOutputStream out = new FileOutputStream(file);
        byte[] buff = new byte[1024];
        int read = 0;
        try {
            while ((read = in.read(buff)) > 0) {
                out.write(buff, 0, read);
            }
        } finally {
            in.close();
            out.close();
        }
    }

    public static void copyAsset(InputStream in, File file) throws IOException {
        //file.createNewFile();
        FileOutputStream out = new FileOutputStream(file);
        byte[] buff = new byte[1024];
        int read = 0;
        try {
            while ((read = in.read(buff)) > 0) {
                out.write(buff, 0, read);
            }
        } finally {
            in.close();
            out.close();
        }
    }

    //Analytics

//    public static void logAppLaunch(Context context, AppInfo info) {
//        //Log this applaunch to Analytics
//        HashMap<String, String> params = new HashMap<String, String>();
//        params.put("packagename", info.packagename);
//        params.put("appname", info.title.toString());
//        params.put("isOUYA", "" + info.isOUYA);
//        params.put("isOUYAGame", "" + info.isOUYAGame);
//        params.put("title", info.title.toString());
//        FlurryAgent.logEvent("AppLaunch", params);
//    }
//
//    public static void logAppLaunch(Context context, DatabaseAppInfo info) {
//        //Log this applaunch to Analytics
//        HashMap<String, String> params = new HashMap<String, String>();
//        params.put("packagename", info.packageName);
//        params.put("appname", info.title.toString());
//        params.put("isOUYA", "" + info.isOUYA());
//        params.put("isOUYAGame", "" + info.isOUYAGame());
//        params.put("title", info.getTitle());
//        FlurryAgent.logEvent("AppLaunch", params);
//    }
//
//    public static void logAppInfo(Context context, String packagename) {
//        HashMap<String, String> params = new HashMap<String, String>();
//        params.put("packagename", packagename);
//        FlurryAgent.logEvent("AppInfo", params);
//    }
//
//    public static void logFilterChange(Context context, int newFilter) {
//        HashMap<String, String> params = new HashMap<String, String>();
//        params.put("filter", "" + newFilter);
//        FlurryAgent.logEvent("AppFilterChange", params);
//    }
//
//    public static void logStartDiscover(Context context) {
//        FlurryAgent.logEvent("startDiscover");
//    }
//
//    public static void logTurnOff(Context context) {
//        FlurryAgent.logEvent("turnOff");
//    }
//
//    public static void logGoToSettings(Context context) {
//        FlurryAgent.logEvent("goToSettings");
//    }
//
//    public static void logGoToApplist(Context context, int newFilter) {
//        HashMap<String, String> params = new HashMap<String, String>();
//        params.put("filter", "" + newFilter);
//        FlurryAgent.logEvent("Applist", params);
//    }
//
//    public static void logSetBackground(Context context, String path) {
//        HashMap<String, String> params = new HashMap<String, String>();
//        params.put("path", path);
//        FlurryAgent.logEvent("setBackground", params);
//    }
//
//    public static void logAddFavorite(Context context, DatabaseAppInfo info) {
//        HashMap<String, String> params = new HashMap<String, String>();
//        params.put("packagename", info.packageName);
//        params.put("title", info.getTitle());
//        FlurryAgent.logEvent("AddFavorite", params);
//    }
//
//    public static void logRemoveFavorite(Context context, DatabaseAppInfo info) {
//        HashMap<String, String> params = new HashMap<String, String>();
//        params.put("packagename", info.packageName);
//        params.put("title", info.getTitle());
//        FlurryAgent.logEvent("AddRemove", params);
//    }
//
//    public static void logAddWidgetConfigure(Context context, String packagename){
//        HashMap<String, String> params = new HashMap<String, String>();
//        params.put("packagename", packagename);
//        FlurryAgent.logEvent("AddWidgetConfigure", params);
//    }
//
//    public static void logAddWidget(Context context, String packagename){
//        HashMap<String, String> params = new HashMap<String, String>();
//        params.put("packagename", packagename);
//        FlurryAgent.logEvent("AddWidget", params);
//    }
//
//    public static void logSetClock(Context context, String type){
//        HashMap<String, String> params = new HashMap<String, String>();
//        params.put("type", type);
//        FlurryAgent.logEvent("SetClock", params);
//    }
//
//    public static void logSetLogo(Context context, String type){
//        HashMap<String, String> params = new HashMap<String, String>();
//        params.put("type", type);
//        FlurryAgent.logEvent("SetLogo", params);
//    }
//
//    public static void logSetMusic(Context context, String path){
//        HashMap<String, String> params = new HashMap<String, String>();
//        params.put("path", path);
//        FlurryAgent.logEvent("SetMusic", params);
//    }
//
//    public static void logSendFeedback(Context context){
//        FlurryAgent.logEvent("SetMusic");
//    }
//
//    public static void logBetaEnable(Context context){
//        FlurryAgent.logEvent("BetaEnable");
//    }
//
//    public static void logBetaDisable(Context context){
//        FlurryAgent.logEvent("BetaDisable");
//    }

    public static void onStop(Context context){
        Intent i = new Intent();
        i.setClass(context, BackgroundMusicService.class);
        i.putExtra("cmd",BackgroundMusicService.CMD_CHECK);
        context.startService(i);
    }

    public static void onStart(Context context){
        Intent i = new Intent();
        i.setClass(context, BackgroundMusicService.class);
        i.putExtra("cmd",BackgroundMusicService.CMD_CHECK);
        context.startService(i);
    }

}