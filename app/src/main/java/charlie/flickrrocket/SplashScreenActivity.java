package charlie.flickrrocket;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;


public class SplashScreenActivity extends ActionBarActivity {

    private static int SPLASH_LENGTH = 4000;
    private LinearLayout appNameLabel;

    //  A basic splash screen that fades in the app name
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        appNameLabel = (LinearLayout)findViewById(R.id.appNameLabel);
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        appNameLabel.startAnimation(anim);
        anim.setDuration(3000);
        anim.setFillAfter(true);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_LENGTH);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
