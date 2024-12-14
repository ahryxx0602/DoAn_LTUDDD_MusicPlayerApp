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

import com.example.musicapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordFragment extends Fragment {

    private TextView goBack;
    private FrameLayout frameLayout;
    private Drawable errorIcon;

    private EditText email;
    private ProgressBar resetPasswordProgressBar;
    private TextView responseMessage;
    private Button resetPassword_Btn;

    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reset_password, container, false);
        goBack = view.findViewById(R.id.goBack);
        frameLayout = getActivity().findViewById(R.id.register_frame_layout);

        errorIcon = getResources().getDrawable(R.drawable.ic_error);

        email = view.findViewById(R.id.email);
        resetPasswordProgressBar = view.findViewById(R.id.progress_resetPaswrd);
        responseMessage = view.findViewById(R.id.responseMessage);
        resetPassword_Btn = view.findViewById(R.id.btn_ResetPassword);

        mAuth = FirebaseAuth.getInstance();

        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        errorIcon.setBounds(0,0,errorIcon.getIntrinsicWidth(), errorIcon.getIntrinsicHeight());

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setFragment(new SignInFragment());
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

        resetPassword_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });
    }

    private void resetPassword()    {
        if (email.getText().toString().matches("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"))   {
            resetPasswordProgressBar.setVisibility(View.VISIBLE);
            mAuth.sendPasswordResetEmail(email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful())    {
                        responseMessage.setText("Check your Email.");
                        responseMessage.setTextColor(getResources().getColor(R.color.green));
                    } else {
                        responseMessage.setText("There is an issue sending Email.");
                        responseMessage.setTextColor(getResources().getColor(R.color.red));
                    }
                    resetPasswordProgressBar.setVisibility(View.INVISIBLE);
                    responseMessage.setVisibility(View.VISIBLE);
                }
            });

        } else {
            email.setError("Invalid Email Pattern!", errorIcon);
            resetPassword_Btn.setEnabled(true);
            resetPassword_Btn.setTextColor(getResources().getColor(R.color.white));
        }
    }
    private void checkInputs()  {
        if (!email.getText().toString().isEmpty())   {
            resetPassword_Btn.setEnabled(true);
            resetPassword_Btn.setTextColor(getResources().getColor(R.color.white));
        } else {
            resetPassword_Btn.setEnabled(false);
            resetPassword_Btn.setTextColor(getResources().getColor(R.color.transWhite));
        }

    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.from_left, R.anim.out_from_right);
        fragmentTransaction.replace(frameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }
}