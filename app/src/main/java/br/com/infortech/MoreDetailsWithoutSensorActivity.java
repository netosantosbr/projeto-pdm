package br.com.infortech;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class MoreDetailsWithoutSensorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_details_without_sensor);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Button sinalizarVagaButton = findViewById(R.id.sinalizarButton);
        TextView ultimaSinalizacaoTextView = findViewById(R.id.ultimaSinalizacao);

        sinalizarVagaButton.setOnClickListener((View view) -> {
            Toast toast = Toast.makeText(getApplicationContext(), "Você sinalizou que há vaga disponível.", Toast.LENGTH_SHORT);
            toast.setMargin(5F, 5F);
            toast.show();
            ultimaSinalizacaoTextView.setText("Ultima sinalização: " + getFormattedTime());
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