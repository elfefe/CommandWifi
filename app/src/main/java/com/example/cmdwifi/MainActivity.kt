package com.example.cmdwifi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        log.movementMethod = ScrollingMovementMethod()

        exec.setOnClickListener {
            executeCmd(if (check_su.isChecked) "su -c " + input.text.toString() else input.text.toString())
        }
    }


    override fun onStart() {
        super.onStart()

        clearLog()
    }

    override fun onResume() {
        super.onResume()

        executeCmd("su -c setprop service.adb.tcp.port 5555")
        executeCmd("su -c stop adbd")
        executeCmd("su -c start adbd")
    }

    private fun executeCmd(text: String) {
        Runtime.getRuntime().exec(text).waitFor()
        Toast.makeText(applicationContext, "'$text' have been executed.", Toast.LENGTH_LONG).show()

        log.text = extractLog(text)
    }

    private fun extractLog(command: String): String {


        // write log to file
        // val pid = android.os.Process.myPid()

        try {
            val process = Runtime.getRuntime().exec(command)

            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var lines = ""

            var i = 0
            reader.forEachLine { line ->
                if ((++i) < 10000)
                    lines += "$line \n"
            }

            return lines
        } catch (e: IOException) {
            Toast.makeText(applicationContext, e.toString(), Toast.LENGTH_SHORT).show()
        }

        return ""
    }

   /* private fun extractLog(): String {

        //write log to file
        val pid = android.os.Process.myPid()

        try {
            val command = "logcat -d $filter"
            val process = Runtime.getRuntime().exec(command)

            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var lines = ""

            var i = 0
            reader.forEachLine { line ->
                if ((++i) < 10000)
                    lines += "$line \n"
            }

            return lines
        } catch (e: IOException) {
            Toast.makeText(applicationContext, e.toString(), Toast.LENGTH_SHORT).show()
        }


        //clear the log
        *//*try {
            Runtime.getRuntime().exec("logcat -c")
        } catch (e: IOException) {
            Toast.makeText(applicationContext, e.toString(), Toast.LENGTH_SHORT).show()
        }*//*

        return ""
    } */

    fun clearLog() {
        try {
            Runtime.getRuntime().exec("logcat -c")
        } catch (e: IOException) {
            Toast.makeText(applicationContext, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }
}
