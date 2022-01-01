package my.app.androidapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class CustomUsersListGridView extends BaseAdapter
{
    private Context mContext;
    private final int [] id;
    private final String [] firstName;
    private final String [] lastName;
    private final int [] phone;
    private final String [] email;

    public CustomUsersListGridView(Context mContext, int[] id, String[] firstName, String[] lastName, int[] phone, String[] email) {
        this.mContext = mContext;
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
    }

    @Override
    public int getCount() {
        return id.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int x, View convertView, ViewGroup parent) {
        View gridViewAndroid;
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        gridViewAndroid = new View(mContext);
        gridViewAndroid = inflater.inflate(R.layout.custom_user_list_gridview, null);

        TextView UserId = (TextView) gridViewAndroid.findViewById(R.id.UserIdTxt);
        TextView UserName = (TextView) gridViewAndroid.findViewById(R.id.UserNameTxt);
        TextView UseerPhone = (TextView) gridViewAndroid.findViewById(R.id.UserPhoneTxt);
        TextView UserEmail = (TextView) gridViewAndroid.findViewById(R.id.UserEmailTxt);
        Button UserDeleteBtn = gridViewAndroid.findViewById(R.id.UserDeleteBtn);

        UserId.setText("ID: " + id[x]);
        UserName.setText(firstName[x] + " " + lastName[x]);
        UseerPhone.setText(phone[x]+"");
        UserEmail.setText(email[x]);

        UserDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(mContext)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Delete User")
                        .setMessage("Are you sure you want to delete this user?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if( UsersListPage.ClickedBtn.equals("Firebase")) {
                                    DatabaseReference leadersRef = FirebaseDatabase.getInstance().getReference("users");
                                    Query query = leadersRef.orderByChild("userId").equalTo(id[x]);
                                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot snapshot) {
                                            for (DataSnapshot child : snapshot.getChildren()) {
                                                if (child.child("userId").getValue(Integer.class).equals(id[x])) {
                                                    child.getRef().removeValue();
                                                    Toast.makeText(mContext, "User Deleted from Firebase", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    });
                                }
                                else
                                {

                                    DBHelper dbhelper = new DBHelper(mContext);
                                    SQLiteDatabase database = dbhelper.getWritableDatabase();

                                    database.delete("users","userId=" + id[x],null);
                                    UsersListPage.GetUsersSqlite();
                                    Toast.makeText(mContext, "User Deleted from Sqlite", Toast.LENGTH_LONG).show();

                                }


                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });




        return gridViewAndroid;
    }
}
