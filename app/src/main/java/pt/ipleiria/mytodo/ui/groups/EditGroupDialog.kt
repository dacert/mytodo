package pt.ipleiria.mytodo.ui.groups

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import pt.ipleiria.mytodo.R
import pt.ipleiria.mytodo.shared.SharedUser
import pt.ipleiria.mytodo.ui.groups.viewModels.EditGroupDialogViewModel


class EditGroupDialog : DialogFragment() {

    companion object {
        const val TAG = "EditGroupDialog"
        private const val KEY_TITLE = "KEY_TITLE"

        fun newInstance(title: String): EditGroupDialog {
            val args = Bundle()
            args.putString(KEY_TITLE, title)
            val fragment = EditGroupDialog()
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var viewModel: EditGroupDialogViewModel
    lateinit var listener: EditGroupListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.edit_group_dialog, container, false)
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
        viewModel = ViewModelProvider(this).get(EditGroupDialogViewModel::class.java)
        dialog!!.setCanceledOnTouchOutside(false)

        val titleText = view.findViewById<TextView>(R.id.dialog_tilte)
        val ownerText = view.findViewById<TextView>(R.id.group_owner)
        val nameEditText = view.findViewById<EditText>(R.id.group_name)
        val membersEditText = view.findViewById<EditText>(R.id.group_members)
        val saveButton = view.findViewById<Button>(R.id.save_group)
        val cancelButton = view.findViewById<Button>(R.id.cancel_group)
        val loadingProgressBar = view.findViewById<ProgressBar>(R.id.group_loading)

        titleText.text = arguments?.getString(KEY_TITLE)

        ownerText.text = "Owner: ${SharedUser.email}"

        viewModel.formState.observe(this,
            Observer { state ->
                if (state == null) {
                    return@Observer
                }
                saveButton.isEnabled = state.isDataValid
                state.nameError?.let {
                    nameEditText.error = getString(it)
                }
                state.membersError?.let {
                    membersEditText.error = getString(it)
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
                viewModel.dataChanged(
                    nameEditText.text.toString(),
                    membersEditText.text.toString()
                )
            }
        }
        nameEditText.addTextChangedListener(afterTextChangedListener)
        membersEditText.addTextChangedListener(afterTextChangedListener)

        cancelButton.setOnClickListener {
            dismiss()
        }

        saveButton.setOnClickListener {
            loadingProgressBar.visibility = View.VISIBLE
            viewModel.add(
                nameEditText.text.toString().trim(),
                membersEditText.text.toString().trim()
            ) { isSuccess, error ->
                if(isSuccess) {
                    dismiss()
                } else {
                    val appContext = context?.applicationContext ?: return@add
                    Toast.makeText(appContext, error, Toast.LENGTH_LONG).show()
                }
                listener.onSaved(isSuccess)
            }
        }
    }

    interface EditGroupListener {
        fun onSaved(isSuccess: Boolean)
    }
}