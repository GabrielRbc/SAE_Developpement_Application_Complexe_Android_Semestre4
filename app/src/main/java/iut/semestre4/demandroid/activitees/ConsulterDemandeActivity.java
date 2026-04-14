package iut.semestre4.demandroid.activitees;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.*;

import iut.semestre4.demandroid.R;
import iut.semestre4.demandroid.models.AppelAPI;

public class ConsulterDemandeActivity extends AppCompatActivity {

    private Spinner liste;

    private final ArrayList<Demande> listeComplete = new ArrayList<>();
    private final ArrayList<Demande> listeAffichee = new ArrayList<>();

    private ArrayAdapter<Demande> adapter;

    private final ArrayList<Type> typesSpinner = new ArrayList<>();

    private String apikey;

    public static class Type {
        public int id;
        public String nom;

        public Type(int id, String nom) {
            this.id = id;
            this.nom = nom;
        }

        @NonNull
        @Override
        public String toString() {
            return nom;
        }
    }

    public static class Demande {
        public Integer id;
        public Integer idType;
        public String nom;
        public String status;

        public Demande(Integer id, Integer idType, String nom, String status) {
            this.id = id;
            this.idType = idType;
            this.nom = nom;
            this.status = status;
        }

        @NonNull
        @Override
        public String toString() {
            return nom + " (" + status + ")";
        }
    }

    public class DemandeAdapter extends ArrayAdapter<Demande> {
        public DemandeAdapter(Context context, List<Demande> demandes) {
            super(context, 0, demandes);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_demande, parent, false);
            }

            Demande demande = getItem(position);
            TextView tvNom = convertView.findViewById(R.id.textNomDemande);
            View bulle = convertView.findViewById(R.id.bulleStatut);

            tvNom.setText(demande.nom);

            // Gestion des couleurs selon le statut
            int couleur;
            switch (demande.status) {
                case "VALIDE":
                    couleur = Color.parseColor("#4CAF50"); // Vert
                    break;
                case "REFUSE":
                    couleur = Color.parseColor("#F44336"); // Rouge
                    break;
                case "EN ATTENTE":
                default:
                    couleur = Color.parseColor("#FF9800"); // Orange
                    break;
            }

            // On applique la couleur à la bulle (en créant un cercle)
            GradientDrawable shape = new GradientDrawable();
            shape.setShape(GradientDrawable.OVAL);
            shape.setColor(couleur);
            bulle.setBackground(shape);

            return convertView;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulter_demande);

        ListView listeDemande = findViewById(R.id.listeDemande);
        liste = findViewById(R.id.filtreDemande);

        apikey = getIntent().getStringExtra("APIKEY");
        adapter = new DemandeAdapter(this, listeAffichee);
        listeDemande.setAdapter(adapter);
        listeDemande.setOnItemClickListener((parent, view, position, id) -> {
            Demande d = listeAffichee.get(position);

            // Création de l'intention pour changer de page
            Intent intent = new Intent(ConsulterDemandeActivity.this, DemandeActivity.class);

            // Passage des paramètres
            intent.putExtra("APIKEY", apikey);
            // On transforme l'int en String car DemandeActivity attend un String pour id_demande
            intent.putExtra("ID_DEMANDE", String.valueOf(d.id));

            startActivity(intent);
        });

        if (apikey != null) {
            recupererTypesDemandes();
            recupererMesDemandes();
        } else {
            Toast.makeText(this, "Erreur : Clé API manquante", Toast.LENGTH_SHORT).show();
        }
    }

    private void recupererTypesDemandes() {
        String URL = "http://10.96.33.12/index.php?demande=typeDemandes";

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                response -> {
                    try {
                        if (response.getString("status").equals("OK")) {
                            typesSpinner.clear();
                            typesSpinner.add(new Type(-2, "Tout"));
                            typesSpinner.add(new Type(-1, "Demande Libre"));
                            JSONArray typesArray = response.getJSONArray("types");
                            for (int i = 0; i < typesArray.length(); i++) {
                                JSONObject type = typesArray.getJSONObject(i);
                                typesSpinner.add(new Type(
                                        type.getInt("id_type"),
                                        type.getString("nom_type")
                                ));
                            }
                            ArrayAdapter<Type> spinnerAdapter = new ArrayAdapter<>(
                                    this,
                                    android.R.layout.simple_spinner_item,
                                    typesSpinner
                            );
                            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            liste.setAdapter(spinnerAdapter);
                            liste.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                                    appliquerFiltre(position);
                                }
                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {}
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Erreur types", Toast.LENGTH_LONG).show()
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("apikey", apikey);
                return headers;
            }
        };
        AppelAPI.getInstance(this).addToRequestQueue(request);
    }

    private void recupererMesDemandes() {
        String URL = "http://10.96.33.12/index.php?demande=mesDemandes";
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                URL,
                null,
                response -> {
                    try {
                        if (response.getString("status").equals("OK")) {
                            listeComplete.clear();
                            JSONArray demandes = response.getJSONArray("demandes");

                            for (int i = 0; i < demandes.length(); i++) {
                                JSONObject demande = demandes.getJSONObject(i);

                                Integer id = null;
                                if (demande.has("id_demande") && !demande.isNull("id_demande")) {
                                    id = demande.getInt("id_demande");
                                }

                                Integer idType = null;
                                if (demande.has("id_type") && !demande.isNull("id_type")) {
                                    idType = demande.getInt("id_type");
                                }

                                String nom = "Demande Libre";
                                if (demande.has("nom") && !demande.isNull("nom")) {
                                    String nomApi = demande.getString("nom").trim();
                                    if (!nomApi.isEmpty()) {
                                        nom = nomApi;
                                    }
                                }

                                String status = demande.getString("status");
                                listeComplete.add(new Demande(id, idType, nom, status));
                            }
                            appliquerFiltre(0);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Erreur demandes", Toast.LENGTH_LONG).show()
        ) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("apikey", apikey);
                return headers;
            }
        };
        AppelAPI.getInstance(this).addToRequestQueue(request);
    }

    private void appliquerFiltre(int position) {
        if (typesSpinner.isEmpty() || position < 0 || position >= typesSpinner.size()) return;

        listeAffichee.clear();
        Type typeSelectionne = typesSpinner.get(position);

        for (Demande d : listeComplete) {
            if (typeSelectionne.id == -2) {
                listeAffichee.add(d);
            } else if (typeSelectionne.id == -1) {
                if (d.idType == null) {
                    listeAffichee.add(d);
                }
            } else {
                if (d.idType != null && d.idType.equals(typeSelectionne.id)) {
                    listeAffichee.add(d);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }
}