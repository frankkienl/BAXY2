package nl.frankkie.baxy2.web;

import android.content.Intent;
import android.net.Uri;

import java.io.File;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;
import nl.frankkie.baxy2.activities.WebserverActivity;

/**
 * Created by FrankkieNL on 12-8-13.
 */
public class InstallAPK extends WebPage {
    public NanoHTTPD.Response serve(String uri, NanoHTTPD.Method method, Map<String, String> header, Map<String, String> parms, Map<String, String> files) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(parms.get("path"))), "application/vnd.android.package-archive");
        WebserverActivity.Companion.getContext().startActivity(intent);
        NanoHTTPD.Response response = new NanoHTTPD.Response(NanoHTTPD.Response.Status.OK, NanoHTTPD.MIME_HTML, "OK !");
        return response;
    }
}
