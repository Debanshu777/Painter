package com.debanshu777.painter.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.util.jar.Attributes
import kotlin.math.abs

class PaintView(context: Context,attributes: AttributeSet) : View(context,attributes) {
    private lateinit var btnBackground: Bitmap
    private lateinit var btnView:Bitmap
    private lateinit var mPaint:Paint
    lateinit var mPath:Path
    private var colorBackground:Int = 0
    private var sizeBrush:Int=0
    var sizeEraser:Int=0
    var mX:Float=0f
    var mY:Float=0f
    lateinit var mCanvas: Canvas
    val DIFFERENCE_SPACE:Int=4
    private var listAction:ArrayList<Bitmap>
    init {
        sizeEraser=sizeBrush-12
        colorBackground=Color.WHITE
        listAction= ArrayList()

        mPaint.color = Color.BLACK
        mPaint.isAntiAlias=true
        mPaint.isDither=true
        mPaint.style=Paint.Style.STROKE
        mPaint.strokeCap=Paint.Cap.ROUND
        mPaint.strokeJoin=Paint.Join.ROUND
        mPaint.strokeWidth=toPx(sizeBrush)

    }

    private fun toPx(sizeBrush: Int): Float {
        return  sizeBrush*(resources.displayMetrics.density)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        btnBackground= Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888)
        btnView= Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888)
        mCanvas= Canvas(btnView)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(colorBackground)
        canvas.drawBitmap(btnBackground,0f,0f,null)
        canvas.drawBitmap(btnView,0f,0f,null)
    }
    fun setColorBackground(color:Int):Unit{
        colorBackground=color
        invalidate()
    }

    fun setSizeBrush(size:Int){
        sizeBrush=size
        mPaint.strokeWidth= toPx(sizeBrush)
    }
    fun setBrushColor(color:Int){
        mPaint.color = color
    }

    fun setEraser(size:Int){
        sizeEraser=size
        mPaint.strokeWidth=toPx(sizeBrush)
    }

    fun enableEraser(){
        mPaint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    fun disableEraser(){
        mPaint.xfermode=null
        mPaint.shader=null
        mPaint.maskFilter=null
    }

    fun addLastAction(bitmap: Bitmap){
        listAction.add(bitmap)
    }

    fun returnLastAction(){
        if(listAction.size>0){
            listAction.removeAt(listAction.size - 1)
            if(listAction.size>0){
                btnView= listAction[listAction.size-1]
            }else{
                btnView= Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888)
            }
            mCanvas= Canvas(btnView)
            invalidate()
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val x: Float? = event?.x
        val y: Float? = event?.y
        when(event?.action){
            MotionEvent.ACTION_DOWN->{
                touchStart(x,y)
            }
            MotionEvent.ACTION_MOVE->{
                touchMove(x,y)
            }
            MotionEvent.ACTION_UP->{
                touchUp()
            }
        }

        return true
    }

    private fun touchUp() {
        mPath.reset()
    }

    private fun touchMove(x: Float?, y: Float?) {
        val dx:Float= abs(x!!-mX)
        val dy:Float= abs(y!!-mY)
        if(dx>=DIFFERENCE_SPACE || dy>=DIFFERENCE_SPACE){
           mPath.quadTo(x,y,(x-mX)/2,(y-mY)/2)
           mY=y
           mX=x
           mCanvas.drawPath(mPath,mPaint)
           invalidate()
        }
    }

    private fun touchStart(x: Float?, y: Float?) {
        mPath.moveTo(x!!,y!!)
        mX= x
        mY=y
    }

    fun getBitMap():Bitmap{
        this.isDrawingCacheEnabled=true
        this.buildDrawingCache()
        val bitmap: Bitmap= Bitmap.createBitmap(this.getDrawingCache())
        this.isDrawingCacheEnabled=false
        return bitmap
    }
}