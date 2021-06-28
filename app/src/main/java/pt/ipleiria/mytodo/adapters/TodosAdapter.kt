package pt.ipleiria.mytodo.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pt.ipleiria.mytodo.adapters.viewHolders.TodoViewHolder
import pt.ipleiria.mytodo.base.OnItemClickListener
import pt.ipleiria.mytodo.databinding.TodoListItemBinding
import pt.ipleiria.mytodo.models.Todo

class TodosAdapter() : RecyclerView.Adapter<TodoViewHolder>() {
    var list: List<Todo> = emptyList()
    lateinit var itemClickListener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val dataBinding = TodoListItemBinding.inflate(inflater, parent, false)
        return TodoViewHolder(dataBinding)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.setup(list[position])
        holder.itemView.setOnClickListener { v -> itemClickListener.onClick(list[position])}
    }

    fun updateList(list: List<Todo>) {
        this.list = list
        notifyDataSetChanged()
    }
}