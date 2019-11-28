package com.thepeaklab.module.windykotlin.core

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

/**
 * Created by thomas on 02.05.19.
 */

interface FileManager {

    fun saveImage(
        context: Context,
        source: ImageSource,
        filename: String,
        path: String,
        compressFormat: CompressFormat,
        compressQuality: Int
    ): String?
}

@SuppressWarnings("LogConditional")
class FileManagerImpl : FileManager {

    /**
     * write image to storage and return path
     *
     */
    override fun saveImage(
        context: Context,
        source: ImageSource,
        filename: String,
        path: String,
        compressFormat: CompressFormat,
        compressQuality: Int
    ): String? {

        if (File(path, filename).exists()) {
            return File(path, filename).absolutePath
        }

        var fos: FileOutputStream? = null
        val baos = ByteArrayOutputStream()
        try {

            when {
                source.bitmap != null -> source.bitmap.compress(compressFormat.getBitmapCompressFormat(), compressQuality, baos)
                source.stream != null -> {
                    if (compressFormat == CompressFormat.GIF) {
                        val buffer = ByteArray(1024)
                        var read = source.stream.read(buffer, 0, buffer.size)
                        while (read != -1) {
                            baos.write(buffer, 0, read)
                            read = source.stream.read(buffer, 0, buffer.size)
                        }
                        baos.flush()
                    } else {
                        BitmapFactory.decodeStream(source.stream).compress(compressFormat.getBitmapCompressFormat(), compressQuality, baos)
                    }
                }
                else -> throw IllegalArgumentException("The ImageSource needs to define a image by 'bitmap' or 'stream'")
            }

            val subFolder = File(path)

            if (!subFolder.exists()) {
                subFolder.mkdirs()
            }

            val finalFile = File(subFolder, filename)
            fos = FileOutputStream(finalFile)
            fos.write(baos.toByteArray())

            return finalFile.absolutePath
        } catch (e: FileNotFoundException) {
            Log.e(javaClass.simpleName, e.toString())
        } catch (e: IOException) {
            Log.e(javaClass.simpleName, e.toString())
        } finally {
            fos?.close()
        }

        return null
    }
}

data class ImageSource(val bitmap: Bitmap? = null, val stream: InputStream? = null)

enum class CompressFormat {
    GIF, WEBP, PNG, JPG, OTHER;

    fun getBitmapCompressFormat(): Bitmap.CompressFormat? {
        return when (this) {
            GIF -> null
            WEBP -> Bitmap.CompressFormat.WEBP
            PNG -> Bitmap.CompressFormat.PNG
            JPG -> Bitmap.CompressFormat.JPEG
            OTHER -> Bitmap.CompressFormat.PNG
        }
    }
}
