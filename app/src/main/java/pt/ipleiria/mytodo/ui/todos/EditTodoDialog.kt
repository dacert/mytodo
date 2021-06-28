package pt.ipleiria.mytodo.ui.todos

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import pt.ipleiria.mytodo.R
import pt.ipleiria.mytodo.models.Group
import pt.ipleiria.mytodo.shared.SharedUser
import pt.ipleiria.mytodo.ui.groups.EditGroupDialog
import pt.ipleiria.mytodo.ui.groups.viewModels.EditGroupDialogViewModel
import pt.ipleiria.mytodo.ui.todos.viewModels.EditTodoDialogViewModel


class EditTodoDialog : DialogFragment() {

    companion object {
        const val TAG = "EditTodoDialog"
        private const val KEY_TITLE = "KEY_TITLE"
        private const val KEY_GROUPID = "KEY_GROUPID"

        fun newInstance(title: String, groupId: String): EditTodoDialog {
            val args = Bundle()
            args.putString(KEY_TITLE, title)
            args.putString(KEY_GROUPID, groupId)
            val fragment = EditTodoDialog()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var viewModel: EditTodoDialogViewModel
    private lateinit var groupId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.edit_todo_dialog, container, false)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(EditTodoDialogViewModel::class.java)
        dialog!!.setCanceledOnTouchOutside(false)

        val titleText = view.findViewById<TextView>(R.id.dialog_tilte)
        val textEditText = view.findViewById<EditText>(R.id.todo_text)

        val saveButton = view.findViewById<Button>(R.id.save_todo)
        val cancelButton = view.findViewById<Button>(R.id.cancel_todo)

        val loadingProgressBar = view.findViewById<ProgressBar>(R.id.todo_loading)

        //set title
        titleText.text = arguments?.getString(KEY_TITLE)
        groupId = arguments?.getString(KEY_GROUPID).toString()

        viewModel.dataLoading.observe(this, Observer { isLoading ->
            if (isLoading == null) {
                return@Observer
            }
            loadingProgressBar.visibility = if(isLoading) View.VISIBLE else View.GONE
        })

        viewModel.toastMessage.observe(this, Observer {
            if(it.isNotEmpty()) Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        })

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

        val afterTextChangedListener = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // ignore
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                // ignore
            }

            override fun afterTextChanged(s: Editable) {
                viewModel.dataChanged(textEditText.text.toString())
            }
        }
        textEditText.addTextChangedListener(afterTextChangedListener)

        cancelButton.setOnClickListener {
            dismiss()
        }

        val onComplete = onComplete@ { isSuccess: Boolean ->
            if(isSuccess) {
                dismiss()
            }
            return@onComplete
        }

        saveButton.setOnClickListener {
            viewModel.add(
                groupId,
                textEditText.text.toString().trim(),
                onComplete)
        }
    }
}