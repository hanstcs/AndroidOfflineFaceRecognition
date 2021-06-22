package com.example.facerecognition

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.facerecognition.ui.ImageViewHolder
import com.example.facerecognition.base.BaseActivity
import com.example.facerecognition.base.BaseAdapter
import com.example.facerecognition.customview.gone
import com.example.facerecognition.customview.visible
import com.example.facerecognition.databinding.ActivityMainBinding
import com.example.facerecognition.db.ImagesModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    private val viewModel: HomeViewModel by viewModels()

    private lateinit var imageAdapter: BaseAdapter<ImagesModel, ImageViewHolder>

    private val startForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.loadImages()
        }
    }

    override fun bindingInflater(layoutInflater: LayoutInflater) =
        ActivityMainBinding.inflate(layoutInflater)

    override fun setupView() {
        binding.fabCamera.setOnClickListener {
            startForResult.launch(
                Intent(this, CameraActivity::class.java)
                    .apply {
                        putExtra(CameraActivity.EXTRA_FRONT_CAMERA, true)
                    }
            )
        }
        imageAdapter = BaseAdapter(
            { parent, _ -> ImageViewHolder.inflate(parent) },
            { viewHolder, _, data ->
                viewHolder.bind(data.id)
                viewHolder.setActionListener {
                    Toast.makeText(baseContext, "Filename : ${data.id}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        )
        binding.rvImages.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 2)
            adapter = imageAdapter
        }
    }

    override fun loadData() {
        viewModel.loadImages()
    }

    override fun observeData() {
        viewModel.state.observe(this, { state ->
            when (state) {
                is HomeViewState.ShowLoading -> {
                    binding.progressBar.visible()
                    binding.tvEmpty.gone()
                }
                is HomeViewState.ShowImages -> {
                    binding.progressBar.gone()
                    binding.tvEmpty.gone()
                    imageAdapter.addClearItems(state.images)
                }
                is HomeViewState.EmptyImages -> {
                    binding.progressBar.gone()
                    binding.tvEmpty.visible()
                    imageAdapter.clear()
                }
            }
        })
    }
}
