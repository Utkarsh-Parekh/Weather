package com.example.weatherdata.Activity

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.weatherdata.APIinterfaces.Weatherinterface
import com.example.weatherdata.R
import com.example.weatherdata.databinding.ActivityMainBinding
import com.example.weatherdata.weatherdatamodel.Weatherdatainfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Thread.sleep
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread
import kotlin.math.log

class MainActivity : AppCompatActivity() {


    private lateinit var databinding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databinding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        fetchweatherdata("Godhra")
        searchingcity()

    }

    private fun fetchweatherdata(cityname : String) {
        val retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/").build().create(Weatherinterface::class.java)

        val responsedata = retrofit.getcurrentcityweatherdata(cityname,"aa49d15b5e277de6b26975198a55344d","metric")
        responsedata.enqueue(object : Callback<Weatherdatainfo> {
            @SuppressLint("ResourceType")
            override fun onResponse(
                call: Call<Weatherdatainfo>,
                response: Response<Weatherdatainfo>
            ) {
                val responsebody = response.body()
                if(response.isSuccessful && responsebody != null){
                    val temperature= responsebody.main.temp.toString()
                    databinding.textViewTemperature.setText("$temperature °C")
                    val humidity = responsebody.main.humidity.toString()
                    databinding.textViewHumidity.setText("$humidity %")
                    val windspeed = responsebody.wind.speed.toString()
                    databinding.textViewWindspeed.setText("$windspeed m/s")

                    val maxtemp = responsebody.main.temp_max.toString()
                    databinding.textViewMaxtemp.setText("$maxtemp °C")
                    val mintemp = responsebody.main.temp_min.toString()
                    databinding.textViewMintemp.setText("$mintemp °C")

                    val condition = responsebody.weather.firstOrNull()?.main?: "Unknown"
                    databinding.textViewTempType.setText(condition)

                    databinding.textViewDaydate.setText("${dayName(System.currentTimeMillis())} | ${date()}")
                    databinding.textViewLoccation.setText(cityname)

                    val sunrise = responsebody.sys.sunrise
                    databinding.textViewSunrisetime.text = "${sunrisesunset(sunrise)}"

                    val sunset = responsebody.sys.sunset
                    databinding.textViewSunset.text = "${sunrisesunset(sunset)}"


                    when(condition){
                        "Clear","Sky","Sunny" -> {
                            Glide.with(this@MainActivity).load(R.drawable.sunny).placeholder(R.raw.animation_progressbar)
                                .into(databinding.imageViewTypeWeather)
                            databinding.progressbar.visibility = View.GONE
                        }

                        "Partly cloudy","Clouds","Overcast","Mist","Foggy","Haze","Smoke"-> {
                            Glide.with(this@MainActivity).load(R.drawable.cloudy).placeholder(R.raw.animation_progressbar)
                                .into(databinding.imageViewTypeWeather)

                            databinding.progressbar.visibility = View.GONE
                        }

                        "Light Rain","Drizzle","Moderate Rain","Showers","Heavy Rain" -> {
                            Glide.with(this@MainActivity).load(R.drawable.rainy).placeholder(R.raw.animation_progressbar)
                                .into(databinding.imageViewTypeWeather)

                            databinding.progressbar.visibility = View.GONE
                        }

                        "Light Snow","Moderate Snow","Heavy Snow","Blizzard" -> {
                            Glide.with(this@MainActivity).load(R.drawable.snowy).placeholder(R.raw.animation_progressbar)
                                .into(databinding.imageViewTypeWeather)

                            databinding.progressbar.visibility = View.GONE
                        }

                        "Thunderstorm" -> {
                            Glide.with(this@MainActivity).load(R.drawable.storm).placeholder(R.raw.animation_progressbar)
                                .into(databinding.imageViewTypeWeather)
                            databinding.progressbar.visibility = View.GONE
                        }

                        else -> {
                            Glide.with(this@MainActivity).load(R.drawable.sunny).placeholder(R.raw.animation_progressbar)
                                .into(databinding.imageViewTypeWeather)
                            databinding.progressbar.visibility = View.GONE
                        }
                    }
                }
            }

            override fun onFailure(call: Call<Weatherdatainfo>, t: Throwable) {
                Toast.makeText(this@MainActivity,"Error",Toast.LENGTH_SHORT).show()
            }

        })


    }


    fun dayName(timestamps : Long) : String{
        val simpledateformat = SimpleDateFormat("EEEE", Locale.getDefault())
        return simpledateformat.format(Date())
    }

    fun date() : String{
        val simpledateformat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return simpledateformat.format(Date())
    }

    fun sunrisesunset(timestamps: Int) : String{
        val simpledateformat = SimpleDateFormat("HH:MM", Locale.getDefault())
        return simpledateformat.format(Date())
    }


    private fun searchingcity() {
        val searchtext = databinding.searchInput
        searchtext.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchweatherdata(query)
                }

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }


}