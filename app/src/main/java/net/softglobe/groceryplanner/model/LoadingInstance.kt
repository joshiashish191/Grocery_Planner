package net.softglobe.groceryplanner.model

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import net.softglobe.groceryplanner.R

object LoadingInstance {
    private lateinit var progressDialog : Dialog
     fun showLoading(context : Context) {
        progressDialog = Dialog(context)
        val view = LayoutInflater.from(context).inflate(R.layout.loading,  null)
        progressDialog.setContentView(view)
        progressDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        progressDialog.setCancelable(false)
        progressDialog.show()
    }

    fun hideLoading() {
        progressDialog.dismiss()
    }
}