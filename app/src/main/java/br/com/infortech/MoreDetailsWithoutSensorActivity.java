package br.com.infortech;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.infortech.model.Parking;

public class MoreDetailsWithoutSensorActivity extends AppCompatActivity {

    TextView title;
    TextView exclusividadeCliente;
    TextView taxa;
    TextView ultimaSinalizacao;
    Button sinalizarVagaButton;
    DatabaseReference drParkings, drUser;
    TextView ultimaSinalizacaoTextView;
    FirebaseAuth fbAuth;
    String nomeRealUsuario = "undefined";
    Parking choosedParking;
    ImageView imgCheckVigilancia;
    ImageView imgCheckCameraMonitoramento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_details_without_sensor);
        this.fbAuth = FirebaseAuth.getInstance();

        FirebaseDatabase fbDB = FirebaseDatabase.getInstance();
        FirebaseUser fbUser = fbAuth.getCurrentUser();
        drUser = fbDB.getReference("users/" + fbUser.getUid());
        drParkings = fbDB.getReference("parkings/");
        Drawable checkDrawable = getResources().getDrawable(R.drawable.check);
        Drawable uncheckDrawable = getResources().getDrawable(R.drawable.uncheck);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        title = findViewById(R.id.tvTituloMoreDetails);
        exclusividadeCliente = findViewById(R.id.tvExclusividadeClienteMoreDetails);
        taxa = findViewById(R.id.tvTaxaMoreDetails);
        imgCheckCameraMonitoramento = findViewById(R.id.imgCheckCameraMonitoramento);
        imgCheckVigilancia = findViewById(R.id.imgCheckVigilancia);

        sinalizarVagaButton = findViewById(R.id.sinalizarButton);
        ultimaSinalizacaoTextView = findViewById(R.id.ultimaSinalizacao);

        Intent intent = getIntent();
        ArrayList<Parking> parkingsList = (ArrayList<Parking>) intent.getSerializableExtra("parkingsList");
        String parkingName = intent.getStringExtra("parkingName");
        choosedParking = parkingsList.stream().filter((x) -> x.getNome().equals(parkingName)).findFirst().get();

        if(choosedParking.getCameraMonitoramento()) {
            imgCheckCameraMonitoramento.setImageDrawable(checkDrawable);
        } else {
            imgCheckCameraMonitoramento.setImageDrawable(uncheckDrawable);
        }

        if(choosedParking.getVigilancia()) {
            imgCheckVigilancia.setImageDrawable(checkDrawable);
        } else {
            imgCheckVigilancia.setImageDrawable(uncheckDrawable);
        }

        title.setText(choosedParking.getNome());
        exclusividadeCliente.setText("Exclusividade para cliente: " + (choosedParking.getExclusividade_cliente() ? "Sim" : "Não"));
        taxa.setText("Taxa: R$ " + String.format("%.2f", choosedParking.getTaxa()));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String dataFormatada = choosedParking.getSinalizacaoHora().format(formatter);
        ultimaSinalizacaoTextView.setText("Última sinalização: " + dataFormatada + " feita por " + choosedParking.getSinalizacaoAutor() + "(" + getDiffTime(choosedParking.getSinalizacaoHora()) +" minutos atrás).");

        sinalizarVagaButton.setOnClickListener((View view) -> {
            Toast toast = Toast.makeText(getApplicationContext(), "Você sinalizou que há vaga disponível.", Toast.LENGTH_SHORT);
            toast.show();

            drUser.get().addOnCompleteListener(task-> {
                nomeRealUsuario = task.getResult().child("name").getValue().toString();
                System.out.println("NOME REAL USUARIO: "+ nomeRealUsuario);
            });


            drParkings.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for(DataSnapshot dtsp : snapshot.getChildren()) {
                        if(dtsp.child("nome").getValue(String.class).equals(choosedParking.getNome())) {
                            dtsp.getRef().child("sinalizacaoAutor").setValue(nomeRealUsuario);
                            dtsp.getRef().child("sinalizacaoHora").setValue(
                                    Instant.now().atZone(ZoneId.of("America/Sao_Paulo"))
                                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    //TODO
                }
            });
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public String getDiffTime(LocalDateTime ldt) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        ZonedDateTime currentZonedDateTime = currentDateTime.atZone(ZoneId.systemDefault());
        ZonedDateTime targetZonedDateTime = ldt.atZone(ZoneId.systemDefault());

        Duration duration = Duration.between(currentZonedDateTime, targetZonedDateTime);
        long minutes = duration.toMinutes();

        return String.valueOf(Math.abs(minutes));
    }
}