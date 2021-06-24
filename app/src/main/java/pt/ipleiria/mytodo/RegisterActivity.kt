package pt.ipleiria.mytodo

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_register.*
import pt.ipleiria.mytodo.shared.SharedFireBase
import java.util.logging.Level
import java.util.logging.Logger

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
        setContentView(R.layout.activity_register)
        title = "Register"
        register_login_btn.setOnClickListener{
            onBackPressed()
        }

        register_btn.setOnClickListener {
            when{
                TextUtils.isEmpty(register_email.text.toString().trim{ it <= ' '}) -> {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Please enter email.",
                        Toast.LENGTH_LONG
                    ).show()
                }
                TextUtils.isEmpty(register_pass.text.toString().trim{ it <= ' '}) -> {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Please enter password.",
                        Toast.LENGTH_LONG
                    ).show()
                }
                else -> {
                    val email = register_email.text.toString().trim{ it <= ' '}
                    val password = register_pass.text.toString().trim{ it <= ' '}
                    SharedFireBase.auth.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful){
                                val firebaseUser: FirebaseUser = task.result!!.user!!
                                Toast.makeText(
                                        this@RegisterActivity,
                                        "You are registered Successfully.",
                                        Toast.LENGTH_LONG
                                ).show()

                                val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                intent.putExtra("user_id", firebaseUser.uid)
                                intent.putExtra("email_id", firebaseUser.email)
                                startActivity(intent)
                                finish()
                            }else{
                                logger.log(Level.INFO, task.exception!!.message.toString())
                                Toast.makeText(
                                        this@RegisterActivity,
                                        task.exception!!.message.toString(),
                                        Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                }
            }

        }

    }
}