package iut.semestre4.demandroid.activitees;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import iut.semestre4.demandroid.R;
import iut.semestre4.demandroid.models.AppelAPI;

public class FicheActivity extends AppCompatActivity {

    private TextView nomFiche;
    private TextView prenomFiche;
    private TextView mailFiche;
    private TextView numFiche;
    private TextView roleFiche;

    private String apikey;

    private String URL_FICHE = "http://10.96.33.12/index.php?demande=fiche";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fiche);

        nomFiche = findViewById(R.id.idNom);
        prenomFiche = findViewById(R.id.idPrenom);
        mailFiche = findViewById(R.id.idMail);
        numFiche = findViewById(R.id.idNumero);
        roleFiche = findViewById(R.id.idRole);

        apikey = getIntent().getStringExtra("APIKEY");

        if (apikey != null) {
            recupererFicheUtilisateur(apikey);
        } else {
            Toast.makeText(this, "Erreur : Clé API manquante", Toast.LENGTH_SHORT).show();
        }
    }

    private void recupererFicheUtilisateur(String apikey) {

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                URL_FICHE,
                null,
                response -> {
                    try {
                        String status = response.getString("status");
                        if (status.equals("OK")) {
                            nomFiche.setText(response.getString("nom"));
                            prenomFiche.setText(response.getString("prenom"));
                            mailFiche.setText(response.getString("mail"));
                            numFiche.setText(response.getString("num_tel"));
                            roleFiche.setText(response.getString("role"));
                        } else {
                            Toast.makeText(
                                    FicheActivity.this,
                                    "Erreur : " + response.getString("message"),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    String message = "Erreur réseau";
                    if (error.networkResponse != null) {
                        message = "Code HTTP : " + error.networkResponse.statusCode;
                    }
                    Toast.makeText(FicheActivity.this, message, Toast.LENGTH_LONG).show();
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
}