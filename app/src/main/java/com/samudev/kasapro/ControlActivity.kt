package com.samudev.kasapro

import android.os.AsyncTask
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.samudev.kasapro.util.Util
import com.samudev.kasapro.util.WebUtil

import kotlinx.android.synthetic.main.activity_control.*
import java.util.*

class ControlActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_control)
        setSupportActionBar(toolbar)

        fab_add_device.setOnClickListener { view ->
            Snackbar.make(view, resources.getString(R.string.not_implemented), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        AskForToken().execute("email", "password")  // not to be pushed to github..
    }

    // Example how to make http call off ui thread
    class AskForToken(): AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg params: String?): String {
            val email = params[0] ?: return ""
            val password = params[1] ?: return ""
            return WebUtil.getToken(Util.getNewUuid(), email, password)
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            Log.v(ControlActivity::class.java.simpleName, result)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_control, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
