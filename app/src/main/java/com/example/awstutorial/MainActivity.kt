package com.example.awstutorial

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        // prepare our List view and RecyclerView (cells)
        setupRecyclerViewNotes(note_list)
        setupRecyclerViewItems(item_list)

        setupAuthButton(UserData)

        UserData.isSignedIn.observe(this, Observer<Boolean> { isSignedUp ->
            // update UI
            Log.i(TAG, "isSignedIn changed : $isSignedUp")

            //animation inspired by https://www.11zon.com/zon/android/multiple-floating-action-button-android.php
            if (isSignedUp) {
                fabAuth.setImageResource(R.drawable.ic_baseline_lock_open)
                Log.d(TAG, "Showing fabADD")
                fabAdd.show()
                fabAdd.animate().translationY(0.0F - 1.1F * fabAuth.customSize)
                fabItemAdd.show()
                fabItemAdd.animate().translationY(0.0F - 1.1F * fabAuth.customSize)
            } else {
                fabAuth.setImageResource(R.drawable.ic_baseline_lock)
                Log.d(TAG, "Hiding fabADD")
                fabAdd.hide()
                fabAdd.animate().translationY(0.0F)
                fabItemAdd.hide()
                fabItemAdd.animate().translationY(0.0F)
            }
        })

        // register a click listener
        fabAdd.setOnClickListener {
            startActivity(Intent(this, AddNoteActivity::class.java))
        }
        // register a click listener
        fabItemAdd.setOnClickListener {
            startActivity(Intent(this, AddActivityItem::class.java))
        }
    }

    // recycler view is the list of cells
    private fun setupRecyclerViewNotes(recyclerView: RecyclerView) {
        // update individual cell when the Note data are modified
        UserData.notes().observe(this, Observer<MutableList<UserData.Note>> { notes ->
            Log.d(TAG, "Note observer received ${notes.size} notes")

            // let's create a RecyclerViewAdapter that manages the individual cells
            recyclerView.adapter = NoteRecyclerViewAdapter(notes)
        })
        // add a touch gesture handler to manager the swipe to delete gesture
        val noteSwipeCallback = object : SwipeCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {

                Toast.makeText(activity, "deleted", Toast.LENGTH_SHORT).show()

                //Remove swiped item from list and notify the RecyclerView
                Log.d(TAG, "Going to remove ${viewHolder.adapterPosition}")

                // get the position of the swiped item in the list
                val position = viewHolder.adapterPosition

                // remove to note from the userdata will refresh the UI
                val note = UserData.deleteNote(position)

                // async remove from backend
                Backend.deleteNote(note)
            }
        }
        val itemTouchHelper = ItemTouchHelper(noteSwipeCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

    }

    // recycler view is the list of cells
    private fun setupRecyclerViewItems(recyclerView: RecyclerView) {
        // update individual cell when the Item data are modified
        UserData.items().observe(this, Observer<MutableList<UserData.Item>> { items ->
            Log.d(TAG, "Item observer received ${items.size} notes")

            // let's create a RecyclerViewAdapter that manages the individual cells
            recyclerView.adapter = ItemRecyclerViewAdapter(items)
        })



        // add a touch gesture handler to manager the swipe to delete gesture
        val itemSwipeCallback = object : SwipeCallback(this) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {

                Toast.makeText(activity, "deleted", Toast.LENGTH_SHORT).show()

                //Remove swiped item from list and notify the RecyclerView
                Log.d(TAG, "Going to remove ${viewHolder.adapterPosition}")

                // get the position of the swiped item in the list
                val position = viewHolder.adapterPosition

                // remove to note from the userdata will refresh the UI
                val item = UserData.deleteItem(position)

                // async remove from backend
                Backend.deleteItem(item)
            }
        }
        val itemTouchHelper = ItemTouchHelper(itemSwipeCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

    }

    private fun setupAuthButton(userData: UserData) {

        // register a click listener
        fabAuth.setOnClickListener { view ->

            val authButton = view as FloatingActionButton

            if (userData.isSignedIn.value!!) {
                authButton.setImageResource(R.drawable.ic_baseline_lock_open)
                Backend.signOut()
            } else {
                authButton.setImageResource(R.drawable.ic_baseline_lock_open)
                Backend.signIn(this)
            }
        }
    }
    // receive the web redirect after authentication
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Backend.handleWebUISignInResponse(requestCode, resultCode, data)
    }
    companion object {
        private const val TAG = "MainActivity"
    }
}