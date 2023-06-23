package br.com.infortech;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import br.com.infortech.model.Parking;
import br.com.infortech.model.User;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_LOCATION_PERMISSION = 2;
    private GoogleMap mMap;
    private MapView mapView;
    Button btnSignOut, btnInfo;
    User user;
    DatabaseReference drUser, drParkings;
    FirebaseAuth fbAuth;
    FirebaseAuthListener authListener;
    List<Parking> listOfParkings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.fbAuth = FirebaseAuth.getInstance();
        this.authListener = new FirebaseAuthListener(this);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        btnSignOut = findViewById(R.id.btnSignOut);
        btnInfo = findViewById(R.id.btnInfo);

        FirebaseDatabase fbDB = FirebaseDatabase.getInstance();
        FirebaseUser fbUser = fbAuth.getCurrentUser();
        drUser = fbDB.getReference("users/" + fbUser.getUid());
        drParkings = fbDB.getReference("parkings/");

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                alertDialog.setMessage("Você deseja realmente sair?!");
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Sair", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        FirebaseUser user = mAuth.getCurrentUser();

                        if(user != null) {
                            mAuth.signOut();
                        } else {
                            Toast.makeText(MainActivity.this, "Erro ao sair!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();

            }
        });

        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);

                 LayoutInflater inflater = LayoutInflater.from(MainActivity.this);

                 View dialogView = inflater.inflate(R.layout.activity_dialog, null);

                 alertDialogBuilder.setView(dialogView);

                 AlertDialog alertDialog = alertDialogBuilder.create();
                 alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                     @Override
                     public void onClick(DialogInterface dialog, int which) {
                         alertDialog.dismiss();
                     }
                 });
                 alertDialog.show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    public CompletableFuture<List<Parking>> loadParkingsFromFirebase() {
        CompletableFuture<List<Parking>> future = new CompletableFuture<>();

        drParkings.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                listOfParkings = new ArrayList<>();

                for(DataSnapshot dtsp: snapshot.getChildren()) {
                    listOfParkings.add(new Parking().fromDataSnapshot(dtsp));
                }
                future.complete(listOfParkings);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                future.completeExceptionally(error.toException());
            }
        });

        return future;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Cria os marcadores no mapa e atribui a eles propriedades.
        loadParkingsFromFirebase().thenAccept(parkings -> {
           listOfParkings = parkings;

            for(Parking parking : listOfParkings) {
                BitmapDescriptor icon = parking.getTipo() == 1 ? resizeFromDrawableAndReturnAsBitmapDescriptor(R.drawable.parkingbikeicon, 96, 96)
                        : resizeFromDrawableAndReturnAsBitmapDescriptor(R.drawable.parkingcarroicon, 96, 96);

                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(parking.getLatitude(), parking.getLongitude()))
                        .title(parking.getNome()))
                        .setIcon(icon);
            }
        });

        //Move a câmera para a latitude e longitude estabelecida com o zoom estabelecido.
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-8.117928, -34.903104), 16));

        //Pede permissão para acesso a localização.
        methodToRequestPermissions();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Intent intent = new Intent(MainActivity.this, MoreDetailsWithoutSensorActivity.class);
                intent.putExtra("parkingName", marker.getTitle());
                intent.putExtra("parkingsList", (Serializable) listOfParkings);
                startActivity(intent);
                return false;
            }
        });

    }

    public void methodToRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.ACCESS_FINE_LOCATION }, REQUEST_LOCATION_PERMISSION);
        }
    }

    public BitmapDescriptor resizeFromDrawableAndReturnAsBitmapDescriptor(int resourceId, int width, int height) {
        Bitmap bitmapImage = BitmapFactory.decodeResource(getResources(), resourceId);

        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmapImage, width, height, false);

        return BitmapDescriptorFactory.fromBitmap(resizedBitmap);
    }

    @Override
    public void onStart() {
        super.onStart();
        fbAuth.addAuthStateListener(authListener);
    }
    @Override
    public void onStop() {
        super.onStop();
        fbAuth.removeAuthStateListener(authListener);
    }
}