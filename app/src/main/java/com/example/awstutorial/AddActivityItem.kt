package com.example.awstutorial

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_add_item.*
import java.util.*

class AddActivityItem : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)

        cancel.setOnClickListener {
            this.finish()
        }

        addNote.setOnClickListener {
            val item = UserData.Item(
                    UUID.randomUUID().toString(),
                    name?.text.toString(),
                    description?.text.toString(),
                    location?.text.toString()
            )

            Backend.createItem(item)

            UserData.addItem(item)

            // close activity
            this.finish()
        }
    }

    companion object {
        private const val TAG = "AddNoteActivity"
    }
}