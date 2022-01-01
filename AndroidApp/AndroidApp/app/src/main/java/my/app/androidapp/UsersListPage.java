package my.app.androidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class UsersListPage extends AppCompatActivity {

    static ArrayList<users> usersArrayList = new ArrayList<>();
   static int [] id, phone;
    static String [] firstName, lastName, email;

    static Button weatherTxt;
    static String weatherData;

    static GridView usersGv;
    static CustomUsersListGridView custom;
    Button addBtn, FirebaseBtn, SqliteBtn;
    static String ClickedBtn = "Firebase";

    Activity activity;
    static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list_page);

        activity = this;
        context = UsersListPage.this;

        weatherTxt = findViewById(R.id.weatherTxt);
        try {
            weatherAPI();
        } catch (JSONException e) {
            Toast.makeText(getApplicationContext(), e.getMessage()+"", Toast.LENGTH_LONG).show();
        }

        weatherTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WeatherDialog.weatherData = weatherData;
                WeatherDialog custom = new WeatherDialog();
                custom.showDialog(UsersListPage.this);
            }
        });


        usersGv = findViewById(R.id.UsersGV);

        DatabaseReference dref=FirebaseDatabase.getInstance().getReference();
        dref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                GetUsersFirebase();
            }
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                GetUsersFirebase();
            }
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                GetUsersFirebase();
            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                GetUsersFirebase();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        addBtn = findViewById(R.id.addUserBtn);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AddUserPage.class);
                startActivity(i);
            }
        });


        FirebaseBtn = findViewById(R.id.firebaseBtn);
        SqliteBtn = findViewById(R.id.sqliteBtn);

        FirebaseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClickedBtn = "Firebase";
                GetUsersFirebase();
                FirebaseBtn.setBackgroundResource(R.drawable.clickedbtn);
                FirebaseBtn.setTextColor(Color.parseColor("#FFFFFF"));

                SqliteBtn.setBackgroundResource(R.drawable.emptybtn);
                SqliteBtn.setTextColor(Color.parseColor("#096374"));

            }
        });

        SqliteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClickedBtn = "Sqlite";
                FetchFromFirebase();
                FirebaseBtn.setBackgroundResource(R.drawable.emptybtn);
                FirebaseBtn.setTextColor(Color.parseColor("#096374"));

                SqliteBtn.setBackgroundResource(R.drawable.clickedbtn);
                SqliteBtn.setTextColor(Color.parseColor("#FFFFFF"));

            }
        });


        usersGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id2) {

                CustomUserDialog.userId = id[position];
                CustomUserDialog.userFirstName = firstName[position];
                CustomUserDialog.userLastName = lastName[position];
                CustomUserDialog.userPhone = phone[position];
                CustomUserDialog.userEmail = email[position];

                CustomUserDialog custom = new CustomUserDialog();
                custom.showDialog(UsersListPage.this);
            }
        });

    }

    public static void GetUsersFirebase()
    {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference moviesRef = rootRef.child("users");
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                usersArrayList = new ArrayList<>();
                for (DataSnapshot children : dataSnapshot.getChildren())
                {
                    users u = children.getValue(users.class);
                    usersArrayList.add(u);
                }

                id = new int [usersArrayList.size()];
                firstName = new String[usersArrayList.size()];
                lastName = new String[usersArrayList.size()];
                phone = new int [usersArrayList.size()];
                email = new String[usersArrayList.size()];

                for(int i=0; i < usersArrayList.size(); i++)
                {
                    users u = usersArrayList.get(i);

                    id[i] = u.getUserId();
                    firstName[i] = u.getFirstName();
                    lastName[i] = u.getLastName();
                    phone[i] = u.getPhoneNumber();
                    email[i] = u.getEmailAddress();
                }

                custom = new CustomUsersListGridView(context, id, firstName, lastName, phone, email);
                usersGv.setAdapter(custom);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        };
        moviesRef.addListenerForSingleValueEvent(eventListener);
    }


    public static void FetchFromFirebase()
    {

        DBHelper dbhelper = new DBHelper(context);
        SQLiteDatabase database = dbhelper.getWritableDatabase();

        for(int i=0; i < usersArrayList.size(); i++)
        {
            ContentValues values = new ContentValues();
            values.put("userId", Integer.parseInt(usersArrayList.get(i).getUserId()+""));
            values.put("firstName", usersArrayList.get(i).getFirstName());
            values.put("lastName", usersArrayList.get(i).getLastName());
            values.put("phoneNumber", Integer.parseInt(usersArrayList.get(i).getPhoneNumber()+""));
            values.put("emailAddress", usersArrayList.get(i).getEmailAddress());

           database.insert("users", null, values);

        }

        Toast.makeText(context, "Data fetched from Firebase", Toast.LENGTH_LONG).show();
        GetUsersSqlite();
    }

    public static void GetUsersSqlite()
    {
         usersArrayList = new ArrayList<>();

        DBHelper dbhelper = new DBHelper(context);
        SQLiteDatabase database = dbhelper.getReadableDatabase();

        Cursor cursor = database.query("users", new String [] {"*"},null,null,null,null,null);

        while(cursor.moveToNext())
        {
            int ID= cursor.getInt(0);
            String First = cursor.getString(1);
            String Last = cursor.getString(2);
            int Phone = cursor.getInt(3);
            String Email = cursor.getString(4);
            usersArrayList.add(new users(ID, First, Last, Phone, Email));
        }

        cursor.close();

        id = new int [usersArrayList.size()];
        firstName = new String[usersArrayList.size()];
        lastName = new String[usersArrayList.size()];
        phone = new int [usersArrayList.size()];
        email = new String[usersArrayList.size()];

        for(int i=0; i < usersArrayList.size(); i++)
        {
            users u = usersArrayList.get(i);

            id[i] = u.getUserId();
            firstName[i] = u.getFirstName();
            lastName[i] = u.getLastName();
            phone[i] = u.getPhoneNumber();
            email[i] = u.getEmailAddress();
        }

        custom = new CustomUsersListGridView(context, id, firstName, lastName, phone, email);
        usersGv.setAdapter(custom);

    }



    private void weatherAPI ( ) throws JSONException {

        String tempUrl = "https://api.openweathermap.org/data/2.5/weather?q=Riyadh&appid=6d3839ada24a6ba82616670bafc7cb90";
        DecimalFormat df = new DecimalFormat("#.##");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, tempUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                String output = "";
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONArray jsonArray = jsonResponse.getJSONArray("weather");
                    JSONObject jsonObjectWeather = jsonArray.getJSONObject(0);
                    String description = jsonObjectWeather.getString("description");
                    JSONObject jsonObjectMain = jsonResponse.getJSONObject("main");
                    double temp = jsonObjectMain.getDouble("temp") - 273.15;
                    double feelsLike = jsonObjectMain.getDouble("feels_like") - 273.15;
                    float pressure = jsonObjectMain.getInt("pressure");
                    int humidity = jsonObjectMain.getInt("humidity");
                    JSONObject jsonObjectWind = jsonResponse.getJSONObject("wind");
                    String wind = jsonObjectWind.getString("speed");
                    JSONObject jsonObjectClouds = jsonResponse.getJSONObject("clouds");
                    String clouds = jsonObjectClouds.getString("all");
                    JSONObject jsonObjectSys = jsonResponse.getJSONObject("sys");
                    String countryName = jsonObjectSys.getString("country");
                    String cityName = jsonResponse.getString("name");
                    output += "Current weather of " + cityName + " (" + countryName + ")"
                            + "\n Temp: " + df.format(temp) + " °C"
                            + "\n Feels Like: " + df.format(feelsLike) + " °C"
                            + "\n Humidity: " + humidity + "%"
                            + "\n Description: " + description
                            + "\n Wind Speed: " + wind + "m/s (meters per second)"
                            + "\n Cloudiness: " + clouds + "%"
                            + "\n Pressure: " + pressure + " hPa";
                    weatherData = output;
                    weatherTxt.setText(df.format(temp) + " °C");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.toString().trim(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }



}