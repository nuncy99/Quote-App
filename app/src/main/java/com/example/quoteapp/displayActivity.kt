package com.example.quoteapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.test.isEditable
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class displayActivity : AppCompatActivity() {

    private lateinit var myRef: DatabaseReference
    private lateinit var quoteFill: EditText
    private lateinit var editBtn: Button
    private lateinit var deleteBtn: Button
    private val TAG = "FirebaseLogg"
    private var isEditable = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_display)

        val quoteId: String = intent.getStringExtra("quoteId") ?: "xyz"
        val database = Firebase.database
        myRef = database.getReference("quotes")
        quoteFill = findViewById(R.id.quoteText)
        editBtn = findViewById(R.id.editBtn)
        deleteBtn = findViewById(R.id.deleteBtn)
        // quoteFill.setText(quoteId)

        val quote = Quote(id = quoteId, quote = quoteFill.text.toString())

        getQuoteById(quoteId) { quote ->
            if (quote != null) {
                quoteFill.setText(quote.quote)
            } else {
                Log.d(TAG, "Quote not found")
            }
        }

        editBtn.setOnClickListener {
            if (isEditable) {
                if (quoteFill.text.isNotEmpty()) {
                    val quote = Quote(
                        id = quoteId,
                        quote = quoteFill.text.toString()
                    )
                    updateQuote(quote)
                }
            } else {
                isEditable = true
                editBtn.setText("SAVE")
                quoteFill.isEnabled = true
            }
        }
        deleteBtn.setOnClickListener {
            deleteConfirmationDialog(quoteId)

        }
    }


    private fun updateQuote(quote: Quote) {
        quote.id?.let { quoteId ->
            myRef.child(quoteId).setValue(quote)
                .addOnSuccessListener {
                    Log.d(TAG, "Quote updated Successfully")
                    Toast.makeText(this, "Quote updated successfully", Toast.LENGTH_SHORT)
                    quoteFill.isEnabled = false
                    isEditable = false
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Quote not updated", e)

                }
        }

    }

    private fun getQuoteById(quoteId: String, onResult: (Quote?) -> Unit) {
        myRef.child(quoteId).get()
            .addOnSuccessListener { dataSnapshot ->
                val result = dataSnapshot.getValue(Quote::class.java)
                onResult(result)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error fetching Quote by ID", exception)
                onResult(null)
            }

    }

    private fun deleteConfirmationDialog(quoteId: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Quote")
        builder.setMessage("Are you sure you want to delete this quote")
        builder.setPositiveButton("OK") { dialogInterface, _ ->
            deleteQuote(quoteId)
            dialogInterface.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialogInterface, _ ->
            dialogInterface.dismiss()

        }
        builder.setCancelable(false)
        val alertDialog = builder.create()
        alertDialog.show()


    }

    private fun deleteQuote(quoteId: String) {
        myRef.child(quoteId).removeValue()
            .addOnSuccessListener {
                Log.d(TAG, "Quote deleted successfully")
                Toast.makeText(this, "Quote deleted Successfully!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to delete Quote", e)
            }
    }
}