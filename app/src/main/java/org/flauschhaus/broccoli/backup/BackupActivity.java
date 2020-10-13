package org.flauschhaus.broccoli.backup;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class BackupActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toast.makeText(this, "Huhu!", Toast.LENGTH_LONG).show();
        finish();
    }

}
