package club.postdata.covid19cuba

import android.app.Activity
import android.os.Bundle
import org.mapsforge.core.graphics.Paint
import org.mapsforge.core.graphics.Style
import org.mapsforge.core.model.BoundingBox
import org.mapsforge.core.model.LatLong
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import org.mapsforge.map.android.util.AndroidUtil
import org.mapsforge.map.android.view.MapView
import org.mapsforge.map.datastore.MapDataStore
import org.mapsforge.map.layer.renderer.TileRendererLayer
import org.mapsforge.map.reader.MapFile
import org.mapsforge.map.rendertheme.InternalRenderTheme
import java.io.File

class MapActivity : Activity() {
    private var mapName = "cuba.map"
    private var mapView: MapView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidGraphicFactory.createInstance(application)
        mapView = MapView(this)
        setContentView(mapView)
        try {
            mapView!!.mapScaleBar.isVisible = true
            mapView!!.setBuiltInZoomControls(false)
            val tileCache = AndroidUtil.createTileCache(this, "mapcache",
                    mapView!!.model.displayModel.tileSize, 1f,
                    mapView!!.model.frameBufferModel.overdrawFactor)
            val mapFile = File(getExternalFilesDir(null), mapName)
            val mapDataStore: MapDataStore = MapFile(mapFile)
            val tileRendererLayer = TileRendererLayer(tileCache, mapDataStore,
                    mapView!!.model.mapViewPosition, AndroidGraphicFactory.INSTANCE)
            tileRendererLayer.setXmlRenderTheme(InternalRenderTheme.DEFAULT)
            mapView!!.layerManager.layers.add(tileRendererLayer)
            val paint: Paint = AndroidGraphicFactory.INSTANCE.createPaint()
            paint.color = AndroidGraphicFactory.INSTANCE.createColor(100, 255, 0, 0)
            paint.strokeWidth = 0f
            paint.setStyle(Style.FILL)
            val latLongList = listOf(LatLong(23.1136, -82.3666),
                    LatLong(23.1136, -82.3666),
                    LatLong(23.1130, -82.3660),
                    LatLong(23.1140, -82.3667),
                    LatLong(23.1100, -82.3670),
                    LatLong(23.1150, -82.3666))
            val circles = MultiCircle(latLongList, 100f, paint, null)
            mapView!!.layerManager.layers.add(circles)
            val bounding: BoundingBox = BoundingBox.fromString("19.25330,-85.10663,23.47253,-73.70831")
            mapView!!.model.mapViewPosition.mapLimit = bounding
            mapView!!.setCenter(LatLong(23.1136, -82.3666))
            mapView!!.setZoomLevel(12.toByte())
            mapView!!.setZoomLevelMin(8.toByte())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        mapView!!.destroyAll()
        AndroidGraphicFactory.clearResourceMemoryCache()
        super.onDestroy()
    }

}