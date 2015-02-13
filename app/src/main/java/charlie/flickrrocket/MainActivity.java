package charlie.flickrrocket;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends ActionBarActivity {

    //UI Elements
    private EditText searchTag;
    private Button searchButton;
    private Button nextButton;
    private Button prevButton;
    private ImageView imageView;
    private RelativeLayout progressScreen;
    private TextView imageDescription;

    //Singleton data class
    private ImageModel imageModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        searchButton = (Button)findViewById(R.id.searchButton);
        nextButton = (Button)findViewById(R.id.nextButton);
        prevButton = (Button)findViewById(R.id.prevButton);
        imageView = (ImageView)findViewById(R.id.imageView);
        progressScreen = (RelativeLayout)findViewById(R.id.mainLoadingScreen);
        imageDescription = (TextView)findViewById(R.id.imageDescription);
        searchTag = (EditText)findViewById(R.id.searchTag);
        searchTag.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_GO) {
                    //execute search when user presses "GO" on software keyboard
                    displayProgressScreen();
                    executeJSONRequest(searchTag.getText().toString());
                }
                return false;
            }
        });

        imageModel = ImageModel.getInstance();

        if (savedInstanceState == null) {
            //if this is the first time onCreate() is called, it will automatically execute a search with tag "rocket"
            executeJSONRequest(searchTag.getText().toString());
        } else {
            //this will be called on a configuration change, such as a screen rotation
            if (imageModel != null && imageModel.hasBitmap()) {
                hideProgressScreen();
                reloadImage();
            } else {
                executeJSONRequest(imageModel.getLastSearchTag());
            }
        }

    }

    //  Executes search of Flickr API with the specified tag (default "rocket" if not set)
    public void executeJSONRequest(String tag) {
        imageModel.clear();
        if (tag.isEmpty()) {
            imageModel.setLastSearchTag("rocket");
            new JSONRequest(this).execute("rocket");
        } else {
            imageModel.setLastSearchTag(tag);
            new JSONRequest(this).execute(tag);
        }
    }

    //  onClick() method of "Search!" button on main UI.
    //  1.  Hides software keyboard if present
    //  2.  Clears the imageView if filled
    //  3.  Displays progress screen and disables UI clicks
    //  4.  Executes custom AsyncTask
    public void searchButtonClicked(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager)  this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        imageView.setImageBitmap(null);
        displayProgressScreen();
        disableClicks();
        executeJSONRequest(searchTag.getText().toString());
    }

    //  onClick() method of ImageView on main UI.
    //  1.  Calls function to load next Image in the list (if the list is not null and is not empty)
    public void imageViewClicked(View v) {
        if(imageModel != null && !imageModel.isEmpty())
            loadNextImage(1);
    }

    //  onClick() method of "Next" on main UI.
    //  1.  Calls function to load next Image in the list (if the list is not null and is not empty)
    public void nextButtonClicked(View v) {
        if(imageModel != null && !imageModel.isEmpty())
            loadNextImage(1);
    }

    //  onClick() method of "Previous" on main UI.
    //  1.  Calls function to load previous Image in the list (if the list is not null and is not empty)
    public void prevButtonClicked(View v) {
        if(imageModel != null && !imageModel.isEmpty())
            loadNextImage(-1);
    }

    //  This function is called in the onPostExecute() function of the JSONRequest class.
    //  1.  Receives JSON string and parses that into a JSON object.
    //  2.  The array of photo data is pulled from the object and put in a JSONArray
    //  3.  Iterate through the JSONArray and add the photo data to the Singleton ImageModel class
    //  4.  If no results were added to the array, Toast is displayed stating such
    //  5.  Else, the first image in the ImageModel is automatically loaded into the imageView
    public void compileImageData(String json) {
        try {
            JSONObject object = new JSONObject(json);
            JSONObject photos = object.getJSONObject("photos");
            JSONArray photoArray = photos.getJSONArray("photo");
            for (int i = 0; i < photoArray.length(); i++) {
                JSONObject photo = photoArray.getJSONObject(i);

                String id = photo.getString("id");
                String farm = photo.getString("farm");
                String server = photo.getString("server");
                String secret = photo.getString("secret");
                String description = photo.getString("title");

                if (description.isEmpty() || description == null)
                    description = "No Description";

                imageModel.add(new ImageData(id, farm, server, secret, description));
            }
            if (imageModel.isEmpty()) {
                Toast.makeText(this, "That tag produced no results.", Toast.LENGTH_SHORT).show();
                imageView.setImageBitmap(null);
                imageDescription.setText("");
                enableClicks();
            } else {
                loadNextImage(0);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //  Takes an ImageData object from the ImageModel based on the indexChange variable and executes
    //  the custom AsyncTask, BitmapRequest.
    //  1.  imageView is faded out to smooth transition between bitmaps
    //  2.  ImageData object is fetched from the singleton ImageModel
    //  3.  the description label in the UI is set to the description text in the ImageData object
    //  4.  description text is saved to ImageModel to be used to repopulate UI if screen is rotated
    //  5.  BitmapRequest AsyncTask is executed
    public void loadNextImage(int indexChange) {

        disableClicks();
        AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        imageView.startAnimation(fadeOut);
        fadeOut.setDuration(350);
        fadeOut.setFillAfter(true);

        String[] nextImage = imageModel.getNextImage(indexChange).toStringArray();
        imageDescription.setText(nextImage[4]);
        imageModel.setLastImageDescription(nextImage[4]);
        new BitmapRequest(this).execute(nextImage);
    }

    //  This is called when the UI is recreated due to a screen rotation
    //  1.  Reloads the saved Bitmap and description text from the singleton ImageModel
    public void reloadImage() {
        imageView.setImageBitmap(imageModel.getCurrentBitMap());
        imageDescription.setText(imageModel.getLastImageDescription());
    }

    //  This function loads the given Bitmap into the imageView. Called by the onPostExecute()
    //  function in the BitMapRequest AsyncTask.
    //  1.  Sets the imageView's current image as the given Bitmap
    //  2.  Saves the Bitmap into the singleton class ImageModel to be used to repopulate the UI if
    //      screen is rotated
    //  3.  Fade in the imageView
    //  4.  re-enable clicks on the UI
    public void displayImage(Bitmap bmp) {

        imageView.setImageBitmap(bmp);
        imageModel.setCurrentBitmap(bmp);
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        imageView.startAnimation(fadeIn);
        fadeIn.setDuration(350);
        fadeIn.setFillAfter(true);
        enableClicks();
    }

    //  Hides the progress screen
    public void displayProgressScreen() {
        progressScreen.setVisibility(View.VISIBLE);
    }

    //  Shows the progress screen
    public void hideProgressScreen() {
        progressScreen.setVisibility(View.GONE);
    }

    //  Disables UI clicks on certain elements to prevent button spamming
    public void disableClicks() {
        nextButton.setClickable(false);
        prevButton.setClickable(false);
        imageView.setClickable(false);
        searchButton.setClickable(false);
    }

    //  Re-enables UI clicks
    public void enableClicks() {
        nextButton.setClickable(true);
        prevButton.setClickable(true);
        imageView.setClickable(true);
        searchButton.setClickable(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
