package com.debanshu777.painter.ui.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.debanshu777.painter.R
import com.debanshu777.painter.adapter.OptionAdapter
import com.debanshu777.painter.model.Option
import com.debanshu777.painter.utils.Constant.Companion.BACKGROUND
import com.debanshu777.painter.utils.Constant.Companion.BRUSH
import com.debanshu777.painter.utils.Constant.Companion.ERASER
import com.debanshu777.painter.utils.Constant.Companion.PALETTE
import com.debanshu777.painter.utils.Constant.Companion.UNDO
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerClickListener
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import kotlinx.android.synthetic.main.fragment_paint.*
import kotlinx.android.synthetic.main.item_option.*
import kotlinx.android.synthetic.main.layout_dialog.view.*

class PaintFragment : Fragment(R.layout.fragment_paint) {
    private lateinit var optionAdapter: OptionAdapter
    private lateinit var list: ArrayList<Option>
    private var colorBackground: Int = 0
    private var colorBrush: Int = 0
    private var brushSize: Int = 0
    private var eraserSize: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialSetup()
        gallery_btn.setOnClickListener {
            findNavController().navigate(R.id.action_paintFragment_to_savedImageFragment)
        }
        optionAdapter.setOnItemClickListener {
            when(it.optionName){
                BRUSH->{
                    paint_base_layout.disableEraser()
                    showDialogSize(false)
                }
                ERASER->{
                    paint_base_layout.enableEraser()
                    showDialogSize(true)
                }
                UNDO->{
                    paint_base_layout.returnLastAction()
                }
                BACKGROUND->{
                    updateColor(BACKGROUND)
                }
                PALETTE->{
                    updateColor(PALETTE)
                }

            }
        }

    }

    private fun updateColor(optionName: String) {
        val color= if(optionName == BACKGROUND){
            colorBackground
        }else{
            colorBrush
        }
        ColorPickerDialogBuilder
            .with(context)
            .setTitle("Choose Color")
            .initialColor(color)
            .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
            .setPositiveButton("OK"
            ) { _, lastSelectedColor, _ ->
                if(optionName == BACKGROUND){
                    Log.e("ColorPicker Background",colorBackground.toString())
                    colorBackground=lastSelectedColor
                    paint_base_layout.setColorBackground(colorBackground)
                }else{
                    Log.e("ColorPicker Brush",colorBackground.toString())
                    colorBrush=lastSelectedColor
                    paint_base_layout.setBrushColor(colorBrush)
                }
            }
            .setNegativeButton("Cancel"
            ) { dialog, _ -> dialog.dismiss()}
            .build()
            .show()

    }


    private fun showDialogSize(isEraser: Boolean) {
        val builder:AlertDialog.Builder=AlertDialog.Builder(activity)
        val view:View=LayoutInflater.from(context).inflate(R.layout.layout_dialog,null,false)

        view.seekbar_size.max=99
        if(isEraser){
            view.status_tools_selected.text="Eraser Size"
            view.iv_tools.setImageResource(R.drawable.ic_erase)
            view.status_size.text="Selected Size:${eraserSize}"
        }else{
            view.status_tools_selected.text="Brush Size"
            view.iv_tools.setImageResource(R.drawable.ic_brush)
            view.status_size.text="Selected Size:${brushSize}"
        }
        view.seekbar_size.setOnSeekBarChangeListener(object :
            SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
               if(isEraser){
                   eraserSize+=1
                   view.status_size.text="Selected Size:${eraserSize}"
                   paint_base_layout.setEraserSize(eraserSize)
               }else{
                   brushSize+=1
                   view.status_size.text="Selected Size:${brushSize}"
                   paint_base_layout.setSizeBrush(brushSize)
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

    private fun data(): ArrayList<Option> {
        list = ArrayList()
        list.add(
            Option(
                R.drawable.ic_brush,
                BRUSH
            )
        )
        list.add(
            Option(
                R.drawable.ic_erase,
                ERASER
            )
        )
        list.add(
            Option(
                R.drawable.ic_palette, PALETTE
            )
        )
        list.add(
            Option(
                R.drawable.ic_fill_color, BACKGROUND
            )
        )
        list.add(
            Option(
                R.drawable.ic_undo, UNDO
            )
        )

        Log.e("Tag", list.size.toString())
        return list
    }

    private fun initialSetup() {

        colorBackground = Color.WHITE
        colorBrush = Color.BLACK
        eraserSize = brushSize - 12;
        //Recycler View Setup
        data()
        optionAdapter = OptionAdapter(list)
        options_layout.apply {
            adapter = optionAdapter
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        }
    }


}