package com.debanshu777.painter.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
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
import kotlinx.android.synthetic.main.fragment_paint.*

class PaintFragment : Fragment(R.layout.fragment_paint) {
    private lateinit var optionAdapter: OptionAdapter
    lateinit var list: ArrayList<Option>
    var colorBackground: Int = 0
    var colorBrush: Int = 0
    var brushSize: Int = 0
    var eraserSize: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialSetup()
        gallery_btn.setOnClickListener {
            findNavController().navigate(R.id.action_paintFragment_to_savedImageFragment)
        }
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