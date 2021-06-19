package com.example.awstutorial

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import com.amplifyframework.AmplifyException
import com.amplifyframework.api.aws.AWSApiPlugin
import com.amplifyframework.api.graphql.model.ModelMutation
import com.amplifyframework.api.graphql.model.ModelQuery
import com.amplifyframework.auth.AuthChannelEventName
import com.amplifyframework.auth.AuthException
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession
import com.amplifyframework.auth.result.AuthSessionResult
import com.amplifyframework.auth.result.AuthSignInResult
import com.amplifyframework.core.Amplify
import com.amplifyframework.core.InitializationStatus
import com.amplifyframework.datastore.generated.model.ItemData
import com.amplifyframework.datastore.generated.model.QuestData
import com.amplifyframework.datastore.generated.model.YourData
import com.amplifyframework.hub.HubChannel
import com.amplifyframework.hub.HubEvent
import java.util.*

object Backend {

    private const val TAG = "Backend"

    fun initialize(applicationContext: Context) : Backend {
        try {
            Amplify.addPlugin(AWSCognitoAuthPlugin())
            Amplify.addPlugin(AWSApiPlugin())
            Amplify.configure(applicationContext)
            Log.i(TAG, "Initialized Amplify")
        } catch (e: AmplifyException) {
            Log.e(TAG, "Could not initialize Amplify", e)
        }
        Log.i(TAG, "registering hub event")

// listen to auth event
        Amplify.Hub.subscribe(HubChannel.AUTH) { hubEvent: HubEvent<*> ->

            when (hubEvent.name) {
                InitializationStatus.SUCCEEDED.toString() -> {
                    Log.i(TAG, "Amplify successfully initialized")
                }
                InitializationStatus.FAILED.toString() -> {
                    Log.i(TAG, "Amplify initialization failed")
                }
                else -> {
                    when (AuthChannelEventName.valueOf(hubEvent.name)) {
                        AuthChannelEventName.SIGNED_IN -> {
                            updateUserData(true)
                            Log.i(TAG, "HUB : SIGNED_IN")
                        }
                        AuthChannelEventName.SIGNED_OUT -> {
                            updateUserData(false)
                            Log.i(TAG, "HUB : SIGNED_OUT")
                        }
                        else -> Log.i(TAG, """HUB EVENT:${hubEvent.name}""")
                    }
                }
            }
        }

        Log.i(TAG, "retrieving session status")

// is user already authenticated (from a previous execution) ?
        Amplify.Auth.fetchAuthSession(
                { result ->
                    Log.i(TAG, result.toString())
                    val cognitoAuthSession = result as AWSCognitoAuthSession
                    // update UI
                    this.updateUserData(cognitoAuthSession.isSignedIn)
                    when (cognitoAuthSession.identityId.type) {
                        AuthSessionResult.Type.SUCCESS ->  Log.i(TAG, "IdentityId: " + cognitoAuthSession.identityId.value)
                        AuthSessionResult.Type.FAILURE -> Log.i(TAG, "IdentityId not present because: " + cognitoAuthSession.identityId.error.toString())
                    }
                },
                { error -> Log.i(TAG, error.toString()) }
        )
        return this
    }
    private fun updateUserData(withSignedInStatus : Boolean) {
        UserData.setSignedIn(withSignedInStatus)

        val items = UserData.items().value
        val isEmptyI = items?.isEmpty() ?: false

        if (withSignedInStatus && isEmptyI ) {
            this.queryItems()
        } else {
            UserData.resetItems()
        }

        if (withSignedInStatus) {
            queryPlayer()
        }
    }

    fun signOut() {
        Log.i(TAG, "Initiate Signout Sequence")

        Amplify.Auth.signOut(
            { Log.i(TAG, "Signed out!") },
            { error -> Log.e(TAG, error.toString()) }
        )
    }

    fun signIn(callingActivity: Activity) {
        Log.i(TAG, "Initiate Signin Sequence")

        Amplify.Auth.signInWithWebUI(
            callingActivity,
            { result: AuthSignInResult ->  Log.i(TAG, result.toString()) },
            { error: AuthException -> Log.e(TAG, error.toString()) }
        )
    }
    // pass the data from web redirect to Amplify libs
    fun handleWebUISignInResponse(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "received requestCode : $requestCode and resultCode : $resultCode")
        if (requestCode == AWSCognitoAuthPlugin.WEB_UI_SIGN_IN_ACTIVITY_CODE) {
            Amplify.Auth.handleWebUISignInResponse(data)
        }
    }


    fun queryItems() {
        Log.i(TAG, "Querying items")

        Amplify.API.query(
                ModelQuery.list(ItemData::class.java),
                { response ->
                    Log.i(TAG, "Queried")
                    for (itemData in response.data) {
                        Log.i(TAG, itemData.name)
                        // TODO should add all the items at once instead of one by one (each add triggers a UI refresh)
                        UserData.addItem(UserData.Item.from(itemData))
                    }
                },
                { error -> Log.e(TAG, "Query failure", error) }
        )
    }

    fun createItem(item : UserData.Item) {
        Log.i(TAG, "Creating items")

        Amplify.API.mutate(
                ModelMutation.create(item.data),
                { response ->
                    Log.i(TAG, "Created")
                    if (response.hasErrors()) {
                        Log.e(TAG, response.errors.first().message)
                    } else {
                        Log.i(TAG, "Created Note with id: " + response.data.id)
                    }
                },
                { error -> Log.e(TAG, "Create failed", error) }
        )
    }

    fun deleteItem(item : UserData.Item?) {

        if (item == null) return

        Log.i(TAG, "Deleting item $item")

        Amplify.API.mutate(
                ModelMutation.delete(item.data),
                { response ->
                    Log.i(TAG, "Deleted")
                    if (response.hasErrors()) {
                        Log.e(TAG, response.errors.first().message)
                    } else {
                        Log.i(TAG, "Deleted Item $response")
                    }
                },
                { error -> Log.e(TAG, "Delete failed", error) }
        )
    }

    fun createQuest(quest : UserData.Quest) {
        Log.i(TAG, "Creating items")

        Amplify.API.mutate(
                ModelMutation.create(quest.data),
                { response ->
                    Log.i(TAG, "Quest Created")
                    if (response.hasErrors()) {
                        Log.e(TAG, response.errors.first().message)
                    } else {
                        Log.i(TAG, "Created Quest with id: " + response.data.id)
                    }
                },
                { error -> Log.e(TAG, "Create quest failed", error) }
        )
    }

    fun queryNearbyQuests(lattitude: Float, longitude: Float, onSuccess: ()->Unit)
    {
        Log.i(TAG, "Querying nearby quests")
        Amplify.API.query(
                ModelQuery.list(QuestData::class.java),
                { response ->
                    UserData.nearbyQuestsList.clearItems()
                    for (questData in response.data)
                    {
                        UserData.nearbyQuestsList.addItem(UserData.Quest.from(questData))
                    }
                    onSuccess()
                },
                { error -> Log.e(TAG, "Query failure", error) }
        )

    }

    fun queryPlayer() {
        Log.i(TAG, "Querying player")
        Amplify.API.query(
                ModelQuery.list(YourData::class.java),
                { response ->
                    when (response.data.count())
                    {
                        0 -> {
                            val p = UserData.Player(UUID.randomUUID().toString(), null, 0)
                            createPlayer(p)
                            UserData.setPlayer(p)
                        }
                        1 -> {
                            UserData.setPlayer(UserData.Player.from(response.data.first()))
                        }
                        else -> {
                            Log.e(TAG, "Too much entries queried: ${response.data.count()}")
                        }
                    }
                },
                { error -> Log.e(TAG, "Query failure", error) }
        )
    }
    private fun createPlayer(player : UserData.Player) {
        Log.i(TAG, "Creating player")
        Amplify.API.mutate(
                ModelMutation.create(player.data),
                { response ->
                    Log.i(TAG, "Player Created")
                    if (response.hasErrors()) {
                        Log.e(TAG, response.errors.first().message)
                    } else {
                        Log.i(TAG, "Created Player with id: " + response.data.id)
                    }
                },
                { error -> Log.e(TAG, "Create Player failed", error) }
        )
    }
}