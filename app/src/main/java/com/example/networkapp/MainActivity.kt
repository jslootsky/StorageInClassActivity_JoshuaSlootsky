package com.example.networkapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.io.File

// TODO (1: Fix any bugs)
// TODO (2: Add function saveComic(...) to save comic info when downloaded
// TODO (3: Automatically load previously saved comic when app starts)

class MainActivity : AppCompatActivity() {

    private lateinit var requestQueue: RequestQueue
    lateinit var titleTextView: TextView
    lateinit var descriptionTextView: TextView
    lateinit var numberEditText: EditText
    lateinit var showButton: Button
    lateinit var comicImageView: ImageView
    private val internalFileName = "comic_file"
    private lateinit var file: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestQueue = Volley.newRequestQueue(this)

        titleTextView = findViewById<TextView>(R.id.comicTitleTextView)
        descriptionTextView = findViewById<TextView>(R.id.comicDescriptionTextView)
        numberEditText = findViewById<EditText>(R.id.comicNumberEditText)
        showButton = findViewById<Button>(R.id.showComicButton)
        comicImageView = findViewById<ImageView>(R.id.comicImageView)

        file = File(filesDir, internalFileName)

        if(file.exists()){
            try{
                val fileInput = openFileInput(internalFileName)
                val content = fileInput.bufferedReader().use { it.readText() }
                fileInput.close()
                showComic(JSONObject(content))
                Toast.makeText(this, "Loaded saved comic", Toast.LENGTH_SHORT).show()
            }catch (e: Exception){
                e.printStackTrace()
                Toast.makeText(this, "No saved comic found", Toast.LENGTH_SHORT).show()
            }
        }

        showButton.setOnClickListener {
            downloadComic(numberEditText.text.toString())
        }

    }

    // Fetches comic from web as JSONObject
    private fun downloadComic (comicId: String) {
        val url = "https://xkcd.com/$comicId/info.0.json"
        requestQueue.add (
            JsonObjectRequest(url
                , {
                    saveComic(it)//saves the comic while downloading
                    showComic(it)
                  }
                , {}
            )
        )
    }

    // Display a comic for a given comic JSON object
    private fun showComic (comicObject: JSONObject) {
        titleTextView.text = comicObject.getString("title")
        descriptionTextView.text = comicObject.getString("alt")
        Picasso.get().load(comicObject.getString("img")).into(comicImageView)
    }

    // Implement this function
    private fun saveComic(comicObject: JSONObject) {
        try{
            val fileOutput = openFileOutput(internalFileName, MODE_PRIVATE)
            fileOutput.write(comicObject.toString().toByteArray())
            fileOutput.close()
            Toast.makeText(this, "Comic saved!", Toast.LENGTH_SHORT).show()
        }catch (e: Exception){
            e.printStackTrace()
            Toast.makeText(this, "Error saving comic", Toast.LENGTH_SHORT).show()
        }
    }


}