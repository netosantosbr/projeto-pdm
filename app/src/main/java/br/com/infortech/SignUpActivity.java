package br.com.infortech;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import br.com.infortech.model.User;

public class SignUpActivity extends AppCompatActivity {

    FirebaseAuth fbAuth;
    FirebaseAuthListener authListener;

    Button btnFinalizarCadastro;
    EditText edName, edEmail, edPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        this.fbAuth = FirebaseAuth.getInstance();
        this.authListener = new FirebaseAuthListener(this);

        btnFinalizarCadastro = findViewById(R.id.btnFinalizarCadastro);
        edName = findViewById(R.id.edName);
        edEmail = findViewById(R.id.edEmail);
        edPassword = findViewById(R.id.edPassword);

        btnFinalizarCadastro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = edName.getText().toString();
                String email = edEmail.getText().toString();
                String password = edPassword.getText().toString();

                FirebaseAuth mAuth = FirebaseAuth.getInstance();

                if(name.trim().isEmpty() || email.trim().isEmpty() || password.trim().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Um dos campos está vazio. Conserte isso e tente novamente!", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                        String isSuccessfulMessage = task.isSuccessful() ?  "Registrado com sucesso!" : "Registro não completado! - " + task.getException().getMessage();

                        Toast.makeText(SignUpActivity.this, isSuccessfulMessage, Toast.LENGTH_SHORT).show();

                        if (task.isSuccessful()) {
                            User tempUser = new User(name, email);
                            DatabaseReference drUsers = FirebaseDatabase.
                                    getInstance().getReference("users");
                            drUsers.child(mAuth.getCurrentUser().getUid()).
                                    setValue(tempUser);
                        }
                    });
                }


            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        fbAuth.addAuthStateListener(authListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        fbAuth.removeAuthStateListener(authListener);
    }

}