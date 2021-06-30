package pt.ipleiria.mytodo.ui.todos

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.Observer
import pt.ipleiria.mytodo.R
import pt.ipleiria.mytodo.base.EditDialog
import pt.ipleiria.mytodo.dataLayer.models.Base
import pt.ipleiria.mytodo.dataLayer.models.Todo
import pt.ipleiria.mytodo.shared.SharedUser
import pt.ipleiria.mytodo.ui.todos.viewModels.EditTodoDialogViewModel


class EditTodoDialog : EditDialog<EditTodoDialogViewModel>(EditTodoDialogViewModel::class.java) {

    companion object {
        const val TAG = "EditTodoDialog"
        private const val KEY_GROUPID = "KEY_GROUPID"

        fun newInstance(title: String, groupId: String, data: Base?): EditTodoDialog {
            val args = Bundle()
            args.putString(KEY_TITLE, title)
            args.putSerializable(KEY_DATA, data)
            args.putString(KEY_GROUPID, groupId)

            val fragment = EditTodoDialog()
            fragment.arguments = args
            return fragment
        }
    }

    private var todo: Todo? = null
    private lateinit var textEditText: EditText
    private lateinit var groupId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.edit_todo_dialog, container, false)
    }

    override fun onInit(view: View) {
        textEditText = view.findViewById(R.id.todo_text)

        groupId = arguments?.getString(KEY_GROUPID).toString()
        todo = arguments?.getSerializable(KEY_DATA) as Todo?

        textEditText.setText(todo?.text ?: "")
        textEditText.isEnabled = isNew() || isOwner()


        viewModel.formState.observe(this,
            Observer { state ->
                if (state == null) {
                    return@Observer
                }
                saveButton.isEnabled = state.isDataValid
                state.textError?.let {
                    textEditText.error = getString(it)
                }
            })

        textEditText.addTextChangedListener(afterTextChangedListener)

        val onComplete = onComplete@ { isSuccess: Boolean ->
            if(isSuccess) {
                dismiss()
            }
            return@onComplete
        }

        saveButton.setOnClickListener {
            if(isNew()){
                viewModel.add(
                    groupId,
                    textEditText.text.toString().trim(),
                    onComplete)
            } else {
                viewModel.edit(
                    groupId, todo!!.id,
                    textEditText.text.toString().trim(),
                    onComplete)
            }
        }
    }

    override fun onRemove() {
        viewModel.remove(groupId, todo!!.id){ isSuccess ->
            if(isSuccess) {
                dismiss()
            }
        }
    }

    override fun afterTextChanged(s: Editable) {
        viewModel.dataChanged(textEditText.text.toString())
    }

    override fun isOwner(): Boolean {
        val data = arguments?.getSerializable(KEY_DATA) as Todo?
        return data?.by == SharedUser.email
    }

}