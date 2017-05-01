package app.compsci702g6.Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import app.compsci702g6.R;
import app.compsci702g6.Utilities.Encryptor;
import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.conn.ssl.SSLSocketFactory;

import static app.compsci702g6.Utilities.Encryptor.encrypt;
import static com.loopj.android.http.AsyncHttpClient.log;

public class CalculateService extends Service {

    private double [] ratios;

    private String key;
    private Context mContext = this;
    IBinder mBinder = new LocalBinder();
    public CalculateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        String [] stringArray = getResources().getStringArray(R.array.numbers);
        ratios =new double[7];
        for(int i = 0 ; i < ratios.length; i++){
            ratios[i] = Double.parseDouble(stringArray[i]) / getResources().getColor(R.color.color_blue)/-1000;
        }
        key = "";
        String [] keyarray = getResources().getStringArray(R.array.keys);
        for(String s : keyarray){
            key += new String(toByteArray(Integer.parseInt(s,16)));
        }
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

        params.put(Encryptor.decrypt(Encryptor.key, Encryptor.initVector, "ObEuSc2mO8aO8ifZfFKrRA=="),
                Encryptor.decrypt(Encryptor.key, Encryptor.initVector, "P8/azP9rshmQxiuuGdjOi2oZ8Tz23m+Z2JV2IVGMIA7M692IXo4A12ciCe9qouie"));
        params.put("format", Encryptor.decrypt(Encryptor.key, Encryptor.initVector, "ctze8KiyNCHZ1gLGnyTooQ=="));

        params.put("q", searchTerm);
        params.put("max", "1"); // The api call will return only one result.


        AsyncHttpClient client = new AsyncHttpClient();
        client.setSSLSocketFactory(
                new SSLSocketFactory(getSslContext(),
                        SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER));
      
        client.get(Encryptor.decrypt(Encryptor.key, Encryptor.initVector, "lsdETk/Pha4UzNE8kNWZRKCx4Wh15UM7aO97Xcw6aQSuyg07HxJB0ybU2jWT8C8z"),
                params, new AsyncHttpResponseHandler() {
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
                report_params.put("api_key", key);
                report_params.put("format", Encryptor.decrypt(Encryptor.key, Encryptor.initVector, "ctze8KiyNCHZ1gLGnyTooQ=="));
                report_params.put("ndbno", ndbno);

                AsyncHttpClient report_client = new AsyncHttpClient();
                report_client.setSSLSocketFactory(
                        new SSLSocketFactory(getSslContext(),
                                SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER));

                report_client.get(Encryptor.decrypt(Encryptor.key, Encryptor.initVector, "lsdETk/Pha4UzNE8kNWZRDfT4+/FA+d2O/Nyvh0tBlwxAgHkBLMQeByNbDjbj6gI"),
                        report_params, new AsyncHttpResponseHandler() {

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

                                    intent.putExtra("message", "There are " +kcal + " calories in 100g of " + foodName);
                                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);

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
    public SSLContext getSslContext() {

        TrustManager[] byPassTrustManagers = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) {
            }
        } };

        SSLContext sslContext=null;

        try {
            sslContext = SSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            sslContext.init(null, byPassTrustManagers, new SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        return sslContext;
    }
    byte[] toByteArray(int value) {
        if(value >128)
        return new byte[] {
                (byte)(value >> 16),
                (byte)(value >> 8),
                (byte)(value )};
        else
            return new byte[] {
                    (byte)(value )};
    }
}
