package com.app.ams.fragments

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater

import com.app.ams.R

class HomeFragment : Fragment()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }
}