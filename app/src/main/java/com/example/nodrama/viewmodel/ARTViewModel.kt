package com.example.nodrama.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.nodrama.model.Entities.Test
import com.example.nodrama.model.Repository.ARTRepository

class ARTViewModel(application: Application): AndroidViewModel(application){
    private val artRepository : ARTRepository?

    init{
        artRepository = ARTRepository(application)
    }

    /***
     * Return a list of art to display in the view
     */
    fun callARTFromView(userid: String): MutableLiveData<List<Test>?>?{
        return artRepository?.retrieveArtData(userid)
    }

    /**
     * Save ART form submission into the database
     */
    fun saveDataToRepo(userId : String, fullName : String, phoneNumber : String, HRWOption :String, ResultOption :String, saveTime :String, imageUri : Uri){
        artRepository?.saveArtTest(userId, fullName, phoneNumber, HRWOption, ResultOption, saveTime, imageUri)
    }

}