package com.example.cryptov;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class CryptoDataService {
    Context context;

    public CryptoDataService(Context context) {
        this.context = context;
    }

    public interface VolleyResponseListener{
        void onError(String message);

        void onResponse(ArrayList<JSONObject> obj) throws JSONException;
    }


    public void getCoinValue(String coin, String cur, ArrayList<JSONObject> presentList ,VolleyResponseListener volleyResponseListener) throws JSONException {
        String url ="https://api.coingecko.com/api/v3/simple/price?ids="+ coin + "&vs_currencies=" + cur + "&include_24hr_change=true";
        final double[] changeValue = {0};

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onResponse(JSONObject response) {
                        String key ="";
                        ArrayList<JSONObject> coins = new ArrayList<JSONObject>();

                        if(response.isNull(coin)){
                            try {
                                volleyResponseListener.onResponse(coins);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else {
                            Iterator<String> iter =  response.keys();
                            while(iter.hasNext()){
                                key = iter.next();
                                try {
                                    JSONObject temp = response.getJSONObject(key);
                                    temp.accumulate("name", key);
                                    changeValue[0] = temp.getDouble(cur + "_24h_change");
                                    temp.remove(cur + "_24h_change");
                                    temp.accumulate(cur + "_24h_change" ,String.format("%.2f", changeValue[0]));
                                    coins.add(temp);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            coins.addAll(presentList);

                            try {
                                volleyResponseListener.onResponse(coins);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                volleyResponseListener.onError("Something wrong");
            }


        });


        MySingleton.getInstance(context).addToRequestQueue(req);
    }




    public void getCoinValueOnStart(ArrayList<String> presentList, String cur, VolleyResponseListener volleyResponseListener) throws JSONException {
        String coin = "";
        final double[] changeValue = {0};
        for(String x: presentList)
        {
            coin += x +"%2C";
        }

        String url ="https://api.coingecko.com/api/v3/simple/price?ids="+ coin + "&vs_currencies=" + cur + "&include_24hr_change=true";


        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String key ="";
                        ArrayList<JSONObject> coins = new ArrayList<JSONObject>();

                        Iterator<String> iter =  response.keys();
                        while(iter.hasNext()){
                            key = iter.next();
                            try {
                                JSONObject temp = response.getJSONObject(key);
                                temp.accumulate("name", key);
                                changeValue[0] = temp.getDouble(cur + "_24h_change");
                                temp.remove(cur + "_24h_change");
                                temp.accumulate(cur + "_24h_change" ,String.format("%.2f", changeValue[0]));
                                coins.add(temp);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        try {
                            volleyResponseListener.onResponse(coins);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error in cryptoDataService", Toast.LENGTH_SHORT).show();
                volleyResponseListener.onError("Something wrong in CrytpodataService");
            }


        });


        MySingleton.getInstance(context).addToRequestQueue(req);
    }





    public void getManyCoinValue(String coin, String cur, ArrayList<JSONObject> presentList ,VolleyResponseListener volleyResponseListener) throws JSONException {
        String url ="https://api.coingecko.com/api/v3/simple/price?ids="+ coin + "&vs_currencies=" + cur + "&include_24hr_change=true";
        final double[] changeValue = {0};

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onResponse(JSONObject response) {
                        String key ="";
                        ArrayList<JSONObject> coins = new ArrayList<JSONObject>();

                        Iterator<String> iter =  response.keys();
                        while(iter.hasNext()){
                            key = iter.next();
                            try {
                                JSONObject temp = response.getJSONObject(key);
                                temp.accumulate("name", key);
                                changeValue[0] = temp.getDouble(cur + "_24h_change");
                                temp.remove(cur + "_24h_change");
                                temp.accumulate(cur + "_24h_change" ,String.format("%.2f", changeValue[0]));
                                coins.add(temp);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        coins.addAll(presentList);

                        try {
                            volleyResponseListener.onResponse(coins);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                volleyResponseListener.onError("Something wrong");
            }


        });


        MySingleton.getInstance(context).addToRequestQueue(req);
    }


}
