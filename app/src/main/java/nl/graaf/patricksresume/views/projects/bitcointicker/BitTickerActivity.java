package nl.graaf.patricksresume.views.projects.bitcointicker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import nl.graaf.patricksresume.R;
import nl.graaf.patricksresume.views.helpers.BaseActivity;
import timber.log.Timber;

public class BitTickerActivity extends BaseActivity {
    // Constants:
    private final String BASE_URL = "https://apiv2.bitcoinaverage.com/indices/global/ticker/BTC%s";

    // Member Variables:
    private TextView mPriceTextView;

    public static Intent getStartIntent(Context context) {
        return new Intent(context, BitTickerActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bit_ticker);

        mPriceTextView = findViewById(R.id.priceLabel);
        Spinner spinner = findViewById(R.id.currency_spinner);

        // Create an ArrayAdapter using the String array and a spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.currency_array, R.layout.spinner_item_bitcoin);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.spinner_bitcoin_dropdown_item);

        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Timber.d("%s", adapterView.getItemAtPosition(i));
                letsDoSomeNetworking(String.format(BASE_URL, adapterView.getItemAtPosition(i)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Timber.d("Nothing selected.");
            }
        });
    }

    private void letsDoSomeNetworking(String url) {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                // called when response HTTP status is "200 OK"
                Timber.d("JSON: " + response.toString());
                try {
                    mPriceTextView.setText(response.getString("last"));
                } catch (JSONException e) {
                    Timber.e(e, "Failed to parse JSON from %s", url);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable e,
                                  JSONObject response) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                Timber.d("Request fail! Status code: " + statusCode);
                Timber.d("Fail response: " + response);
                Timber.e(e, "ERROR while calling Bitcoin API");
                Snackbar.make(findViewById(R.id.content), "Request Failed",
                        Snackbar.LENGTH_SHORT).show();
            }
        });
    }


}
