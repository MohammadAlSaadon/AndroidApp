package my.app.androidapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class CustomUserDialog extends Activity
{

    static  public Activity c;
    static Dialog dialog;

    static Button updatebtn;
    static EditText id, firstname, lastname, phone, email;
    private static ProgressBar progress;

    static int userId , userPhone;
    static String userFirstName, userLastName, userEmail;

    public  static void showDialog(final Activity activity){

        c = activity;
        dialog = new Dialog(activity);

        dialog.setCanceledOnTouchOutside(true); //To close the dialog on outside the dialog click
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_user_dialog);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog); //To make dialog's corners rounded
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND); //To Not Dim the background


       id = dialog.findViewById(R.id.UpdateUserId);
       firstname = dialog.findViewById(R.id.UpdateUserFirstName);
       lastname = dialog.findViewById(R.id.UpdateUserLastName);
       phone = dialog.findViewById(R.id.UpdateUserPhone);
       email = dialog.findViewById(R.id.UpdateUserEmail);
       progress = (ProgressBar) dialog.findViewById(R.id.UpdateUserProgress);

        id.setText(userId+"");
        firstname.setText(userFirstName);
        lastname.setText(userLastName);
        phone.setText(userPhone+"");
        email.setText(userEmail);

        updatebtn = dialog.findViewById(R.id.updateBtn);
        updatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkIfAllMandatoryFieldsEntered()) {
                    progress.setVisibility(View.VISIBLE);

                    if(UsersListPage.ClickedBtn.equals("Firebase")) {
                        UpdateUserFirebase(Integer.parseInt(id.getText().toString()), firstname.getText().toString(), lastname.getText().toString(),
                                Integer.parseInt(phone.getText().toString()), email.getText().toString());
                    }
                    else
                    {
                        UpdateUserSqlite(Integer.parseInt(id.getText().toString()), firstname.getText().toString(), lastname.getText().toString(),
                                Integer.parseInt(phone.getText().toString()), email.getText().toString());

                    }
                }
            }
        });


        dialog.show();
    }


    public static void UpdateUserFirebase(int Id, String firstName, String lastName, int Phone, String Email)
    {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        final DatabaseReference ref = firebaseDatabase.getReference();
        ref.child("users").orderByChild("userId").equalTo(Id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists() && Id != userId) {
                            Toast.makeText(c, "User ID already exists!", Toast.LENGTH_LONG).show();
                            progress.setVisibility(View.INVISIBLE);
                        }
                        else {

                            DatabaseReference leadersRef = FirebaseDatabase.getInstance().getReference("users");
                            Query query = leadersRef.orderByChild("userId").equalTo(userId);
                            query.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    for (DataSnapshot child : snapshot.getChildren()) {
                                        if (child.child("userId").getValue(Integer.class).equals(userId))
                                        {
                                                child.getRef().child("userId").setValue(Integer.parseInt(id.getText().toString()));
                                                child.getRef().child("firstName").setValue( firstname.getText().toString() );
                                                child.getRef().child("lastName").setValue( lastname.getText().toString() );
                                                child.getRef().child("phoneNumber").setValue(Integer.parseInt(phone.getText().toString()));
                                                child.getRef().child("emailAddress").setValue( email.getText().toString() );

                                                Toast.makeText(c, "User Updated on Firebase", Toast.LENGTH_LONG).show();
                                                UsersListPage.GetUsersFirebase();
                                                progress.setVisibility(View.INVISIBLE);
                                                dialog.dismiss();

                                        }
                                    }
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



    public static void UpdateUserSqlite(int Id, String firstName, String lastName, int Phone, String Email)
    {
        DBHelper dbhelper = new DBHelper(c);
        SQLiteDatabase database = dbhelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("userId", Id);
        values.put("firstName", firstName);
        values.put("lastName", lastName);
        values.put("phoneNumber", Phone);
        values.put("emailAddress", Email);

        int i =  database.update("users", values, "userId=" + userId, null);
        Toast.makeText(c, "User Updated on Sqlite", Toast.LENGTH_LONG).show();
        UsersListPage.GetUsersSqlite();
        progress.setVisibility(View.INVISIBLE);
        dialog.dismiss();
    }


    @Override
    public void onBackPressed() {
        dialog.dismiss();
    }


    private static boolean checkIfAllMandatoryFieldsEntered() {
        // Reset errors.
        id.setError(null);
        firstname.setError(null);
        lastname.setError(null);
        phone.setError(null);
        email.setError(null);

        String ID = id.getText().toString();
        String firstName = firstname.getText().toString();
        String lastName = lastname.getText().toString();
        String Phone = phone.getText().toString();
        String Email = email.getText().toString();

        boolean cancel = false;
        View focusView = null;


        if (TextUtils.isEmpty(ID)) {
            id.setError("This field is required" );
            focusView = id;
            cancel = true;
        }

        if (TextUtils.isEmpty(firstName)) {
            firstname.setError("This field is required" );
            focusView = firstname;
            cancel = true;
        }

        if (TextUtils.isEmpty(lastName)) {
            lastname.setError("This field is required" );
            focusView = lastname;
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
