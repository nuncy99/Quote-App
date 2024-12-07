package com.example.quoteapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import com.example.quoteapp.Quote as Quote

class MainActivity : AppCompatActivity() {


    private lateinit var recyclerView: RecyclerView

    private lateinit var myRef: DatabaseReference
    private lateinit var quoteText: EditText
    private lateinit var saveBtn: Button
    private lateinit var fabAdd: FloatingActionButton
    private lateinit var quoteAdapter: AdapterClass

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_main)



        fabAdd = findViewById(R.id.fabAdd)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        // recyclerView.setHasFixedSize(true)

        val database = Firebase.database
        myRef = database.getReference("quotes")

        getQuotes()

        fabAdd.setOnClickListener {
            showDialog()
        }
    }

    private fun showDialog() {
        // Create an AlertDialog builder
        val builder = AlertDialog.Builder(this)

        // Inflate the custom layout with EditText and Button
        val dialogLayout = layoutInflater.inflate(R.layout.custom_alert_layout, null)
        quoteText = dialogLayout.findViewById(R.id.quoteText)
        saveBtn = dialogLayout.findViewById(R.id.saveBtn)

        // Set the custom layout as the AlertDialog view
        builder.setView(dialogLayout)

        // Create and display the AlertDialog
        val dialog = builder.create()

        // Find and set an OnClickListener to the Button in the custom layout
        saveBtn.setOnClickListener {
            // Get the text from EditText

            val quotes = Quote(
                quote = quoteText.text.toString()
            )
            // Display the text in a Toast
            Toast.makeText(this, "Your Quote has been added", Toast.LENGTH_SHORT).show()
            addQuote(quotes)
            // Dismiss the dialog
            dialog.dismiss()
        }
        dialog.show()

    }

    private fun addQuote(quotes: Quote) {
        val taskId = myRef.push().key
        quotes.id = taskId
        myRef.child(taskId!!).setValue(quotes)
            .addOnSuccessListener {
                Toast.makeText(this, "Quote added successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{

            Toast.makeText(this, "Failed to create user.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getQuotes() {

        //set Recycler View
        //val quotes = mutableListOf<Quote>()

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val quotes = mutableListOf<Quote>()
                for (taskSnapshot in snapshot.children) {
                    val quote = taskSnapshot.getValue(Quote::class.java)
                    if (quote != null) {
                        quotes.add(quote)
                    }
                }
                quoteAdapter=AdapterClass(quotes,
                    onClick = { selectedQuote ->
                        val intent = Intent(this@MainActivity, displayActivity::class.java)
                        intent.putExtra("quoteId", selectedQuote!!.id)
                        startActivity(intent)
                })
                recyclerView.adapter=quoteAdapter

            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}