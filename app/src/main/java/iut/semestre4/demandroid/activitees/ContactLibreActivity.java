package iut.semestre4.demandroid.activitees;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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

public class ContactLibreActivity extends AppCompatActivity {

    private String apikey;

    private EditText text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_libre);

        apikey = getIntent().getStringExtra("APIKEY");

        text = findViewById(R.id.textDemande);

        if (apikey == null) {
            Toast.makeText(this, "Erreur : Clé API manquante", Toast.LENGTH_SHORT).show();
        }

    }

    public void clicEnvoyerContact(View view) {

        String contenu = text.getText().toString().trim();

        if (contenu.isEmpty()) {
            Toast.makeText(this, "Veuillez saisir le contenu", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONObject body = new JSONObject();
            body.put("apikey", apikey);
            body.put("contenu", contenu);

            String URL_SET_DEMANDE = "http://10.96.33.12/index.php?demande=setDemande";
            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    URL_SET_DEMANDE,
                    body,
                    response -> {
                        try {
                            if (response.getString("status").equals("OK")) {
                                Toast.makeText(this, "Demande envoyée !", Toast.LENGTH_SHORT).show();

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
