package com.example.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class StartActivity extends AppCompatActivity {

    ImageView agregarRegistro;
    EditText fecha,horaIni,horaSali;
    int hora,minuto;
    public static int total;
    RecyclerView recyclerView;
    registrosDiariosAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // Escuchar cambios
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.addValueEventListener(postListener);

        recyclerView = findViewById(R.id.listaRegistros);

        // Evento escucha si se modifica la base de datos
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        // Obtener fecha del calendario
        SimpleDateFormat dtf = new SimpleDateFormat("dd-MM-yyyy");
        Calendar calendar = Calendar.getInstance();
        Date dateObj = calendar.getTime();
        String formattedDate = dtf.format(dateObj);

        agregarRegistro = findViewById(R.id.AgregarRegistroEntrada);
        agregarRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                view = getLayoutInflater().inflate(R.layout.agregar_flotante,null);
                builder.setView(view);
                Dialog dialog = builder.create();
                dialog.show();

                ImageView cerrarAgregar = view.findViewById(R.id.cerrarClock);
                cerrarAgregar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                fecha = view.findViewById(R.id.fechaAgregartxt);
                horaIni = view.findViewById(R.id.horaEntradaTxtAgregar);
                horaSali = view.findViewById(R.id.horaSalidaTxtAgregar);

                fecha.setText(formattedDate);
                /*
                // Seleccionar fecha
                fecha.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatePickerFragment newFragment = new DatePickerFragment();

                        newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                // +1 because January is zero
                                final String selectedDate = day + "-" + (month+1) + "-" + year;
                                fecha.setText(selectedDate);
                            }
                        });
                        newFragment.show(getSupportFragmentManager(), "datePicker");
                    }
                });
                */

                horaIni.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        seleccionarHora(horaIni,"Hora de entrada");
                    }
                });

                horaSali.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        seleccionarHora(horaSali,"Hora de salida");
                    }
                });

                Button agregarRegistro = view.findViewById(R.id.agregarBtnRegistro);
                agregarRegistro.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(horaIni.getText().toString().isEmpty() || horaSali.getText().toString().isEmpty()){
                            Toast.makeText(StartActivity.this, "Error: Debes ingresar las horas", Toast.LENGTH_SHORT).show();
                        }else {
                            String[] horaEntrada = horaIni.getText().toString().split(":");
                            String[] horaSalida = horaSali.getText().toString().split(":");
                            int horaEntra, horaSale, minutoEntra, minutoSale;
                            int minutosI, minutosF, diferencia;
                            horaEntra = Integer.parseInt(horaEntrada[0]);
                            minutoEntra = Integer.parseInt(horaEntrada[1]);
                            horaSale = Integer.parseInt(horaSalida[0]);
                            minutoSale = Integer.parseInt(horaSalida[1]);

                            minutosI = (horaEntra * 60) + minutoEntra;
                            minutosF = (horaSale * 60) + minutoSale;

                            if (minutosF < minutosI) {
                                Toast.makeText(StartActivity.this, "Error: la hora de salida es mayor a la de entrada", Toast.LENGTH_SHORT).show();
                            } else {
                                // diferencias de minutos
                                diferencia = minutosF - minutosI;

                                // Toast.makeText(StartActivity.this, String.valueOf(horasTrabajadas + ":" + minutosTrabajados), Toast.LENGTH_SHORT).show();

                                if(total < 2){
                                    String date = fecha.getText().toString();
                                    Registro nuevo = new Registro();
                                    nuevo.fecha = date;
                                    nuevo.numero = total + 1;
                                    nuevo.horaEntrada = horaEntra;
                                    nuevo.horaSalida = horaSale;
                                    nuevo.minutoEntrada = minutoEntra;
                                    nuevo.minutoSalida = minutoSale;
                                    nuevo.minutosTotal = diferencia;

                                    mDatabase.child(uid).child(date).child(String.valueOf(total+1)).setValue(nuevo);

                                    Toast.makeText(StartActivity.this, "Agregado con exito", Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(StartActivity.this, "Error: Has alcanzado el maximo de registros diarios", Toast.LENGTH_SHORT).show();
                                }
                                dialog.dismiss();
                            }
                        }
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

    public void seleccionarHora(EditText espace,String titulo){
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                hora = hour;
                minuto = minute;
                espace.setText(String.format(Locale.getDefault(),"%02d:%02d",hora,minuto));
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,onTimeSetListener,hora,minuto,false);
        timePickerDialog.setTitle(titulo);
        timePickerDialog.show();
    }

    ValueEventListener postListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            Registro registro = dataSnapshot.getValue(Registro.class);

            // Codigo si hay un cambio
            // Obtener fecha del calendario
            SimpleDateFormat dtf = new SimpleDateFormat("dd-MM-yyyy");
            Calendar calendar = Calendar.getInstance();
            Date dateObj = calendar.getTime();
            String formattedDate = dtf.format(dateObj);

            // obtener uid de usuario
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String uid = user.getUid();

            // crear lista
            ArrayList<Registro> list = new ArrayList<>();

            // llenar lista
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child(uid).child(formattedDate).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(StartActivity.this, "Error: No se pudo conectar con la base", Toast.LENGTH_SHORT).show();
                    }else{
                        // actuaizar total
                        total = (int) task.getResult().getChildrenCount();

                        //Toast.makeText(StartActivity.this, "Total: "+total, Toast.LENGTH_SHORT).show();

                        if(total == 0){
                            list.clear();
                            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            registrosDiariosAdapter mAdapter = new registrosDiariosAdapter(list);
                            recyclerView.setAdapter(mAdapter);
                        }else{
                            if(!task.getResult().hasChildren()){
                                Toast.makeText(StartActivity.this, "No tienes registros hoy", Toast.LENGTH_SHORT).show();
                            }else{
                                // actualizar listaRegistros
                                list.clear();

                                for (DataSnapshot child : task.getResult().getChildren()) {
                                    Registro item = new Registro();
                                    item.fecha = String.valueOf(child.child("fecha").getValue());
                                    item.horaEntrada = Integer.parseInt(String.valueOf(child.child("horaEntrada").getValue()));
                                    item.horaSalida = Integer.parseInt(String.valueOf(child.child("horaSalida").getValue()));
                                    item.minutoEntrada = Integer.parseInt(String.valueOf(child.child("minutoEntrada").getValue()));
                                    item.minutoSalida = Integer.parseInt(String.valueOf(child.child("minutoEntrada").getValue()));
                                    item.numero = Integer.parseInt(String.valueOf(child.child("numero").getValue()));
                                    item.minutosTotal = Integer.parseInt(String.valueOf(child.child("minutosTotal").getValue()));
                                    list.add(item);
                                }

                                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                registrosDiariosAdapter mAdapter = new registrosDiariosAdapter(list);
                                recyclerView.setAdapter(mAdapter);
                            }
                        }
                    }
                }
            });
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            // Getting Post failed, log a message
            Toast.makeText(StartActivity.this, databaseError.toException().toString(), Toast.LENGTH_SHORT).show();
        }
    };
}