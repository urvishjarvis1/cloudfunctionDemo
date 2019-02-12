package com.example.cloudfunctiondemo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*

/**
 * MainActivity: This is central activity for this application.
 * @FireBaseUser for user authentication.
 * @FireDataBase for stroing details of user mainly stores emails
 */
class MainActivity : AppCompatActivity() {
    val SIGNIN_CODE = 1;
    var user: FirebaseUser? = null
    val database = FirebaseDatabase.getInstance()
    var userRef: DatabaseReference? = null
    var userData: userdata? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //taking current user from firebase
        user = FirebaseAuth.getInstance().currentUser
        //@providers for providing user multiple way to sign-up for applicaton
        //email/password and Gmail login
        val providers = arrayListOf(AuthUI.IdpConfig.EmailBuilder().build(), AuthUI.IdpConfig.GoogleBuilder().build())
        //Initally user will be null
        if (user == null) {
            //asking user to signup for first time
            startActivityForResult(
                AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build(),
                SIGNIN_CODE
            )
            loginbtn.text = resources.getString(R.string.logout)
        }
        //if user already logged in
        if (user != null) {
            //show the checkbox
            checkbox.visibility = View.VISIBLE

            val userRef1 = database.getReference("users")
            userRef = userRef1.child(user!!.uid)
            loginbtn.text = resources.getString(R.string.logout)
        }

        checkbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Log.d("Checkbox", "here in true")
                userRef?.child("subscribedToMailingList")?.setValue(isChecked)

            } else {
                Log.d("Checkbox", "here in false")
                userRef?.child("subscribedToMailingList")?.setValue(isChecked)

            }
        }

        loginbtn.setOnClickListener {
            if (user == null)
                startActivityForResult(
                    AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build(),
                    SIGNIN_CODE
                )
            else {
                AuthUI.getInstance().signOut(this).addOnCompleteListener {
                    Toast.makeText(this, "Log out successfully ", LENGTH_LONG).show()
                    user = null
                    loginbtn.text = resources.getString(R.string.login)
                    userRef = null
                    checkbox.visibility = View.INVISIBLE
                }.addOnCanceledListener {
                    Toast.makeText(this, "Not able to logout from server ", LENGTH_LONG).show()
                }
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SIGNIN_CODE) {
            val res = IdpResponse.fromResultIntent(data)
            Log.d("res", res.toString())
            if (resultCode == Activity.RESULT_OK) {
                //Successful login/signup
                user = FirebaseAuth.getInstance().currentUser
                //stroing user data to fire base
                val userRef1 = database.getReference("users")
                userRef = userRef1.child(user!!.uid)
                userData = userdata(false, user!!.email)
                userRef!!.setValue(userData)
                getDataFromFirebase()
                loginbtn.text = resources.getString(R.string.logout)
                checkbox.visibility = View.VISIBLE
            } else {
                loginbtn.text = resources.getString(R.string.login)
                Toast.makeText(this, "Signin Failed press login button to cotinue!", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onStop() {
        super.onStop()
    }

    /**
     * getting data from firebase cloud
     */
    fun getDataFromFirebase() {
        if (userRef != null) {
            //setting value listener
            userRef!!.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    Log.w("CheckBox", "Failed to read value.", p0.toException())
                }

                override fun onDataChange(p0: DataSnapshot) {
                    Log.d("Checkbox", p0.value.toString())
                    val userdata = p0.getValue(userdata::class.java)
                    checkbox.isChecked = userdata!!.subscribedToMailingList!!
                    Log.d("checkbox", "" + userdata.subscribedToMailingList!!)
                }

            })
        }
    }
}
