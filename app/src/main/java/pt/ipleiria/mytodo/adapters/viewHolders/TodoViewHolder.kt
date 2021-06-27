package pt.ipleiria.mytodo.adapters.viewHolders

import android.widget.LinearLayout
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import pt.ipleiria.mytodo.BR
import pt.ipleiria.mytodo.R
import pt.ipleiria.mytodo.models.Todo
import pt.ipleiria.mytodo.shared.SharedUser

class TodoViewHolder constructor(private val dataBinding: ViewDataBinding)
    : RecyclerView.ViewHolder(dataBinding.root) {

    fun setup(data: Todo) {
        dataBinding.setVariable(BR.data, data)
        dataBinding.executePendingBindings()

        if(data.by != SharedUser.email)
            itemView.findViewById<LinearLayout>(R.id.todo_list_item)
            .setBackgroundResource(R.color.gray_white_light)
    }
}