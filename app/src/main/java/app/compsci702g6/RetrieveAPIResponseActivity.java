package app.compsci702g6;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Jay on 4/22/2017.
 */

public class RetrieveAPIResponseActivity extends AppCompatActivity {


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api_request);

        Button searchButton = (Button) findViewById(R.id.searchButton);

        searchButton.setOnClickListener(new Button.OnClickListener(){

            @Override
            public void onClick(View v) {
                EditText food_name = (EditText) findViewById(R.id.food_name);
                String searchTerm = food_name.getText().toString();
                final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);

                final TextView api_response = (TextView) findViewById(R.id.api_response);

                RequestParams params = new RequestParams();
                params.put("api_key","u6R9BcFRIIuVJp6AMKwwQpGaawm9pEgJNuDJ2VlC");
                params.put("format", "json");
                params.put("q", searchTerm);
                params.put("max", "5");


                AsyncHttpClient client = new AsyncHttpClient();
                client.get("https://api.nal.usda.gov/ndb/search", params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        progressBar.setVisibility(View.GONE);
                        try{
                            String response = new String(responseBody);
                            Log.d("response header", response);
                            JSONObject obj = new JSONObject(response);
                            Log.d("objToString", obj.toString());
                            api_response.setText(response);
                        } catch (JSONException e){

                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    }
                });
            }
        });


    }

}
