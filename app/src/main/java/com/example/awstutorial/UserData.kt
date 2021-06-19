package com.example.awstutorial

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.amplifyframework.datastore.generated.model.*

// a singleton to hold user data (this is a ViewModel pattern, without inheriting from ViewModel)
object UserData {

    private const val TAG = "UserData"

    //
    // observable properties
    //

    var player = MutableLiveData<Player>(Player("", null, 0))
    fun setPlayer(player : Player)
    {
       this.player.postValue(player)
    }

    // signed in status
    private val _isSignedIn = MutableLiveData<Boolean>(false)
    var isSignedIn: LiveData<Boolean> = _isSignedIn

    fun setSignedIn(newValue : Boolean) {
        // use postvalue() to make the assignation on the main (UI) thread
        _isSignedIn.postValue(newValue)
    }

    // the notes
    private val _items = MutableLiveData<MutableList<Item>>(mutableListOf())

    // please check https://stackoverflow.com/questions/47941537/notify-observer-when-item-is-added-to-list-of-livedata
    private fun <T> MutableLiveData<T>.notifyObserver() {
        this.postValue(this.value)
    }

    fun items() : LiveData<MutableList<Item>>  = _items
    fun addItem(n : Item) {
        val items = _items.value
        if (items != null) {
            items.add(n)
            _items.notifyObserver()
        } else {
            Log.e(TAG, "addNote : note collection is null !!")
        }
    }
    fun deleteItem(at: Int) : Item?  {
        val note = _items.value?.removeAt(at)
        _items.notifyObserver()
        return note
    }

    fun resetItems() {
        this._items.value?.clear()  //used when signing out
        _items.notifyObserver()
    }

    var nearbyQuestsList = LiveItemList<Quest>()


    data class Item (val id: String, val name: String, val description: String, var itemType: ItemType, var location: String, val coordinates: Coordinates?) {
        override fun toString(): String = name

        // return an API NoteData from this Note object
        val data : ItemData
            get() = ItemData.builder()
                .name(this.name)
                .description(this.description)
                .itemType(this.itemType)
                .location(this.location)
                .id(this.id)
                .coordinates(this.coordinates)
                .build()

        // static function to create a Item a NoteData API object
        companion object {
            fun from(itemData : ItemData) : Item {
                return Item(itemData.id, itemData.name, itemData.description, itemData.itemType, itemData.location, itemData.coordinates)
            }

            fun itemType2Idx(itemType : ItemType): Int {
                return when (itemType) {
                    ItemType.SWORD -> 0
                    ItemType.ARMOR -> 1
                    ItemType.SHIELD -> 2
                    ItemType.HELMET -> 3
                    ItemType.RING -> 4
                    ItemType.AMULET -> 5
                }
            }
            fun itemTypeFromIdx(idx : Int): ItemType {
                return when (idx) {
                    0 -> ItemType.SWORD
                    1 -> ItemType.ARMOR
                    2 -> ItemType.SHIELD
                    3 -> ItemType.HELMET
                    4 -> ItemType.RING
                    else -> ItemType.AMULET
                }
            }
        }
    }

    data class Player (val id: String,  val owner: String?, val experience: Int) {
        override fun toString(): String = id

        val data : YourData
            get() = YourData.builder()
                    .experience(this.experience)
                    .id(this.id)
                    .build()

        companion object {
            fun from(yourData : YourData) : Player {
                return Player(yourData.id, yourData.owner, yourData.experience)
            }
        }
    }

    data class Quest (val id: String, val name: String, val status: QuestStatus, val coordinates: Coordinates, val story: String, val remarks: String?, val hint: String?) {
        override fun toString(): String = name
        var playersProgress: PlayersProgress = PlayersProgress.OPEN

        val data : QuestData
            get() = QuestData.builder()
                    .name(this.name)
                    .status(this.status)
                    .coordinates(this.coordinates)
                    .story(this.story)
                    .id(this.id)
                    .remarks(this.remarks)
                    .hint(this.hint)
                    .build()

        companion object {
            fun from(questData: QuestData) : Quest {
                return Quest(questData.id, questData.name, questData.status, questData.coordinates, questData.story, questData.remarks, questData.hint)
            }
        }
        enum class PlayersProgress {
            OPEN, IN_PROGRESS, DONE, FAILED
        }
    }


}