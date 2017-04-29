package app.compsci702g6.Activities;


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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import app.compsci702g6.R;
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
                final String searchTerm = food_name.getText().toString();
                final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);

                final TextView api_response = (TextView) findViewById(R.id.api_response);

                RequestParams params = new RequestParams();
                params.put("api_key","u6R9BcFRIIuVJp6AMKwwQpGaawm9pEgJNuDJ2VlC");
                params.put("format", "json");
                params.put("q", searchTerm);
                params.put("max", "1"); // The api call will return only one result.


                AsyncHttpClient client = new AsyncHttpClient();
                client.get("https://api.nal.usda.gov/ndb/search", params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try{
                            String response = new String(responseBody);
                            //Log.d("response header", response);
                            JSONObject obj = new JSONObject(response);
                            JSONObject list = obj.getJSONObject("list");
                            JSONArray items = list.getJSONArray("item");
                            JSONObject result = items.getJSONObject(0);
                            String ndbno = result.getString("ndbno");
                            getFoodReport(ndbno,searchTerm);
                            //api_response.setText(foodReport.toString());
                            progressBar.setVisibility(View.GONE);

                        } catch (JSONException e){

                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    }

                    public void getFoodReport(String ndbno, final String foodName){
                        RequestParams report_params = new RequestParams();
                        report_params.put("api_key","u6R9BcFRIIuVJp6AMKwwQpGaawm9pEgJNuDJ2VlC");
                        report_params.put("format", "json");
                        report_params.put("ndbno", ndbno);

                        AsyncHttpClient report_client = new AsyncHttpClient();
                        report_client.get("https://api.nal.usda.gov/ndb/reports", report_params, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                String response = new String(responseBody);
                                try {
                                    JSONObject obj = new JSONObject(response);
                                    JSONObject report = obj.getJSONObject("report");
                                    JSONObject food = report.getJSONObject("food");
                                    JSONArray nutrients = food.getJSONArray("nutrients");

                                    for (int i=0;i<nutrients.length();i++){
                                        JSONObject nutrient = nutrients.getJSONObject(i);
                                        if (nutrient.getString("name").equals("Energy")){
                                            double kcal = nutrient.getDouble("value");
                                            api_response.setText("There are " +kcal + " calories in 100g of " + foodName);
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                            }
                        });
                    }
                });
            }
        });


    }



}
