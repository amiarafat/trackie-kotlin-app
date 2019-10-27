package com.arafat.trackiekotlinapp.helper

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.ParcelFileDescriptor
import java.io.File
import java.io.FileNotFoundException
import java.util.HashMap

class MyFileContentProvider () : ContentProvider(){

    val CONTENT_URI = Uri.parse("content://com.arafat.trackiekotlinapp/")
    private val MIME_TYPES = HashMap<String, String>()

       /* MIME_TYPES.put(".jpg", "image/jpeg");
        MIME_TYPES.put(".jpeg", "image/jpeg");*/

    override fun onCreate(): Boolean {

        val mFile : File =  File(context!!.filesDir,"userImage.jpg")
        if (!mFile.exists()) {
            mFile.createNewFile()
        }

        context!!.contentResolver.notifyChange(CONTENT_URI,null)
        return true
    }

    override fun getType(uri: Uri): String? {

        val path :String = uri.toString()

        for (extension in MIME_TYPES.keys) {

            if (path.endsWith(extension)) {

                return MIME_TYPES[extension]

            }

        }

        return null
    }


    @Throws(FileNotFoundException::class)
    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor? {

        val f = File(context!!.filesDir, "userImage.jpg")

        if (f.exists()) {

            return ParcelFileDescriptor.open(
                f,

                ParcelFileDescriptor.MODE_READ_WRITE
            )

        }

        throw FileNotFoundException(uri.path)

    }

    override fun query(
        url: Uri, projection: Array<String>?, selection: String?,

        selectionArgs: Array<String>?, sort: String?
    ): Cursor? {

        throw RuntimeException("Operation not supported")

    }

    override fun insert(uri: Uri, initialValues: ContentValues?): Uri? {

        throw RuntimeException("Operation not supported")

    }

    override fun update(
        uri: Uri, values: ContentValues?, where: String?,

        whereArgs: Array<String>?
    ): Int {

        throw RuntimeException("Operation not supported")

    }

    override fun delete(uri: Uri, where: String?, whereArgs: Array<String>?): Int {

        throw RuntimeException("Operation not supported")

    }
}