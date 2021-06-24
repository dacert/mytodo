package pt.ipleiria.mytodo

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_login.*
import pt.ipleiria.mytodo.shared.SharedFireBase
import java.util.logging.Level
import java.util.logging.Logger

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
        title = "Login"
        setContentView(R.layout.activity_login)
        val user =  SharedFireBase.auth.currentUser
        if (user != null) {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.putExtra("user_id", user.uid)
            intent.putExtra("email_id", user.email)
            startActivity(intent)
            finish()
        }
        login_register_btn.setOnClickListener{
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }

        login_forget_pass_btn.setOnClickListener{
            startActivity(Intent(this@LoginActivity, ForgetPassActiviy::class.java))
        }

        login_btn.setOnClickListener {
            when{
                TextUtils.isEmpty(login_email.text.toString().trim{ it <= ' '}) -> {
                    Toast.makeText(
                        this@LoginActivity,
                        "Please enter email.",
                        Toast.LENGTH_LONG
                    ).show()
                }
                TextUtils.isEmpty(login_pass.text.toString().trim{ it <= ' '}) -> {
                    Toast.makeText(
                        this@LoginActivity,
                        "Please enter password.",
                        Toast.LENGTH_LONG
                    ).show()
                }
                else -> {
                    val email = login_email.text.toString().trim{ it <= ' '}
                    val password = login_pass.text.toString().trim{ it <= ' '}
                    SharedFireBase.auth.signInWithEmailAndPassword(email,password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful){
                                val firebaseUser = task.result!!.user!!
                                Toast.makeText(
                                        this@LoginActivity,
                                        "You are logging Successfully.",
                                        Toast.LENGTH_LONG
                                ).show()

                                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                intent.putExtra("user_id", firebaseUser.uid)
                                intent.putExtra("email_id", firebaseUser.email)
                                startActivity(intent)
                                finish()
                            }else{
                                logger.log(Level.INFO, task.exception!!.message.toString())
                                Toast.makeText(
                                        this@LoginActivity,
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