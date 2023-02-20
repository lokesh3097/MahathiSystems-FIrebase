package msipl.com.mahathisystems;

import android.app.DatePickerDialog;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class NewRecordActivity extends AppCompatActivity
{

    private EditText serialNumberET, calDueDateET, invNumberET, cusNameET, cusConET, emailIDET;

    private String serialNumber, invNumber, cusName, cusCon, emailID;

    private DatePickerDialog datePickerDialog;

    private int pickedDate, pickedMonth, pickedYear;

    private boolean datePicked;

    //private FirebaseDatabase firebaseDatabase;

    //private DatabaseReference rootReference;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_record);

        serialNumberET = (EditText) findViewById(R.id.sno);
        calDueDateET = (EditText) findViewById(R.id.caldate);
        invNumberET = (EditText) findViewById(R.id.ivno);
        cusNameET = (EditText) findViewById(R.id.cname);
        cusConET = (EditText) findViewById(R.id.cno);
        emailIDET = (EditText) findViewById(R.id.emailid);

        datePicked = false;
    }

    public void onSubmitPressed(View view)
    {
        serialNumber = serialNumberET.getText().toString().trim();
        invNumber = invNumberET.getText().toString().trim();
        cusName = cusNameET.getText().toString().trim();
        cusCon = cusConET.getText().toString().trim();
        emailID = emailIDET.getText().toString().trim();

        if (serialNumber.isEmpty())
        {
            serialNumberET.setError("Serial Number Cannot be Empty!");
            serialNumberET.requestFocus();
        }
        else
        {
            if (!datePicked)
            {
                calDueDateET.setError("Pick the Calibration Due Date!");
                calDueDateET.requestFocus();
            }
            else
            {
                if (cusName.isEmpty())
                {
                    cusNameET.setError("Customer Name cannot be Empty!");
                    cusNameET.requestFocus();
                }
                else
                {
                    if (cusCon.isEmpty())
                    {
                        cusConET.setError("Customer Contact Number Cannot be Empty!");
                        cusConET.requestFocus();
                    }
                    else
                    {
                        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://mahathi-systems.firebaseio.com/");

                        //firebaseDatabase.

                        DatabaseReference rootReference = firebaseDatabase.getReference();

                        DatabaseReference devicesRef = rootReference.child("devices");

                        DatabaseReference calReference = rootReference.child("calibration");

                        DatabaseReference snoDeviceRef = devicesRef.child(serialNumber);

                        Analyser analyser1 = new Analyser(serialNumber, pickedDate, pickedMonth, pickedYear, invNumber, cusName, cusCon, emailID);

                        //Analyser analyser2 = new Analyser("20001123", pickedDate, pickedMonth, pickedYear, invNumber, cusName, cusCon);

                        //Map<String, Analyser> map = new HashMap<>();
                        //map.put(serialNumber, analyser1);
                        //map.put("20001123", analyser2);

                        String picked = "" + pickedYear;

                        //Toast.makeText(this, picked, Toast.LENGTH_SHORT).show();

                        DatabaseReference yearRef = calReference.child(picked);

                        DatabaseReference dateReference = yearRef.child(analyser1.date);

                        DatabaseReference snoRef = dateReference.child(serialNumber);

                        snoDeviceRef.setValue(analyser1);

                        snoRef.setValue(analyser1);

                        finish();

                        Toast.makeText(this, "Data Inserted Successfully!!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void showDatePickerDialog(View view)
    {
        Calendar calendar = Calendar.getInstance();

        int curDate = calendar.get(Calendar.DAY_OF_MONTH);
        int curMonth = calendar.get(Calendar.MONTH);
        int curYear = calendar.get(Calendar.YEAR);

        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
            {
                pickedDate = dayOfMonth;
                pickedMonth = month+1;
                pickedYear = year;

                String dateString = dayOfMonth+"/"+(month+1)+"/"+year;

                calDueDateET.setText(dateString);

                //Toast.makeText(NewRecordActivity.this, "Date : "+dayOfMonth+"/"+month+"/"+year, Toast.LENGTH_SHORT).show();

                datePicked = true;
            }
        }, curYear, curMonth,curDate);


        datePickerDialog.show();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();

        //Toast.makeText(this, "Back Pressed", Toast.LENGTH_SHORT).show();

        finish();
    }
}
