package com.example.todoapp.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.example.todoapp.databinding.EachTodoItemBinding

class TodoAdapter(private val list : MutableList<ToDoData>) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>(){

    private var listener : TaskTodoAdapterClicksInterface? = null
    fun setListener(listener: TaskTodoAdapterClicksInterface){
        this.listener = listener
    }

    inner class TodoViewHolder(val binding: EachTodoItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val binding = EachTodoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TodoViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        with(holder){
            with(list[position]){
                binding.todoTask.text = this.task

                binding.deleteTask.setOnClickListener{
                    listener?.onDeleteTaskButtonClick(this)
                }

                binding.editTask.setOnClickListener{
                    listener?.onEditTaskButtonClick(this)
                }
            }
        }
    }

    interface TaskTodoAdapterClicksInterface{
        fun onDeleteTaskButtonClick(toDoData: ToDoData)
        fun onEditTaskButtonClick(toDoData: ToDoData)
    }

}