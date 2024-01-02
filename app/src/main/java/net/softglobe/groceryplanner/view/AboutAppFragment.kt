package net.softglobe.groceryplanner.view

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import net.softglobe.groceryplanner.BuildConfig
import net.softglobe.groceryplanner.R
import net.softglobe.groceryplanner.databinding.FragmentAboutAppBinding
import net.softglobe.groceryplanner.model.Preferences
import java.util.Calendar

class AboutAppFragment : Fragment() {

    private lateinit var binding : FragmentAboutAppBinding
    private lateinit var preferences : Preferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_about_app, container, false)
        // Inflate the layout for this fragment
        initView()
        return binding.root
    }

    private fun initView() {
        binding.appVersion.text = "V ${BuildConfig.VERSION_NAME}"
        binding.softglobe.text = "${Calendar.getInstance().get(Calendar.YEAR)} Softglobe Technologies"
        preferences = Preferences(activity?.applicationContext!!)

        binding.rateUs.setOnClickListener {
            val packageName = activity?.application?.packageName
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
            } catch (e: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
            }
        }

        binding.contact.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_SENDTO)
                intent.data = Uri.parse("mailto:") // only email apps should handle this
                intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("groceryplanner19@gmail.com"))
                intent.putExtra(Intent.EXTRA_SUBJECT, "App feedback")
                startActivity(intent)
            } catch (ex: ActivityNotFoundException) {
                Toast.makeText(
                    activity,
                    "There are no email client installed on your device.", Toast.LENGTH_SHORT
                ).show()
            }
        }

        binding.privacyPolicy.setOnClickListener {
            val url = preferences.getPrivacyPolicyUrl()
            if (url.isNotBlank()) {
                val urlIntent =
                    Intent(Intent.ACTION_VIEW, Uri.parse(preferences.getPrivacyPolicyUrl()))
                startActivity(urlIntent)
            } else {
                Toast.makeText(activity, "No link available", Toast.LENGTH_SHORT).show()
            }
        }
    }
}