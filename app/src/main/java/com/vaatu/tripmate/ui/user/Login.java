package com.vaatu.tripmate.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.vaatu.tripmate.R;
import com.vaatu.tripmate.ui.home.UpcomingTripsActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.ContentValues.TAG;


public class Login extends Fragment {


    @BindView(R.id.emailField)
    EditText emailField;

    @BindView(R.id.passField)
    EditText passField;

    @BindView(R.id.btnSignUp)
    Button btnSignUp;

    @BindView(R.id.btnSignIn)
    Button btnSignIn;

    @BindView(R.id.google_btn)
    SignInButton btnGoogle;

    @BindView(R.id.textViewStatus)
    TextView mStatusTextView;

    @BindView(R.id.determinateBar)
    ProgressBar mProgressBar;

    FirebaseAuth mAuth;

    FirebaseUser currentUser;

    GoogleSignInClient mGoogleSignInClient;


    private static final int RC_SIGN_IN = 9001;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.login, container, false);
        ButterKnife.bind(this, view);

        mProgressBar.getIndeterminateDrawable().setColorFilter(0x3F51B5, android.graphics.PorterDuff.Mode.MULTIPLY);
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();


        if (currentUser != null) {
            mProgressBar.setVisibility(View.VISIBLE);
            Intent mainIntent = new Intent(getContext(), UpcomingTripsActivity.class);
            startActivity(mainIntent);
            mProgressBar.setVisibility(View.INVISIBLE);
            getActivity().finish();
        }

        btnSignUp.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment);
            Bundle bundle = new Bundle();
            if (!emailField.getText().toString().equals("") && !passField.getText().toString().equals("")) {
                bundle.putString("Email", emailField.getText().toString());
                bundle.putString("Pass", passField.getText().toString());
            } else {
                emailField.setError("Enter an Email");
                passField.setError("Must Enter Password");
            }
            navController.navigate(R.id.action_login_signUp, bundle);

        });

        btnSignIn.setOnClickListener(v -> {
            showProgressBar();
            login(emailField.getText().toString(), passField.getText().toString());
        });

        btnGoogle.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });

        return view;

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
//                       updateUI(null);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();


        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
    }


    void login(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        updateUI(null);
                    }

                });
    }

//    private void signIn() {
//        Log.i("Google Sign in", "Im in");
//        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//        startActivityForResult(signInIntent, RC_SIGN_IN);
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        updateUI(null);
                    }

                });
    }

    //daca userul exista pornim a 3-a activitate (upcompingTrips)
    private void updateUI(FirebaseUser user) {
        hideProgressBar();
        if (user != null) {

            Intent mainIntent = new Intent(getContext(), UpcomingTripsActivity.class);
            startActivity(mainIntent);
            getActivity().finish();

        } else {
            mStatusTextView.setText("Invalid Credentials");
        }
    }

    private void hideProgressBar() {
        mProgressBar.setVisibility(View.INVISIBLE);

    }

    private void showProgressBar() {
        mProgressBar.setVisibility(View.VISIBLE);

    }

}
