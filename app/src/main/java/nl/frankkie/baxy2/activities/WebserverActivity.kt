package nl.frankkie.baxy2.activities

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import nl.frankkie.baxy2.MyServer


import java.io.File
import java.io.IOException
import java.net.NetworkInterface
import java.util.Collections
import nl.frankkie.baxy2.R
import nl.frankkie.baxy2.util.Util

/**
 * Created by FrankkieNL on 4-8-13.
 */
class WebserverActivity : Activity() {

    lateinit var tv1: TextView
    lateinit var tv2: TextView
    lateinit var tv2sb: StringBuilder
    var portNumber = 1234
    var handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        initUI()
        initWebserverStuff()
        initQR()
    }

    fun initQR() {
        val imageView = findViewById<View>(R.id.webserver_qr) as ImageView
        //String adres = "http%3A%2F%2F192.168.1.108%3A1234%2F";
        val adres = "http%3A%2F%2F" + getIPAddress(true) + "%3A" + portNumber + "%2F"
        //UrlImageViewHelper.setUrlDrawable(imageView, "http://chart.apis.google.com/chart?cht=qr&chs=512x512&chld=L&choe=UTF-8&chl=" + adres);
    }

    fun initWebserverStuff() {
        //check files
        putFiles()
        //https://github.com/NanoHttpd/nanohttpd
        MyServer.main(this)
    }

    fun putFiles() {
        val webDir = File("/sdcard/BAXY/web/")
        if (!webDir.exists()) {
            webDir.mkdirs()
            val webJsDir = File("/sdcard/BAXY/web/js/")
            val webCssDir = File("/sdcard/BAXY/web/css/")
            webJsDir.mkdirs()
            webCssDir.mkdirs()
            try {
                val asm = assets
                val l = asm.list("")
                val m = asm.list("webkit")
                val list = asm.list("web/js")
                for (s in list!!) {
                    val `in` = asm.open("web/js/$s")
                    try {
                        Util.copyAsset(`in`, File("/sdcard/BAXY/web/js/$s"))
                    } catch (e: Exception) {
                        //
                        e.printStackTrace()
                    }

                }
                val list2 = asm.list("web/css")
                for (s in list2!!) {
                    val `in` = asm.open("web/css/$s")
                    try {
                        Util.copyAsset(`in`, File("/sdcard/BAXY/web/css/$s"))
                    } catch (e: Exception) {
                        //
                        e.printStackTrace()
                    }

                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    fun initUI() {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(R.layout.webserver)
        //Util.setBackground(this);
        Util.setLogo(this)
        tv1 = findViewById<View>(R.id.webserver_tv1) as TextView
        tv2 = findViewById<View>(R.id.webserver_tv2) as TextView
        tv2sb = StringBuilder()
        log("Begin Log")
        val template = "Please use our browser on your PC and go to:\nhttp://%s:1234/"
        tv1.text = String.format(template, getIPAddress(true))
    }

    fun log(s: String) {
        handler.post { logHandler(s) }
    }

    fun logHandler(s: String) {
        Log.e("BAXY", s)
        tv2sb.insert(0, s + "\n")
        tv2.text = tv2sb.toString()
        tv2.invalidate()
        tv2.postInvalidate()
    }


    override fun onStart() {
        super.onStart()
        Util.onStart(this)
    }

    override fun onStop() {
        super.onStop()
        Util.onStop(this)
    }

    companion object {
        lateinit var context: Context

        /**
         * Get IP address from first non-localhost interface
         *
         * @param useIPv4 true=return ipv4, false=return ipv6
         * @return address or empty string
         * http://stackoverflow.com/questions/6064510/how-to-get-ip-address-of-the-device
         */
        fun getIPAddress(useIPv4: Boolean): String {
            try {
                val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
                for (intf in interfaces) {
                    val addrs = Collections.list(intf.inetAddresses)
                    for (addr in addrs) {
                        if (!addr.isLoopbackAddress) {
                            val sAddr = addr.hostAddress.toUpperCase()
                            //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                            val isIPv4 = true
                            if (useIPv4) {
                                if (isIPv4)
                                    return sAddr
                            } else {
                                if (!isIPv4) {
                                    val delim = sAddr.indexOf('%') // drop ip6 port suffix
                                    return if (delim < 0) sAddr else sAddr.substring(0, delim)
                                }
                            }
                        }
                    }
                }
            } catch (ex: Exception) {
            }
            // for now eat exceptions
            return ""
        }

        val uploadHtml = "\n" +
                "<form enctype=\"multipart/form-data\" action=\"/upload/\" method=\"post\">\n" +
                "<input type=\"hidden\" name=\"MAX_FILE_SIZE\" value=\"2000000\">\n" +
                "File: <input name=\"uploadFile\" type=\"file\"><br>\n" +
                "Path: <input type=\"text\" name=\"pad\" value=\"/sdcard/\"><br>\n" +
                "<input name=\"gezien\" value=\"ja\" type=\"hidden\">\n" +
                "<input type=\"submit\" value=\"Start Upload\" name=\"submitButton\">\n" +
                "</form>\n"
    }
}
