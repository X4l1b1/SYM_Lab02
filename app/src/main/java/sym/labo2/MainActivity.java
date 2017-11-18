package sym.labo2;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.view.View;

/**
 * First activity
 */
public class MainActivity extends AppCompatActivity {

    // For logging purposes
    private static final String TAG = MainActivity.class.getSimpleName();

    // Buttons
    private Button textB  = null;
    private Button objB   = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Link to GUI elements -- defined in res/layout/
        this.textB  = (Button)findViewById(R.id.textActivityButton);
        this.objB   = (Button)findViewById(R.id.objActivityButton);

        //Sets button behaviour
        textB.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, TextActivity.class);
                MainActivity.this.startActivity(intent);
            }


        });

        //Sets button behaviour
        objB.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, ObjectActivity.class);
                MainActivity.this.startActivity(intent);
            }


        });
    }

}

