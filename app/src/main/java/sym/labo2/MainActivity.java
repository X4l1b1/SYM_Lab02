package sym.labo2;

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
    public static final String EXTRA_MESSAGE = "ch.heigvd.sym.template.myapp.MAIL";

    // Buttons
    private Button asyncB = null;
    private Button diffB  = null;
    private Button objB   = null;
    private Button comprB = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Link to GUI elements -- defined in res/layout/
        this.asyncB = (Button)findViewById(R.id.asyncActivityButton);
        this.diffB  = (Button)findViewById(R.id.diffActivityButton);
        this.objB   = (Button)findViewById(R.id.objActivityButton);
        this.comprB = (Button)findViewById(R.id.comprActivityButton);

        asyncB.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, sym.labo2.AsyncActivity.class);
                //   intent.putExtra(EXTRA_MESSAGE, "Async");

                //   MainActivity.this.startActivityForResult(intent, 1);
                MainActivity.this.startActivity(intent);
                // Wrong combination, display pop-up dialog and stay on login screen
                //    showErrorDialog(??, ??);
            }


        });

        diffB.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, DelayedActivity.class);
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

        comprB.setOnClickListener(new OnClickListener(){

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, CompressedActivity.class);
                //   intent.putExtra(EXTRA_MESSAGE, "Compr");

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

