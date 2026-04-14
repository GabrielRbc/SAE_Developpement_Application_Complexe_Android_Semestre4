package iut.semestre4.demandroid.models;

import android.content.Context;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;

/**
 * Gestionnaire d'appels API
 *
 * <p>
 *     Cette classe permet de gérer une file d'attente unique (RequestQueue)
 *     pour toute l'application.
 * </p>
 *
 * @author enzo.dumas
 *         gabriel.robache
 *         lola.delperie
 *         toan.hery
 */
public class AppelAPI {

    /** Instance unique du Singleton */
    private static AppelAPI instance;

    /** File d'attente des requêtes Volley */
    private RequestQueue requestQueue;

    /** Contexte de l'application */
    private static Context ctx;

    /**
     * Initialise la file d'attente des requêtes au premier appel.
     *
     * @param context Le contexte de l'activité ou de l'application.
     */
    private AppelAPI(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();
    }

    /**
     * Récupère ou crée l'instance unique de la classe AppelAPI.
     *
     * @param context Le contexte utilisé pour initialiser Volley (utilisera l'ApplicationContext).
     * @return L'instance unique de {@link AppelAPI}.
     */
    public static AppelAPI getInstance(Context context) {
        if (instance == null) {
            instance = new AppelAPI(context);
        }
        return instance;
    }

    /**
     * Récupère la file d'attente des requêtes Volley (RequestQueue).
     * Si la file n'existe pas, elle est créée en utilisant le contexte global de l'application.
     *
     * @return La {@link RequestQueue} active de l'application.
     */
    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    /**
     * Ajoute une requête générique à la file d'attente Volley.
     *
     * @param <T> Le type de réponse attendu (JSONObject, JSONArray, String, ...).
     * @param req La requête à ajouter à la file d'attente.
     */
    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    /**
     * Envoie une requête GET pour récupérer un tableau JSON (JSONArray).
     *
     * @param url           L'URL complète du endpoint.
     * @param listener      Le callback à exécuter en cas de succès (reception du JSONArray).
     * @param errorListener Le callback à exécuter en cas d'échec de la requête.
     */
    public void getTableauJSON(String url, Response.Listener<JSONArray> listener, Response.ErrorListener errorListener) {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, listener, errorListener);
        addToRequestQueue(request);
    }
}