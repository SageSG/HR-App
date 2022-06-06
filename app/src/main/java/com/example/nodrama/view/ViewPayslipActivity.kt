package com.example.nodrama.view

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment.DIRECTORY_DOWNLOADS
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toFile
import androidx.lifecycle.ViewModelProvider
import com.example.nodrama.R
import com.example.nodrama.databinding.ActivityViewpayslipBinding
import com.example.nodrama.model.Entities.Payslip
import com.example.nodrama.viewmodel.PayslipViewModel
import com.example.nodrama.viewmodel.PdfViewModel
import com.example.nodrama.viewmodel.PdfViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationBarView
import com.pdfview.PDFView


class ViewPayslipActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewpayslipBinding
    private var payslip: Payslip = Payslip()
    var getDownload: Boolean = false
    private var payslipViewModel: PayslipViewModel? = null
    var urlPdf: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /**
         * Initialise viewBinding for this activity
         */
        binding = ActivityViewpayslipBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        payslipViewModel = ViewModelProvider(this).get(
            PayslipViewModel::class.java
        )
        payslip = intent.getSerializableExtra("Payslip") as Payslip

        /**
         * Bottom Navigation Logic
         */
        var fab = findViewById<FloatingActionButton>(R.id.navFab)
        fab.setOnClickListener {
            Log.d("Inside onclick", "message")
            val navIntent = Intent(this, NFCActivity::class.java)
            startActivity(navIntent)
        }

        /**
         * Implicitly declare type of bottomNavigationView as NavigationBarView
         */
        var bottomNavigationView = findViewById<NavigationBarView>(R.id.bottomNavigationView)
        bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.miHome -> {
                    val navIntent = Intent(this, MainActivity::class.java)
                    startActivity(navIntent)
                    true
                }
                R.id.miAccount -> {
                    val navIntent = Intent(this, AccountActivity::class.java)
                    startActivity(navIntent)
                    true
                }
                else -> false
            }
        }

        /**
         * Populate month placeholder and description placeholder
         */
        binding.textViewMonth.text = payslip.period
        binding.textViewPayslipInfo.text = payslip.description

        /**
         * Call function to generate Payslip PDF
         */
        generatePDF()

        /**
         * On click listener to download the selected Payslip PDF
         */
        binding.buttonDownload.setOnClickListener {
            if (urlPdf != null) {
                Log.d("downloadPDF",
                    "Before called" + payslip.fileName.toString() + " \n" + urlPdf
                )
                downloadFile(
                    this,
                    payslip.fileName,
                    ".pdf",
                    DIRECTORY_DOWNLOADS,
                    urlPdf
                )
            }
            else
                Toast.makeText(this, "PDF File not Found", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     *Generate PDF and load into PDFViewer
     * Library used: https://github.com/Dmitry-Borodin/pdfview-android
     */
    fun generatePDF() {
        var pdfViewer = findViewById<PDFView>(R.id.activity_main_pdf_view)
        val sharedPreferences = getSharedPreferences("userData", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("userid", null)
        val pdfTitle = sharedPreferences.getString("pdftitle", null)
        Log.d("pdfTitle", pdfTitle.toString())
        payslipViewModel?.returnPayslipUri(userId.toString(), payslip)
        payslipViewModel?.getUriLiveData()?.observe(this) {
            urlPdf = it.toString()
            Log.d("PayslipUri", it.toString())
            val pdfViewModel: PdfViewModel by viewModels<PdfViewModel> {
                PdfViewModelFactory(
                    applicationContext,
                    it.toString(),
                    payslip.fileName.toString()
                )
            }
            pdfViewModel.getLoadedFile().observe(this) { uri ->
                Log.d("uri", urlPdf.toString())
                pdfViewer.fromFile(uri.toFile()).show()
            }
        }
    }

    /**
     *Download payslip to device
     */
    fun downloadFile(
        context: Context,
        fileName: String?,
        fileExtension: String,
        destinationDirectory: String?,
        url: String?
    ) {
        Log.d("downloadPDF", fileName.toString() + " \n" + url)
        val downloadManager: DownloadManager =
            context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(url)
        val request: DownloadManager.Request = DownloadManager.Request(uri)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalFilesDir(
            context,
            destinationDirectory,
            fileName.toString() + fileExtension
        )
        downloadManager.enqueue(request)
        Toast.makeText(this, "Payslip downloading...", Toast.LENGTH_SHORT).show()
    }
}