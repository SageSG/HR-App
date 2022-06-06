package com.example.nodrama.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.*
import com.example.nodrama.model.Entities.Payslip
import com.example.nodrama.model.Repository.PayslipRepository


class PayslipViewModel(application: Application): AndroidViewModel(application) {
    private val payslipRepository : PayslipRepository?
    private val payslipListLiveData: MutableLiveData<List<Payslip>?>?
    private val uri : LiveData<Uri>


    /***
     * return a list of payslip to display in the view
     */
    fun callPayslipFromView(userid: String): MutableLiveData<List<Payslip>?>?{
        return payslipRepository?.getAllPayslip(userid)
    }

    /***
     * method to call the repo to return a payslip uri back
     */
    fun returnPayslipUri(userId: String, payslip: Payslip){
        payslipRepository?.returnPayslip(userId,payslip)
    }

    /***
     * return the payslip uri to display the payslip in the pdfviewer
     */
    fun getUriLiveData():LiveData<Uri>{
        return uri
    }

    init{
        payslipRepository = PayslipRepository(application)
        payslipListLiveData = MutableLiveData()
        uri = payslipRepository.getUriLiveData()
    }

}

/***
 * Ensure that only 1 instance of view model is created
 */
class PayslipViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PayslipViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PayslipViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}