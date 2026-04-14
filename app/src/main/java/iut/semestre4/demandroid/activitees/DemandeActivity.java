package iut.semestre4.demandroid.activitees;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import iut.semestre4.demandroid.R;
import iut.semestre4.demandroid.models.AppelAPI;

public class DemandeActivity extends AppCompatActivity  {

    private TextView identifiantDemande;

    private TextView idType;

    private TextView idContenu;

    private TextView status_demande;

    private String apikey;
    private String id_demande;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demande);

        identifiantDemande = findViewById(R.id.identifiantDemande);
        idType = findViewById(R.id.idType);
        idContenu = findViewById(R.id.idContenu);
        status_demande = findViewById(R.id.idStatus);

        apikey = getIntent().getStringExtra("APIKEY");
        id_demande = getIntent().getStringExtra("ID_DEMANDE");

        if (id_demande == null) {
            Toast.makeText(this, "Erreur : ID demande manquant", Toast.LENGTH_SHORT).show();
        }

        if (apikey != null) {
            afficherDemande();
        } else {
            Toast.makeText(this, "Erreur : Clé API manquante", Toast.LENGTH_SHORT).show();
        }
    }

    public void afficherDemande() {
        String URL = "http://10.96.33.12/index.php?demande=maDemande";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                response -> {
                    try {
                        String status = response.getString("status");
                        if (status.equals("OK")) {
                            JSONArray array = response.getJSONArray("demandes");
                            if (array.length() > 0) {
                                JSONObject demande = array.getJSONObject(0);

                                String nomAffiche = demande.optString("nom", "Demande Libre");

                                if (nomAffiche.equals("null") || nomAffiche.isEmpty()) {
                                    nomAffiche = "Demande Libre";
                                }
                                idType.setText(nomAffiche);

                                identifiantDemande.setText(id_demande);
                                idContenu.setText(demande.getString("contenu"));
                                status_demande.setText(demande.getString("status"));
                            }
                        } else {
                            Toast.makeText(this, "Erreur : " + response.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Erreur de parsing des données", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    String message = "Erreur réseau";
                    if (error.networkResponse != null) {
                        message = "Code HTTP : " + error.networkResponse.statusCode;
                    }
                    Toast.makeText(DemandeActivity.this, message, Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("apikey", apikey);
                headers.put("id_demande", id_demande); // Ton PHP doit être capable de lire ce header
                return headers;
            }
        };
        AppelAPI.getInstance(this).addToRequestQueue(request);
    }
}
