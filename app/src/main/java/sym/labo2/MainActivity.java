package sym.labo2;

import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.view.View;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

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



        textB.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, TextActivity.class);
                //   intent.putExtra(EXTRA_MESSAGE, "Diff");

                //   MainActivity.this.startActivityForResult(intent, 1);
                MainActivity.this.startActivity(intent);
                // Wrong combination, display pop-up dialog and stay on login screen
                //    showErrorDialog(??, ??);
            }


        });


        objB.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, ObjectActivity.class);
                //   intent.putExtra(EXTRA_MESSAGE, "Object");

                //   MainActivity.this.startActivityForResult(intent, 1);
                MainActivity.this.startActivity(intent);
                // Wrong combination, display pop-up dialog and stay on login screen
                //    showErrorDialog(??, ??);
            }


        });
    }


    // Creates and display an error dialog
    protected void showErrorDialog(String mail, String passwd) {
		/*
		 * Pop-up dialog to show error
		 */
        AlertDialog.Builder alertbd = new AlertDialog.Builder(this);
        alertbd.setIcon(android.R.drawable.ic_dialog_alert);
        alertbd.setTitle("Test");
        alertbd.setMessage("Test");
        alertbd.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // we do nothing...
                // dialog close automatically

            }
        });
        alertbd.create().show();
    }
}

