package com.vvvlad42.amusetime.not_used

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore.getInstance


class FirestoreMainActivityTest : AppCompatActivity() {


    private val TAG = "TestFirestore"
    private lateinit var mAuth:FirebaseAuth


    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = mAuth.currentUser
        if (currentUser!=null)
            Toast.makeText(this,"User Is logged in", Toast.LENGTH_LONG).show()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_snippets)

//        val database = FirebaseDatabase.getInstance()
        val db = getInstance()
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance()




        mAuth.signInWithEmailAndPassword("vvvlad42@gmail.com", "pla635").addOnCompleteListener{
            task: Task<AuthResult> ->
                if (task.isSuccessful){
                    Toast.makeText(this,"User Is logged in now", Toast.LENGTH_LONG).show()

                    val user2 = hashMapOf(
                        "first" to "Vlad",
                        "middle" to "The",
                        "last" to "Vetsh",
                        "born" to 1979
                    )
                    // Add a new document with a generated ID
                    db.collection("users")
                        .add(user2)
                        .addOnSuccessListener { documentReference ->
                            Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id} ")
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error adding document", e)
                        }

                    db.collection("users")
                        .get()
                        .addOnSuccessListener { result ->
                            for (document in result) {
                                Log.d(TAG, "${document.id} => ${document.data}")
                            }
                        }
                        .addOnFailureListener { exception ->
                            Log.w(TAG, "Error getting documents.", exception)
                        }

                }
                else{
                    Toast.makeText(this,"User failed to login", Toast.LENGTH_LONG).show()
                }
        }

        // Create a new user with a first and last name
//        val user = hashMapOf(
//            "first" to "Ada",
//            "last" to "Lovelace",
//            "born" to 1815
//        )



    }
}
