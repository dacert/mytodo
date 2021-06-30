package pt.ipleiria.mytodo.adapters.viewHolders

import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import pt.ipleiria.mytodo.BR
import pt.ipleiria.mytodo.R
import pt.ipleiria.mytodo.adapters.viewHolders.helpers.TodoViewHolderHelper.isByCurrentUser
import pt.ipleiria.mytodo.dataLayer.models.Todo
import java.text.SimpleDateFormat

class TodoViewHolder constructor(private val dataBinding: ViewDataBinding)
    : RecyclerView.ViewHolder(dataBinding.root) {

    fun setup(data: Todo) {
        dataBinding.setVariable(BR.data, data)
        dataBinding.executePendingBindings()

        if(data.timestamp != null){
            val simpleDateFormat = SimpleDateFormat("dd.LLL.yyyy HH:mm:ss")
            val dateTime = simpleDateFormat.format(data.timestamp!!).toString()
            itemView.findViewById<TextView>(R.id.todo_list_item_timestamp).text = dateTime
        }

        val color = if(!isByCurrentUser(data)) R.color.gray_white_light else R.color.white
        itemView.findViewById<ConstraintLayout>(R.id.todo_list_item).setBackgroundResource(color)
    }
}