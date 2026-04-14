package iut.semestre4.demandroid.activitees;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import iut.semestre4.demandroid.R;
import iut.semestre4.demandroid.models.AppelAPI;

public class ConnexionActivity extends AppCompatActivity {

    private EditText champIdentifiant;
    private EditText champPassword;

    private String URL_LOGIN = "http://10.96.33.12/index.php?demande=login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);

        champIdentifiant = findViewById(R.id.userID);
        champPassword = findViewById(R.id.password);
    }

    /**
     * Permet de gérer le clic sur le bouton "Connexion"
     * Connexion de l'utilisateur à son compte en fonction de l'API
     * Est redirigé vers activity_menu_principal.xml
     * @param view
     */
    public void onClicConnexion(View view) {
        String identifiant = champIdentifiant.getText().toString();
        String password = champPassword.getText().toString();

        if (identifiant.isEmpty()) {
            Toast.makeText(
                    ConnexionActivity.this,
                    "Identifiant manquant",
                    Toast.LENGTH_SHORT).show();
                    champPassword.setText("");
            return;
        }
        if (password.isEmpty()) {
            Toast.makeText(
                    ConnexionActivity.this,
                    "Mot de passe manquant",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            JSONObject body = new JSONObject();
            body.put("identifiant", identifiant);
            body.put("password", password);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    URL_LOGIN,
                    body,
                    response -> {

                        try {
                            String status = response.getString("status");
                            if(status.equals("OK")){
                                String apikey = response.getString("apikey");
                                Intent intent = new Intent(
                                        ConnexionActivity.this,
                                        MenuPrincipalActivity.class
                                );
                                intent.putExtra("APIKEY", apikey);
                                champIdentifiant.setText("");
                                champPassword.setText("");
                                startActivity(intent);
                            }else{
                                Toast.makeText(
                                        ConnexionActivity.this,
                                        "Connexion échouée",
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    },
                    error -> {

                        String message = "Erreur inconnue";

                        if (error.networkResponse != null) {
                            message = "Code HTTP : " + error.networkResponse.statusCode;
                        } else {
                            message = error.toString();
                        }

                        Toast.makeText(
                                ConnexionActivity.this,
                                message,
                                Toast.LENGTH_LONG
                        ).show();
                        champPassword.setText("");
                        error.printStackTrace();
                    }
            );
            AppelAPI.getInstance(this).addToRequestQueue(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Permet de gérer le clic sur lien internet
     * Ouvre une nouvelle page internet du site internet
     * @param view
     */
    public void onClicLienInternet(View view) {
        String url = "https://wemember.alwaysdata.net";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }
}