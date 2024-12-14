package com.example.musicapp.Fragments;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicapp.Activities.MainActivity;
import com.example.musicapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInFragment extends Fragment {

    private TextView dontHaveAnAccount;
    private TextView fogottenPassword;
    private FrameLayout frameLayout;
    private Drawable errorIcon;

    private EditText email;
    private EditText password;
    private Button signInBtn;
    private ProgressBar signInProgress;

    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        dontHaveAnAccount = view.findViewById(R.id.Dont_have_an_account);
        fogottenPassword = view.findViewById(R.id.forgotten_password);
        frameLayout = getActivity().findViewById(R.id.register_frame_layout);

        errorIcon = getResources().getDrawable(R.drawable.ic_error);

        email = view.findViewById(R.id.email);
        password = view.findViewById(R.id.password);
        signInBtn = view.findViewById(R.id.SignIn_Btn);
        signInProgress = view.findViewById(R.id.signInProgress);

        mAuth = FirebaseAuth.getInstance();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        errorIcon.setBounds(0,0,errorIcon.getIntrinsicWidth(), errorIcon.getIntrinsicHeight());

        dontHaveAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFragment(new SignUpFragment());
            }
        });

        fogottenPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFragment(new ResetPasswordFragment());
            }
        });

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInWithFirebase();
                signInBtn.setEnabled(false);
                signInBtn.setTextColor(getResources().getColor(R.color.transWhite));
            }
        });
    }

    private void signInWithFirebase() {
        if (email.getText().toString().matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"))   {
            signInProgress.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            signInProgress.setVisibility(View.VISIBLE);
                            if (task.isSuccessful())    {
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                getActivity().startActivity(intent);
                                getActivity().finish();
                            } else {
                                Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                signInBtn.setEnabled(true);
                                signInBtn.setTextColor(getResources().getColor(R.color.white));
                            }
                        }
                    });
        } else {
            email.setError("Invalid Email Pattern!",errorIcon);
            signInBtn.setEnabled(true);
            signInBtn.setTextColor(getResources().getColor(R.color.white));
        }
    }

    private void checkInputs()  {
        if (!email.getText().toString().isEmpty())   {
            if (!password.getText().toString().isEmpty())   {
            } else {
                signInBtn.setEnabled(true);
                signInBtn.setTextColor(getResources().getColor(R.color.white));
            }
        } else {
            signInBtn.setEnabled(false);
            signInBtn.setTextColor(getResources().getColor(R.color.transWhite));
        }
    }
    private void setFragment(Fragment fragment) {

        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.from_right, R.anim.out_from_left);
        fragmentTransaction.replace(frameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }
}