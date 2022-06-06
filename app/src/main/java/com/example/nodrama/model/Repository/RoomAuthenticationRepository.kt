package com.example.nodrama.model.Repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.nodrama.model.Entities.RoomAuthentication
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RoomAuthenticationRepository(private val application: Application) {

    private val db: FirebaseDatabase
    private val isRoomAuthorizedLiveData: MutableLiveData<Boolean?>?

    /***
     * Return room auth live data
     */
    fun getRoomAuth(userId: String, roomId: String): MutableLiveData<Boolean?>?{
        db.getReference("RoomAuthentication").child(userId).addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("RoomAuth", snapshot.toString())
                if (snapshot.exists()){
                    var count: Long = 0
                    for (i in snapshot.children){
                        val roomAuth = i.getValue(RoomAuthentication::class.java)
                        if (roomAuth?.RoomId.equals(roomId))
                        {
                            isRoomAuthorizedLiveData?.postValue(true)
                        } else{
                            count++
                        }
                        if (count == snapshot.childrenCount) {
                            isRoomAuthorizedLiveData?.postValue(false)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
        return isRoomAuthorizedLiveData
    }

    /**
     * Initialisation
     */
    init {
        db = FirebaseDatabase.getInstance()
        isRoomAuthorizedLiveData = MutableLiveData()
    }
}