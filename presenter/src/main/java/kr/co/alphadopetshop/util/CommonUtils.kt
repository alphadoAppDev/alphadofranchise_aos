package kr.co.alphadopetshop.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Base64
import android.util.DisplayMetrics
import android.util.Log
import android.view.Display
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kr.co.alphadopetshop.R
import kr.co.alphadopetshop.model.DeviceSize
import java.io.*
import java.lang.Math.*
import java.text.SimpleDateFormat
import kotlin.math.pow

object CommonUtils : LocationListener{
    private val MIN_TIME_BW_UPDATE : Long = 1000 * 60 * 1
    private val MIN_DISTANCE_CHANGE_FOR_UPDATES : Float = 10f

    fun calculateDistance(context : Context, latitude : Double, longitude : Double) : String {
        val r = 6372.8 * 1000
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var userLocation: Location = getLatLng(context, locationManager)

        val dLat = toRadians(latitude - userLocation.latitude)
        val dLon = toRadians(longitude - userLocation.longitude)
        val a = sin(dLat / 2).pow(2.0) + sin(dLon / 2).pow(2.0) * cos(toRadians(userLocation.latitude)) * cos(
            toRadians(latitude)
        )
        val c = 2 * kotlin.math.asin(sqrt(a))
        return  (r * c).toInt().toString()
    }

    fun getLatLng(context: Context, locationManager : LocationManager): Location{
        var currentLatLng: Location? = null
        var hasFineLocationPermission = ContextCompat.checkSelfPermission(context,
            Manifest.permission.ACCESS_FINE_LOCATION)
        var hasCoarseLocationPermission = ContextCompat.checkSelfPermission(context,
            Manifest.permission.ACCESS_COARSE_LOCATION)

        if(hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
            hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED){

                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    MIN_TIME_BW_UPDATE,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES,
                    this
                )

            currentLatLng = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        }

        return currentLatLng!!
    }

    override fun onLocationChanged(location: Location) {

    }

    fun showKeyboard(context: Context, editText: EditText) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    fun hideKeyboard(context: Context, editText: EditText) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText.windowToken, 0)
    }

    fun getDeviceSize(context: Context): DeviceSize {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val outMetrics = DisplayMetrics()
        var display: Display? = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            display = context.display
            display!!.getRealMetrics(outMetrics)

            return DeviceSize(outMetrics.widthPixels, outMetrics.heightPixels)
        } else {
            display = windowManager.defaultDisplay
            display.getMetrics(outMetrics)

            val size = Point()
            display.getSize(size)

            return DeviceSize(size.x, size.y)
        }
    }

    fun transLanguageHospitalType(type : String, context: Context) : String {
        return when(type) {
            "body" -> context.getString(R.string.hospital_type_body)
            "blood" -> context.getString(R.string.hospital_type_blood)
            "ultrasound" -> context.getString(R.string.hospital_type_ultrasound)
            "xray" -> context.getString(R.string.hospital_type_xray)
            else -> ""
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun parseHospitalInfoTime(time : String?) : String {
        val parser = SimpleDateFormat("HH:mm:ss")
        val formatter = SimpleDateFormat("HH:mm")
        return formatter.format(parser.parse(time!!)!!)
    }

    fun makeAlertDialog(layout : Int, context: Context, widthRatio : Double) : AlertDialog {
        val dialogView =  LayoutInflater.from(context).inflate(layout, null)
        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)

        val alertDialog = builder.create()
        alertDialog.show()

        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val params = alertDialog.window?.attributes
        val deviceSize = getDeviceSize(context)
        val deviceRatio : Float = if(deviceSize.width.toFloat() > deviceSize.height.toFloat()) {
            (deviceSize.width.toFloat() / deviceSize.height.toFloat())
        } else {
            (deviceSize.height.toFloat() / deviceSize.width.toFloat())
        }

        if(deviceRatio < Constants.MIN_RATIO) {
            params?.width = ((deviceSize.width / Constants.MIN_RATIO) * widthRatio).toInt()
        } else {
            params?.width = (deviceSize.width * widthRatio).toInt()
        }
        alertDialog.window?.attributes = params
        return alertDialog
    }

    fun parseTypeToKorean(type : String) : String{
        return when {
            type.contains("main_img") -> {
                "대표사진"
            }
            type.contains("exterior_img") -> {
                "전경"
            }
            type.contains("interior_img") -> {
                "내부"
            }
            type.contains("room_img") -> {
                "진료실"
            }
            type.contains("surgery_img") -> {
                "수술실"
            }
            type.contains("recovery_img") -> {
                "회복실"
            }
            type.contains("rest_img") -> {
                "휴게실"
            }
            else -> {
                "주차장"
            }
        }
    }

    fun requestStoragePermission(context: Context, listener: PermissionListener){
        TedPermission.with(context)
            .setPermissionListener(listener)
            .setDeniedMessage("해당 기능을 이용하기 위해서는 저장공간에 대한 권한이 필요합니다.\n\n[설정] > [권한] 에서 권한을 허용할 수 있습니다.")
            .setPermissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE, //소변검사, 외형검사
                Manifest.permission.READ_EXTERNAL_STORAGE, //소변검사, 외형검사
            ).check()
    }
    fun getRealPathFromURIVersionQ(context: Context, contentUri: Uri?): FileDescriptor {
        val contentResolver: ContentResolver = context.contentResolver
        val pdf  = contentResolver.openFileDescriptor(contentUri!!, "r", null)
        return pdf!!.fileDescriptor
    }

    fun getRealPathFromURI(context: Context, contentUri: Uri?): String {
        var path : String? = null
        try{
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = context.contentResolver.query(contentUri!!, proj, null, null, null)
            cursor!!.moveToNext()
            path = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA))
            cursor.close()
        }catch (e: Exception){
            e.printStackTrace()
            Log.d("test","getRealPathFromURI error : ${e.message}")
        }
        return path!!
    }



    fun getDataColumn(
        context: Context,
        uri: Uri?,
        selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(
            column
        )
        try {
            cursor =
                context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val column_index = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(column_index)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    fun resizePicture(context: Context, uri: Uri?, resize: Int): Bitmap? {
        var resizeBitmap: Bitmap? = null
        val options = BitmapFactory.Options()
        try {
            BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri!!), null, options) // 1번
            var width = options.outWidth
            var height = options.outHeight
            var samplesize = 1
            while (true) {
                if (width / 2 < resize || height / 2 < resize) break
                width /= 2
                height /= 2
                samplesize *= 2
            }
            options.inSampleSize = samplesize
            val bitmap = BitmapFactory.decodeStream(
                context.contentResolver.openInputStream(
                    uri
                ), null, options
            )
            resizeBitmap = bitmap
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
        return resizeBitmap
    }

    fun rotateImage(source: Bitmap, angle: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }

    fun saveBitmapToJpeg(bitmap: Bitmap, name: String, context: Context): File {
        val storage: File = context.cacheDir
        val fileName = "$name.jpg"
        val tempFile = File(storage, fileName)
        try {
            tempFile.createNewFile()
            val out = FileOutputStream(tempFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return tempFile
    }

    fun parseDate00(date : String) : String {
        val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val formatter = SimpleDateFormat("yyyy.MM.dd")
        return formatter.format(parser.parse(date))
    }

    fun parseDateToDouble(date : String) : Double {
        val parser = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val formatter = SimpleDateFormat("yyyyMMddHHmmss")
        return formatter.format(parser.parse(date)).toDouble()
    }

    fun stringToBitmap(encodingStr : String) : Bitmap? {
        return try{
            val decodeByte = Base64.decode(encodingStr, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(decodeByte, 0, decodeByte.size)
        }catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun sleep(retryDelayTimemillis : Long) {
        Thread.sleep(retryDelayTimemillis)
    }
}

