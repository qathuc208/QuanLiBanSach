package study.com.quanlibansach.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import study.com.quanlibansach.R;

public class CreateAuthorActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_author);

        final EditText txtFirstName = (EditText) findViewById(R.id.editTextFirstName);
        final EditText txtLastName = (EditText) findViewById(R.id.editTextLastName);
        final Intent intent= getIntent();

        final Button btnClear = (Button) findViewById(R.id.buttonClear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtFirstName.setText("");
                txtLastName.setText("");
                txtFirstName.requestFocus();
            }
        });

        final Button btnInsert = (Button) findViewById(R.id.buttonInsert);
        btnInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent();
                Bundle bundle = new Bundle();

                bundle.putString("firstName", txtFirstName.getText().toString());
                bundle.putString("lastName", txtLastName.getText().toString());
                intent.putExtra("DATA_AUTHOR", bundle);
                setResult(MainActivity.SEND_DATA_FROM_AUTHOR_ACTIVITY, intent);
                CreateAuthorActivity.this.finish();
            }
        });

        Bundle bundle = intent.getBundleExtra("DATA");
        if (bundle != null && bundle.getInt("KEY") == 1) {
            String f2 = bundle.getString("getField2");
            String f3 = bundle.getString("getField3");

            txtFirstName.setText(f2);
            txtLastName.setText(f3);

            btnInsert.setText("UPDATE");
            this.setTitle("View Details");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_create_author, menu);
        return true;
    }
}
