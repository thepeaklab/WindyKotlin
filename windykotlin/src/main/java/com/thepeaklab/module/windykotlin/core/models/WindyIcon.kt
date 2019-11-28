package com.thepeaklab.module.windykotlin.core.models

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.annotation.DrawableRes
import androidx.core.content.FileProvider
import com.thepeaklab.module.windykotlin.core.CompressFormat
import com.thepeaklab.module.windykotlin.core.FileManagerImpl
import com.thepeaklab.module.windykotlin.core.ImageSource
import java.io.File
import java.util.Locale
import java.util.UUID

/**
 * WindyIcon
 * Windy
 *
 * Created by Thomas Cirksena on 2019-11-27.
 * Copyright © 2019 the peak lab. gmbh & co. kg. All rights reserved.
 */

data class WindyIcon(
    val icon: Icon,
    val iconSize: Point? = null,
    val iconAnchor: Point? = null,
    val popupAnchor: Point? = null,
    val shadowUrl: String? = null,
    val shadowSize: Point? = null,
    val shadowAnchor: Point? = null
) {

    /**
     * map icon to windykotlin conform format
     *
     */
    fun toWindyDto(context: Context, markerUUID: UUID): WindyIconDTO {
        return WindyIconDTO(
            iconUrl = getIconUrl(context, markerUUID),
            iconSize = iconSize?.arrayRepresentation(),
            iconAnchor = iconAnchor?.arrayRepresentation(),
            popupAnchor = popupAnchor?.arrayRepresentation(),
            shadowUrl = shadowUrl,
            shadowSize = shadowSize?.arrayRepresentation(),
            shadowAnchor = shadowAnchor?.arrayRepresentation()
        )
    }

    /**
     * get icon url
     *
     * @return web- or content provider url
     */
    private fun getIconUrl(context: Context, uuid: UUID): String {

        return when {
            icon.url != null -> return icon.url
            icon.drawable != null -> getDrawablPath(context, icon.drawable, uuid)
            icon.asset != null -> getAssetPath(context, icon.asset)
            else -> throw IllegalArgumentException("The Icon needs to define a image by 'url', 'drawable' or 'asset'")
        }
    }

    /**
     * return the path of a file the represents the asset.
     *
     * The path of the asset itself cant be referenced by windykotlin so a image file is
     * created based on the asset. That file can be referenced by a ContentProvider url.
     *
     * @return contentprovider url or empty string
     */
    private fun getAssetPath(context: Context, assetPath: String): String {

        return try {
            val filename = assetPath.substring(assetPath.lastIndexOf(File.pathSeparator) + 1)
            val inputStream = context.assets.open(assetPath)

            val compressFormat = when (MimeTypeMap.getFileExtensionFromUrl(filename.toLowerCase(Locale.getDefault()))) {
                "gif" -> CompressFormat.GIF
                "webp" -> CompressFormat.WEBP
                "jpg", "jpeg" -> CompressFormat.JPG
                else -> CompressFormat.OTHER
            }

            val absPath = FileManagerImpl().saveImage(
                context = context,
                source = ImageSource(stream = inputStream),
                filename = filename, path = context.cacheDir.absolutePath + File.separator + "markers",
                compressFormat = compressFormat,
                compressQuality = 90
            ) ?: ""

            getContentProviderURI(context, absPath).toString()
        } catch (e: Exception) {
            Log.e("WindyHTMLResources", "icon asset '${icon.drawable}' not found!", e)
            ""
        }
    }

    /**
     * return the path of a file the represents the drawable.
     *
     * The path of the drawable itself cant be referenced by windykotlin so a image file based on the drawable is
     * created. That file can be referenced by a ContentProvider url.
     *
     * @return contentprovider url or empty string
     */
    private fun getDrawablPath(context: Context, @DrawableRes drawableRes: Int, uuid: UUID): String {

        return try {

            val v = BitmapFactory.decodeResource(context.resources, drawableRes)

            val absPath = FileManagerImpl().saveImage(
                context = context,
                source = ImageSource(bitmap = v),
                filename = "${uuid}_image.png",
                path = context.cacheDir.absolutePath + File.separator + "markers",
                compressFormat = CompressFormat.PNG,
                compressQuality = 90
            ) ?: ""

            return getContentProviderURI(context, absPath).toString()
        } catch (e: Exception) {
            Log.e("WindyHTMLResources", "icon drawable '${icon.drawable}' not found!", e)
            ""
        }
    }

    /**
     * get content provider uri for file
     *
     */
    private fun getContentProviderURI(context: Context, filePath: String): Uri {
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", File(filePath))
    }
}