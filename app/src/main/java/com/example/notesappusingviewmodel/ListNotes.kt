package com.example.notesappusingviewmodel

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.notes_app.addnote.AddNotes
import com.example.notes_app.showitemrecycleview.Detail_Note
import com.example.notes_app.viewmodel.NotesViewModel
import com.example.notesappusingviewmodel.adapter.NotesAdapter
import com.example.notesappusingviewmodel.adapter.notesList
import com.example.notesappusingviewmodel.databinding.FragmentListNotesBinding

private lateinit var notesViewModel: NotesViewModel




class ListNotes : Fragment(), SearchView.OnQueryTextListener {
    lateinit var binding:FragmentListNotesBinding
    val notesAdapter= NotesAdapter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        val inflater = TransitionInflater.from(requireContext())
        enterTransition = inflater.inflateTransition(R.transition.slide_right)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{
        binding= FragmentListNotesBinding.inflate(inflater,container,false)
        (activity as AppCompatActivity).setSupportActionBar(binding.toolBar)
        (activity as AppCompatActivity).supportActionBar?.show()
        binding.rcycle.layoutManager = LinearLayoutManager(context)
        binding.rcycle.adapter = notesAdapter
        notesViewModel = ViewModelProvider(this).get(NotesViewModel::class.java)
        notesViewModel.readAllNotes.observe(viewLifecycleOwner, Observer{ notes ->
            notesAdapter.setData(notes)
        })
        setRecyclerViewItemTouchListener()
        setAdapterOnClickListener()
        return binding.root
    }
    private fun setAdapterOnClickListener() {
        notesAdapter.setOnClickListener(object:NotesAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                val title= notesList[position].title
                val description= notesList[position].description
                val position:Int= notesList[position].id
                val bundle = Bundle()
                bundle.putString("title", title)
                bundle.putString("description", description)
                bundle.putInt("id",position)
                activity?.supportFragmentManager?.commit {
                    setReorderingAllowed(true)
                    hide(activity?.supportFragmentManager?.findFragmentByTag("main")!!)
                    add<Detail_Note>(R.id.fragmentContainer, args = bundle)
                    addToBackStack(null)
                }

            }

            override fun onItemClick(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

            }

        } )

    }

    private fun setRecyclerViewItemTouchListener() {
        val itemTouchCallBack=object : ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
        {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position=viewHolder.absoluteAdapterPosition
                notesViewModel.deleteNotes(notesAdapter.getNoteAt(viewHolder.absoluteAdapterPosition))
                binding.rcycle.adapter!!.notifyItemRemoved(position)
                Toast.makeText(requireContext(),"Note Deleted", Toast.LENGTH_SHORT).show()

            }

        }
        val itemTouchHelper= ItemTouchHelper(itemTouchCallBack)
        itemTouchHelper.attachToRecyclerView(binding.rcycle)

    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.floatingAddNoteButton.setOnClickListener()
        {
            activity?.supportFragmentManager?.commit {
                setCustomAnimations(
                    R.anim.slide_in,
                    R.anim.fade_out,
                    R.anim.fade_in,
                    R.anim.slide_out
                )

                setReorderingAllowed(true)
                hide(activity?.supportFragmentManager?.findFragmentByTag("main")!!)
                add(R.id.fragmentContainer, AddNotes())
                addToBackStack(null)

            }
        }

    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.search_menu, menu)
        val menuItem = menu?.findItem(R.id.search)
        val searchView = menuItem.actionView as SearchView
        searchView?.isSubmitButtonEnabled=true
        searchView.setOnQueryTextListener(this)
    }
    override fun onQueryTextSubmit(query: String?): Boolean {
        if(query!= null)
        {
            searchData(query)
        }
        return true
    }

    override fun onQueryTextChange(query: String?): Boolean {
        if(query!= null)
        {
            searchData(query)
        }
        return true
    }

    private fun searchData(query: String)
    {
        val searchQuery= "%$query%"
        notesViewModel.searchDatabase(searchQuery).observe(this) { list ->
            list.let {
                notesAdapter.setData(it)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.finish()
    }


}






