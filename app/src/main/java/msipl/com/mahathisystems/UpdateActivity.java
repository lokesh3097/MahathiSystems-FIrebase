package msipl.com.mahathisystems;

import android.app.DatePickerDialog;
import android.content.Intent;
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

public class UpdateActivity extends AppCompatActivity
{
    EditText serialNumberET, caldueET, invoiceNumET, cusNameET, cusConET, emailET;

    String snum, inv, cnam, cnum, cemail;
    int cday, cmon, cyear;

    private DatePickerDialog datePickerDialog;

    //private boolean datePicked;

    //private FirebaseDatabase firebaseDatabase;

    //private DatabaseReference rootReference;

    private DatabaseReference snoRef;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        serialNumberET = (EditText) findViewById(R.id.sno_update);
        caldueET = (EditText) findViewById(R.id.caldate_update);
        invoiceNumET = (EditText) findViewById(R.id.ivno_update);
        cusNameET = (EditText) findViewById(R.id.cname_update);
        cusConET = (EditText) findViewById(R.id.cno_update);
        emailET = (EditText) findViewById(R.id.emailid_update);

        //datePicked = false;

        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra("key", true);
        startActivityForResult(intent, 1234);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode == RESULT_OK)
        {
            Analyser analyser = (Analyser) data.getSerializableExtra("analyser");

            //Toast.makeText(this, "Serial Number : "+analyser.serialNumber, Toast.LENGTH_LONG).show();

            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://mahathi-systems.firebaseio.com/");

            DatabaseReference rootReference = firebaseDatabase.getReference();

            DatabaseReference devicesRef = rootReference.child("devices");

            snoRef = devicesRef.child(analyser.serialNumber);

            serialNumberET.setText(analyser.serialNumber);
            caldueET.setText(analyser.day+"/"+analyser.month+"/"+analyser.year);
            invoiceNumET.setText(analyser.invoiceNumber);
            cusNameET.setText(analyser.customerName);
            cusConET.setText(analyser.customerContact);
            emailET.setText(analyser.emailId);

            cday = analyser.day;
            cmon = analyser.month;
            cyear = analyser.year;
        }
        else if(resultCode == RESULT_CANCELED)
        {
            finish();

            Toast.makeText(this, "No Record Found!!!", Toast.LENGTH_LONG).show();
        }
    }

    public void onUpdatePressed(View view)
    {
        snum = serialNumberET.getText().toString().trim();
        inv = invoiceNumET.getText().toString().trim();
        cnam = cusNameET.getText().toString().trim();
        cnum = cusConET.getText().toString().trim();
        cemail = emailET.getText().toString().trim();

        if(cnam.isEmpty())
        {
            cusNameET.setError("Customer Name Cannot be Empty!");
            cusNameET.requestFocus();
        }
        else
        {
            if(cnum.isEmpty())
            {
                cusConET.setError("Customer Contact Cannot be Empty!");
                cusConET.requestFocus();
            }
            else
            {
                Analyser updatedAnalyser = new Analyser(snum, cday, cmon, cyear, inv, cnam, cnum, cemail);

                snoRef.setValue(updatedAnalyser);

                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://mahathi-systems.firebaseio.com/");

                DatabaseReference rootReference = firebaseDatabase.getReference();

                DatabaseReference calRef = rootReference.child("calibration");

                DatabaseReference dateRef = calRef.child(""+updatedAnalyser.year).child(updatedAnalyser.date);

                DatabaseReference serRef = dateRef.child(updatedAnalyser.serialNumber);

                serRef.setValue(updatedAnalyser);

                finish();

                Toast.makeText(this, "Data Updated Successfully!", Toast.LENGTH_LONG).show();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void showDatePickerDialogUpdate(View view)
    {
        Calendar calendar = Calendar.getInstance();

        int curDate = calendar.get(Calendar.DAY_OF_MONTH);
        int curMonth = calendar.get(Calendar.MONTH);
        int curYear = calendar.get(Calendar.YEAR);

        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
            {
                cday = dayOfMonth;
                cmon = month+1;
                cyear = year;

                String dateString = dayOfMonth+"/"+(month+1)+"/"+year;

                caldueET.setText(dateString);

                //Toast.makeText(NewRecordActivity.this, "Date : "+dayOfMonth+"/"+month+"/"+year, Toast.LENGTH_SHORT).show();

                //datePicked = true;
            }
        }, curYear, curMonth,curDate);


        datePickerDialog.show();
    }


    @Override
    public void onBackPressed()
    {
        super.onBackPressed();

        finish();
    }
}
