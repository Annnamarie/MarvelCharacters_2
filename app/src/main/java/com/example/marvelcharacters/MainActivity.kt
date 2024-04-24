package com.example.marvelcharacters

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import org.json.JSONException
import java.security.MessageDigest

data class MarvelCharacter(
    val name: String,
    val description: String,
    val imageUrl: String
)


class MainActivity : AppCompatActivity() {

    private lateinit var marvelList: MutableList<MarvelCharacter>
    private lateinit var rvMarvel: RecyclerView
    private lateinit var adapter: MarvelAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val userQuery = findViewById<EditText>(R.id.user_query)
        val searchButton = findViewById<Button>(R.id.search_button)
        rvMarvel = findViewById(R.id.marvel_list)
        marvelList = mutableListOf()
        adapter = MarvelAdapter(marvelList)
        rvMarvel.adapter = adapter
        rvMarvel.layoutManager = LinearLayoutManager(this@MainActivity)
        rvMarvel.addItemDecoration(
            DividerItemDecoration(
                this@MainActivity,
                LinearLayoutManager.VERTICAL
            )
        )

        searchButton.setOnClickListener {
            val query = userQuery.text.toString()
            fetchMarvelCharacter(query)
        }

        fetchMarvelCharacter()
    }

    private fun stringToMd5(s: String): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(s.toByteArray())
        return digest.fold("", { str, it -> str + "%02x".format(it) })
    }

    private fun fetchMarvelCharacter(query: String ="") {
        val timestamp = System.currentTimeMillis()
        val privateKey = "key"
        val publicKey = "key"
        val hash = stringToMd5("$timestamp$privateKey$publicKey")
        val encodedQuery = query.replace(" ", "%20")

        val url =
            "https://gateway.marvel.com:443/v1/public/characters?nameStartsWith=$encodedQuery&ts=$timestamp&apikey=$publicKey&hash=$hash"

        val client = AsyncHttpClient()

        client.get(url, object : JsonHttpResponseHandler() {
            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                response: String?,
                throwable: Throwable?
            ) {
                Log.e("MarvelCharacters", "Failed to fetch characters: ${throwable?.message}")
            }

            override fun onSuccess(statusCode: Int, headers: Headers?, response: JSON?) {
                try {
                    val jsonObject = response?.jsonObject
                    val data = jsonObject?.getJSONObject("data")
                    val results = data?.getJSONArray("results")

                    results?.let {
                        for (i in 0 until it.length()) {
                            val character = it.getJSONObject(i)
                            val thumbnail = character.getJSONObject("thumbnail")
                            val path = thumbnail.getString("path")
                            val extension = thumbnail.getString("extension")
                            val marvelImageURL = "$path/portrait_uncanny.$extension"
                            val name = character.getString("name")
                            val description = character.getString("description")
                            val marvelCharacter = MarvelCharacter(name, description, marvelImageURL)
                            marvelList.add(marvelCharacter)
                        }
                        adapter.notifyDataSetChanged()
                    }
                } catch (e: JSONException) {
                    Log.e("MarvelCharacters", "Error parsing JSON: ${e.message}")
                }
            }
        })
    }


}
