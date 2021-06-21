package com.example.facerecognition.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<B : ViewBinding> : AppCompatActivity() {

    protected lateinit var binding: B

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = bindingInflater(layoutInflater)
        setContentView(binding.root)
        setupView()
        observeData()
        loadData()
    }

    abstract fun bindingInflater(layoutInflater: LayoutInflater): B

    abstract fun setupView()

    abstract fun observeData()

    abstract fun loadData()
}
