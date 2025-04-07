package com.example.playlistmaker.media.ui.playlists.create_playlist

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
import com.example.playlistmaker.media.domain.models.Playlist
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (arguments != null) {
            viewModel.getPlaylistById(requireArguments().getInt("id"))
        }

        fun editedPlaylistInflate(playlist: Playlist) {
            binding.saveButton.text = "Сохранить"
            binding.etNameText.setText(playlist.name)
            binding.etDescriptionText.setText(playlist.description)
            Glide.with(binding.cover)
                .load(playlist.coverPath)
                .placeholder(R.drawable.track_cover_placeholder)
                .transform(RoundedCorners(binding.cover.context.resources.getDimensionPixelSize(R.dimen.player_cover_corner_radius)))
                .centerCrop()
                .into(binding.cover)
        }

        viewModel.playlist.observe(viewLifecycleOwner) { playlist ->
            editedPlaylistInflate(playlist)
        }

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

        fun toast(status: String) {
            Toast.makeText(activity, "Плейлист ${viewModel.getName()} $status", Toast.LENGTH_LONG)
                .show()
        }

        // сохранение
        binding.saveButton.setOnClickListener {
            actualUri?.also { saveImageToPrivateStorage(it) }
            if (arguments == null) {
                viewModel.addPlaylist(
                    binding.etNameText.text.toString(),
                    binding.etDescriptionText.text.toString()
                )
            } else viewModel.updatePlaylist(
                binding.etNameText.text.toString(),
                binding.etDescriptionText.text.toString()
            )
            if (arguments == null) {
                toast("создан")
            } else toast("обновлён")
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
            if (arguments != null) finishFragment() else requireDialog()
        }
        callback.isEnabled
        binding.backButton.setOnClickListener {
            if (arguments != null) finishFragment() else requireDialog()
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
            .load(file.absolutePath)
            .placeholder(R.drawable.track_cover_placeholder)
            //.transform(RoundedCorners(binding.cover.context.resources.getDimensionPixelSize(R.dimen.player_cover_corner_radius)))
            .centerCrop()
            .into(binding.cover)

    }


}