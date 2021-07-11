package com.debanshu777.painter.ui.fragment

import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.debanshu777.painter.R
import com.debanshu777.painter.adapter.GalleryAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_paint.*
import kotlinx.android.synthetic.main.fragment_saved_image.*
import java.io.File

class SavedImageFragment : Fragment(R.layout.fragment_saved_image) {

    private lateinit var galleryAdapter:GalleryAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialSetup()
        galleryAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("file", it)
            }
            findNavController().navigate(R.id.action_savedImageFragment_to_fullImageScreenFragment,bundle)
        }

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }


            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition

                Snackbar.make(view, "Successfully Deleted Article", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo") {

                    }
                    show()
                }
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(galleryView)
        }


    }

    private fun initialSetup() {
        toolbar.title="Pictures"
        galleryAdapter = GalleryAdapter(loadFile())
        galleryView.apply {
            adapter = galleryAdapter
            layoutManager = GridLayoutManager(activity,  3)
        }
    }

    private fun loadFile(): ArrayList<File> {
        val picList:ArrayList<File> = ArrayList()
        val parendDir= if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.Q){
            File(
                requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                    .toString() + File.separator + getString(R.string.app_name)
            )
        }else {
            File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    .toString() + File.separator + getString(R.string.app_name)
            )
        }
        val files=parendDir.listFiles()

        for(i in files){
           if(i.name.endsWith(".png")){
               picList.add(i)
           }
        }
        return picList
    }

}