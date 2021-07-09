package com.debanshu777.painter.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.debanshu777.painter.R
import com.debanshu777.painter.adapter.OptionAdapter
import com.debanshu777.painter.model.Option
import kotlinx.android.synthetic.main.fragment_paint.*

class PaintFragment : Fragment(R.layout.fragment_paint) {
    private lateinit var optionAdapter: OptionAdapter
    lateinit var list: ArrayList<Option>
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        data()
        setupRecyclerView()
        gallery_btn.setOnClickListener {
            findNavController().navigate(R.id.action_paintFragment_to_savedImageFragment)
        }
    }
    private fun data(): ArrayList<Option> {
        list = ArrayList()
        list.add(
            Option(
                R.drawable.ic_brush,
                "Brush"
            )
        )
        list.add(
            Option(
                R.drawable.ic_erase,
                "Eraser"
            )
        )
        list.add(
            Option(
                R.drawable.ic_palette
                ,"Palette"
            )
        )
        list.add(
            Option(
                R.drawable.ic_fill_color
                ,"Background"
            )
        )
        list.add(
            Option(
                R.drawable.ic_undo
                ,"Undo"
            )
        )

        Log.e("Tag", list.size.toString())
        return list
    }
    private fun setupRecyclerView() {
        optionAdapter= OptionAdapter(list)
        options_layout.apply {
            adapter = optionAdapter
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        }
    }


}