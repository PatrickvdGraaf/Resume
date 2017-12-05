package nl.graaf.patricksresume.views.projects.githubber.activity.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.EditText
import nl.graaf.patricksresume.BuildConfig
import nl.graaf.patricksresume.R

/**
 *
 * Created by Patrick van de Graaf on 12/4/2017.
 *
 */
class CredentialsDialog : DialogFragment() {
    private var mListener: ICredentialsDialogListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view: View = activity.layoutInflater.inflate(R.layout.dialog_credentials, null)
        val usernameEditText: EditText = view.findViewById(R.id.username_edittext)
        val passwordEditText: EditText = view.findViewById(R.id.password_edittext)

        usernameEditText.setText(arguments.getString("username"))
        passwordEditText.setText(arguments.getString("password"))

        if (BuildConfig.DEBUG && usernameEditText.text.toString().isEmpty()) {
            //TODO remove this soon
            usernameEditText.setText("PatrickvdGraaf")
            passwordEditText.setText("tlotrtbfme2")
        }

        val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
                .setView(view)
                .setTitle("Credentials")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Continue", { _: DialogInterface, _: Int ->
                    mListener?.onDialogPositiveClick(usernameEditText.text.toString(),
                            passwordEditText.text.toString())
                })
        return builder.create()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (activity is ICredentialsDialogListener) {
            mListener = activity as ICredentialsDialogListener
        } else {
            throw NotImplementedError("Activity must implement CredentialsDialog")
        }
    }
}