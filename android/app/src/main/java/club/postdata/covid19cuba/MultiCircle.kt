package club.postdata.covid19cuba

import org.mapsforge.core.graphics.Canvas
import org.mapsforge.core.graphics.Paint
import org.mapsforge.core.model.BoundingBox
import org.mapsforge.core.model.LatLong
import org.mapsforge.core.model.Point
import org.mapsforge.core.model.Rectangle
import org.mapsforge.core.util.MercatorProjection
import org.mapsforge.map.layer.Layer
import kotlin.math.max
import kotlin.math.roundToInt

open class MultiCircle : Layer {
    private var latLongList: List<LatLong>
    private var radius = 0f
    private var paintFill: Paint?
    private var paintStroke: Paint?
    private var isKeepAligned: Boolean = false

    constructor(latLongList: List<LatLong>, radius: Float, paintFill: Paint?, paintStroke: Paint?,
                isKeepAligned: Boolean) {
        setRadiusInternal(radius)
        this.latLongList = latLongList
        this.paintFill = paintFill
        this.paintStroke = paintStroke
        this.isKeepAligned = isKeepAligned
    }

    constructor(latLongList: List<LatLong>, radius: Float, paintFill: Paint?, paintStroke: Paint?) :
            this(latLongList, radius, paintFill, paintStroke, false)

    @Synchronized
    fun contains(center: Point, point: Point?, latitude: Double, zoomLevel: Byte): Boolean {
        // Touch min 20x20 px at baseline mdpi (160dpi)
        val distance = max(20 / 2 * displayModel.scaleFactor,
                getRadiusInPixels(latitude, zoomLevel).toFloat()).toDouble()
        return center.distance(point) < distance
    }

    @Synchronized
    override fun draw(boundingBox: BoundingBox, zoomLevel: Byte, canvas: Canvas,
                      tlPoint: Point) {
        if (paintStroke == null && paintFill == null) {
            return
        }
        for (latLong in latLongList) {
            val latitude = latLong.latitude
            val longitude = latLong.longitude
            val mapSize = MercatorProjection.getMapSize(zoomLevel, displayModel.tileSize)
            val pixelX = MercatorProjection.longitudeToPixelX(longitude, mapSize) - tlPoint.x
            val pixelY = MercatorProjection.latitudeToPixelY(latitude, mapSize) - tlPoint.y
            val radiusInPixel = getRadiusInPixels(latitude, zoomLevel)
            val canRect = Rectangle(0.0, 0.0, canvas.width.toDouble(), canvas.height.toDouble())
            if (!canRect.intersectsCircle(pixelX, pixelY, radiusInPixel.toDouble())) {
                return
            }
            if (paintStroke != null) {
                if (isKeepAligned) {
                    paintStroke!!.setBitmapShaderShift(tlPoint)
                }
                canvas.drawCircle(pixelX.roundToInt(), pixelY.roundToInt(), radiusInPixel, paintStroke)
            }
            if (paintFill != null) {
                if (isKeepAligned) {
                    paintFill!!.setBitmapShaderShift(tlPoint)
                }
                canvas.drawCircle(pixelX.roundToInt(), pixelY.roundToInt(), radiusInPixel, paintFill)
            }
        }
    }

    private fun getRadiusInPixels(latitude: Double, zoomLevel: Byte): Int {
        val mapSize = MercatorProjection.getMapSize(zoomLevel, displayModel.tileSize)
        return MercatorProjection.metersToPixels(radius, latitude, mapSize).toInt()
    }

    private fun setRadiusInternal(radius: Float) {
        require(!(radius < 0 || radius.isNaN())) { "invalid radius: $radius" }
        this.radius = radius
    }
}