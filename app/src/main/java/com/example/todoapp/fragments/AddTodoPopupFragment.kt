package com.example.todoapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.navigation.navArgument
import com.example.todoapp.R
import com.example.todoapp.databinding.FragmentAddTodoPopupBinding
import com.example.todoapp.utils.ToDoData
import com.google.android.material.textfield.TextInputEditText

class AddTodoPopupFragment : DialogFragment() {
    private lateinit var binding: FragmentAddTodoPopupBinding
    private lateinit var listener : DialogNextBtnClickListener
    private var todoData : ToDoData? = null

    fun setListener(listener: DialogNextBtnClickListener){
        this.listener = listener
    }

    companion object{
        const val TAG = "AddTodoPopupFragment"


        @JvmStatic
        fun newInstance(taskId:String, task:String) = AddTodoPopupFragment().apply {

           arguments = Bundle().apply {
                putString("taskId", taskId)
                putString("task", task)
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddTodoPopupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if ( arguments != null){
            todoData = ToDoData(
                arguments?.getString("taskId").toString(),
                arguments?.getString("task").toString())

            binding.todoEt.setText(todoData?.task)
        }
        registerEvent()
    }

    private fun registerEvent(){
        binding.todoNextBtn.setOnClickListener{
            val todoTask = binding.todoEt.text.toString().trim()
            if (todoTask.isNotEmpty()){
                if (todoData == null) {
                    listener.onSaveTask(todoTask, binding.todoEt)
                }
                else{
                    todoData?.task = todoTask
                    listener.onUpdateTask(todoData!!, binding.todoEt)
                }
            }else{
                Toast.makeText(context, "please type some task", Toast.LENGTH_SHORT).show()
            }
        }
        binding.todoClose.setOnClickListener{
            dismiss()
        }
    }

    interface DialogNextBtnClickListener{
        fun onSaveTask(todo : String, todoEt : TextInputEditText)
        fun onUpdateTask(todoData : ToDoData, todoEt : TextInputEditText)
    }

}