package com.example.storyapp.util

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import androidx.test.espresso.idling.CountingIdlingResource
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

fun isEmailValid(email : String) : Boolean{
    val emailPattern = "[a-zA-Z\\d._-]+@[a-z]+\\.+[a-z]+".toRegex()
    return email.matches(emailPattern)
}

fun createTempImageFile(context: Context) : File{
    val timeStamp : String = SimpleDateFormat("MM-DD-yyyy").format(Date())
    val storageDir : File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(
        "JPEG_${timeStamp}",
        ".jpg",
        storageDir
    )
}

fun uriToFile(selectedImg : Uri, context: Context) : File{
    val contentResolver : ContentResolver = context.contentResolver
    val myFile = createTempImageFile(context)

    val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
    val outputStream : OutputStream = FileOutputStream(myFile)
    val buf = ByteArray(1024)
    var len : Int
    while(inputStream.read(buf).also{ len = it } > 0){
        outputStream.write(buf, 0, len)
    }
    outputStream.close()
    inputStream.close()

    return myFile
}

fun reduceFileImage(file : File) : File{
    val bitmap = BitmapFactory.decodeFile(file.path)
    var compressQuality = 100
    var streamLength: Int
    do {
        val bmpStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
        val bmpPicByteArray = bmpStream.toByteArray()
        streamLength = bmpPicByteArray.size
        compressQuality -= 5
    } while (streamLength > 1000000)
    bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))
    return file
}

fun rotateBitmap(bitmap: Bitmap, isBackCamera: Boolean = false): Bitmap {
    val matrix = Matrix()
    return if (isBackCamera) {
        matrix.postRotate(90f)
        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width ,bitmap.height, matrix,  true)
    } else {
        matrix.postRotate(-90f)
        matrix.postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
        Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
}

object EspressoIdlingResource {

    private const val RESOURCE = "GLOBAL"

    @JvmField
    val countingIdlingResource = CountingIdlingResource(RESOURCE)

    fun increment() {
        countingIdlingResource.increment()
    }

    fun decrement() {
        if (!countingIdlingResource.isIdleNow) {
            countingIdlingResource.decrement()
        }
    }
}

inline fun <T> wrapEspressoIdlingResource(function: () -> T): T {
    EspressoIdlingResource.increment() // Set app as busy.
    return try {
        function()
    } finally {
        EspressoIdlingResource.decrement() // Set app as idle.
    }
}
