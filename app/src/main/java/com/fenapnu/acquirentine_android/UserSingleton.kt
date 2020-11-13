package com.fenapnu.acquirentine_android

import com.fenapnu.acquirentine_android.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration

import java.util.*

object UserSingleton {


    var currentUser = User()


    private val usersCache = mutableMapOf<String,User>()

    private var onCurrentUserDataChangedListener: OnCurrentUserDataChanged? = null
    private var onUserDataRetrieved: OnUserDataRetrieved? = null

    private var userListener: ListenerRegistration? = null

    // keeping the constructor private to enforce the usage of getInstance.
    init {
        print("User Singleton invoked")
    }



    fun addToUserCache(user: User){
        user.uid?.let { usersCache.put(it, user) }
    }



    fun setListeners(){
        if(userListener != null){
            return;
        }
        setUserDataListener()

    }

    fun setLocalUserDataChangedListener(listener: OnCurrentUserDataChanged){
        onCurrentUserDataChangedListener = listener;
    }


    fun setUserDataRetrievedListener(listener: OnUserDataRetrieved){
        onUserDataRetrieved = listener;
    }



    fun signOut(){
        FirebaseAuth.getInstance().signOut()
    }



    //accepts a position argument to be passed back for reloading the associated cell
    fun getUserAsync(uid: String, position: Int): User? {

        val user =  usersCache[uid]
        if(user != null){
            return user;
        }


        Thread().run {
            // do some calculation
            DataManager.getUsersPath().document(uid).addSnapshotListener(EventListener { value, error ->
                if (error != null) {
                    onUserDataRetrieved?.userRetrievalError()
                    return@EventListener;
                }
                if (!value!!.exists()) {
                    onUserDataRetrieved?.userRetrievalError()
                    return@EventListener
                }
                val u = value.toObject(User::class.java)
                u?.uid?.let { usersCache.put(it, u) };
                u?.let { onUserDataRetrieved?.userRetrieved(it, position) };
            })
        }

        return null;
    }






    fun setUserDataListener(){
        val uid: String = FirebaseAuth.getInstance().currentUser?.uid ?: return

       userListener = DataManager.getUsersPath().document(uid).addSnapshotListener(EventListener { value, error ->
            if (error != null) {
                return@EventListener
            }
            if (!value!!.exists()) {
                return@EventListener
            }
            val user = value.toObject(User::class.java)
            if (user != null) {
                currentUser = user
                onCurrentUserDataChangedListener?.onUserUpdated(user)
            }
        })
    }



    //will update the logged in status of the application when called
    val userIsLoggedIn: Boolean
        get() {
            if(FirebaseAuth.getInstance().currentUser != null){
                return true
            }
            return false
        }



    //INTERFACES --

    /*
    *
    * Called when the Firestore listener is triggered on changes
    *
     */
    interface OnCurrentUserDataChanged {
        fun onUserUpdated(user: User)
    }


    /*
    *
    * Called when the user data has been downloaded
    *
    */
    interface OnUserDataRetrieved {
        fun userRetrievalError()
        fun userRetrieved(user: User, pos: Int);
    }

}




