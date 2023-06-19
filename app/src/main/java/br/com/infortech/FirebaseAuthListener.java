package br.com.infortech;

import android.app.Activity;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

public class FirebaseAuthListener implements FirebaseAuth.AuthStateListener {
    private final Activity activity;

    public FirebaseAuthListener(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onAuthStateChanged(@NotNull FirebaseAuth firebaseAuth) {
        FirebaseUser user = firebaseAuth.getCurrentUser();

        Intent intent = null;
        if ((user != null) && !(activity instanceof MainActivity)) {
            intent = new Intent(activity, MainActivity.class);
        }
        if ((user == null) && (activity instanceof MainActivity)) {
            intent = new Intent(activity, LoginActivity.class);
        }
        if (intent != null) {
            activity.startActivity(intent);
            activity.finish();
        }
    }
}
