package com.devrajnish.invoicegenerator

import android.Manifest
import android.app.Activity
import android.app.DownloadManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.itextpdf.text.BaseColor
import com.itextpdf.text.Font
import com.itextpdf.text.pdf.BaseFont
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.itextpdf.text.*
import com.itextpdf.text.PageSize.A4
import com.itextpdf.text.pdf.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_stock.*
import java.io.*
import java.lang.Integer.parseInt
import java.nio.file.Files
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {


    var uriforstorage: Uri? = null;
    val colorPrimary = BaseColor(40, 116, 240)
    val FONT_SIZE_DEFAULT = 12f
    val FONT_SIZE_SMALL = 8f
    var basfontLight: BaseFont =
        BaseFont.createFont("res/font/app_font_light.ttf", "UTF-8", BaseFont.EMBEDDED)
    var appFontLight = Font(basfontLight, FONT_SIZE_SMALL)

    var basfontRegular: BaseFont =
        BaseFont.createFont("res/font/app_font_regular.ttf", "UTF-8", BaseFont.EMBEDDED)
    var appFontRegular = Font(basfontRegular, FONT_SIZE_DEFAULT)


    var basfontSemiBold: BaseFont =
        BaseFont.createFont("res/font/app_font_semi_bold.ttf", "UTF-8", BaseFont.EMBEDDED)
    var appFontSemiBold = Font(basfontSemiBold, 24f)


    var basfontBold: BaseFont =
        BaseFont.createFont("res/font/app_font_bold.ttf", "UTF-8", BaseFont.EMBEDDED)
    var appFontBold = Font(basfontBold, FONT_SIZE_DEFAULT)

    val PADDING_EDGE = 10f
    val TEXT_TOP_PADDING = 3f
    val TABLE_TOP_PADDING = 10f
    val TEXT_TOP_PADDING_EXTRA = 30f
    val BILL_DETAILS_TOP_PADDING = 80f
    val data = ArrayList<ModelItems>()



    lateinit var q1et : EditText
    lateinit var q2et : EditText
    lateinit var q3et : EditText
    lateinit var q4et : EditText



    var Totalamount = 0;

    lateinit var customer_et : EditText
    lateinit var bottomNavigationView: BottomNavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        q1et = findViewById<EditText>(R.id.p1_et);
        q2et = findViewById<EditText>(R.id.p2_et);
        q3et = findViewById<EditText>(R.id.p3_et);
        q4et = findViewById<EditText>(R.id.p4_et);



        customer_et = findViewById(R.id.customername_et);

        supportActionBar?.hide()

        generatePdf.setOnClickListener {

            Dexter.withContext(this)
                .withPermissions(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE

                ).withListener(object : MultiplePermissionsListener {
                    @RequiresApi(Build.VERSION_CODES.R)
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        if (report.areAllPermissionsGranted()) {
                            permissionGranted()
                        } else {
                            toast("permissions missing :(")
                            //askpermission();

                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        permissions: List<PermissionRequest>,
                        token: PermissionToken
                    ) {
                        token.continuePermissionRequest()
                    }
                }).check()
        }



        bottomNavigationView = findViewById(R.id.bottom_navigator)
        bottomNavigationView.selectedItemId = R.id.home

        bottomNavigationView.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.rate -> {
                    startActivity(Intent(applicationContext, RateUpdate::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.home -> {
                    return@OnNavigationItemSelectedListener true
                }
                R.id.stock -> {
                    startActivity(Intent(applicationContext, StockActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })



    }



    @RequiresApi(Build.VERSION_CODES.R)
    private fun permissionGranted() {

        initData();

        //createFile(Environment.DIRECTORY_DOCUMENTS.toUri());
        writepdf()



    }

    private fun writepdf()
    {

        val format = SimpleDateFormat("dd:MM:yyyy - hh:mm:ss");
        val date = Date()
        val datestring = "${format.format(date)}"


        appFontRegular.color = BaseColor.WHITE
        appFontRegular.size = 10f
        val doc = Document(A4, 0f, 0f, 0f, 0f)
        val outPath = getExternalFilesDir(null).toString() + "/Bill-" + datestring +".pdf" //location where the pdf will store
       Log.d("loc", outPath)

        val writer = PdfWriter.getInstance(doc, FileOutputStream(outPath))
        doc.open()
        //Header Column Init with width nad no. of columns
        initInvoiceHeader(doc)
        doc.setMargins(20f, 20f, PADDING_EDGE, PADDING_EDGE)
        initBillDetails(doc)
        // addLine(writer)
        initTableHeader(doc)
        initItemsTable(doc)
        initPriceDetails(doc)
        initFooter(doc)
        doc.close()


        val file = File(outPath)
        if(!file.exists())
            file.createNewFile();
        val path : Uri = FileProvider.getUriForFile(applicationContext,
            applicationContext.getApplicationContext().getPackageName()
                    + ".provider", file);

        //downloadfile(file);

        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(path, "application/pdf")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            toast("There is no PDF Viewer ")
        }



    }

    private fun updatestocks(q1 : Int , q2 : Int ,q3 : Int ,q4 : Int ) {

        //previous stock
        var q1p = 0;
        var q2p = 0;
        var q3p = 0;
        var q4p = 0;

        var sharedPreferences = getSharedPreferences("stock" , MODE_PRIVATE);
        q1p = sharedPreferences.getInt("s1", 0);
        q2p = sharedPreferences.getInt("s2", 0);
        q3p = sharedPreferences.getInt("s3", 0);
        q4p = sharedPreferences.getInt("s4", 0);

        //update
        val q1u = q1p - q1;
        val q2u = q2p - q2;
        val q3u = q3p - q3;
        val q4u = q4p - q4;

        var editor = sharedPreferences.edit()
        editor.putInt("s1" , q1u);
        editor.putInt("s2" , q2u);
        editor.putInt("s3" , q3u);
        editor.putInt("s4" , q4u);

        editor.commit()


    }

    private fun downloadfile(file: File) {

        val url : Uri = file.toUri();
        val mediaStorageDir = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "143-opa"
        )
        try {
            val source = File(url.path);
            val src = FileInputStream(source).channel
            val dst = FileOutputStream(mediaStorageDir).channel
            dst.transferFrom(src, 0, src.size())
            src.close()
            dst.close()
        }
        catch (e : Exception){
            e.printStackTrace()

            toast("Failed to download in public directory due to" +{e.message});
        }
        /* val request = DownloadManager.Request(url);
         request.setTitle(url.toString());
         request.setMimeType("application/pdf");
         request.allowScanningByMediaScanner();
         request.setAllowedOverMetered(true);
         request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION)
         request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,"invoice");
         val dm = getSystemService(DOWNLOAD_SERVICE) as DownloadManager;
         dm.enqueue(request);*/

    }


    private fun initFooter(doc: Document) {
        appFontRegular.color = colorPrimary
        val footerTable = PdfPTable(1)
        footerTable.totalWidth = A4.width
        footerTable.isLockedWidth = true
        val thankYouCell =
            PdfPCell(Phrase("THANK YOU FOR YOUR BUSINESS", appFontRegular))
        thankYouCell.border = Rectangle.NO_BORDER
        thankYouCell.paddingLeft = PADDING_EDGE
        thankYouCell.paddingTop = 40f
        thankYouCell.horizontalAlignment = Rectangle.ALIGN_CENTER
        footerTable.addCell(thankYouCell)
        doc.add(footerTable)

    }

    private fun initData() {


        var item1 = ModelItems("          SSP" ,
                                " ",
                               5,1000,0,10);
        var item2 = ModelItems("          MOP" ,
            " ",
            5,1000,0,10);
        var item3 = ModelItems("          DAP" ,
            " ",
            5,1000,0,10);
        var item4 = ModelItems("          Urea" ,
            " ",
            5,1000,0,10);



        val sharedPreferences = getSharedPreferences("RateList", MODE_PRIVATE);
        item1.disAmount = sharedPreferences.getInt("ratep1", 0);
        item2.disAmount = sharedPreferences.getInt("ratep2", 0);
        item3.disAmount = sharedPreferences.getInt("ratep3", 0);
        item4.disAmount = sharedPreferences.getInt("ratep4", 0);

        var q1 = 0;
        var q2 = 0;
        var q3 = 0;
        var q4 = 0;

        if(q1et.text.toString() != "")
            q1 = Integer.parseInt(q1et.text.toString())
        else
            q1 = 0;

        if(q2et.text.toString() != "")
            q2 = Integer.parseInt(q2et.text.toString())
        else
            q2 = 0;


        if(q3et.text.toString() != "")
            q3 = Integer.parseInt(q3et.text.toString())
        else
            q3 = 0;


        if(q4et.text.toString() != "")
            q4 = Integer.parseInt(q4et.text.toString())
        else
            q4 = 0;

        updatestocks(q1,q2,q3,q4);



        item1.quantity = q1;
        item2.quantity = q2;
        item3.quantity = q3;
        item4.quantity = q4;



        item1.netAmount = item1.quantity * item1.disAmount;
        item2.netAmount = item2.quantity * item2.disAmount;
        item3.netAmount = item3.quantity * item3.disAmount;
        item4.netAmount = item4.quantity * item4.disAmount;


        Totalamount = item1.netAmount + item2.netAmount + item3.netAmount + item4.netAmount;

        data.clear();
        data.add(item1);
        data.add(item2);
        data.add(item3);
        data.add(item4);

    }





    private fun initPriceDetails(doc: Document) {
        val priceDetailsTable = PdfPTable(2)
        priceDetailsTable.totalWidth = A4.width
        priceDetailsTable.setWidths(floatArrayOf(5f, 2f))
        priceDetailsTable.isLockedWidth = true

        appFontRegular.color = colorPrimary

        val txtTotalCell = PdfPCell(Phrase("TOTAL : ", appFontRegular))
        txtTotalCell.border = Rectangle.NO_BORDER
        txtTotalCell.horizontalAlignment = Rectangle.ALIGN_RIGHT
        txtTotalCell.paddingTop = TEXT_TOP_PADDING
        txtTotalCell.paddingBottom = TEXT_TOP_PADDING
        txtTotalCell.paddingLeft = PADDING_EDGE
        priceDetailsTable.addCell(txtTotalCell)
        appFontBold.color = colorPrimary
        val totalCell = PdfPCell(Phrase("Rs ${Totalamount}", appFontBold))
        totalCell.border = Rectangle.NO_BORDER
        totalCell.horizontalAlignment = Rectangle.ALIGN_RIGHT
        totalCell.paddingTop = TEXT_TOP_PADDING
        totalCell.paddingBottom = TEXT_TOP_PADDING
        totalCell.paddingRight = PADDING_EDGE
        priceDetailsTable.addCell(totalCell)

        doc.add(priceDetailsTable)
    }

    private fun initItemsTable(doc: Document) {
        val itemsTable = PdfPTable(4)
        itemsTable.isLockedWidth = true
        itemsTable.totalWidth = A4.width
        itemsTable.setWidths(floatArrayOf(1.5f, 1f, 1f,  1.1f))

        for (item in data) {
            itemsTable.deleteBodyRows()



            val itemdetails = PdfPTable(1)
            val itemName = PdfPCell(Phrase(item.itemName, appFontRegular))
            itemName.border = Rectangle.NO_BORDER
            val itemDesc = PdfPCell(Phrase(item.itemDesc, appFontLight))
            itemDesc.border = Rectangle.NO_BORDER
            itemdetails.addCell(itemName)
            itemdetails.addCell(itemDesc)

            val itemCell = PdfPCell(itemdetails)
            itemCell.border = Rectangle.NO_BORDER
            itemCell.paddingTop = TABLE_TOP_PADDING
            itemCell.paddingLeft = PADDING_EDGE
            itemsTable.addCell(itemCell)


            val quantityCell = PdfPCell(Phrase("${item.quantity}", appFontRegular))
            quantityCell.border = Rectangle.NO_BORDER
            quantityCell.horizontalAlignment = Rectangle.ALIGN_RIGHT
            quantityCell.paddingTop = TABLE_TOP_PADDING
            itemsTable.addCell(quantityCell)

            val disAmount = PdfPCell(Phrase("Rs ${item.disAmount}", appFontRegular))
            disAmount.border = Rectangle.NO_BORDER
            disAmount.horizontalAlignment = Rectangle.ALIGN_RIGHT
            disAmount.paddingTop = TABLE_TOP_PADDING
            itemsTable.addCell(disAmount)


            val netAmount = PdfPCell(Phrase("Rs ${item.netAmount}", appFontRegular))
            netAmount.horizontalAlignment = Rectangle.ALIGN_RIGHT
            netAmount.border = Rectangle.NO_BORDER
            netAmount.paddingTop = TABLE_TOP_PADDING
            netAmount.paddingRight = PADDING_EDGE
            itemsTable.addCell(netAmount)
            doc.add(itemsTable)
        }
    }


    private fun initTableHeader(doc: Document) {

        doc.add(Paragraph("\n\n\n\n\n")) //adds blank line to place table header below the line

        val titleTable = PdfPTable(5)
        titleTable.isLockedWidth = true
        titleTable.totalWidth = A4.width
        titleTable.setWidths(floatArrayOf(0.2f,1.5f, 1f, 1f,  1.1f))
        appFontBold.color = colorPrimary

        val itemCell1 = PdfPCell(Phrase("        ", appFontBold))
        itemCell1.border = Rectangle.NO_BORDER
        itemCell1.paddingTop = TABLE_TOP_PADDING
        itemCell1.paddingBottom = TABLE_TOP_PADDING
        itemCell1.paddingLeft = PADDING_EDGE
        titleTable.addCell(itemCell1)

        val itemCell = PdfPCell(Phrase("Description", appFontBold))
        itemCell.border = Rectangle.NO_BORDER
        itemCell.paddingTop = TABLE_TOP_PADDING
        itemCell.paddingBottom = TABLE_TOP_PADDING
        itemCell.paddingLeft = PADDING_EDGE
        titleTable.addCell(itemCell)


        val quantityCell = PdfPCell(Phrase("Quantity", appFontBold))
        quantityCell.border = Rectangle.NO_BORDER
        quantityCell.horizontalAlignment = Rectangle.ALIGN_RIGHT
        quantityCell.paddingBottom = TABLE_TOP_PADDING
        quantityCell.paddingTop = TABLE_TOP_PADDING
        titleTable.addCell(quantityCell)

        val disAmount = PdfPCell(Phrase("Price", appFontBold))
        disAmount.border = Rectangle.NO_BORDER
        disAmount.horizontalAlignment = Rectangle.ALIGN_RIGHT
        disAmount.paddingBottom = TABLE_TOP_PADDING
        disAmount.paddingTop = TABLE_TOP_PADDING
        titleTable.addCell(disAmount)



        val netAmount = PdfPCell(Phrase("Net Amount", appFontBold))
        netAmount.horizontalAlignment = Rectangle.ALIGN_RIGHT
        netAmount.border = Rectangle.NO_BORDER
        netAmount.paddingTop = TABLE_TOP_PADDING
        netAmount.paddingBottom = TABLE_TOP_PADDING
        netAmount.paddingRight = PADDING_EDGE
        titleTable.addCell(netAmount)
        doc.add(titleTable)
    }

    private fun addLine(writer: PdfWriter) {
        val canvas: PdfContentByte = writer.directContent
        canvas.setColorStroke(colorPrimary)
        canvas.moveTo(40.0, 480.0)

        // Drawing the line
        canvas.lineTo(560.0, 480.0)
        canvas.setLineWidth(3f)

        // Closing the path stroke
        canvas.closePathStroke()
    }

    private fun initBillDetails(doc: Document) {
        val billDetailsTable =
            PdfPTable(3)  // table to show customer address, invoice, date and total amount
        billDetailsTable.setWidths(
            floatArrayOf(
                2f,
                1.82f,
                2f
            )
        )
        billDetailsTable.isLockedWidth = true
        billDetailsTable.paddingTop = 30f

        billDetailsTable.totalWidth =
            A4.width // set content width to fill document
        val customerAddressTable = PdfPTable(1)
        appFontRegular.color = BaseColor.GRAY
        appFontRegular.size = 8f
        val txtBilledToCell = PdfPCell(
            Phrase(
                "Billed To",
                appFontLight
            )
        )
        txtBilledToCell.border = Rectangle.NO_BORDER
        customerAddressTable.addCell(
            txtBilledToCell
        )

        val name = customer_et.text.toString();

        appFontRegular.size = FONT_SIZE_DEFAULT
        appFontRegular.color = BaseColor.BLACK
        val clientAddressCell1 = PdfPCell(
            Paragraph(
                "${name}",
                appFontRegular
            )
        )
        clientAddressCell1.border = Rectangle.NO_BORDER
        clientAddressCell1.paddingTop = TEXT_TOP_PADDING
        customerAddressTable.addCell(clientAddressCell1)

        val clientAddressCell2 = PdfPCell(
            Paragraph(
                " ",
                appFontRegular
            )
        )
        clientAddressCell2.border = Rectangle.NO_BORDER
        clientAddressCell2.paddingTop = TEXT_TOP_PADDING
        customerAddressTable.addCell(clientAddressCell2)


        val clientAddressCell3 = PdfPCell(
            Paragraph(
                " ",
                appFontRegular
            )
        )
        clientAddressCell3.border = Rectangle.NO_BORDER
        clientAddressCell3.paddingTop = TEXT_TOP_PADDING
        customerAddressTable.addCell(clientAddressCell3)


        val clientAddressCell4 = PdfPCell(
            Paragraph(
                " ",
                appFontRegular
            )
        )
        clientAddressCell4.border = Rectangle.NO_BORDER
        clientAddressCell4.paddingTop = TEXT_TOP_PADDING
        customerAddressTable.addCell(clientAddressCell4)

        val billDetailsCell1 = PdfPCell(customerAddressTable)
        billDetailsCell1.border = Rectangle.NO_BORDER

        billDetailsCell1.paddingTop = BILL_DETAILS_TOP_PADDING

        billDetailsCell1.paddingLeft = PADDING_EDGE

        billDetailsTable.addCell(billDetailsCell1)


        val invoiceNumAndData = PdfPTable(1)
        appFontRegular.color = BaseColor.LIGHT_GRAY
        appFontRegular.size = 8f
        val txtInvoiceNumber = PdfPCell(Phrase("Invoice Number", appFontLight))
        txtInvoiceNumber.paddingTop = BILL_DETAILS_TOP_PADDING
        txtInvoiceNumber.border = Rectangle.NO_BORDER
        invoiceNumAndData.addCell(txtInvoiceNumber)
        appFontRegular.color = BaseColor.BLACK
        appFontRegular.size = 12f
        val invoiceNumber = PdfPCell(Phrase("BMC00${(1234..9879).random()}", appFontRegular))
        invoiceNumber.border = Rectangle.NO_BORDER
        invoiceNumber.paddingTop = TEXT_TOP_PADDING
        invoiceNumAndData.addCell(invoiceNumber)



        appFontRegular.color = BaseColor.LIGHT_GRAY
        appFontRegular.size = 5f
        val txtDate = PdfPCell(Phrase("Date Of Issue", appFontLight))
        txtDate.paddingTop = TEXT_TOP_PADDING_EXTRA
        txtDate.border = Rectangle.NO_BORDER
        invoiceNumAndData.addCell(txtDate)

        val format = SimpleDateFormat("dd/MM/yyyy");
        val date = Date()


        appFontRegular.color = BaseColor.BLACK
        appFontRegular.size = FONT_SIZE_DEFAULT
        val dateCell = PdfPCell(Phrase("${format.format(date)}", appFontRegular))
        dateCell.border = Rectangle.NO_BORDER
        invoiceNumAndData.addCell(dateCell)

        val dataInvoiceNumAndData = PdfPCell(invoiceNumAndData)
        dataInvoiceNumAndData.border = Rectangle.NO_BORDER
        billDetailsTable.addCell(dataInvoiceNumAndData)

        val totalPriceTable = PdfPTable(1)
        val txtInvoiceTotal = PdfPCell(Phrase("Invoice Total", appFontLight))
        txtInvoiceTotal.paddingTop = BILL_DETAILS_TOP_PADDING
        txtInvoiceTotal.horizontalAlignment = Rectangle.ALIGN_RIGHT
        txtInvoiceTotal.border = Rectangle.NO_BORDER
        totalPriceTable.addCell(txtInvoiceTotal)

        appFontSemiBold.color = colorPrimary
        val totalAomountCell = PdfPCell(Phrase("Rs ${Totalamount}", appFontSemiBold))
        totalAomountCell.border = Rectangle.NO_BORDER
        totalAomountCell.horizontalAlignment = Rectangle.ALIGN_RIGHT
        totalPriceTable.addCell(totalAomountCell)
        val dataTotalAmount = PdfPCell(totalPriceTable)
        dataTotalAmount.border = Rectangle.NO_BORDER
        dataTotalAmount.paddingRight = PADDING_EDGE
        dataTotalAmount.verticalAlignment = Rectangle.ALIGN_BOTTOM

        billDetailsTable.addCell(dataTotalAmount)
        doc.add(billDetailsTable)
    }

    private fun initInvoiceHeader(doc: Document) {
        val d = resources.getDrawable(R.drawable.logoagro)
        val bitDw = d as BitmapDrawable
        val bmp = bitDw.bitmap
        val stream = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val image = Image.getInstance(stream.toByteArray())
        val headerTable = PdfPTable(1)

        headerTable.isLockedWidth = true
        headerTable.totalWidth = A4.width // set content width to fill document
        val cell = PdfPCell(Image.getInstance(image)) // Logo Cell
        cell.border = Rectangle.NO_BORDER // Removes border
        cell.paddingTop = TEXT_TOP_PADDING_EXTRA // sets padding
        cell.paddingRight = TABLE_TOP_PADDING
        cell.paddingLeft = PADDING_EDGE
        cell.horizontalAlignment = Rectangle.ALIGN_CENTER
        cell.paddingBottom = TEXT_TOP_PADDING_EXTRA

        cell.backgroundColor = BaseColor(255,255,255)// sets background color
        cell.horizontalAlignment = Element.ALIGN_CENTER
        headerTable.addCell(cell) // Adds first cell with logo

        val contactTable =
            PdfPTable(1) // new vertical table for contact details
        val phoneCell =
            PdfPCell(
                Paragraph(
                    "+91 8547984369",
                    appFontRegular
                )
            )
        phoneCell.border = Rectangle.NO_BORDER
        phoneCell.horizontalAlignment = Element.ALIGN_RIGHT
        phoneCell.paddingTop = TEXT_TOP_PADDING

        //contactTable.addCell(phoneCell)

        val emailCellCell = PdfPCell(Phrase("sreeharikariot@gmail.com", appFontRegular))
        emailCellCell.border = Rectangle.NO_BORDER
        emailCellCell.horizontalAlignment = Element.ALIGN_RIGHT
        emailCellCell.paddingTop = TEXT_TOP_PADDING

        //contactTable.addCell(emailCellCell)

        val webCell = PdfPCell(Phrase("www.kariot.me", appFontRegular))
        webCell.border = Rectangle.NO_BORDER
        webCell.paddingTop = TEXT_TOP_PADDING
        webCell.horizontalAlignment = Element.ALIGN_RIGHT

        //contactTable.addCell(webCell)


        val headCell = PdfPCell(contactTable)
        headCell.border = Rectangle.NO_BORDER
        headCell.horizontalAlignment = Element.ALIGN_RIGHT
        headCell.verticalAlignment = Element.ALIGN_MIDDLE
        headCell.backgroundColor = colorPrimary

        //headerTable.addCell(headCell)

        val address = PdfPTable(1)
        val line1 = PdfPCell(
            Paragraph(
                "Address Line 1",
                appFontRegular
            )
        )
        line1.border = Rectangle.NO_BORDER
        line1.paddingTop = TEXT_TOP_PADDING
        line1.horizontalAlignment = Element.ALIGN_RIGHT

        //address.addCell(line1)

        val line2 = PdfPCell(Paragraph("Address Line 2", appFontRegular))
        line2.border = Rectangle.NO_BORDER
        line2.paddingTop = TEXT_TOP_PADDING
        line2.horizontalAlignment = Element.ALIGN_RIGHT

        //address.addCell(line2)

        val line3 = PdfPCell(Paragraph("Address Line 3", appFontRegular))
        line3.border = Rectangle.NO_BORDER
        line3.paddingTop = TEXT_TOP_PADDING
        line3.horizontalAlignment = Element.ALIGN_RIGHT

       // address.addCell(line3)


        val addressHeadCell = PdfPCell(address)
        addressHeadCell.border = Rectangle.NO_BORDER
        addressHeadCell.setLeading(22f, 25f)
        addressHeadCell.horizontalAlignment = Element.ALIGN_RIGHT
        addressHeadCell.verticalAlignment = Element.ALIGN_MIDDLE
        addressHeadCell.backgroundColor = colorPrimary
        addressHeadCell.paddingRight = PADDING_EDGE
       // headerTable.addCell(addressHeadCell)

        doc.add(headerTable)
    }



    val CREATE_FILE = 1

    private fun createFile(pickerInitialUri: Uri) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
            putExtra(Intent.EXTRA_TITLE, "invoice.pdf")

            // Optionally, specify a URI for the directory that should be opened in
            // the system file picker before your app creates the document.
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }
        startActivityForResult(intent, CREATE_FILE)
    }

     val PICK_PDF_FILE = 2

    fun openFile(pickerInitialUri: Uri) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"

            // Optionally, specify a URI for the file that should appear in the
            // system file picker when it loads.
            putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri)
        }

        startActivityForResult(intent, PICK_PDF_FILE)
    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int, resultData: Intent?) {
        super.onActivityResult(requestCode, resultCode, resultData)
        if (requestCode == CREATE_FILE
            && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            resultData?.data?.also { uri ->
                uriforstorage = uri;
                writepdf()

            }
        }

        if (requestCode == PICK_PDF_FILE
            && resultCode == Activity.RESULT_OK) {
            // The result data contains a URI for the document or directory that
            // the user selected.
            resultData?.data?.also { uri ->

            }
        }
    }



}