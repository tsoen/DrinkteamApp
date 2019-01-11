package drinkteam._old;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadWebpageTask extends AsyncTask<String, Void, String> {
	
	// Object qui définit l'action à réaliser une fois la tâche terminée //
	private AsyncResult callback;
	
	/**
	 * Constructeur - Appelle doInBackground
	 * @param callback Object qui définit l'action à réaliser une fois la tâche terminée
	 */
	public DownloadWebpageTask(AsyncResult callback) {
		this.callback = callback;
	}
	
	/**
	 * Traitement à réaliser
	 * Appelé par DownloadWebpageTask.execute()
	 * @param urls URLs des fichiers à lire - nombre de paramètres (URLs) indéfinis
	 * @return Contenu texte du fichier
	 */
	@Override
	protected String doInBackground(String... urls) {
		try {
			return downloadUrl(urls[0]);
		}
		catch (IOException e) {
			return "Unable to download the requested page.";
		}
	}
	
	/**
	 * Appelé à la fin du traitement
	 * @param result String retournée par doInBackground()
	 */
	@Override
	protected void onPostExecute(String result) {
		
		// supprime les headers inutiles //
		int start = result.indexOf("{", result.indexOf("{") + 1);
		int end = result.lastIndexOf("}");
		String jsonResponse = result.substring(start, end);
		
		try {
			// parse le contenu du fichier en JSON et déclenche le traitement final //
			JSONObject table = new JSONObject(jsonResponse);
			callback.onResult(table);
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Se connecte à un fichier, le lit et renvoi son contenu sous forme de texte
	 * @param urlString URL du fichier à lire
	 * @return Le contenu du fichier sous forme de texte
	 * @throws IOException Exception
	 */
	private String downloadUrl(String urlString) throws IOException {
		InputStream inputStream = null;
		try {
			
			// connexion au fichier à l'URL donnée //
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			
			// timeout pour se connecter au fichier (millisecondes) //
			conn.setConnectTimeout(15000);
			
			// timeout pour lire le fichier (millisecondes) //
			conn.setReadTimeout(10000);
			
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			
			conn.connect();
			inputStream = conn.getInputStream();
			
			return convertStreamToString(inputStream);
		}
		finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
	}
	
	/**
	 * Lit un fichier et renvoi son contenu sous forme de texte
	 * @param inputStream Flux de lecture du fichier
	 * @return Le contenu du fichier sous forme de texte
	 */
	private String convertStreamToString(InputStream inputStream) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		String res = "";
		String line;
		try {
			while ((line = reader.readLine()) != null) {
				res += line + "\n";
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				inputStream.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return res;
	}
}
