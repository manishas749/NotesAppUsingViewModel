package com.example.notes_app.showitemrecycleview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.notes_app.datamodel.Notes
import com.example.notes_app.viewmodel.NotesViewModel
import com.example.notesappusingviewmodel.databinding.FragmentDetailNoteBinding

class Detail_Note : Fragment() {
    lateinit var binding: FragmentDetailNoteBinding
    lateinit var notesViewModel: NotesViewModel
    var adapterPosition: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailNoteBinding.inflate(inflater, container, false)
        notesViewModel = ViewModelProvider(this).get(NotesViewModel::class.java)
        val titleNote = requireArguments().getString("title")
        val descriptionNote = requireArguments().getString("description")
        val position = requireArguments().getInt("id")
        binding.textviewTitleDetail.setText(titleNote)
        binding.textviewDescriptionDetail.setText(descriptionNote)
        adapterPosition=position
        binding.btnEdit.setOnClickListener()
        {
            editData()
            onDestroy()
            activity?.supportFragmentManager?.popBackStack()

        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun editData() {
        val noteTitle = binding.textviewTitleDetail.text.toString()
        val noteDescription = binding.textviewDescriptionDetail.text.toString()
        if (inputCheck(noteTitle, noteDescription)) {
            val notesUpdate = Notes(adapterPosition, noteTitle, noteDescription)
            notesViewModel.updateNotes(notesUpdate)
            android.widget.Toast.makeText(
                requireContext(),
                "Updated",
                android.widget.Toast.LENGTH_SHORT
            ).show()
        } else {
            android.widget.Toast.makeText(
                requireContext(),
                "Title is Empty",
                android.widget.Toast.LENGTH_SHORT
            ).show()

        }
    }

    private fun inputCheck(noteTitle: String, noteDescription: String): Boolean {
        return !(android.text.TextUtils.isEmpty(noteTitle) && android.text.TextUtils.isEmpty(
            noteDescription
        ))


    }
}

