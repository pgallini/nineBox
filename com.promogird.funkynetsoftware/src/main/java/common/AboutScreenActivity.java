package common;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.promogird.funkynetsoftware.R;

/**
 * Created by Paul Gallini on 2/22/17.
 *
 * Display the About screen
 */
public class AboutScreenActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Bundle extras = getIntent().getExtras();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_app);

        findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View view) {
                                                                    finish();
                                                                }
                                                            }
        );
    }

}
