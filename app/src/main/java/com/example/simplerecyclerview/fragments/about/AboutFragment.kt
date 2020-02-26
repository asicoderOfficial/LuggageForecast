package com.example.simplerecyclerview.fragments.about


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.simplerecyclerview.R
import com.example.simplerecyclerview.databinding.FragmentAboutBinding

/**
 * Fragment about the author of the app.
 *
 * @author Asicoder
 */
class AboutFragment : Fragment() {

    private lateinit var binding: FragmentAboutBinding
    private val description: Description =
        Description("Hi!\nMy name is Asier Serrano, a sophomore at Polythecnic University of Valencia, Spain. I am the developer of this simple mobile app. I hope it has helped you to make your luggage for some trip!\nIf you want to know more about me, you can visit my webpage at https://asicoderofficial.github.io/Asicoder.github.io/ or send me an e-mail to asicoderofficial@gmail.com\nGreat feedback in order to improve is welcome! :)")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_about, null, false)
        binding.description = description
        return binding.root
    }
}
