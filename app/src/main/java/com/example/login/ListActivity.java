package com.example.login;

import static java.util.Calendar.getInstance;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ListActivity extends AppCompatActivity {

    Button filtrar;
    EditText fechaI,fechaF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        filtrar = findViewById(R.id.btnFiltrar);
        fechaI = findViewById(R.id.editTextDate);
        fechaF = findViewById(R.id.editTextDate2);

        /*
        MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
        // Titulo
        builder.setTitleText("Ingrese un rango de fecha");
        // Seleccionar dia de hoy por defecto
        builder.setSelection(MaterialDatePicker.todayInUtcMilliseconds());
        MaterialDatePicker materialDatePicker = builder.build();
         */

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

        /*
        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                fecha.setText(materialDatePicker.);
            }
        });
         */

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

                            // Toast.makeText(ListActivity.this, ""+Integer.parseInt(fechaIni[0]), Toast.LENGTH_SHORT).show();

                            if (finalMes || resto == 0 || inicioMes){
                                if(finalMes || inicioMes){
                                    Toast.makeText(ListActivity.this, "Final de mes o primer quincena", Toast.LENGTH_SHORT).show();
                                    // Codigo que cuente los dias sabado y domingo
                                    // date2.getDay() - 0 domingo, 1 lunes ... 6 sabado.

                                }else{
                                    long semanas = daysBetween / 7;
                                    horasTrabajar = 44 * semanas;

                                    Toast.makeText(ListActivity.this, "Horas requeridas: "+horasTrabajar, Toast.LENGTH_SHORT).show();
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