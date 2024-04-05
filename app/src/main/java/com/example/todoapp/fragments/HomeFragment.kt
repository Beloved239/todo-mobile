package com.example.todoapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.example.todoapp.databinding.FragmentHomeBinding
import com.example.todoapp.utils.ToDoData
import com.example.todoapp.utils.TodoAdapter
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment(), AddTodoPopupFragment.DialogNextBtnClickListener,
    TodoAdapter.TaskTodoAdapterClicksInterface {
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseRef :DatabaseReference
    private lateinit var navControl : NavController
    private lateinit var binding: FragmentHomeBinding
    private var popupFragment: AddTodoPopupFragment? = null
    private lateinit var todoAdapter: TodoAdapter
    private lateinit var mList : MutableList<ToDoData>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        getDataFromFirebase()
        registerEvent()
    }

    private fun registerEvent(){
        binding.addBtnHome.setOnClickListener{
            if(popupFragment != null){
                childFragmentManager.beginTransaction().remove(popupFragment!!).commit()
            }
            popupFragment = AddTodoPopupFragment()
            popupFragment!!.setListener(this)
            popupFragment!!.show(
                childFragmentManager,
                "AddTodoPopupFragment"
            )

        }
    }

    private fun init(view: View){
        navControl = Navigation.findNavController(view)
        auth = FirebaseAuth.getInstance()
        databaseRef = FirebaseDatabase.getInstance().reference.child("Tasks").child(auth.currentUser?.uid.toString())

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        mList = mutableListOf()

        todoAdapter = TodoAdapter(mList)
        todoAdapter.setListener(this)
        binding.recyclerView.adapter = todoAdapter
    }

    private fun getDataFromFirebase(){
        databaseRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                mList.clear()
                for (taskSnapShot in snapshot.children){
                    val todoTask = taskSnapShot.key?.let {
                        ToDoData(it, taskSnapShot.value.toString())
                    }

                    if (todoTask != null){
                        mList.add(todoTask)
                    }
                }
                todoAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
            }

        })


    }

    override fun onSaveTask(todo: String, todoEt: TextInputEditText) {
        databaseRef.push().setValue(todo).addOnCompleteListener{
            if (it.isSuccessful){
                Toast.makeText(context, "Task saved successfully", Toast.LENGTH_SHORT).show()
                todoEt.text = null
            }else{
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()
            }

            popupFragment?.dismiss()
        }
    }

    override fun onDeleteTaskButtonClick(toDoData: ToDoData) {
        databaseRef.child(toDoData.taskId).removeValue().addOnCompleteListener{
            if (it.isSuccessful) {
                Toast.makeText(context, "Deleted successfully", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context, it.exception?.message, Toast.LENGTH_SHORT).show()

            }
        }
    }

    override fun onEditTaskButtonClick(toDoData: ToDoData) {
        if (popupFragment != null)
            childFragmentManager.beginTransaction().remove(popupFragment!!).commit()
        popupFragment = AddTodoPopupFragment.newInstance(toDoData.taskId, toDoData.task)
        popupFragment?.setListener(this)
    }


    override fun onUpdateTask(todoData: ToDoData, todoEt: TextInputEditText) {
        TODO("Not yet implemented")
    }


}