package com.example.facerecognition

import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.viewModels
import com.example.facerecognition.base.BaseActivity
import com.example.facerecognition.customview.gone
import com.example.facerecognition.customview.visible
import com.example.facerecognition.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    private val viewModel: HomeViewModel by viewModels()

    override fun bindingInflater(layoutInflater: LayoutInflater) =
        ActivityMainBinding.inflate(layoutInflater)

    override fun setupView() {
        binding.btnOpenCamera.setOnClickListener {
            Toast.makeText(this, "Not implemented", Toast.LENGTH_LONG).show()
        }
        binding.btnRecognition.setOnClickListener {
            Toast.makeText(this, "Not implemented", Toast.LENGTH_LONG).show()
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
                }
                is HomeViewState.ShowImages -> {
                    binding.progressBar.gone()
                }
                is HomeViewState.EmptyImages -> {
                    binding.progressBar.gone()
                    Toast.makeText(this, "Empty image", Toast.LENGTH_LONG).show()
                }
            }
        })
    }
}
