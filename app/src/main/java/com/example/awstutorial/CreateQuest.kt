package com.example.awstutorial

import android.location.Location
import android.os.Bundle
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import com.amplifyframework.datastore.generated.model.Coordinates
import com.amplifyframework.datastore.generated.model.QuestStatus
import kotlinx.android.synthetic.main.activity_create_quest.*
import java.util.*

class CreateQuest : AppCompatActivity() {
    lateinit var coordinates: Coordinates
    private lateinit var locationServiceManager: LocationServiceManager
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_quest)
        val location = locationServiceManager.getLastLocation()
        val latitude = location?.latitude ?: 0.0
        val longitude = location?.longitude ?: 0.0
        coordinates = Coordinates.builder()
                .latitude(latitude)
                .longitude(longitude)
                .build()
        val latitudeStr = Location.convert(latitude, Location.FORMAT_SECONDS)
        val longitudeStr = Location.convert(longitude, Location.FORMAT_SECONDS)
        coordinates_text.text = "N:$latitudeStr, E:$longitudeStr"
        create_quest_button.visibility = VISIBLE

        cancel_creating_quest_button.setOnClickListener {
            this.finish()
        }

        create_quest_button.setOnClickListener {
            val quest = UserData.Quest(
                    UUID.randomUUID().toString(),
                    name.text.toString(),
                    QuestStatus.ACCEPTED,
                    coordinates,
                    story.text.toString(),
                    remarks.text.toString(),
                    hint_text.text.toString()
            )
            Backend.createQuest(quest)
            UserData.questList.addItem(quest)
            this.finish()
        }
    }

    override fun onStart() {
        super.onStart()
        locationServiceManager = LocationServiceManager(this)
        locationServiceManager.bind()
    }

    override fun onStop() {
        super.onStop()
        locationServiceManager.unbind()
    }
}