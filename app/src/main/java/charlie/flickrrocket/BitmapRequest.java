package charlie.flickrrocket;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by charlie on 2/10/15.
 */
public class BitmapRequest extends AsyncTask < String, Void, Bitmap > {

    private MainActivity callerActivity;

    public BitmapRequest(MainActivity activity) {
        callerActivity = activity;
    }

    //  Executes an HTTP request to get a Bitmap from a web URL.
    //  1.  Gathers the id, farm, server, and secret values from params
    //  2.  Inserts values into template url and executes request
    //  3.  Decodes input stream into a Bitmap using BitmapFactory
    //  4.  Sends Bitmap to onPostExecute()
    @Override
    protected Bitmap doInBackground(String... params) {
        Bitmap result = null;
        String id = params[0];
        String farm = params[1];
        String server = params[2];
        String secret = params[3];

        try {
            URL url = new URL("http://farm" + farm + ".static.flickr.com/" + server + "/" + id + "_" + secret + "_m.jpg");
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(10000);

            final int statusCode = connection.getResponseCode();
            if (statusCode != HttpURLConnection.HTTP_OK) {
                Log.d("WEB", "The JSONRequest failed with a status code of " + statusCode + ".");
            } else {
                InputStream inputStream = connection.getInputStream();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inScaled = false;
                result = BitmapFactory.decodeStream(inputStream, null, options);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    //  This function takes the given Bitmap and sends it to the main activity so it can be put into
    //  the main UI
    @Override
    protected void onPostExecute(Bitmap result) {
        callerActivity.displayImage(result);
    }
}
