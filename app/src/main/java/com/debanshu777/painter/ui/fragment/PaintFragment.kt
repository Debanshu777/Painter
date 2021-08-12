package com.debanshu777.painter.ui.fragment

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.debanshu777.painter.R
import com.debanshu777.painter.adapter.OptionAdapter
import com.debanshu777.painter.model.Option
import com.debanshu777.painter.ui.MainActivity
import com.debanshu777.painter.ui.MainActivityViewModel
import com.debanshu777.painter.utils.Constant.Companion.BACKGROUND
import com.debanshu777.painter.utils.Constant.Companion.BRUSH
import com.debanshu777.painter.utils.Constant.Companion.CAMERA
import com.debanshu777.painter.utils.Constant.Companion.ERASER
import com.debanshu777.painter.utils.Constant.Companion.IMAGE
import com.debanshu777.painter.utils.Constant.Companion.PALETTE
import com.debanshu777.painter.utils.Constant.Companion.PERMISSION_STORAGE_REQUEST_CODE
import com.debanshu777.painter.utils.Constant.Companion.PICK_IMAGE
import com.debanshu777.painter.utils.Constant.Companion.UNDO
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import kotlinx.android.synthetic.main.fragment_paint.*
import kotlinx.android.synthetic.main.layout_dialog.view.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*
import kotlin.collections.ArrayList

class PaintFragment : Fragment(R.layout.fragment_paint), EasyPermissions.PermissionCallbacks {
    lateinit var viewModel: MainActivityViewModel
    private lateinit var optionAdapter: OptionAdapter
    private lateinit var list: ArrayList<Option>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel
        initialSetup()

        gallery_btn.setOnClickListener {
            findNavController().navigate(R.id.action_paintFragment_to_savedImageFragment)
        }
        viewModel.paintView.observe(viewLifecycleOwner,{
            paint_base_layout.setImage(it.getBitMap())
        })
        viewModel.brushSize.observe(viewLifecycleOwner,
            {
                paint_base_layout.setSizeBrush(if(it!=null && it >0) it else 0)
        })
        viewModel.eraserSize.observe(viewLifecycleOwner,
            {
                paint_base_layout.setEraserSize(if(it!=null && it >0) it else 0)
            })
        viewModel.colorBackground.observe(viewLifecycleOwner,{
            paint_base_layout.setColorBackground(it)
        })
        viewModel.colorBrush.observe(viewLifecycleOwner,{
            paint_base_layout.setBrushColor(it)
        })
        optionAdapter.setOnItemClickListener {
            when (it.optionName) {
                BRUSH -> {
                    paint_base_layout.toMove = false
                    paint_base_layout.disableEraser()
                    paint_base_layout.invalidate()
                    viewModel.showDialogSize(false,requireContext())
                }
                ERASER -> {
                    paint_base_layout.enableEraser()
                    viewModel.showDialogSize(true,requireContext())
                }
                UNDO -> {
                    paint_base_layout.returnLastAction()
                }
                BACKGROUND -> {
                    viewModel.updateColor(BACKGROUND,requireContext())
                }
                PALETTE -> {
                    viewModel.updateColor(PALETTE,requireContext())
                }
                IMAGE -> {
                    getImage()
                }

            }
        }
        save_btn.setOnClickListener {
            saveImages()
        }
        share_btn.setOnClickListener {
            Toast.makeText(requireContext(), "To be done", Toast.LENGTH_LONG).show()
        }

    }

    private fun getImage() {
        val intent: Intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/"
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_IMAGE && data != null && resultCode == RESULT_OK) {
            val pickedImage: Uri = data.data!!
            val filePath = Array(1) { MediaStore.Images.Media.DATA }
            val cursor =
                requireActivity().contentResolver.query(pickedImage, filePath, null, null, null)
            cursor?.moveToFirst()
            val imagePath = cursor?.getString(cursor.getColumnIndex(filePath[0]))

            var options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.ARGB_8888

            val bitmap = BitmapFactory.decodeFile(imagePath, options)
            paint_base_layout.setImage(bitmap)
            cursor?.close()


        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun hasExternalStoragePermission(): Boolean = EasyPermissions.hasPermissions(
        requireContext(),
        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private fun requestExternalStoragePermission() {
        EasyPermissions.requestPermissions(
            requireActivity(),
            "This Application cannot work without Storage Permission",
            PERMISSION_STORAGE_REQUEST_CODE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        saveImages()
    }

    private fun saveImages() {
        if (!hasExternalStoragePermission()) {
            requestExternalStoragePermission()
        } else {
            val bitmap: Bitmap = paint_base_layout.getBitMap()
            val filename = "${UUID.randomUUID()}.png"
            var outputStream: OutputStream?
            var isSaved: Boolean

            Log.e("Saved Image Name", filename)
            Log.e(
                " Saved Image Path",
                activity?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                    .toString() + File.separator + getString(R.string.app_name)
            )

            val folder: File = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                File(
                    requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                        .toString() + File.separator + getString(R.string.app_name)
                )
            } else {
                File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                        .toString() + File.separator + getString(R.string.app_name)
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
                    val resolver: ContentResolver = requireContext().contentResolver
                    val contentValue = ContentValues()
                    contentValue.put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    contentValue.put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                    contentValue.put(
                        MediaStore.MediaColumns.RELATIVE_PATH,
                        Environment.DIRECTORY_PICTURES + File.separator + getString(R.string.app_name)
                    )
                    val uri =
                        resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValue)
                    outputStream = resolver.openOutputStream(uri!!)
                    isSaved = bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)

                } else {
                    sendPictureToGallery(imageUri)
                }
                if (isSaved) {
                    Snackbar.make(paint_base_layout, "Image Saved", Snackbar.LENGTH_LONG).show()

                } else {
                    Snackbar.make(paint_base_layout, "Something Went Wrong", Snackbar.LENGTH_LONG)
                        .show()
                }

                outputStream?.flush()
                outputStream?.close()
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }

        }
    }

    private fun sendPictureToGallery(imageUri: Uri) {
        val i = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        i.data = imageUri
        requireActivity().sendBroadcast(i)
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
                R.drawable.ic_image,
                IMAGE
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
        //Recycler View Setup
        data()
        optionAdapter = OptionAdapter(list)
        options_layout.apply {
            adapter = optionAdapter
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(requireActivity(), perms)) {
            SettingsDialog.Builder(requireActivity()).build().show()
        } else {
            requestExternalStoragePermission()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        Snackbar.make(paint_base_layout, "Permission Granted", Snackbar.LENGTH_LONG).show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults,
            requireContext()
        )
    }
}