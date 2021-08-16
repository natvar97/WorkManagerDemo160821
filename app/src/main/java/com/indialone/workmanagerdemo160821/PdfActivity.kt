package com.indialone.workmanagerdemo160821

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewTreeObserver
import com.github.barteksc.pdfviewer.util.FitPolicy

import com.indialone.workmanagerdemo160821.databinding.ActivityPdfBinding

class PdfActivity : AppCompatActivity(){

    private lateinit var mBinding: ActivityPdfBinding
    private var pdfFilePath = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityPdfBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        if (intent.hasExtra("pdfFilePath")) {
            pdfFilePath = intent.getStringExtra("pdfFilePath")!!
        }

        mBinding.pdfView.fromUri(Uri.parse(pdfFilePath))
            .pages(0, 2, 1, 3, 3, 3) // all pages are displayed by default
            .enableSwipe(true) // allows to block changing pages using swipe
            .swipeHorizontal(false)
            .enableDoubletap(true)
            .defaultPage(0)
            // allows to draw something on the current page, usually visible in the middle of the screen
            .enableAnnotationRendering(false) // render annotations (such as comments, colors or forms)
            .password(null)
            .scrollHandle(null)
            .enableAntialiasing(true) // improve rendering a little bit on low-res screens
            // spacing between pages in dp. To define spacing color, set view background
            .spacing(0)
            .autoSpacing(false) // add dynamic spacing to fit each page on its own on the screen
            .pageFitPolicy(FitPolicy.WIDTH) // mode to fit pages in the view
            .fitEachPage(false) // fit each page to the view, else smaller pages are scaled relative to largest page.
            .pageSnap(false) // snap pages to screen boundaries
            .pageFling(false) // make a fling change only a single page like ViewPager
            .nightMode(false) // toggle night mode
            .load();

    }
}