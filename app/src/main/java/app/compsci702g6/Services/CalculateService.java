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

import app.compsci702g6.R;
import app.compsci702g6.Utilities.Encryptor;
import cz.msebera.android.httpclient.Header;

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
        log.e("bbb",key);
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

//        String s = "u6R9BcFRIIuVJp6AMKwwQpGaawm9pEgJNuDJ2VlC";
//        String s1 = "u6R9BcFRIIu";
//        String s2 ="VJp6AMKw";
//        String s3 = "wQpGaawm9p";
//        String s4 ="EgJNuDJ2VlC";;
//        byte[] b = s.getBytes();
//        while(s.length()>0){
//            if(s.length()>3){
//                //log.e("bbb",String.valueOf(ByteBuffer.wrap(s.substring(0,3).getBytes()).getInt()));
//                byte[] sb = s.substring(0,3).getBytes();
////                for(byte bb : sb){
////                    log.e("bbb",String.valueOf(bb));
////                }
//                int in =  fromByteArray(sb);
//               // log.e("bbb",String.valueOf(in));
//                log.e("bbb",Integer.toHexString(in));
//                log.e("bbb",new String(toByteArray(in)));
//                //s = "";
//                s = s.substring(3,s.length());
//            }
//            else {
//
//                byte[] sb = s.substring(0,s.length()).getBytes();
////                for(byte bb : sb){
////                    log.e("bbb",String.valueOf(bb));
////                }
//                int in =  fromByteArray(sb);
//                // log.e("bbb",String.valueOf(in));
//                log.e("bbb",Integer.toHexString(in));
//                log.e("bbb",new String(toByteArray(in)));
//                //s = "";
//                //log.e("bbb",new String(s.substring(0,s.length()).getBytes()));
//                s = "";
//            }
//        }
//        for(byte y : b ){
//            log.e("aaa",String.valueOf(y));
//        }
//        for(int i  = 0 ; i < s.length();i++){
//            log.e("aaa",String.valueOf(Character.digit(s.charAt(i),10)));
//        }
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
                report_params.put("api_key", key);
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

                                    String encrypted = encrypt(Encryptor.key, Encryptor.initVector, "Hello World");
                                    String decrypted = Encryptor.decrypt(Encryptor.key, Encryptor.initVector, encrypted);
                                    String finalString = "Encrypted: " + encrypted + " Decrypted: " + decrypted;

                                    intent.putExtra("message", "There are " +kcal + " calories in 100g of " + foodName + ". " + finalString);
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
    int fromByteArray(byte[] bytes) {
        if(bytes.length>1)
        return bytes[0] << 16 | (bytes[1] & 0xFF) << 8 | (bytes[2] & 0xFF) ;
        else
            return bytes[0]  & 0xFF ;
    }

}
