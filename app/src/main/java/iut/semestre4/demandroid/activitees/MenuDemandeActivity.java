package iut.semestre4.demandroid.activitees;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import iut.semestre4.demandroid.R;

public class MenuDemandeActivity extends AppCompatActivity {

    private String apikey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_demande);
        apikey = getIntent().getStringExtra("APIKEY");

        if (apikey == null) {
            Toast.makeText(this, "Erreur : Clé API manquante", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Permet de gérer le clic sur le bouton "Demandes prédéfinies"
     * Envoie l'utilisateur sur la vue des demandes prédéfinies
     * @param view
     */
    public void onClicPredefini (View view) {
        Intent intent = new Intent(MenuDemandeActivity.this, DemandePredefiniesActivity.class);
        intent.putExtra("APIKEY", apikey);
        startActivity(intent);
    }

    /**
     * Permet de gérer le clic sur le bouton "Nous contacter librement"
     * Envoie l'utilisateur sur la vue des demandes libres
     * @param view
     */
    public void onClicLibre (View view) {
        Intent intent = new Intent(MenuDemandeActivity.this, ContactLibreActivity.class);
        intent.putExtra("APIKEY", apikey);
        startActivity(intent);
    }

}
