package com.example.savefile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast

import androidx.activity.result.contract.ActivityResultContracts

import androidx.core.app.ActivityCompat

import androidx.documentfile.provider.DocumentFile

import java.io.*


class MainActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Save file
        var btnSave: Button = findViewById(R.id.button_save)
        btnSave.setOnClickListener {
            chooseDirectory()
        }

    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            val treeUri: Uri? = data?.data
            val pickedDir = DocumentFile.fromTreeUri(this!!, treeUri!!) // change "this" for your context

            // Create a new file and write into it
            saveFile(pickedDir)
        }
    }

    fun chooseDirectory(){
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
        //startActivityForResult(intent, 42) // Deprecated
        resultLauncher.launch(intent)
    }

    fun isStoragePermissionGranted(): Boolean {
        val tag = "Storage Permission"
        return if (Build.VERSION.SDK_INT >= 23) {
            if (this?.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v(tag, "Permission is granted")
                true
            } else {
                Log.v(tag, "Permission is revoked")
                ActivityCompat.requestPermissions(
                    this!!, // change "this" for your activity
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1
                )
                false
            }
        } else {
            Log.v(tag, "Permission is granted")
            true
        }
    }

    fun saveFile(pickedDir: DocumentFile?) {
        if (isStoragePermissionGranted()) { // check or ask permission

            val fname = "fileName.csv"
            val file = pickedDir?.createFile("text/csv", fname) // There you can change the file's type to your preference
            // For example, if you want save a txt file, you write "text/txt" and change the fileName's extension to fileName.txt
            
            try {
                val data = arrayOf("column1","column2","column3") // content that will be written to file
                val out: OutputStream = this!!.contentResolver.openOutputStream(file?.uri!!)!! // change "this" for your context
                val csvWriter = CSVWriter(out)
                csvWriter.writeNext(data)
                out.close()
                Toast.makeText(this, "Report saved successfully", Toast.LENGTH_LONG).show()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onClick(view: View?) {
        view?.findViewById<Button>(R.id.button_save)?.setOnClickListener(this)
    }

}


