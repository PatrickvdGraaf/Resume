package nl.graaf.patricksresume.views.projects.clima;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import nl.graaf.patricksresume.R;
import nl.graaf.patricksresume.views.helpers.BaseActivity;

public class ChangeCityActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clima_change_city_layout);
        final EditText editTextField = findViewById(R.id.queryET);

        ImageButton imageButtonBack = findViewById(R.id.backButton);
        imageButtonBack.setOnClickListener(view -> finish());

        //TODO change to activityForResult
        editTextField.setOnEditorActionListener((textView, i, keyEvent) -> {
            String newCity = editTextField.getText().toString();
            Intent newCityIntent = new Intent(ChangeCityActivity.this,
                    ClimaActivity.class);
            newCityIntent.putExtra("City", newCity);
            startActivity(newCityIntent);
            return false;
        });
    }
}
