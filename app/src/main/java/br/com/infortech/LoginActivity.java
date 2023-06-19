package br.com.infortech;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth fbAuth;
    FirebaseAuthListener authListener;

    Button btnSignIn;
    EditText edEmail, edPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.fbAuth = FirebaseAuth.getInstance();
        this.authListener = new FirebaseAuthListener(this);

        btnSignIn = findViewById(R.id.btnSignIn);
        edEmail = findViewById(R.id.edEmailSignIn);
        edPassword = findViewById(R.id.edPasswordSignIn);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edEmail.getText().toString();
                String password = edPassword.getText().toString();
                FirebaseAuth mAuth = FirebaseAuth.getInstance();

                mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if(!task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Falha no login. Confira suas credenciais!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public void signUpClickAction(View view) {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
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