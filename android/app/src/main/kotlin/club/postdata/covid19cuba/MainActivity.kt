package club.postdata.covid19cuba

import android.content.Intent
import androidx.annotation.NonNull
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugins.GeneratedPluginRegistrant

class MainActivity : FlutterActivity() {
    private val channelName = "io.educup.methodchannelstest/test"

    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        GeneratedPluginRegistrant.registerWith(flutterEngine)
        val platform = MethodChannel(flutterEngine.dartExecutor.binaryMessenger, channelName)
        platform.setMethodCallHandler { call, result ->
            when (call.method) {
                "openMap" -> {
                    val intent = Intent(this@MainActivity, MapActivity::class.java)
                    startActivity(intent)
                }
                else -> result.notImplemented()
            }
        }
    }
}