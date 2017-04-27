package app.compsci702g6;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import static com.loopj.android.http.AsyncHttpClient.log;

public class CalculateService extends Service {

    private final double [] ratios = {0.119,0.170,0.208,0.142,0.109,0.138,0.12};

    private Context mContext = this;
    IBinder mBinder = new LocalBinder();
    public CalculateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    public class LocalBinder extends Binder {
        public CalculateService getServerInstance() {
            return CalculateService.this;
        }
    }
    public double calculateCalories(int weight, int time, int sport){
        return time*weight*ratios[sport];
    }
    public double calculateTime(int weight, int calories,int sport){
        return  calories/(weight*ratios[sport]);
    }
    public void search(String food_name) {

        final String searchTerm = food_name;

        RequestParams params = new RequestParams();
        params.put("api_key", "u6R9BcFRIIuVJp6AMKwwQpGaawm9pEgJNuDJ2VlC");
        params.put("format", "json");
        params.put("q", searchTerm);
        params.put("max", "1"); // The api call will return only one result.


        AsyncHttpClient client = new AsyncHttpClient();
        client.get("https://api.nal.usda.gov/ndb/search", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String response = new String(responseBody);
                    //Log.d("response header", response);
                    JSONObject obj = new JSONObject(response);
                    JSONObject list = obj.getJSONObject("list");
                    JSONArray items = list.getJSONArray("item");
                    JSONObject result = items.getJSONObject(0);
                    String ndbno = result.getString("ndbno");
                    getFoodReport(ndbno, searchTerm);
                    //api_response.setText(foodReport.toString());


                } catch (JSONException e) {

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }

            public void getFoodReport(String ndbno, final String foodName) {
                RequestParams report_params = new RequestParams();
                report_params.put("api_key", "u6R9BcFRIIuVJp6AMKwwQpGaawm9pEgJNuDJ2VlC");
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

                            for (int i = 0; i < nutrients.length(); i++) {
                                JSONObject nutrient = nutrients.getJSONObject(i);
                                if (nutrient.getString("name").equals("Energy")) {
                                    double kcal = nutrient.getDouble("value");
                                    Intent intent = new Intent("api_result");
                                    // add data
                                    intent.putExtra("message", "There are " +kcal + " calories in 100g of " + foodName);
                                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                                    log.e("message", "There are " +kcal + " calories in 100g of " + foodName);
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


}
