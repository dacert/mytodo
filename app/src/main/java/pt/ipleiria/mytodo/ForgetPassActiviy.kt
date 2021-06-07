package pt.ipleiria.mytodo

import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_forget_pass_activiy.*
import java.util.logging.Level
import java.util.logging.Logger

class ForgetPassActiviy : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
        title = "Forget Password"
        setContentView(R.layout.activity_forget_pass_activiy)
        forget_pass_btn_back.setOnClickListener{
            onBackPressed()
        }
        forget_pass_btn.setOnClickListener {
            when{
                TextUtils.isEmpty(forget_pass_email.text.toString().trim{ it <= ' '}) -> {
                    Toast.makeText(
                        this@ForgetPassActiviy,
                        "Please enter email.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    val email = forget_pass_email.text.toString().trim{ it <= ' '}
                    FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful){
                                Toast.makeText(
                                        this@ForgetPassActiviy,
                                        "Email Sent Successfully. Please Check your email to reset you password",
                                        Toast.LENGTH_SHORT
                                ).show()
                                onBackPressed()
                            }else{
                                logger.log(Level.INFO, task.exception!!.message.toString())
                                Toast.makeText(
                                        this@ForgetPassActiviy,
                                        task.exception!!.message.toString(),
                                        Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            }

        }
    }
}