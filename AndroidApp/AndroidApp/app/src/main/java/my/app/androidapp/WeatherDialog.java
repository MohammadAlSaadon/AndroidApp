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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class WeatherDialog extends Activity
{

    static  public Activity c;
    static Dialog dialog;

    static TextView weatherTxt;
    static String weatherData;

    public  static void showDialog(final Activity activity){

        c = activity;
        dialog = new Dialog(activity);

        dialog.setCanceledOnTouchOutside(true); //To close the dialog on outside the dialog click
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_weather_dialog);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog); //To make dialog's corners rounded
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND); //To Not Dim the background


       weatherTxt = dialog.findViewById(R.id.weather);
       weatherTxt.setText(weatherData);


        dialog.show();
    }



    @Override
    public void onBackPressed() {
        dialog.dismiss();
    }


}
