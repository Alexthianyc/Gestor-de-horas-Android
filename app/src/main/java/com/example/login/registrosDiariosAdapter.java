package com.example.login;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class registrosDiariosAdapter extends RecyclerView.Adapter<registrosDiariosAdapter.ViewHolder>{

    ArrayList<Registro> localDataSet;

    public registrosDiariosAdapter(ArrayList<Registro> dataSet) {
        localDataSet = dataSet;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView numero,fecha,horaIni,horaFIn,laboradas;
        ImageView editar, eliminar;

        public ViewHolder(View view) {
            super(view);

            numero = view.findViewById(R.id.ViewNumber);
            fecha = view.findViewById(R.id.ViewDate);
            horaIni = view.findViewById(R.id.ViewHourStart);
            horaFIn = view.findViewById(R.id.ViewHourEnd);
            laboradas = view.findViewById(R.id.horasLaboradas);

            editar = view.findViewById(R.id.imageViewEdit);
            eliminar = view.findViewById(R.id.imageViewDel);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, @SuppressLint("RecyclerView") final int position) {
        viewHolder.numero.setText("Numero de registo: " + localDataSet.get(position).numero);
        viewHolder.fecha.setText("Fecha: " + localDataSet.get(position).fecha);
        String horaE,horaS,minE,minS;
        if(localDataSet.get(position).horaEntrada < 10){
            horaE = "0" + localDataSet.get(position).horaEntrada;
        }else{
            horaE = "" + localDataSet.get(position).horaEntrada;
        }
        if(localDataSet.get(position).horaSalida < 10){
            horaS = "0" + localDataSet.get(position).horaSalida;
        }else{
            horaS = "" + localDataSet.get(position).horaSalida;
        }
        if(localDataSet.get(position).minutoEntrada < 10){
            minE = "0" + localDataSet.get(position).minutoEntrada;
        }else{
            minE = "" + localDataSet.get(position).minutoEntrada;
        }
        if(localDataSet.get(position).minutoSalida < 10){
            minS = "0" + localDataSet.get(position).minutoSalida;
        }else{
            minS = "" + localDataSet.get(position).minutoSalida;
        }
        viewHolder.horaIni.setText("Hora de inicio: " + horaE + ":" + minE);
        viewHolder.horaFIn.setText("Hora de salida: " + horaS + ":" + minS);

        int horasTrabajadas = (int) Math.floor(localDataSet.get(position).minutosTotal / 60);
        int minutosTrabajados = localDataSet.get(position).minutosTotal % 60;
        viewHolder.laboradas.setText("Tiempo laborado: " + horasTrabajadas + ":" + minutosTrabajados);

        viewHolder.eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setCancelable(true);
                builder.setTitle("Eliminar");
                String mensaje = "Deseas eliminar el registro? ";
                builder.setMessage(mensaje);
                builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        String uid = user.getUid();

                        if(localDataSet.get(position).numero == 2){
                            mDatabase.child(uid).child(localDataSet.get(position).fecha)
                                    .child(String.valueOf(localDataSet.get(position).numero)).removeValue();
                            Toast.makeText(view.getContext(), "Elimiado con exito", Toast.LENGTH_SHORT).show();
                        }else{
                            if(StartActivity.total == 2) {
                                // eliminar registro 1
                                mDatabase.child(uid).child(localDataSet.get(position).fecha)
                                        .child(String.valueOf(localDataSet.get(position).numero)).removeValue();

                                // modificar registro 2
                                mDatabase.child(uid).child(localDataSet.get(position).fecha).
                                        child("2").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                                // mover datos del registro 2 al 1 nuevo
                                                mDatabase.child(uid).child(localDataSet.get(position).fecha)
                                                        .child("1").setValue(task.getResult().getValue());
                                                mDatabase.child(uid).child(localDataSet.get(position).fecha)
                                                        .child("1").child("numero").setValue("1");

                                                // eliminar registro 2
                                                mDatabase.child(uid).child(localDataSet.get(position).fecha)
                                                        .child("2").removeValue();
                                            }
                                        });

                                Toast.makeText(view.getContext(), "Elimiado con exito", Toast.LENGTH_SHORT).show();
                            }else{
                                mDatabase.child(uid).child(localDataSet.get(position).fecha)
                                        .child(String.valueOf(localDataSet.get(position).numero)).removeValue();
                                Toast.makeText(view.getContext(), "Elimiado con exito", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(view.getContext(), "Operacion cancelada con exito", Toast.LENGTH_LONG).show();
                    }
                });
                builder.create();
                builder.show();
            }
        });

        viewHolder.editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                LayoutInflater inflater = LayoutInflater.from(view.getContext());
                view = inflater.inflate(R.layout.editar_flotante,null);
                builder.setView(view);
                Dialog dialog = builder.create();
                dialog.show();

                ImageView cerrarAgregar = view.findViewById(R.id.cerrarActualizar);
                cerrarAgregar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                EditText fecha,horaIni,horaSali,numero;
                fecha = view.findViewById(R.id.fechaActualizarTxt);
                horaIni = view.findViewById(R.id.horaEntradaTxteditar);
                horaSali = view.findViewById(R.id.horaSalidaTxtEditar);
                numero = view.findViewById(R.id.numeroRegistroActualizar);

                fecha.setText(localDataSet.get(position).fecha);
                horaIni.setText(horaE+":"+minE);
                horaSali.setText(horaS+":"+minS);
                numero.setText(String.valueOf(localDataSet.get(position).numero));

                Button actualizar = view.findViewById(R.id.actualizarBtnRegistro);
                actualizar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(view.getContext(), "Actualizando", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dayli_list, parent,false);

        return new ViewHolder(view);
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
