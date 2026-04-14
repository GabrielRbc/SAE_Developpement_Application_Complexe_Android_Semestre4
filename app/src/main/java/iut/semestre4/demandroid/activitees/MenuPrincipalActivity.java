package iut.semestre4.demandroid.activitees;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import iut.semestre4.demandroid.R;
import iut.semestre4.demandroid.models.AppelAPI;

public class MenuPrincipalActivity extends AppCompatActivity {

    private String apikey;

    private String nom;
    private String prenom;
    private String role;

    private TextView texteBienvenue;

    private String URL_USER_INFO = "http://10.96.33.12/index.php?demande=information";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);
        texteBienvenue = findViewById(R.id.texteBienvenue);
        apikey = getIntent().getStringExtra("APIKEY");

        if (apikey != null) {
            recupererInfosUtilisateur(apikey);
        } else {
            Toast.makeText(this, "Erreur : Clé API manquante", Toast.LENGTH_SHORT).show();
        }

    }

    private void recupererInfosUtilisateur(String apikey) {

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                URL_USER_INFO,
                null,
                response -> {
                    try {
                        String status = response.getString("status");
                        if (status.equals("OK")) {
                            nom = response.getString("nom");
                            prenom = response.getString("prenom");
                            role = response.getString("role");
                            texteBienvenue.setText(
                                    String.format(getString(R.string.bienvenue), prenom, nom, role)
                            );

                        } else {
                            String message = response.getString("message");
                            Toast.makeText(
                                    MenuPrincipalActivity.this,
                                    "Erreur récupération infos : " + message,
                                    Toast.LENGTH_LONG
                            ).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(
                                MenuPrincipalActivity.this,
                                "Erreur parsing JSON",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                },
                error -> {
                    String message = "Erreur inconnue";
                    if (error.networkResponse != null) {
                        message = "Erreur HTTP : " + error.networkResponse.statusCode;
                    } else if (error.getMessage() != null) {
                        message = error.getMessage();
                    }
                    Toast.makeText(MenuPrincipalActivity.this, message, Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }
        ) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("apikey", apikey);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        AppelAPI.getInstance(this).addToRequestQueue(request);
    }

    /**
     * Permet de gérer le clic sur le bouton "Faire une demande"
     * Envoie l'utilisateur sur la vue du menu des demandes
     * @param view
     */
    public void onClicDemande(View view) {
        // Création de l'intent pour aller vers MenuPrincipalActivity
        Intent intent = new Intent(MenuPrincipalActivity.this, MenuDemandeActivity.class);
        intent.putExtra("APIKEY", apikey);
        // Démarrer la nouvelle activity
        startActivity(intent);
    }

    /**
     * Permet de gérer le clic sur le bouton "Consulter ma fiche"
     * Envoie l'utilisateur sur la vue de la fiche
     * @param view
     */
    public void onClicFiche(View view) {
        // Création de l'intent pour aller vers FicheActivity
        Intent intent = new Intent(MenuPrincipalActivity.this, FicheActivity.class);

        // Ajout de la clé API pour qu'elle soit transmise à la nouvelle vue
        intent.putExtra("APIKEY", apikey);

        // Démarrer la nouvelle activity
        startActivity(intent);
    }

    /**
     * Permet de gérer le clic sur le bouton "Consulter mes demandes"
     * Envoie l'utilisateur sur la vue de ses anciennes demandes
     * @param view
     */
    public void onClicConsulter (View view) {
        Intent intent = new Intent(MenuPrincipalActivity.this, ConsulterDemandeActivity.class);
        intent.putExtra("APIKEY", apikey);
        startActivity(intent);
    }

    /**
     * Permet de gérer le clic sur le bouton "Déconnexion"
     * Déconnecte l'utilisateur et le renvoie à la page de connexion
     * @param view
     */
    public void onClicDeconnexion(View view) {
        finish();
    }
}