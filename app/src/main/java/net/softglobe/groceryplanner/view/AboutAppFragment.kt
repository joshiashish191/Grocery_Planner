package net.softglobe.groceryplanner.view

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import net.softglobe.groceryplanner.BuildConfig
import net.softglobe.groceryplanner.model.Preferences
import net.softglobe.groceryplanner.ui.theme.GroceryPlannerTheme
import java.util.Calendar

class AboutAppFragment : Fragment() {

    private lateinit var preferences : Preferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        preferences = Preferences(activity?.applicationContext!!)
        return ComposeView(requireContext()).apply {
            setContent {
                GroceryPlannerTheme {
                    AboutAppScreen(
                        appVersion = "V ${BuildConfig.VERSION_NAME}",
                        copyrightText = "${Calendar.getInstance().get(Calendar.YEAR)} Softglobe Technologies",
                        onRateUsClick = { rateUs() },
                        onContactClick = { contact() },
                        onPrivacyPolicyClick = { privacyPolicy() }
                    )
                }
            }
        }
    }

    private fun rateUs() {
        val packageName = activity?.application?.packageName
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")))
        }
    }

    private fun contact() {
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

    private fun privacyPolicy() {
        val url = preferences.getPrivacyPolicyUrl()
        if (url.isNotBlank()) {
            val urlIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(urlIntent)
        } else {
            Toast.makeText(activity, "No link available", Toast.LENGTH_SHORT).show()
        }
    }
}
