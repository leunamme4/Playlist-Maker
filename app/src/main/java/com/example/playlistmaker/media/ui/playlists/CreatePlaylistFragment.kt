package com.example.playlistmaker.media.ui.playlists

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.example.playlistmaker.player.ui.PlayerActivity
import com.example.playlistmaker.root.RootActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class CreatePlaylistFragment : Fragment() {

    private var _binding: FragmentCreatePlaylistBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModel<CreatePlaylistViewModel>()

    private var actualUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //регистрируем событие, которое вызывает photo picker
        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                //обрабатываем событие выбора пользователем фотографии
                if (uri != null) {
                    binding.cover.apply { scaleType = ImageView.ScaleType.FIT_CENTER }
                        .setImageURI(uri)
                    actualUri = uri
                }
            }

        //по нажатию на кнопку pickImage запускаем photo picker
        binding.cover.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        //по нажатию на кнопку loadImageFromStorage пытаемся загрузить фотографию из нашего хранилища
//        binding.loadImageFromStorage.setOnClickListener {
//            val filePath = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum")
//            val file = File(filePath, "first_cover.jpg")
//            binding.storageImage.setImageURI(file.toUri())
//        }

        // сохранение
        binding.saveButton.setOnClickListener {
            actualUri?.also { saveImageToPrivateStorage(it) }
            viewModel.addPlaylist(
                binding.etNameText.text.toString(),
                binding.etDescriptionText.text.toString()
            )
            Toast.makeText(activity, "Плейлист ${viewModel.getName()} создан", Toast.LENGTH_LONG)
                .show();
            finishFragment()
        }

        // name
        val nameTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                binding.saveButton.isEnabled = !s.isNullOrEmpty()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.saveButton.isEnabled = !s.isNullOrEmpty()
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        binding.etNameText.addTextChangedListener(nameTextWatcher)


        // обработка нажатия на кнопку назад
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            requireDialog()
        }
        callback.isEnabled
        binding.backButton.setOnClickListener {
            requireDialog()
        }
    }

    private fun requireDialog() {
        if (actualUri != null || !binding.etNameText.text.isNullOrEmpty() || !binding.etDescriptionText.text.isNullOrEmpty()) {
            MaterialAlertDialogBuilder(this@CreatePlaylistFragment.requireContext())
                .setTitle("Завершить создание плейлиста?")
                .setNeutralButton("Отмена") { dialog, which ->
                    // ничего не делаем
                }.setNegativeButton("Завершить") { dialog, which ->
                    finishFragment()
                }.show()
        } else finishFragment()
    }

    private fun finishFragment() {
        if (requireActivity() is RootActivity) {
            findNavController().popBackStack()
        } else parentFragmentManager.popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? PlayerActivity)?.returnFromCreatePlaylist()
        (activity as? RootActivity)?.showNavigationBar()
    }

    private fun saveImageToPrivateStorage(uri: Uri) {
        val filePath = File(
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "playlist_covers"
        )
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val file = File(filePath, "${uri.lastPathSegment}.jpg")
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)

        viewModel.addCover(file.absolutePath)

        Glide.with(binding.cover)
            .load(uri)
            .centerCrop()
            .placeholder(R.drawable.placeholder_player_light)
            .transform(RoundedCorners(binding.cover.context.resources.getDimensionPixelSize(R.dimen.player_cover_corner_radius)))
            .into(binding.cover)


    }


}