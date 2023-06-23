package br.com.infortech;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
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
    String nomeRealUsuario;
    Parking choosedParking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_details_without_sensor);
        this.fbAuth = FirebaseAuth.getInstance();

        FirebaseDatabase fbDB = FirebaseDatabase.getInstance();
        FirebaseUser fbUser = fbAuth.getCurrentUser();
        drUser = fbDB.getReference("users/" + fbUser.getUid());
        drParkings = fbDB.getReference("parkings/");


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        title = findViewById(R.id.tvTituloMoreDetails);
        exclusividadeCliente = findViewById(R.id.tvExclusividadeClienteMoreDetails);
        taxa = findViewById(R.id.tvTaxaMoreDetails);

        sinalizarVagaButton = findViewById(R.id.sinalizarButton);
        ultimaSinalizacaoTextView = findViewById(R.id.ultimaSinalizacao);

        Intent intent = getIntent();
        ArrayList<Parking> parkingsList = (ArrayList<Parking>) intent.getSerializableExtra("parkingsList");
        String parkingName = intent.getStringExtra("parkingName");
        choosedParking = parkingsList.stream().filter((x) -> x.getNome().equals(parkingName)).findFirst().get();

        title.setText(choosedParking.getNome());
        exclusividadeCliente.setText("Exclusividade para cliente: " + (choosedParking.getExclusividade_cliente() ? "Sim" : "Não"));
        taxa.setText("Taxa: R$ " + String.format("%.2f", choosedParking.getTaxa()));
        ultimaSinalizacaoTextView.setText("Última sinalização: " + choosedParking.getSinalizacaoHora() + " feita por " + choosedParking.getSinalizacaoAutor());

        sinalizarVagaButton.setOnClickListener((View view) -> {
            Toast toast = Toast.makeText(getApplicationContext(), "Você sinalizou que há vaga disponível.", Toast.LENGTH_SHORT);
            toast.setMargin(5F, 5F);
            toast.show();

            drUser.get().addOnCompleteListener(task-> {
                nomeRealUsuario = task.getResult().child("name").getValue().toString();
            });


            drParkings.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    for(DataSnapshot dtsp : snapshot.getChildren()) {
                        if(dtsp.child("nome").getValue(String.class).equals(choosedParking.getNome())) {
                            dtsp.getRef().child("sinalizacaoAutor").setValue(nomeRealUsuario);
                            dtsp.getRef().child("sinalizacaoHora").setValue(
                                    Instant.now().atZone(ZoneId.of("UTC"))
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


    public String getFormattedTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

}