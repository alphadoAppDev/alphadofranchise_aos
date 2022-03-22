package kr.co.alphadopetshop.ui.health_check.review

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gun0912.tedpermission.PermissionListener
import com.jakewharton.rxbinding4.widget.textChanges
import com.nguyenhoanglam.imagepicker.model.Config
import com.nguyenhoanglam.imagepicker.ui.imagepicker.ImagePicker
import com.nguyenhoanglam.imagepicker.widget.GridSpacingItemDecoration
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.kotlin.subscribeBy
import kr.co.alphadopetshop.BaseFragment
import kr.co.alphadopetshop.R
import kr.co.alphadopetshop.databinding.FragmentMakeReview01Binding
import kr.co.alphadopetshop.databinding.ItemReviewAddImgBinding
import kr.co.alphadopetshop.databinding.ItemReviewImgBinding
import kr.co.alphadopetshop.util.CommonUtils
import java.io.File
import java.io.IOException
import java.text.DecimalFormat


class MakeReviewFragment01 : BaseFragment(), ImageInsertAdapter.OnClickItem, ImageInsertAdapter.OnDeleteClickListener{
    private var binding : FragmentMakeReview01Binding? = null
    private var imageAdapter : ImageInsertAdapter? = null
    private val decimalFormat = DecimalFormat("#,###")

    companion object { @JvmStatic fun newInstance() = MakeReviewFragment01() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_make_review01, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setRxAction()
        setImageRecycler()
    }

    private fun setRxAction(){
        compositeDisposable.add(
            binding!!.editContent.textChanges()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy {
                    if(it.count() == 0) {
                        binding!!.txtCount.text = "0 / 1,000"
                        binding!!.layoutGuide.visibility = View.VISIBLE

                        (activity as MakeReviewActivity).inActivateButton()
                    } else {
                        binding!!.txtCount.text = "${decimalFormat.format(it.count())} / 1,000"
                        binding!!.layoutGuide.visibility = View.GONE
                        (activity as MakeReviewActivity).setReview(it.toString())

                        (activity as MakeReviewActivity).activateButton()
                    }
                }
        )
    }

    private fun setImageRecycler(){
        imageAdapter = ImageInsertAdapter(this, this)
        imageAdapter!!.addItem(null)
        binding!!.recyclerImg.addItemDecoration(
            kr.co.alphadopetshop.util.GridSpacingItemDecoration (4, 10, false))
        binding!!.recyclerImg.adapter = imageAdapter
    }

    fun checkValidReview() : Boolean{
        if(binding!!.editContent.text.toString().isNotEmpty()) {
            return true
        }

        return false
    }

    fun setRequestEditText(){
        binding!!.editContent.requestFocus()
        CommonUtils.showKeyboard(requireContext(), binding!!.editContent)
    }


    override fun onDestroy() {
        super.onDestroy()

        compositeDisposable.clear()
        binding = null
    }

    override fun onClickItem(position: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            CommonUtils.hideKeyboard(requireContext(), binding!!.editContent)
            val imagePicker = ImagePicker.with(this@MakeReviewFragment01)
                .setFolderMode(true)
                .setFolderTitle("앨범")
                .setRootDirectoryName(Config.ROOT_DIR_DCIM)
                .setDirectoryName("Image Picker")
                .setMultipleMode(true)
                .setShowNumberIndicator(true)
                .setMaxSize(3)
                .setLimitMessage(getString(R.string.review_upload_image_limit))
                .setRequestCode(100)

            imagePickerLaunch.launch(imagePicker.intent)
        } else {
            CommonUtils.requestStoragePermission(requireActivity().applicationContext, storagePermissionListener)
        }
    }

    val imagePickerLaunch = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode == Activity.RESULT_OK) {
            val images = ImagePicker.getImages(data = it.data)
            for(element in images) {
                if(imageAdapter!!.itemCount <= 3) {
                    var bmp = CommonUtils.resizePicture(requireContext(), element.uri, 100)

                    if(bmp != null){
                        try {
                            var ei : ExifInterface? = null
                            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                ei = ExifInterface(CommonUtils.getRealPathFromURIVersionQ(requireContext(), element.uri))
                            } else {
                                ei = ExifInterface(CommonUtils.getRealPathFromURI(requireContext(), element.uri))
                            }

                            val orientation = ei.getAttributeInt(
                                ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_NORMAL
                            )

                            when (orientation) {
                                ExifInterface.ORIENTATION_ROTATE_90 -> {
                                    bmp = CommonUtils.rotateImage(bmp, 90f)
                                }
                                ExifInterface.ORIENTATION_ROTATE_180 -> {
                                    bmp = CommonUtils.rotateImage(bmp, 180f)
                                }
                                ExifInterface.ORIENTATION_ROTATE_270 -> {
                                    bmp = CommonUtils.rotateImage(bmp, 270f)
                                }
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                        imageAdapter!!.addItem(bmp)
                    }
                }
            }
            imageAdapter!!.notifyDataSetChanged()
        }
    }

    private val storagePermissionListener = object : PermissionListener {
        override fun onPermissionGranted() {
            CommonUtils.hideKeyboard(requireContext(), binding!!.editContent)
            val imagePicker = ImagePicker.with(this@MakeReviewFragment01)
                .setFolderMode(true)
                .setFolderTitle("앨범")
                .setRootDirectoryName(Config.ROOT_DIR_DCIM)
                .setDirectoryName("Image Picker")
                .setMultipleMode(true)
                .setShowNumberIndicator(true)
                .setMaxSize(3)
                .setLimitMessage(getString(R.string.review_upload_image_limit))
                .setRequestCode(100)

            imagePickerLaunch.launch(imagePicker.intent)
        }

        override fun onPermissionDenied(deniedPermissions: MutableList<String>?) { }
    }

    override fun onDeleteClick(position: Int) {
        imageAdapter!!.removeItem(position)
        imageAdapter!!.notifyDataSetChanged()
    }

    fun getImagesFile() : ArrayList<File>  {
        val list = ArrayList<File>()

        for(i in 0 until imageAdapter!!.getItems().size) {
            if(imageAdapter!!.getItems()[i] != null)
                list.add(CommonUtils.saveBitmapToJpeg(imageAdapter!!.getItems()[i]!!, "review_image$i", requireContext()))
        }
        return list
    }
}


class ImageInsertAdapter(
    private val listener: OnClickItem,
    private val deleteListener : OnDeleteClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var items = ArrayList<Bitmap?>()
    private val TYPE_BUTTON = 0
    private val TYPE_PHOTO = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == TYPE_PHOTO) {
            var binding = DataBindingUtil.inflate<ItemReviewImgBinding>(
                LayoutInflater.from(parent.context),
                R.layout.item_review_img,
                parent,
                false
            )

//            var params = binding.root.layoutParams as GridLayoutManager.LayoutParams
//            val deviceSize = CommonUtils.getDeviceSize(binding.root.context)
//            params.width = (deviceSize.width / 4)
//            params.height = (deviceSize.width / 4)
//            params.leftMargin = 10
//            params.rightMargin = 10
//            params.topMargin = 20
//            params.bottomMargin = 20
//            binding.root.layoutParams = params

            return ReviewImageViewHolder(binding, deleteListener)
        } else {
            var binding = DataBindingUtil.inflate<ItemReviewAddImgBinding>(
                LayoutInflater.from(parent.context),
                R.layout.item_review_add_img,
                parent,
                false
            )

//            var params = binding.root.layoutParams as GridLayoutManager.LayoutParams
//            val deviceSize = CommonUtils.getDeviceSize(binding.root.context)
//            params.width = (deviceSize.width / 4)
//            params.height = (deviceSize.width / 4)
//            params.leftMargin = 10
//            params.rightMargin = 10
//            params.topMargin = 20
//            params.bottomMargin = 20
//            binding.root.layoutParams = params
            return ReviewAddImgViewHolder(binding, listener)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(getItemViewType(position) == TYPE_PHOTO)
            (holder as ReviewImageViewHolder).bind(items[position])
        else
            (holder as ReviewAddImgViewHolder).bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun addItem(bmp: Bitmap?) {
        if(bmp == null) {
            items.add(null)
        } else{
            items.add(bmp)
        }
    }

    fun removeItem(position: Int) {
        items.removeAt(position)
    }


    fun getItems(): ArrayList<Bitmap?> {
        return items
    }

    override fun getItemViewType(position: Int): Int {
        return when(items[position]) {
            null -> {
                TYPE_BUTTON
            }
            else -> {
                TYPE_PHOTO
            }
        }
    }


    class ReviewImageViewHolder(
        private val binding: ItemReviewImgBinding,
        private val deleteListener : OnDeleteClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Bitmap?){
            if (item != null) {
//                binding.imgPhoto.setImageURI(Uri.parse(item))
                binding.imgPhoto.setImageBitmap(item)
                binding.btnDelete.setOnClickListener {
                    deleteListener.onDeleteClick(adapterPosition)
                }
            }
        }
    }

    class ReviewAddImgViewHolder(
        private val binding: ItemReviewAddImgBinding,
        private val listener: OnClickItem,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Bitmap?){
            binding.topView.setOnClickListener { listener.onClickItem(adapterPosition) }
        }
    }
    interface OnClickItem {
        fun onClickItem(position: Int)
    }
    interface OnDeleteClickListener{
        fun onDeleteClick(position: Int)
    }
}