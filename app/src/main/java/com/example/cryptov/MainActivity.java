package com.example.cryptov;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Formatter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;

public class MainActivity extends AppCompatActivity {
    Button click;
    ListView listview;
    Spinner  currency, SortBy;
    AutoCompleteTextView auto;
    SwipeRefreshLayout swipeToRefresh;
    TextView currencytext;
    FirebaseAuth auth;
    private DatabaseReference mDatabase;
    

    String userId;
    String[] currencies = {"usd","btc","eth", "inr", "eur"};
    String[] sortByStrings = {"Default", "By value(ascending)","By value(descending)", "By change(ascending)", "By change(descending)"};
    String[] cryptocoinsAutoComplete = {"bitcoin", "ethereum", "dogecoin", "litecoin", "namecoin", "peercoin","ripple", "dash", "stellar","tether","cardano","chainlink","polkadot","bitcoin-cash","bitcoin-atom", "tron","eos", "solana", "iota", "monero","cosmos", "tezos","avalanche","neo","kusama","algorand", "thorchain","dai","sushi"};
    ArrayList<String> cryptocoins = new ArrayList<String>();



    final String[] passingCurrency = {""};

    final ArrayList<JSONObject>[] inList = new ArrayList[]{new ArrayList<>()};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listview = findViewById(R.id.l1);
        click = findViewById(R.id.b1);
        currency = findViewById(R.id.currency);
        auto = findViewById(R.id.autosearchandadd);
        SortBy = findViewById(R.id.sortbyspinner);
        swipeToRefresh = findViewById(R.id.swipeRefreshLayout);
        auth = FirebaseAuth.getInstance();
        currencytext = findViewById(R.id.currencytextview);

        SharedPreferences sp = getApplicationContext().getSharedPreferences("UserIDs", Context.MODE_PRIVATE);
        userId = sp.getString("id", "");

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userId).child("coins");



        ArrayAdapter<String> cur = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, currencies);
        cur.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        currency.setAdapter(cur);

        ArrayAdapter<String> sort = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, sortByStrings);
        sort.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        SortBy.setAdapter(sort);

        ArrayAdapter<String> search = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_dropdown_item_1line, cryptocoinsAutoComplete);
        auto.setAdapter(search);

        //remove duplicates
        HashSet<String> hashSet = new HashSet<String>();
        hashSet.addAll(cryptocoins);
        cryptocoins.clear();
        cryptocoins.addAll(hashSet);

        coinRetrieval();


        currencytext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, cryptocoins.toString(), Toast.LENGTH_SHORT).show();
            }
        });



        swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                funtionToRenderList();

                swipeToRefresh.setRefreshing(false);

            }
        });



        SortBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sortingFunction(SortBy.getSelectedItem().toString());
                try {
                    displayList();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        currency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                passingCurrency[0] = currencies[position];
                CryptoDataService cryptoDataService = new CryptoDataService(MainActivity.this);

                try {
                    Toast.makeText(MainActivity.this, "Loading...", Toast.LENGTH_SHORT).show();
                    String coinRequest = "";

                    //remove duplicates
                    HashSet<String> hashSet = new HashSet<String>();
                    hashSet.addAll(cryptocoins);
                    cryptocoins.clear();
                    cryptocoins.addAll(hashSet);

                    for(String x: cryptocoins){
                        coinRequest += x + "%2C";
                    }
                    inList[0].clear();
                    cryptoDataService.getManyCoinValue(coinRequest, passingCurrency[0] ,inList[0], new CryptoDataService.VolleyResponseListener() {
                        @Override
                        public void onError(String message) {
                            Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onResponse(ArrayList<JSONObject> obj) throws JSONException {
                            inList[0] = obj;
                            sortingFunction(SortBy.getSelectedItem().toString());
                            displayList();


                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(MainActivity.this, "Select currency", Toast.LENGTH_SHORT).show();
            }
        });





        click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String coin = auto.getText().toString();

                //remove duplicates
                HashSet<String> hashSet = new HashSet<String>();
                hashSet.addAll(cryptocoins);
                cryptocoins.clear();
                cryptocoins.addAll(hashSet);


                boolean check = true;
                for(JSONObject x : inList[0]){
                    try {
                        if(x.getString("name").toLowerCase().equals(auto.getText().toString().toLowerCase())){
                            check = false;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if(check){
                    CryptoDataService cryptoDataService = new CryptoDataService(MainActivity.this);
                    try {
                        cryptoDataService.getCoinValue(auto.getText().toString().toLowerCase(), passingCurrency[0], inList[0],new CryptoDataService.VolleyResponseListener() {
                            @Override
                            public void onError(String message) {
                                Toast.makeText(MainActivity.this, "Something wrong", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onResponse(ArrayList<JSONObject> objects) throws JSONException {

                                if(objects.isEmpty()){
                                    Toast.makeText(MainActivity.this, coin.toUpperCase() + " is not supported.\nPlease check again", Toast.LENGTH_SHORT).show();
                                    sortingFunction(SortBy.getSelectedItem().toString());

                                    displayList();
                                }else{
                                    inList[0] = objects;
                                    cryptocoins.add(coin);
                                    mDatabase.push().setValue(coin);

                                    sortingFunction(SortBy.getSelectedItem().toString());

                                    displayList();
                                    Toast.makeText(MainActivity.this, coin.toUpperCase() + " added", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    Toast.makeText(MainActivity.this, coin.toUpperCase() + " already exists", Toast.LENGTH_SHORT).show();
                }

                auto.setText("");

            }
        });


        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //remove duplicates
                HashSet<String> hashSet = new HashSet<String>();
                hashSet.addAll(cryptocoins);
                cryptocoins.clear();
                cryptocoins.addAll(hashSet);

                new AlertDialog.Builder(MainActivity.this)
                        .setIcon(android.R.drawable.ic_delete)
                        .setTitle("Are you sure?")
                        .setMessage("Do you want to delete this coin?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String deletingCoin = null;
                                try {
                                    deletingCoin = inList[0].get(position).getString("name").toLowerCase();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                String finalDeletingCoin = deletingCoin;

                                mDatabase.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot ds : snapshot.getChildren()) {
                                            String childu = (String) ds.getValue();
                                            String key = ds.getKey();
                                            assert childu != null;
                                            if (childu.equals(finalDeletingCoin)) {
                                                assert key != null;
                                                mDatabase.child(key).setValue(null);

                                                cryptocoins.remove(finalDeletingCoin);
                                                Toast.makeText(MainActivity.this, finalDeletingCoin.toUpperCase() + " is removed", Toast.LENGTH_SHORT).show();
                                                funtionToRenderList();

                                                mDatabase.removeEventListener(this);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                            }
                        })
                        .setNegativeButton("No", null)
                        .show();


                return true;
            }
        });


        if(savedInstanceState != null){
            cryptocoins = savedInstanceState.getStringArrayList("coinsList");
        }

    }

    public void coinRetrieval(){

        cryptocoins.clear();

        mDatabase.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()){
                    DataSnapshot snapshot = task.getResult();

                    for(DataSnapshot dss : snapshot.getChildren()){
                        String stringname = dss.getValue(String.class);
                        cryptocoins.add(stringname);
                    }
                    funtionToRenderList();
                }
            }
        });

    }


    public void funtionToRenderList(){

        CryptoDataService cryptoDataService = new CryptoDataService(MainActivity.this);
        //remove duplicates
        HashSet<String> hashSet = new HashSet<String>();
        hashSet.addAll(cryptocoins);
        cryptocoins.clear();
        cryptocoins.addAll(hashSet);

        try {
            inList[0].clear();
            Toast.makeText(MainActivity.this, "Loading....", Toast.LENGTH_SHORT).show();

            cryptoDataService.getCoinValueOnStart(cryptocoins, passingCurrency[0], new CryptoDataService.VolleyResponseListener() {
                @Override
                public void onError(String message) {
                    Toast.makeText(MainActivity.this, "Something went wrong in rendering list", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onResponse(ArrayList<JSONObject> object) throws JSONException {
                    inList[0] = object;

                    sortingFunction(SortBy.getSelectedItem().toString());

                    displayList();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



    private void displayList() throws JSONException {
        String symbol = "";
        switch(passingCurrency[0]) {
            case "usd":
                symbol = "$";
                break;
            case "inr":
                symbol = "₹";
                break;
            case "btc":
                symbol = "₿";
                break;
            case "eur":
                symbol = "€";
                break;
            case "eth":
                symbol = "Ξ";
                break;
        }

        ArrayList<coinStructure> coinList = new ArrayList<>();

        for(JSONObject x: inList[0]){
            coinStructure newCoin = new coinStructure(x.getString("name").toUpperCase(), symbol + x.getString(passingCurrency[0]), x.getString(passingCurrency[0] + "_24h_change"));
            coinList.add(newCoin);

        }

        CoinListAdapter adapter = new CoinListAdapter(MainActivity.this, R.layout.adapter_view_layout, coinList);
        listview.setAdapter(adapter);


    }

    private void sortingFunction(String sortString){
        switch (sortString){
            case "By value(ascending)":{
                Collections.sort(inList[0], new Comparator<JSONObject>() {
                    @Override
                    public int compare(JSONObject lhs, JSONObject rhs) {
                        try {
                            if(   Float.parseFloat(lhs.getString(passingCurrency[0])) - Float.parseFloat(rhs.getString(passingCurrency[0])) > 0){
                                return 1;
                            }else {
                                return -1;
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return 0;
                    }
                });

                break;

            }
            case "By value(descending)":{
                Collections.sort(inList[0], new Comparator<JSONObject>() {
                    @Override
                    public int compare(JSONObject lhs, JSONObject rhs) {
                        try {
                            if(   Float.parseFloat(lhs.getString(passingCurrency[0])) - Float.parseFloat(rhs.getString(passingCurrency[0])) > 0){
                                return 1;
                            }else {
                                return -1;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return 0;
                    }
                });
                Collections.reverse(inList[0]);
                break;
            }
            case "By change(ascending)":{
                Collections.sort(inList[0], new Comparator<JSONObject>() {
                    @Override
                    public int compare(JSONObject lhs, JSONObject rhs) {
                        try {
                            if(  Double.parseDouble(lhs.getString(passingCurrency[0] + "_24h_change")) - Double.parseDouble(rhs.getString(passingCurrency[0] + "_24h_change")) > 0){
                                return 1;
                            }else {
                                return -1;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return 0;
                    }
                });

                break;
            }
            case "By change(descending)":{
                Collections.sort(inList[0], new Comparator<JSONObject>() {
                    @Override
                    public int compare(JSONObject lhs, JSONObject rhs) {
                        try {
                            if(  Double.parseDouble(lhs.getString(passingCurrency[0] + "_24h_change")) - Double.parseDouble(rhs.getString(passingCurrency[0] + "_24h_change")) > 0){
                                return 1;
                            }else {
                                return -1;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return 0;
                    }
                });
                Collections.reverse(inList[0]);
                break;
            }
            default:{
//                Collections.sort(inList[0], new Comparator<JSONObject>() {
//                    @Override
//                    public int compare(JSONObject lhs, JSONObject rhs) {
//                        try {
//                            if( !lhs.getString("name").equals(rhs.getString("name"))){
//                                return 1;
//                            }else {
//                                return -1;
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        return 0;
//                    }
//                });

                Collections.sort(inList[0], new Comparator<JSONObject>() {
                    @Override
                    public int compare(JSONObject lhs, JSONObject rhs) {
                        try {
                            return lhs.getString("name").compareToIgnoreCase(rhs.getString("name"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return 0;
                    }
                });

            }
        }



    }

    private long pressedTime;

    @Override
    public void onBackPressed() {

        if (pressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            this.finishAffinity();
        } else {
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        pressedTime = System.currentTimeMillis();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.Logout:{
                auth.signOut();
                Intent intent = new Intent(MainActivity.this, HomePageActivity.class);
                startActivity(intent);
                break;
            }

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.clear();
        outState.putStringArrayList("coinsList",cryptocoins);
    }
}