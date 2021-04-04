package pt.ipleiria.mytodo

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

class HomeFragment : Fragment() {

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val SDK_INT = Build.VERSION.SDK_INT
        if (SDK_INT > 8) {
            val policy = StrictMode.ThreadPolicy.Builder()
                    .permitAll().build()
            StrictMode.setThreadPolicy(policy)
            //your codes here
        }
    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        setActivityTitle("Home")
        // Database reference pointing to root of database
        // Inflate the layout for this fragment
        // return inflater.inflate(R.layout.fragment_home, container, false)
        return inflater.inflate(R.layout.fragment_home, container, false)
    }
}