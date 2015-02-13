package charlie.flickrrocket;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * AUTHOR:  Charlie Albright
 * CREATED: 2/7/2015
 * NOTES:   This class takes the submitted search tag and makes a request to the Flickr API to
 *          gather the details of all images tagged with the submitted tag. Images themselves are
 *          fetched through another AsyncTask class.
 */
public class JSONRequest extends AsyncTask < String, Void, String > {

    private MainActivity callerActivity;

    public JSONRequest(MainActivity activity) {
        callerActivity = activity;
    }

    //  Executes HTTP request to Flickr API with the given search tag
    //  1.  Gets the search tag from params
    //  2.  Executes HTTP Request
    //  3.  Sends the input stream to accessory function for JSON building
    //  4.  Sends JSON string to onPostExecute()
    @Override
    protected String doInBackground(String... params) {
        String result = "";
        String tag = params[0].toLowerCase().trim().replace(" ", "");

        try {
            URL url = new URL("https://api.flickr.com/services/rest/?format=json&sort=random&method=flickr.photos.search&tags="
                    + tag + "&tag_mode=all&api_key=0e2b6aaf8a6901c264acb91f151a3350&nojsoncallback=1");
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(10000);

            final int statusCode = connection.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK) {
                Log.d("WEB", "The JSONRequest failed with a status code of " + statusCode + ".");
            } else {
                InputStream inputStream = new BufferedInputStream(connection.getInputStream());
                result = getResponseText(inputStream);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    //  When the HTTP Request is done, this sends information back to the main activity and updates
    //  some UI elements.
    //  1.  sends raw JSON string to compileImageData() on main activity
    //  2.  hides the progress screen since the web request is done
    @Override
    protected void onPostExecute(String result) {
        callerActivity.compileImageData(result);
        callerActivity.hideProgressScreen();
    }

    //  This function parses the given InputStream into a valid JSON string
    private String getResponseText(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder stringBuilder = new StringBuilder();
        String line = reader.readLine();
        while (line != null) {
            stringBuilder.append(line + "\n");
            line = reader.readLine();
        }
        return stringBuilder.toString();
    }
}
