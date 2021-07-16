package com.debanshu777.painter.widget

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.annotation.DrawableRes
import com.debanshu777.painter.R
import kotlin.math.abs


class PaintView(context: Context, attributes: AttributeSet) : View(context, attributes) {
    private lateinit var btnBackground: Bitmap
    private lateinit var btnView: Bitmap
    private var image: Bitmap?
    private var captureImage: Bitmap
    private lateinit var originalImage: Bitmap
    private lateinit var rotateImage: Bitmap
    private var mPaint: Paint = Paint()
    private var mPath: Path = Path()
    private var colorBackground: Int
    private var sizeBrush: Int = 5
    private var sizeEraser: Int
    private var leftPosition: Float = 50f
    private var topPosition: Float = 50f
    private var mX: Float = 0.0f
    private var mY: Float = 0.0f
    private lateinit var mCanvas: Canvas
    private val DIFFERENCE_SPACE: Int = 4
    private var listAction: ArrayList<Bitmap>
    var toMove: Boolean
    var toResize: Boolean
    private var refX: Float = 0.0f
    private var refY: Float = 0.0f
    private var xCenter: Float = 0.0f
    private var yCenter: Float = 0.0f
    private var xRotate: Float = 0.0f
    private var yRotate: Float = 0.0f
    private var angle: Float = 0.0f

    init {
        sizeEraser = sizeBrush - 12
        colorBackground = Color.WHITE
        listAction = ArrayList()
        toMove = false
        toResize = false

        image = null
        val drawable = resources.getDrawable(R.drawable.ic_camera)
        Log.e("here",drawable.toString())
        captureImage = getBitmap(R.drawable.ic_camera)
        rotateImage = getBitmap(R.drawable.ic_rotate)
        //captureImage = BitmapFactory.decodeResource(context.resources, R.drawable.ic_camera)

        mPaint.color = Color.BLACK
        mPaint.isAntiAlias = true
        mPaint.isDither = true
        mPaint.style = Paint.Style.STROKE
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.strokeJoin = Paint.Join.ROUND
        mPaint.strokeWidth = toPx(sizeBrush)

    }

    private fun toPx(sizeBrush: Int): Float {
        return sizeBrush * (resources.displayMetrics.density)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        btnBackground = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        btnView = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        mCanvas = Canvas(btnView)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(colorBackground)
        if (image != null && toMove) {
            drawImage(canvas)
            //canvas.drawBitmap(image!!, leftPosition, topPosition, null)
            xCenter=leftPosition+image!!.width/2 - captureImage.width/2
            yCenter=topPosition+image!!.height/2 - captureImage.height/2


            xRotate=leftPosition+image!!.width+toPx(10)
            yRotate=topPosition-toPx(10)
            canvas.drawBitmap(rotateImage,xRotate,yRotate,null)
            canvas.drawBitmap(captureImage,xCenter,yCenter,null)
        }
        canvas.drawBitmap(btnBackground, 0f, 0f, null)
        canvas.drawBitmap(btnView, 0f, 0f, null)
    }

    private fun drawImage(canvas: Canvas) {
        var matrix= Matrix()
        matrix.setRotate(angle,(image!!.width/2).toFloat(), (image!!.height/2).toFloat())
        matrix.postTranslate(leftPosition,topPosition)
        canvas.drawBitmap(image!!,matrix,null)
    }

    fun setColorBackground(color: Int) {
        colorBackground = color
        invalidate()
        Log.e("SetBackgroundColor ", color.toString())
    }

    fun setSizeBrush(size: Int) {
        sizeBrush = size
        mPaint.strokeWidth = toPx(sizeBrush)
    }

    fun setBrushColor(color: Int) {
        mPaint.color = color
        invalidate()
        Log.e("SetBrushColor ", color.toString())
    }

    fun setEraserSize(size: Int) {
        sizeEraser = size
        Log.e("SizeEraser ", sizeEraser.toString())
        mPaint.strokeWidth = toPx(sizeEraser)
    }

    private fun getBitmap(@DrawableRes resId: Int): Bitmap {
        val drawable = resources.getDrawable(resId)
        val canvas = Canvas()
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        canvas.setBitmap(bitmap)
        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
        drawable.draw(canvas)
        return bitmap
    }
    fun enableEraser() {
        mPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    fun disableEraser() {
        mPaint.xfermode = null
        mPaint.shader = null
        mPaint.maskFilter = null
    }

    private fun addLastAction(bitmap: Bitmap) {
        listAction.add(bitmap)
    }

    fun returnLastAction() {
        if (listAction.size > 0) {
            listAction.removeAt(listAction.size - 1)
            if (listAction.size > 0) {
                btnView = listAction[listAction.size - 1]
            } else {
                btnView = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            }
            mCanvas = Canvas(btnView)
            invalidate()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x: Float = event.x
        val y: Float = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchStart(x, y)
                refX = x
                refY = y
                if (toMove) {
                    toResize = isToResize(refX,refY)
                       if((refX>=xCenter && refX<xCenter+ captureImage.width)
                           && (refY>=yCenter && refY<yCenter+ captureImage.height)){
                           val newCanvas=Canvas(btnBackground)
                           drawImage(newCanvas)
                           invalidate()
                       }
                    if((refX>=xRotate && refX<=xRotate+rotateImage.width)
                        && (refY>=yRotate && refY<=yRotate+rotateImage.height)){
                        angle+=40
                        invalidate()
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (!toMove)
                    touchMove(x, y)
                else{
                    val nX=event.x
                    val nY=event.y

                    if(toResize){
                        val xScale: Int = if(nX>refX){
                            ((image!!.width)+(nX-refX)).toInt()
                        }else{
                            ((image!!.width)-(refX-nX)).toInt()
                        }
                        val yScale: Int = if(nY>refY){
                            ((image!!.height)+(nY-refY)).toInt()
                        }else{
                            ((image!!.height)-(refY-nY)).toInt()
                        }
                        if(xScale >0 || yScale>0){
                            image= Bitmap.createScaledBitmap(originalImage,xScale,yScale,false)
                        }
                    }

                    leftPosition+=nX-refX
                    topPosition+=nY-refY
                    refX=nX
                    refY=nY
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
                addLastAction(getBitMap())
                touchUp()
            }
        }

        return true
    }

    private fun isToResize(refX: Float, refY: Float): Boolean {
        if((refX<=leftPosition || refX>=leftPosition+image!!.width)
            ||((refY <=topPosition || refY >=topPosition+image!!.height)))
                return true
        return false
    }

    private fun touchUp() {
        mPath.reset()
        invalidate()
    }

    private fun touchMove(x: Float?, y: Float?) {
        val dx: Float = abs(x!! - mX)
        val dy: Float = abs(y!! - mY)
        if (dx >= DIFFERENCE_SPACE || dy >= DIFFERENCE_SPACE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2)
            mY = y
            mX = x
            mCanvas.drawPath(mPath, mPaint)
            invalidate()
        }
    }

    private fun touchStart(x: Float?, y: Float?) {
        mPath.reset();
        mPath.moveTo(x!!, y!!)
        mX = x
        mY = y
        invalidate()
    }

    fun getBitMap(): Bitmap {
        this.isDrawingCacheEnabled = true
        this.buildDrawingCache()
        val bitmap: Bitmap = Bitmap.createBitmap(this.getDrawingCache())
        this.isDrawingCacheEnabled = false
        return bitmap
    }

    fun setImage(bitmap: Bitmap) {
        toMove=true
        image = Bitmap.createScaledBitmap(bitmap, width / 2, height / 2, true)
        originalImage=image!!
        invalidate()
    }
}