package my.app.androidapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddUserPage extends AppCompatActivity {

    EditText id, firstName, lastName, phone, email;
    Button addBtn;
    private ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_page);

        id = findViewById(R.id.addUserId);
        firstName = findViewById(R.id.addUserFirstName);
        lastName = findViewById(R.id.addUserLastName);
        phone = findViewById(R.id.addUserPhone);
        email = findViewById(R.id.addUserEmail);
        progress = findViewById(R.id.addUserProgress);

        addBtn = findViewById(R.id.add);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkIfAllMandatoryFieldsEntered()) {
                    progress.setVisibility(View.VISIBLE);

                    if(UsersListPage.ClickedBtn.equals("Firebase")) {
                        addUserFirebase(Integer.parseInt(id.getText().toString()), firstName.getText().toString(), lastName.getText().toString(),
                                Integer.parseInt(phone.getText().toString()), email.getText().toString());
                    }
                    else
                    {
                        addUserSqlite(Integer.parseInt(id.getText().toString()), firstName.getText().toString(), lastName.getText().toString(),
                                Integer.parseInt(phone.getText().toString()), email.getText().toString());
                    }
                }
            }
        });

    }


    public  void addUserFirebase(int Id, String firstName, String lastName, int Phone, String Email)
    {
        final users u = new users(Id, firstName, lastName, Phone, Email);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference ref = firebaseDatabase.getReference();
        ref.child("users").orderByChild("userId").equalTo(Integer.parseInt(id.getText().toString()))
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists() ) {
                            Toast.makeText(getApplicationContext(), "User ID already exists!", Toast.LENGTH_LONG).show();
                            progress.setVisibility(View.INVISIBLE);
                        } else
                            {
                            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                            final DatabaseReference ref = firebaseDatabase.getReference();
                            ref.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    ref.child("users").push().setValue(u);
                                    Toast.makeText(getApplicationContext(), "User Added Successfully to Firebase", Toast.LENGTH_LONG).show();
                                    progress.setVisibility(View.INVISIBLE);
                                    Intent i = new Intent(getApplicationContext(),UsersListPage.class);
                                    startActivity(i);

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });


    }


    public  void addUserSqlite(int Id, String firstName, String lastName, int Phone, String Email)
    {

        DBHelper dbhelper = new DBHelper(getApplicationContext());
        SQLiteDatabase database = dbhelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("userId", Id);
            values.put("firstName", firstName);
            values.put("lastName", lastName);
            values.put("phoneNumber", Phone);
            values.put("emailAddress", Email);

            long rowId = database.insert("users", null, values);
        if(rowId != 0)
        {
            Toast.makeText(getApplicationContext(), "User Added Successfully to Sqlite", Toast.LENGTH_LONG).show();
            progress.setVisibility(View.INVISIBLE);
            UsersListPage.GetUsersSqlite();
            Intent i = new Intent(getApplicationContext(),UsersListPage.class);
            startActivity(i);
        }


    }


    private boolean checkIfAllMandatoryFieldsEntered() {
        // Reset errors.
        id.setError(null);
        firstName.setError(null);
        lastName.setError(null);
        phone.setError(null);
        email.setError(null);

        String ID = id.getText().toString();
        String firstname = firstName.getText().toString();
        String lastname = lastName.getText().toString();
        String Phone = phone.getText().toString();
        String Email = email.getText().toString();

        boolean cancel = false;
        View focusView = null;


        if (TextUtils.isEmpty(ID)) {
            id.setError("This field is required" );
            focusView = id;
            cancel = true;
        }

        if (TextUtils.isEmpty(firstname)) {
            firstName.setError("This field is required" );
            focusView = firstName;
            cancel = true;
        }

        if (TextUtils.isEmpty(lastname)) {
            lastName.setError("This field is required" );
            focusView = lastName;
            cancel = true;
        }

        if (TextUtils.isEmpty(Phone)) {
            phone.setError("This field is required" );
            focusView = phone;
            cancel = true;
        }


        if (TextUtils.isEmpty(Email)) {
            email.setError("This field is required" );
            focusView = email;
            cancel = true;
        }


        if (cancel) {
            focusView.requestFocus();
        } else {

            return true;
        }
        return false;
    }

}