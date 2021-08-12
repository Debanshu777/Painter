package com.debanshu777.painter.ui

import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.SeekBar
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.debanshu777.painter.R
import com.debanshu777.painter.utils.Constant
import com.debanshu777.painter.widget.PaintView
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_paint.*
import kotlinx.android.synthetic.main.layout_dialog.view.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*

class MainActivityViewModel(app:Application) : ViewModel(){
    var colorBackground: MutableLiveData<Int> = MutableLiveData()
    var colorBrush: MutableLiveData<Int> = MutableLiveData()
    var brushSize: MutableLiveData<Int> = MutableLiveData()
    var eraserSize: MutableLiveData<Int> = MutableLiveData()
    var isSaved:MutableLiveData<Boolean> = MutableLiveData()
    var paintView:MutableLiveData<PaintView> = MutableLiveData()
    init {
        paintView=MutableLiveData()
        colorBackground = MutableLiveData(Color.WHITE)
        colorBrush = MutableLiveData(Color.BLACK)
        brushSize = MutableLiveData(5)
        eraserSize = MutableLiveData(5)
    }

    fun showDialogSize(isEraser: Boolean,context:Context) {
        val builder: AlertDialog.Builder= AlertDialog.Builder(context)
        val view: View = LayoutInflater.from(context).inflate(R.layout.layout_dialog,null,false)

        view.seekbar_size.max=99
        if(isEraser){
            view.seekbar_size.progress = eraserSize.value!!
            view.status_tools_selected.text="Eraser Size"
            view.iv_tools.setImageResource(R.drawable.ic_erase)
            view.status_size.text="Selected Size:${eraserSize.value}"
        }else{
            view.seekbar_size.progress = brushSize.value!!
            view.status_tools_selected.text="Brush Size"
            view.iv_tools.setImageResource(R.drawable.ic_brush)
            view.status_size.text="Selected Size:${brushSize.value}"
        }
        view.seekbar_size.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if(isEraser){
                    eraserSize.postValue(progress)
                    view.status_size.text="Selected Size:${eraserSize.value}"

                }else{
                    brushSize.postValue(progress)
                    view.status_size.text="Selected Size:${brushSize.value}"
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })
        builder.setPositiveButton("Save"
        ) { dialog, _ ->
            dialog.dismiss()
        }
        builder.setView(view)
        builder.show()

    }


    fun updateColor(optionName: String,context:Context) {
        val color = if (optionName == Constant.BACKGROUND) {
            colorBackground
        } else {
            colorBrush
        }
        ColorPickerDialogBuilder
            .with(context)
            .setTitle("Choose Color")
            .initialColor(color.value!!)
            .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
            .setPositiveButton(
                "OK"
            ) { _, lastSelectedColor, _ ->
                if (optionName == Constant.BACKGROUND) {
                    Log.e("ColorPicker Background", colorBackground.toString())
                    colorBackground.postValue(lastSelectedColor)
                    //paint_base_layout.setColorBackground(colorBackground)
                } else {
                    Log.e("ColorPicker Brush", colorBackground.toString())
                    colorBrush.postValue(lastSelectedColor)
                    //paint_base_layout.setBrushColor(colorBrush)
                }
            }
            .setNegativeButton(
                "Cancel"
            ) { dialog, _ -> dialog.dismiss() }
            .build()
            .show()

    }

    private fun sendPictureToGallery(imageUri: Uri, activity:Activity) {
        val i = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        i.data = imageUri
        activity.sendBroadcast(i)
    }
    fun saveImages(bitmap:Bitmap,activity:Activity) {
            //val bitmap: Bitmap = paint_base_layout.getBitMap()
            val filename = "${UUID.randomUUID()}.png"
            var outputStream: OutputStream?
            var isSaved: Boolean

            Log.e("Saved Image Name", filename)
            Log.e(
                " Saved Image Path",
                activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                    .toString() + File.separator + activity.getString(R.string.app_name)
            )

            val folder: File = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                File(
                    activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                        .toString() + File.separator + activity.getString(R.string.app_name)
                )
            } else {
                File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                        .toString() + File.separator + activity.getString(R.string.app_name)
                )
            }
            if (!folder.exists()) {
                folder.mkdirs()
            }
            try {
                val toBeSavedFile = File(folder.toString() + File.separator + filename)
                val imageUri = Uri.fromFile(toBeSavedFile)

                Log.e("File Stream Name", folder.toString() + File.separator + filename)
                outputStream = FileOutputStream(toBeSavedFile)
                isSaved = bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val resolver: ContentResolver = activity.contentResolver
                    val contentValue = ContentValues()
                    contentValue.put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    contentValue.put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                    contentValue.put(
                        MediaStore.MediaColumns.RELATIVE_PATH,
                        Environment.DIRECTORY_PICTURES + File.separator + activity.getString(R.string.app_name)
                    )
                    val uri =
                        resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValue)
                    outputStream = resolver.openOutputStream(uri!!)
                    isSaved= bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)

                } else {
                    sendPictureToGallery(imageUri,activity)
                }
//                if (isSaved) {
//                    Snackbar.make(paint_base_layout, "Image Saved", Snackbar.LENGTH_LONG).show()
//
//                } else {
//                    Snackbar.make(paint_base_layout, "Something Went Wrong", Snackbar.LENGTH_LONG)
//                        .show()
//                }

                outputStream?.flush()
                outputStream?.close()
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }

    }

    fun storeDrawingStateOnOrientationChange(view: PaintView){
        Log.e("here",""+view.toMove)
        paintView.postValue(view)
    }
}