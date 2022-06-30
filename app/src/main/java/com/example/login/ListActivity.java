package com.example.login;

import static java.util.Calendar.getInstance;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ListActivity extends AppCompatActivity {

    Button filtrar;
    EditText fechaI,fechaF;
    TextView diasLaborar,diasLaborados,horasRestantes,horasExtras;
    long horasTrabajadas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        filtrar = findViewById(R.id.btnFiltrar);
        fechaI = findViewById(R.id.editTextDate);
        fechaF = findViewById(R.id.editTextDate2);
        diasLaborar = findViewById(R.id.textHorasRequeridos);
        diasLaborados = findViewById(R.id.textHorasLaborados);
        horasRestantes = findViewById(R.id.textHorasRestantes);
        horasExtras = findViewById(R.id.textHorasExtras);

        fechaI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // materialDatePicker.show(getSupportFragmentManager(),"dateRangePicker");

                DatePickerFragment newFragment;
                newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        String dayS,monthS;
                        // +1 because January is zero
                        if(day < 10){
                            dayS = "0" + day;
                        }else{
                            dayS = String.valueOf(day);
                        }
                        if(month < 10){
                            monthS = "0" + (month + 1);
                        }else{
                            monthS = String.valueOf((month + 1));
                        }
                        final String selectedDate = dayS + "-" + monthS + "-" + year;
                        fechaI.setText(selectedDate);
                    }
                });
                newFragment.show(getSupportFragmentManager(), "datePickerI");
            }
        });

        fechaF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerFragment newFragment;
                newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        String dayS,monthS;
                        // +1 because January is zero
                        if(day < 10){
                            dayS = "0" + day;
                        }else{
                            dayS = String.valueOf(day);
                        }
                        if(month < 10){
                            monthS = "0" + (month + 1);
                        }else{
                            monthS = String.valueOf((month + 1));
                        }
                        final String selectedDate = dayS + "-" + monthS + "-" + year;
                        fechaF.setText(selectedDate);
                    }
                });
                newFragment.show(getSupportFragmentManager(), "datePickerF");
            }
        });

        diasLaborar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                actulizarRestantesExtras();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        diasLaborados.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                actulizarRestantesExtras();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        filtrar.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if (fechaI.getText().toString().isEmpty() || fechaF.getText().toString().isEmpty()) {
                    Toast.makeText(ListActivity.this, "Error: Debes ingresar un rago de fechas", Toast.LENGTH_SHORT).show();
                }else {
                    // Codigo para vefiricar que la fecha final sea menor que la fecha inicial
                    try {
                        SimpleDateFormat sdformat = new SimpleDateFormat("dd-MM-yyyy");
                        Date date1 = sdformat.parse(fechaI.getText().toString());
                        Date date2 = sdformat.parse(fechaF.getText().toString());

                        if (date2.compareTo(date1) > 0) {
                            // Codigo para mostrar los registros
                            // Obtenemos el numero de dias entre las fechas
                            long diff = (date2.getTime() + (1000 * 60 * 60 * 24)) - date1.getTime();
                            long daysBetween = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

                            // Obtenemos si es final de mes para realizar un proceso distinto
                            String[] fechaIni = fechaI.getText().toString().split("-");
                            String[] fechaFin = fechaF.getText().toString().split("-");
                            int dia = Integer.parseInt(fechaFin[0]);
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(date2);
                            calendar.set(Calendar.DAY_OF_YEAR, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                            Date mes = calendar.getTime();
                            boolean finalMes = false;
                            boolean fechaFinalMes = (dia == Integer.parseInt(String.valueOf(mes.getDate())));
                            boolean finalMesDia = (daysBetween == 16 || daysBetween == 14 || daysBetween == 13 || daysBetween == 15);
                            if(finalMesDia && fechaFinalMes){
                                finalMes = true;
                            }
                            boolean inicioMes = (Integer.parseInt(fechaIni[0]) == 1 && daysBetween == 15);

                            // obtenemos true si es numero de dias es multiplo de 7
                            long resto = daysBetween % 7;
                            long horasTrabajar = 0;
                            long semanas = 0;
                            horasTrabajadas = 0;

                            Date aux2;
                            aux2 = sumarRestarDiasFecha(date2,1);

                            Date aux;
                            aux = sumarRestarDiasFecha(date1,0);
                            // Toast.makeText(ListActivity.this, ""+Integer.parseInt(fechaIni[0]), Toast.LENGTH_SHORT).show();

                            // obtener uid de usuario
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            String uid = user.getUid();

                            // crear lista
                            ArrayList<Registro> list = new ArrayList<>();
                            list.clear();
                            diasLaborados.setText("0");

                            if (finalMes || resto == 0 || inicioMes){
                                if(finalMes || inicioMes){
                                    // Toast.makeText(ListActivity.this, "Final de mes o primer quincena", Toast.LENGTH_SHORT).show();
                                    // Codigo que cuente los dias sabado y domingo
                                    while (aux.getTime() < aux2.getTime()){
                                        LocalDate fechaAux = aux.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                                        String formattedDate = fechaAux.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

                                        // Codigo de llenar arrego del rango
                                        // llenar lista
                                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                                        mDatabase.child(uid).child(formattedDate).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                if(task.getResult().hasChildren()){
                                                    // actualizar listaRegistros
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

                                                        //Log.e("mensaje",String.valueOf(horasTrabajadas));
                                                        int minutos = Integer.parseInt(String.valueOf(child.child("minutosTotal").getValue()));
                                                        horasTrabajadas += minutos;
                                                    }

                                                    // scote cuestiones
                                                    diasLaborados.setText(String.valueOf(horasTrabajadas/60));

                                                    Log.e("mensajeMin",String.valueOf(horasTrabajadas));
                                                    Log.e("mensajeLista",String.valueOf(list.size()));
                                                }else{
                                                    int lab = Integer.parseInt(diasLaborados.getText().toString());
                                                    if(lab == 0){
                                                        diasLaborados.setText("0");
                                                    }
                                                }
                                            }
                                        });
                                        // formattedDate ya recorre todas las fechas falta consulta
                                        if(aux.getDay() == 6){
                                            horasTrabajar += 4;
                                        }else if(aux.getDay() == 0){
                                        }else{
                                            horasTrabajar += 8;
                                        }

                                        aux = sumarRestarDiasFecha(aux,1);
                                    }

                                    diasLaborar.setText(String.valueOf(horasTrabajar));
                                    // date2.getDay() - 0 domingo, 1 lunes ... 6 sabado.

                                }else{
                                    semanas = daysBetween / 7;
                                    horasTrabajar = 44 * semanas;
                                    diasLaborar.setText(String.valueOf(horasTrabajar));

                                    while (aux.getTime() < aux2.getTime()){
                                        LocalDate fechaAux = aux.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                                        String formattedDate = fechaAux.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

                                        // Codigo de llenar arrego del rango
                                        // llenar lista
                                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                                        mDatabase.child(uid).child(formattedDate).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                if(task.getResult().hasChildren()){
                                                    // actualizar listaRegistros
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

                                                        int minutos = Integer.parseInt(String.valueOf(child.child("minutosTotal").getValue()));
                                                        horasTrabajadas += minutos;
                                                    }

                                                    // scote cuestiones
                                                    diasLaborados.setText(String.valueOf(horasTrabajadas/60));

                                                    Log.e("mensajeMin",String.valueOf(horasTrabajadas));
                                                    Log.e("mensajeLista",String.valueOf(list.size()));
                                                }else{
                                                    int lab = Integer.parseInt(diasLaborados.getText().toString());
                                                    if(lab == 0){
                                                        diasLaborados.setText("0");
                                                    }
                                                }
                                            }
                                        });
                                        // formattedDate ya recorre todas las fechas falta consulta
                                        aux = sumarRestarDiasFecha(aux,1);
                                    }
                                }
                            }else{
                                Toast.makeText(ListActivity.this, "Error: El rango debe ser multiplo de 7 o una quincena", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ListActivity.this, "Error: La fecha final no puede ser mayor que la inicial", Toast.LENGTH_SHORT).show();
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void actulizarRestantesExtras(){
        int dlr = Integer.parseInt(diasLaborar.getText().toString());
        int dls = Integer.parseInt(diasLaborados.getText().toString());
        int resta = 0;
        if(dlr > dls){
            resta = dlr-dls;
            horasRestantes.setText(""+resta);
            horasExtras.setText("0");
        }else{
            resta = dls-dlr;
            horasExtras.setText(""+resta);
            horasRestantes.setText("0");
        }
    }

    public Date sumarRestarDiasFecha(Date fecha, int dias){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha); // Configuramos la fecha que se recibe
        calendar.add(Calendar.DAY_OF_YEAR, dias);  // numero de días a añadir, o restar en caso de días<0
        return calendar.getTime(); // Devuelve el objeto Date con los nuevos días añadidos
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout_item_list:
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(ListActivity.this,MainActivity.class));
                break;
            case R.id.add_item_list:
                finish();
                startActivity(new Intent(ListActivity.this,StartActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}