package com.example.loginapp

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.*

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private companion object
    {
        private const val TAG="MainActivity"
        private const val RC_GOOGLE_SIGN_IN=4921
    }
    // Initialize Firebase Auth
    private lateinit var auth: FirebaseAuth
    var FirebaseAuth:FirebaseAuth?=null

    lateinit var callbackManager:CallbackManager
    var id = ""
    var firstName = ""
    var middleName = ""
    var lastName = ""
    var name = ""
    var picture = ""
    var email = ""
    var accessToken = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        auth = Firebase.auth
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val client:GoogleSignInClient= GoogleSignIn.getClient(this, gso)

        val btnSignIn: SignInButton =findViewById(R.id.loginG)
        btnSignIn.setOnClickListener{
            val signInIntent=client.signInIntent
            startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN)
        }

        FirebaseAuth= com.google.firebase.auth.FirebaseAuth.getInstance()

        callbackManager= CallbackManager.Factory.create()

        val facebook_login_btn: Button =findViewById(R.id.loginF)
        facebook_login_btn.setOnClickListener {
            LoginManager.getInstance().logInWithReadPermissions(this, listOf("public_profile", "email"))
        }
        // Callback registration
        LoginManager.getInstance().registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
            override fun onSuccess(loginResult: LoginResult?) {
                Log.d("TAG", "Success Login")
                // Get User's Info
                getUserProfile(loginResult?.accessToken, loginResult?.accessToken?.userId)
            }

            override fun onCancel() {
                Toast.makeText(this@MainActivity, "Login Cancelled", Toast.LENGTH_LONG).show()
            }

            override fun onError(exception: FacebookException) {
                Toast.makeText(this@MainActivity, exception.message, Toast.LENGTH_LONG).show()
            }
        })
        if (isLoggedIn()) {
            Log.d("LoggedIn? :", "YES")
        }
        else {
            Log.d("LoggedIn? :", "NO")
        }
    }

    @SuppressLint("LongLogTag")
    fun getUserProfile(token: AccessToken?, userId: String?) {

        val parameters = Bundle()
        parameters.putString(
                "fields",
                "id, first_name, middle_name, last_name, name, picture, email"
        )
        GraphRequest(token,
                "/$userId/",
                parameters,
                HttpMethod.GET,
                GraphRequest.Callback { response ->
                    val jsonObject = response.jsonObject

                    // Facebook Access Token
                    // You can see Access Token only in Debug mode.
                    // You can't see it in Logcat using Log.d, Facebook did that to avoid leaking user's access token.
                    if (BuildConfig.DEBUG) {
                        FacebookSdk.setIsDebugEnabled(true)
                        FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS)
                    }
                    accessToken = token.toString()
                    // Facebook Id
                    if (jsonObject.has("id")) {
                        val facebookId = jsonObject.getString("id")
                        Log.i("Facebook Id: ", facebookId.toString())
                        id = facebookId.toString()
                    } else {
                        Log.i("Facebook Id: ", "Not exists")
                        id = "Not exists"
                    }

                    // Facebook First Name
                    if (jsonObject.has("first_name")) {
                        val facebookFirstName = jsonObject.getString("first_name")
                        Log.i("Facebook First Name: ", facebookFirstName)
                        firstName = facebookFirstName
                    } else {
                        Log.i("Facebook First Name: ", "Not exists")
                        firstName = "Not exists"
                    }

                    // Facebook Middle Name
                    if (jsonObject.has("middle_name")) {
                        val facebookMiddleName = jsonObject.getString("middle_name")
                        Log.i("Facebook Middle Name: ", facebookMiddleName)
                        middleName = facebookMiddleName
                    } else {
                        Log.i("Facebook Middle Name: ", "Not exists")
                        middleName = "Not exists"
                    }

                    // Facebook Last Name
                    if (jsonObject.has("last_name")) {
                        val facebookLastName = jsonObject.getString("last_name")
                        Log.i("Facebook Last Name: ", facebookLastName)
                        lastName = facebookLastName
                    } else {
                        Log.i("Facebook Last Name: ", "Not exists")
                        lastName = "Not exists"
                    }

                    // Facebook Name
                    if (jsonObject.has("name")) {
                        val facebookName = jsonObject.getString("name")
                        Log.i("Facebook Name: ", facebookName)
                        name = facebookName
                    } else {
                        Log.i("Facebook Name: ", "Not exists")
                        name = "Not exists"
                    }

                    // Facebook Profile Pic URL
                    if (jsonObject.has("picture")) {
                        val facebookPictureObject = jsonObject.getJSONObject("picture")
                        if (facebookPictureObject.has("data")) {
                            val facebookDataObject = facebookPictureObject.getJSONObject("data")
                            if (facebookDataObject.has("url")) {
                                val facebookProfilePicURL = facebookDataObject.getString("url")
                                Log.i("Facebook Profile Pic URL: ", facebookProfilePicURL)
                                picture = facebookProfilePicURL
                            }
                        }
                    } else {
                        Log.i("Facebook Profile Pic URL: ", "Not exists")
                        picture = "Not exists"
                    }

                    // Facebook Email
                    if (jsonObject.has("email")) {
                        val facebookEmail = jsonObject.getString("email")
                        Log.i("Facebook Email: ", facebookEmail)
                        email = facebookEmail
                    } else {
                        Log.i("Facebook Email: ", "Not exists")
                        email = "Not exists"
                    }
                    openDetailsActivity()
                }).executeAsync()
    }

    private fun openDetailsActivity() {
        val myIntent = Intent(this, LogoutActivity::class.java)
        myIntent.putExtra("facebook_name", name)
        myIntent.putExtra("facebook_picture", picture)
        myIntent.putExtra("facebook_email", email)
        startActivity(myIntent)
    }

    fun isLoggedIn(): Boolean {
        val accessToken = AccessToken.getCurrentAccessToken()
        val isLoggedIn = accessToken != null && !accessToken.isExpired
        return isLoggedIn
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(user: FirebaseUser?)
    {
        if(user==null)
        {
            Log.w(TAG, "User is null, try again!")
            return
        }
        getUserProfile()
        finish()
    }

    private fun getUserProfile() {
        val account: GoogleSignInAccount? =GoogleSignIn.getLastSignedInAccount(this)
        val i=Intent(this, MainActivity2::class.java)
        i.putExtra("ACC",account)
        startActivity(i)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success")
                        val user = auth.currentUser
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        updateUI(null)
                    }
                }
        }
}

