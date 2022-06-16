package com.example.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;

public class MainActivity extends AppCompatActivity {

    private GoogleSignInClient clienteGoogle;
    private static final int RC_SIGN_IN = 123;

    private FirebaseAuth mAuth;
    EditText email, password;
    Button registrar, iniciarSesion, google;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        email = findViewById(R.id.txtEmail);
        password = findViewById(R.id.txtPassword);
        registrar = findViewById(R.id.registrar);
        iniciarSesion = findViewById(R.id.iniciarSesion);
        google = findViewById(R.id.google);

        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                view = getLayoutInflater().inflate(R.layout.registrar_flotante,null);
                builder.setView(view);
                Dialog dialog = builder.create();
                dialog.show();

                ImageView cerrar = view.findViewById(R.id.imageView);
                cerrar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                EditText emailRegistrar, nameRegistrar, passwordRegistrar, passwordConfirmRegistrar;
                nameRegistrar = view.findViewById(R.id.nombreRegistrarTxt);
                emailRegistrar = view.findViewById(R.id.emailRegistrarTxt);
                passwordRegistrar = view.findViewById(R.id.PasswordRegistrarTxt);
                passwordConfirmRegistrar = view.findViewById(R.id.PasswordConfirmarRegistrarTxt);

                Button registrarCuenta;
                registrarCuenta = view.findViewById(R.id.userNew);
                registrarCuenta.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!nameRegistrar.getText().toString().isEmpty()) {
                            if (!emailRegistrar.getText().toString().isEmpty()) {
                                if (passwordRegistrar.getText().toString().equals(passwordConfirmRegistrar.getText().toString())) {
                                    //Toast.makeText(MainActivity.this, "ok", Toast.LENGTH_SHORT).show();
                                    // Registrar cuenta
                                    String txtEmail = emailRegistrar.getText().toString();
                                    String txtPassword = passwordRegistrar.getText().toString();

                                    if (txtPassword.length() <= 6) {
                                        mensaje("Contrasenia debe tener mas de 6 caracteres");
                                    } else {
                                        mAuth.createUserWithEmailAndPassword(txtEmail, txtPassword)
                                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(getApplicationContext(), "Usuario registrado con exito", Toast.LENGTH_SHORT).show();
                                                        dialog.dismiss();
                                                    }else{
                                                        Toast.makeText(getApplicationContext(), "Error: cuenta no registrada", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                    }

                                }else{
                                    mensaje("Las contraseñas deben se iguales");
                                }
                            }else{
                                mensaje("Debes ingresar un correo");
                            }
                        }else{
                            mensaje("Debes ingresar un nombre");
                        }
                    }
                });
            }
        });

        iniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txtEmail = email.getText().toString();
                String txtPassword = password.getText().toString();
                if (txtEmail.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Debes ingresar un correo valido", Toast.LENGTH_SHORT).show();
                }else {
                    if (txtPassword.length() <= 6) {
                        Toast.makeText(getApplicationContext(), "Contrasenia debe tener mas de 6 caracteres", Toast.LENGTH_SHORT).show();
                    } else {
                        mAuth.signInWithEmailAndPassword(txtEmail, txtPassword)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()) {
                                        email.setText("");
                                        password.setText("");
                                        finish();
                                        startActivity(new Intent(MainActivity.this,StartActivity.class));
                                    }else{
                                        Toast.makeText(getApplicationContext(), "Error: correo o contrasenia invalida", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    }
                }
            }
        });

        crearSolicitud();

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                singIn();
            }
        });
    }

    private void singIn() {
        // Intent cuando se inicie sesion
        Intent signIntent = clienteGoogle.getSignInIntent();
        startActivityForResult(signIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                AutenticacionFirebase(account);
            } catch (ApiException e) {
                // ...)
            }
        }
    }

    private void AutenticacionFirebase(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            // se inicio correctamente
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            Toast.makeText(getApplicationContext(),"Nombre: "+ user.getDisplayName(),Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(MainActivity.this,StartActivity.class));
                        }else{
                            Toast.makeText(getApplicationContext(),"No se pudo ingresar",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void crearSolicitud() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        clienteGoogle = GoogleSignIn.getClient(getApplicationContext(), gso);
    }

    public void mensaje(String mensaje){
        Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
    }
}