package eth.social.sefer.helpers

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.net.Uri
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import kotlin.math.min

/*
 * Copyright (C) 2018 OpenIntents.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
object FileUtils {
  private const val DOCUMENTS_DIR = "documents"

  // configured android:authorities in AndroidManifest (https://developer.android.com/reference/android/support/v4/content/FileProvider)
  private const val AUTHORITY = "YOUR_AUTHORITY.provider"

  /**
   * TAG for log messages.
   */
  private const val TAG = "FileUtils"
  private const val DEBUG = false // Set to true to enable logging

  /**
   * @return Whether the URI is a local one.
   */
  private fun isLocal(url: String?): Boolean {
    return url != null && !url.startsWith("http://") && !url.startsWith("https://")
  }

  /**
   * @param uri The Uri to check.
   * @return Whether the Uri authority is local.
   */
  private fun isLocalStorageDocument(uri: Uri): Boolean {
    return AUTHORITY == uri.authority
  }

  /**
   * @param uri The Uri to check.
   * @return Whether the Uri authority is ExternalStorageProvider.
   */
  private fun isExternalStorageDocument(uri: Uri): Boolean {
    return "com.android.externalstorage.documents" == uri.authority
  }

  /**
   * @param uri The Uri to check.
   * @return Whether the Uri authority is DownloadsProvider.
   */
  private fun isDownloadsDocument(uri: Uri): Boolean {
    return "com.android.providers.downloads.documents" == uri.authority
  }

  /**
   * @param uri The Uri to check.
   * @return Whether the Uri authority is MediaProvider.
   */
  private fun isMediaDocument(uri: Uri): Boolean {
    return "com.android.providers.media.documents" == uri.authority
  }

  /**
   * @param uri The Uri to check.
   * @return Whether the Uri authority is Google Photos.
   */
  private fun isGooglePhotosUri(uri: Uri): Boolean {
    return "com.google.android.apps.photos.content" == uri.authority
  }

  private fun isGoogleDriveUri(uri: Uri): Boolean {
    return "com.google.android.apps.docs.storage.legacy" == uri.authority ||
        "com.google.android.apps.docs.storage" == uri.authority
  }

  /**
   * Get the value of the data column for this Uri. This is useful for
   * MediaStore Uris, and other file-based ContentProviders.
   *
   * @param context       The context.
   * @param uri           The Uri to query.
   * @param selection     (Optional) Filter used in the query.
   * @param selectionArgs (Optional) Selection arguments used in the query.
   * @return The value of the _data column, which is typically a file path.
   */
  private fun getDataColumn(
    context: Context, uri: Uri?, selection: String?,
    selectionArgs: Array<String>?
  ): String? {
    var cursor: Cursor? = null
    val column = MediaStore.Files.FileColumns.DATA
    val projection = arrayOf(
      column
    )
    try {
      cursor = context.contentResolver.query(
        uri!!, projection, selection, selectionArgs,
        null
      )
      if (cursor != null && cursor.moveToFirst()) {
        if (DEBUG) DatabaseUtils.dumpCursor(cursor)
        val columnIndex = cursor.getColumnIndexOrThrow(column)
        return cursor.getString(columnIndex)
      }
    } catch (e: Exception) {
      Log.d(TAG, "getDataColumn: ${e.message}")
    } finally {
      cursor?.close()
    }
    return null
  }

  /**
   * Get a file path from a Uri. This will get the the path for Storage Access
   * Framework Documents, as well as the _data field for the MediaStore and
   * other file-based ContentProviders.<br></br>
   * <br></br>
   * Callers should check whether the path is local before assuming it
   * represents a local file.
   *
   * @param context The context.
   * @param uri     The Uri to query.
   * @see .isLocal
   * @see .getFile
   */
  private fun getPath(context: Context, uri: Uri): String {
    val absolutePath = getLocalPath(context, uri)
    return absolutePath ?: uri.toString()
  }

  private fun getLocalPath(context: Context, uri: Uri): String? {
    if (DEBUG) Log.d(
      "$TAG File -",
      "Authority: " + uri.authority +
          ", Fragment: " + uri.fragment +
          ", Port: " + uri.port +
          ", Query: " + uri.query +
          ", Scheme: " + uri.scheme +
          ", Host: " + uri.host +
          ", Segments: " + uri.pathSegments.toString()
    )

    // DocumentProvider
    if (DocumentsContract.isDocumentUri(context, uri)) {
      // LocalStorageProvider
      if (isLocalStorageDocument(uri)) {
        // The path is the id
        return DocumentsContract.getDocumentId(uri)
      } else if (isExternalStorageDocument(uri)) {
        val docId = DocumentsContract.getDocumentId(uri)
        val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val type = split[0]
        if ("primary".equals(type, ignoreCase = true)) {
          return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
        } else if ("home".equals(type, ignoreCase = true)) {
          return Environment.getExternalStorageDirectory()
            .toString() + "/documents/" + split[1]
        }
      } else if (isDownloadsDocument(uri)) {
        val id = DocumentsContract.getDocumentId(uri)
        if (id != null && id.startsWith("raw:")) {
          return id.substring(4)
        }
        val contentUriPrefixesToTry = arrayOf(
          "content://downloads/public_downloads",
          "content://downloads/my_downloads"
        )
        for (contentUriPrefix in contentUriPrefixesToTry) {
          val contentUri = ContentUris.withAppendedId(
            Uri.parse(contentUriPrefix),
            java.lang.Long.valueOf(id)
          )
          try {
            val path = getDataColumn(context, contentUri, null, null)
            if (path != null) {
              return path
            }
          } catch (_: Exception) {
          }
        }

        // path could not be retrieved using ContentResolver, therefore copy file to accessible cache using streams
        val fileName = getFileName(context, uri)
        val cacheDir = getDocumentCacheDir(context)
        val file = generateFileName(fileName, cacheDir)
        var destinationPath: String? = null
        if (file != null) {
          destinationPath = file.absolutePath
          saveFileFromUri(context, uri, destinationPath)
        }
        return destinationPath
      } else if (isMediaDocument(uri)) {
        val docId = DocumentsContract.getDocumentId(uri)
        val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val contentUri: Uri? = when (split[0]) {
          "image" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
          "video" -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
          "audio" -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
          else -> null
        }
        val selection = "_id=?"
        val selectionArgs = arrayOf(
          split[1]
        )
        return getDataColumn(context, contentUri, selection, selectionArgs)
      } else if (isGoogleDriveUri(uri)) {
        return getGoogleDriveFilePath(uri, context)
      }
    } else if ("content".equals(uri.scheme, ignoreCase = true)) {

      // Return the remote address
      if (isGooglePhotosUri(uri)) {
        return uri.lastPathSegment
      } else if (isGoogleDriveUri(uri)) {
        return getGoogleDriveFilePath(uri, context)
      }
      return getDataColumn(context, uri, null, null)
    } else if ("file".equals(uri.scheme, ignoreCase = true)) {
      return uri.path
    }
    return null
  }

  /**
   * Convert Uri into File, if possible.
   *
   * @return file A local file that the Uri was pointing to, or null if the
   * Uri is unsupported or pointed to a remote resource.
   * @author paulburke
   * @see .getPath
   */
  fun getFile(context: Context, uri: Uri): File? {
    val path = getPath(context, uri)
    if (isLocal(path)) {
      return File(path)
    }
    return null
  }

  private fun getDocumentCacheDir(context: Context): File {
    val dir = File(context.cacheDir, DOCUMENTS_DIR)
    if (!dir.exists()) {
      dir.mkdirs()
    }
    logDir(context.cacheDir)
    logDir(dir)
    return dir
  }

  private fun logDir(dir: File) {
    if (!DEBUG) return
    Log.d(TAG, "Dir=$dir")
    val files = dir.listFiles()
    for (file in files!!) {
      Log.d(TAG, "File=" + file.path)
    }
  }

  private fun generateFileName(givenName: String?, directory: File): File? {
    var name = givenName ?: return null
    var file = File(directory, name)
    if (file.exists()) {
      var fileName = name
      var extension = ""
      val dotIndex = name.lastIndexOf('.')
      if (dotIndex > 0) {
        fileName = name.substring(0, dotIndex)
        extension = name.substring(dotIndex)
      }
      var index = 0
      while (file.exists()) {
        index++
        name = "$fileName($index)$extension"
        file = File(directory, name)
      }
    }
    try {
      if (!file.createNewFile()) {
        return null
      }
    } catch (e: IOException) {
      Log.w(TAG, e)
      return null
    }
    logDir(directory)
    return file
  }

  private fun saveFileFromUri(context: Context, uri: Uri, destinationPath: String?) {
    var `is`: InputStream? = null
    var bos: BufferedOutputStream? = null
    try {
      `is` = context.contentResolver.openInputStream(uri)
      bos = BufferedOutputStream(FileOutputStream(destinationPath, false))
      val buf = ByteArray(1024)
      `is`!!.read(buf)
      do {
        bos.write(buf)
      } while (`is`.read(buf) != -1)
    } catch (e: IOException) {
      e.printStackTrace()
    } finally {
      try {
        `is`?.close()
        bos?.close()
      } catch (e: IOException) {
        e.printStackTrace()
      }
    }
  }

  private fun getFileName(context: Context, uri: Uri): String? {
    val mimeType = context.contentResolver.getType(uri)
    var filename: String? = null
    if (mimeType == null) {
      val path = getPath(context, uri)
      filename = run {
        val file = File(path)
        file.name
      }
    } else {
      val returnCursor = context.contentResolver.query(
        uri, null,
        null, null, null
      )
      if (returnCursor != null) {
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        filename = returnCursor.getString(nameIndex)
        returnCursor.close()
      }
    }
    return filename
  }

  private fun getGoogleDriveFilePath(uri: Uri, context: Context): String {
    val returnCursor = context.contentResolver.query(uri, null, null, null, null)
    /*
     * Get the column indexes of the data in the Cursor,
     *     * move to the first row in the Cursor, get the data,
     *     * and display it.
     * */
    val nameIndex = returnCursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
    returnCursor.moveToFirst()
    val name = returnCursor.getString(nameIndex)
    returnCursor.close()
    val file = File(context.cacheDir, name)
    try {
      val inputStream = context.contentResolver.openInputStream(uri)
      val outputStream = FileOutputStream(file)
      var read: Int
      val maxBufferSize = 1 * 1024 * 1024
      val bytesAvailable = inputStream!!.available()
      val bufferSize = min(bytesAvailable, maxBufferSize)
      val buffers = ByteArray(bufferSize)
      while (inputStream.read(buffers).also { read = it } != -1) {
        outputStream.write(buffers, 0, read)
      }
      inputStream.close()
      outputStream.close()
    } catch (e: Exception) {
      e.printStackTrace()
    }

    return file.path
  }
}