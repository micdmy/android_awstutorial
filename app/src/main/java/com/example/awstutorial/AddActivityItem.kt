package com.example.awstutorial

import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.amplifyframework.datastore.generated.model.Coordinates
import com.amplifyframework.datastore.generated.model.ItemType
import kotlinx.android.synthetic.main.activity_add_item.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class AddActivityItem : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private var lastSelectedType: ItemType = ItemType.SWORD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)
        val bundle = intent.extras
        val latitude: Double? = bundle?.getDouble("latitude")
        val longitude: Double? = bundle?.getDouble("longitude")
        if ( latitude != null && longitude != null) {
            val latitudeStr = Location.convert(latitude, Location.FORMAT_SECONDS)
            val longitudeStr = Location.convert(longitude, Location.FORMAT_SECONDS)
            findViewById<TextView>(R.id.coordinatesText).text = "N:$latitudeStr, E:$longitudeStr"
        }

        val spinner: Spinner = findViewById(R.id.itemType)
        spinner.onItemSelectedListener = this
        ArrayAdapter.createFromResource(
                this,
                R.array.item_type_names,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        cancel.setOnClickListener {
            this.finish()
        }

        addNote.setOnClickListener {
            val coordinates= Coordinates.builder()
                    .latitude(latitude?:0.0)
                    .longitude(longitude?:0.0)
                    .build()
            val item = UserData.Item(
                    UUID.randomUUID().toString(),
                    name?.text.toString(),
                    description?.text.toString(),
                    lastSelectedType,
                    location?.text.toString(),
                    coordinates
            )

            Backend.createItem(item)

            UserData.addItem(item)

            // close activity
            this.finish()
        }
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
        // An item was selected. You can retrieve the selected item using
        lastSelectedType = UserData.Item.itemTypeFromIdx(pos)
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Another interface callback
    }

    fun pos2ItemType(pos : Int) {

    }

    companion object {
        private const val TAG = "AddNoteActivity"
    }
}