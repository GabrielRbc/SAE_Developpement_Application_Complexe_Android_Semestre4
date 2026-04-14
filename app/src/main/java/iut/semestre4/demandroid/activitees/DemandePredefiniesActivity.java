package iut.semestre4.demandroid.activitees;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import iut.semestre4.demandroid.R;
import iut.semestre4.demandroid.models.AppelAPI;

public class DemandePredefiniesActivity extends AppCompatActivity {

    private Spinner liste;
    private EditText text;

    private String apikey;

    public static class TypeDemande {
        private final int id;
        private final String nom;

        public TypeDemande(int id, String nom) {
            this.id = id;
            this.nom = nom;
        }

        public int getId() {
            return id;
        }

        @NonNull
        @Override
        public String toString() {
            return nom;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demande_predefinies);

        liste = findViewById(R.id.listeDeroulante);
        text = findViewById(R.id.textDemande);

        apikey = getIntent().getStringExtra("APIKEY");

        if (apikey != null) {
            recupererTypesDemandes(apikey);
        } else {
            Toast.makeText(this, "Erreur : Clé API manquante", Toast.LENGTH_SHORT).show();
        }
    }

    private void recupererTypesDemandes(String apikey) {

        String URL_TYPE_DEMANDES = "http://10.96.33.12/index.php?demande=typeDemandes";
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                URL_TYPE_DEMANDES,
                null,
                response -> {
                    try {
                        if (response.getString("status").equals("OK")) {

                            JSONArray typesArray = response.getJSONArray("types");

                            ArrayList<TypeDemande> types = new ArrayList<>();

                            types.add(new TypeDemande(-1, "Sélectionner un type"));

                            for (int i = 0; i < typesArray.length(); i++) {
                                JSONObject type = typesArray.getJSONObject(i);

                                types.add(new TypeDemande(
                                        type.getInt("id_type"),
                                        type.getString("nom_type")
                                ));
                            }

                            ArrayAdapter<TypeDemande> adapter = new ArrayAdapter<>(
                                    this,
                                    android.R.layout.simple_spinner_item,
                                    types
                            );

                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            liste.setAdapter(adapter);

                        } else {
                            Toast.makeText(
                                    this,
                                    response.getString("message"),
                                    Toast.LENGTH_LONG
                            ).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(this, "Erreur récupération types", Toast.LENGTH_LONG).show();
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

    public void clicEnvoyerDemande(View view) {

        TypeDemande selectedType = (TypeDemande) liste.getSelectedItem();
        String contenu = text.getText().toString().trim();

        if (selectedType.getId() == -1) {
            Toast.makeText(this, "Veuillez sélectionner un type", Toast.LENGTH_SHORT).show();
            return;
        }
        if (contenu.isEmpty()) {
            Toast.makeText(this, "Veuillez saisir le contenu", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONObject body = new JSONObject();
            body.put("apikey", apikey);
            body.put("contenu", contenu);
            body.put("id_type", selectedType.getId());

            String URL_SET_DEMANDE = "http://10.96.33.12/index.php?demande=setDemande";
            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    URL_SET_DEMANDE,
                    body,
                    response -> {
                        try {
                            if (response.getString("status").equals("OK")) {
                                Toast.makeText(this, "Demande envoyée !", Toast.LENGTH_SHORT).show();

                                liste.setSelection(0);
                                text.setText("");

                            } else {
                                Toast.makeText(this, response.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    error -> {
                        Toast.makeText(this, "Erreur envoi demande", Toast.LENGTH_LONG).show();
                        error.printStackTrace();
                    }
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json");
                    return headers;
                }
            };

            AppelAPI.getInstance(this).addToRequestQueue(request);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}