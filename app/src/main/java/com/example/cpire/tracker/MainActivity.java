package com.example.cpire.tracker;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toolbar;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    String APIUrl = "https://api.coindesk.com/v1/bpi/currentprice.json";
    String HttpGetResult;
    HttpGet getRequest = new HttpGet();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView main_dollars =(TextView) findViewById(R.id.main_dollars);
        final TextView main_euros =(TextView) findViewById(R.id.main_euros);
        final TextView main_time =(TextView) findViewById(R.id.main_time);

        try {
            HttpGetResult = getRequest.execute(APIUrl).get();
            main_time.setText("Time : " + getTime(HttpGetResult));
            main_dollars.setText("Price $ :" + getPrice(HttpGetResult, true));
            main_euros.setText("Price € :" + getPrice(HttpGetResult, false));
        }catch (Exception e){
            e.printStackTrace();
        }

        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(60000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                HttpGet getRequest = new HttpGet();
                                try {
                                    HttpGetResult = getRequest.execute(APIUrl).get();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                main_time.setText("Updated: " + getTime(HttpGetResult));
                                main_dollars.setText("Price $:" + getPrice(HttpGetResult, true));
                                main_euros.setText("Price €:" + getPrice(HttpGetResult, false));
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    System.out.println("Error :" + e);
                }
            }
        };

        t.start();

    }

    String getTime (String time) {
        String stringTime;
        String stringUpdated = "noUpdate";

        try{
            JSONObject timeObject = new JSONObject(time);
            stringTime = timeObject.getString("time");
            JSONObject updatedObject = new JSONObject(stringTime);
            stringUpdated = updatedObject.getString("updated");
        }catch (JSONException e){
            System.out.println("Error: " + e);
        }
        return stringUpdated;
    }

    String getPrice (String price, Boolean Dollar) {
        String stringBpi;
        String stringCurrency;
        String stringRate = "noRate";
        try{
            JSONObject timeObject = new JSONObject(price);
            stringBpi = timeObject.getString("bpi");
            JSONObject BpiObject = new JSONObject(stringBpi);
            if (Dollar){
                stringCurrency = BpiObject.getString("USD");
                JSONObject USDObject = new JSONObject(stringCurrency);
                stringRate =  USDObject.getString("rate");
            }else{
                stringCurrency = BpiObject.getString("EUR");
                JSONObject USDObject = new JSONObject(stringCurrency);
                stringRate =  USDObject.getString("rate");
            }
        }catch (JSONException e){
            System.out.println("Error: " + e);
        }

        return stringRate;
    }

}
