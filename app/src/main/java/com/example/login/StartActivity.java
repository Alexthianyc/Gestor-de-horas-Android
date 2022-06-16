package com.example.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class StartActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    ImageView agregarRegistro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        //mAuth = FirebaseAuth.getInstance();
        //FirebaseUser user = mAuth.getCurrentUser();
        //Toast.makeText(this, user.getDisplayName(), Toast.LENGTH_SHORT).show();

        agregarRegistro = findViewById(R.id.AgregarRegistroEntrada);
        agregarRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                view = getLayoutInflater().inflate(R.layout.agregar_flotante,null);
                builder.setView(view);
                Dialog dialog = builder.create();
                dialog.show();

                ImageView cerrarAgregar = view.findViewById(R.id.cerrarAgregarFlotante);
                cerrarAgregar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_start, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout_item:
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(StartActivity.this,MainActivity.class));

                break;
            case R.id.Listado_item:
                finish();
                startActivity(new Intent(StartActivity.this,ListActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}