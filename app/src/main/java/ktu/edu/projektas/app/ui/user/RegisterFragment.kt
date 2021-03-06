package ktu.edu.projektas.app.ui.user

import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import ktu.edu.projektas.R
import ktu.edu.projektas.app.data.User
import ktu.edu.projektas.databinding.FragmentRegisterBinding

// fragment class for user's signup
class RegisterFragment: Fragment() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var binding: FragmentRegisterBinding
    private var fdb: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fdb.firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(false)
            .build()
        mAuth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = viewLifecycleOwner

        binding.btRegister.setOnClickListener { registerUser()  }

        return binding.root
    }

    private fun registerUser() {
        if(binding.etFirstName.editText?.text.toString().trim().isEmpty()) {
            binding.etFirstName.error = "First name is required"
            binding.etFirstName.requestFocus()
            return
        }
        if(binding.etLastName.editText?.text.toString().trim().isEmpty()) {
            binding.etLastName.error = "Last name is required"
            binding.etLastName.requestFocus()
            return
        }
        if(binding.etEmail.editText?.text.toString().trim().isEmpty()) {
            binding.etEmail.error = "Email is required"
            binding.etEmail.requestFocus()
            return
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.editText?.text.toString()).matches()) {
            binding.etEmail.error = "Email is invalid"
            binding.etEmail.requestFocus()
            return
        }
        if(binding.etPassword.editText?.text.toString().trim().isEmpty()) {
            binding.etPassword.error = "Password is required"
            binding.etPassword.requestFocus()
            return
        }
        if(binding.etPassword.editText?.text.toString().length < 6){
            binding.etPassword.error = "Password has to be at least 6 characters long"
            binding.etPassword.requestFocus()
            return
        }
        if(binding.etPassword.editText?.text.toString() != binding.etRepeatPassword.editText?.text.toString()){
            binding.etRepeatPassword.error = "Passwords must match"
            binding.etRepeatPassword.requestFocus()
            return
        }
        mAuth.createUserWithEmailAndPassword(binding.etEmail.editText?.text.toString(), binding.etPassword.editText?.text.toString()).addOnCompleteListener{
            task ->
            if(task.isSuccessful){
                activity?.let { Snackbar.make(it.findViewById(R.id.drawer_layout), "User has been registered", Snackbar.LENGTH_LONG) }
                    ?.show()
                var currentUser = FirebaseAuth.getInstance().currentUser

                val user = User(binding.etFirstName.editText?.text.toString(), binding.etLastName.editText?.text.toString(), binding.etEmail.editText?.text.toString(), "Student", binding.etGroup.editText?.text.toString(),
                    currentUser!!.uid)

                currentUser?.let { fdb.collection("users").document(it.uid).set(user) }

                view?.findNavController()?.navigate(R.id.action_registerFragment_to_loginFragment)
            }
            else {
                activity?.let { Snackbar.make(it.findViewById(R.id.drawer_layout), "Error has occurred, please try again", Snackbar.LENGTH_LONG) }
                    ?.show()
            }
        }
    }
}