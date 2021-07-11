package com.debanshu777.painter.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.debanshu777.painter.R
import kotlinx.android.synthetic.main.fragment_full_image_screen.*

class FullImageScreenFragment : Fragment(R.layout.fragment_full_image_screen) {
    private val args: FullImageScreenFragmentArgs by navArgs()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val file=args.file
        imageView.setImageURI(file.toUri())
    }
}