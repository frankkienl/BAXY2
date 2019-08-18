package nl.frankkie.baxy2.web;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

import static fi.iki.elonen.NanoHTTPD.Response;

/**
 * Created by FrankkieNL on 13-8-13.
 */
public class GetApps extends WebPage {

    public Response serve(String uri, NanoHTTPD.Method method, Map<String, String> header, Map<String, String> parms, Map<String, String> files) {
        Response response = null;
//        if (WebserverActivity.Companion.getContext() == null) {
//            return new Response(Response.Status.NOT_FOUND, "text/html", "Apps not found !\ntry restarting webserver.");
//        }
//        DatabaseOpenHelper helper = DatabaseOpenHelper.CreateInstance(WebserverActivity.Companion.getContext());
//        Cursor cursor = helper.WriteableDatabase.rawQuery("SELECT id FROM appinfo", null);
//        if (cursor.getCount() == 0) {
//            return new Response(Response.Status.NOT_FOUND, "text/html", "Apps not found !\nGo to applist and restart webserver.");
//        }
//        ArrayList<DatabaseAppInfo> appInfoArrayList = new ArrayList<DatabaseAppInfo>();
//        while (cursor.moveToNext()) {
//            DatabaseAppInfo appInfo = new DatabaseAppInfo(cursor.getInt(0));
//            appInfo.OnLoad();
//            appInfoArrayList.add(appInfo);
//        }
//        JSONSerializer serializer = new JSONSerializer();
//        ArrayList<String> excludes = new ArrayList<String>();
//        excludes.add("componentName");
//        excludes.add("lastOpened");
//        excludes.add("timesOpened");
//        serializer.setExcludes(excludes);
//        String json = serializer.serialize(appInfoArrayList);
//        response = new Response(Response.Status.OK, "text/json", json);
        return response;
    }
}
