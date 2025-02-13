package com.example.helpers

import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputLayout

class ClearErrorTextWatcher(
    private val textInputLayout: TextInputLayout,
    private val errorMessage: String
) : TextWatcher {

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        // Check if the value is empty and show an error if true
        if (s.isNullOrEmpty()) {
            // Show error message if text is empty
            textInputLayout.error = errorMessage
            textInputLayout.isErrorEnabled = true
        } else {
            // Clear the error if text is not empty
            textInputLayout.error = null
            textInputLayout.isErrorEnabled = false
        }
    }

    override fun afterTextChanged(s: Editable?) {}
}


