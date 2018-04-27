package net.north101.android.ghplayertracker

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import org.androidannotations.annotations.AfterViews
import org.androidannotations.annotations.EBean
import org.androidannotations.annotations.ViewById

@EBean
abstract class EditNumberDialog : DialogFragment() {
    private lateinit var view2: View

    @ViewById(R.id.text)
    protected lateinit var textView: EditText

    abstract val title: String
    abstract var value: Int

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context!!)
        val inflater = activity!!.layoutInflater
        view2 = inflater.inflate(R.layout.edit_number_layout, null as ViewGroup?)

        builder.setView(view2)
                .setTitle(title)
                .setPositiveButton("OK", null)
                .setNegativeButton("CANCEL") { dialog, id ->
                    this@EditNumberDialog.dialog.cancel()
                }

        return builder.create()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialog.setOnShowListener { dialog ->
            val button = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
            button.setOnClickListener {
                textView.error = null
                value = try {
                    Integer.parseInt(textView.text.toString())
                } catch (e: Exception) {
                    textView.error = "Invalid Number"
                    return@setOnClickListener
                }

                dialog.dismiss()
            }
        }
        return view2
    }

    @AfterViews
    open fun afterViews() {
        textView.setText(value.toString())
    }
}