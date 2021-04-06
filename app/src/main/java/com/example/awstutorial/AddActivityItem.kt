package com.example.awstutorial

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.amplifyframework.datastore.generated.model.ItemType
import kotlinx.android.synthetic.main.activity_add_item.*
import java.util.*

class AddActivityItem : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private var lastSelectedType: ItemType = ItemType.SWORD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)

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
            val item = UserData.Item(
                    UUID.randomUUID().toString(),
                    name?.text.toString(),
                    description?.text.toString(),
                    lastSelectedType,
                    location?.text.toString()
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