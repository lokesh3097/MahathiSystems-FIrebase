package msipl.com.mahathisystems;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class AutoAlertActivity extends AppCompatActivity
{
    private ListView listView;

    private ProgressDialog progressDialog;

    private ArrayList arrayList = new ArrayList();

    private boolean flag;

    private static int TIME_OUT = 30000;

    private ArrayList combined_arrayList = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_alert);

        listView = (ListView) findViewById(R.id.listview);

        progressDialog = new ProgressDialog(AutoAlertActivity.this);
        progressDialog.setTitle("Please Wait...");
        progressDialog.setMessage("Loading Data...");
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        flag = false;

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://mahathi-systems.firebaseio.com/");

        DatabaseReference rootReference = firebaseDatabase.getReference();

        //DatabaseReference devicesReference = rootReference.child("devices");

        DatabaseReference calReference = rootReference.child("calibration");

        //Toast.makeText(this, cur, Toast.LENGTH_SHORT).show();

        for (int i = 0; i < 30; ++i)
        {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_YEAR, i);

            int curDate = calendar.get(Calendar.DAY_OF_MONTH);
            int curMonth = calendar.get(Calendar.MONTH);
            int curYear = calendar.get(Calendar.YEAR);

            DatabaseReference yearRef = calReference.child(""+curYear);

            String cur = curDate + "-" + (curMonth+1) + "-" + curYear;

            //Query query = devicesReference.orderByChild("date").equalTo(cur);

            DatabaseReference dateRef = yearRef.child(cur);

            //dateRef.ad

            //Query query = dateRef.orderByChild("serialNumber");

            dateRef.addChildEventListener(new ChildEventListener()
            {
                //ArrayList arrayList = new ArrayList();

                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s)
                {
                    Analyser temp = dataSnapshot.getValue(Analyser.class);

                    arrayList.add(temp.serialNumber);

                    combined_arrayList.add(temp.date + "\t\t\t\t\t" + temp.serialNumber);

                    ArrayAdapter arrayAdapter = new ArrayAdapter(AutoAlertActivity.this, android.R.layout.simple_dropdown_item_1line, combined_arrayList);
                    listView.setAdapter(arrayAdapter);

                    flag = true;

                    progressDialog.dismiss();
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s)
                {
                    //Toast.makeText(AutoAlertActivity.this, "No Data Found!!!", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot)
                {
                    //progressDialog.dismiss();
                    //Toast.makeText(AutoAlertActivity.this, "No Data Found!!!", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s)
                {
                    //progressDialog.dismiss();
                    //Toast.makeText(AutoAlertActivity.this, "No Data Found!!!", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {

                }
            });
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run()
            {
                if(!flag)
                {
                    progressDialog.dismiss();
                    Toast.makeText(AutoAlertActivity.this, "No Data Found!!!", Toast.LENGTH_LONG).show();
                }
            }
        }, TIME_OUT);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                //Toast.makeText(AutoAlertActivity.this, "Position : " + position, Toast.LENGTH_SHORT).show();
                String sNum = arrayList.get(position).toString();

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference rootReference = database.getReference();

                DatabaseReference devicesReferences = rootReference.child("devices");

                final DatabaseReference databaseResult = devicesReferences.child(sNum);

                databaseResult.addListenerForSingleValueEvent(new ValueEventListener()
                {
                    //Analyser analyser;

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        final Analyser result = dataSnapshot.getValue(Analyser.class);

                        try
                        {
                            String date = result.day+"/"+result.month+"/"+result.year;

                            String message = "Serial Number : " + result.serialNumber + "\n" + "Calibration Due Date : " + date + "\nInvoice No : " + result.invoiceNumber + "\nCustomer Name : " +
                                    result.customerName + "\nCustomer Contact : " + result.customerContact + "\nEmail ID : " + result.emailId;

                            progressDialog.dismiss();

                            new AlertDialog.Builder(AutoAlertActivity.this)
                                    .setCancelable(false)
                                    .setTitle("Device Information...")
                                    .setMessage(message)
                                    .setPositiveButton("Call Customer", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which)
                                        {
                                            String number = "tel:"+result.customerContact;

                                            Intent make_call = new Intent(Intent.ACTION_DIAL, Uri.parse(number));
                                            startActivity(make_call);

                                            dialog.dismiss();
                                        }
                                    })
                                    .setNeutralButton("Okay", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .setNegativeButton("Send Email", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which)
                                        {
                                            String email[] = new String[]{result.emailId};
                                            String subject = "Calibration Reminder for Alcohol Breath Analyser with Serial Number : " + result.serialNumber;
                                            String message = "Dear Sir/Madam,\n\t\tThe Breath Alcohol Analyser with Serial Number : " + result.serialNumber + " is due for calibration.\n\n\nThanks and Regards,\nVasu.\n+91 93 92 509 500";

                                            Intent emailIntent = new Intent(Intent.ACTION_SEND);
                                            emailIntent.setType("message/rfc822");
                                            emailIntent.putExtra(Intent.EXTRA_EMAIL, email);
                                            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                                            emailIntent.putExtra(Intent.EXTRA_TEXT, message);

                                            try
                                            {
                                                startActivity(Intent.createChooser(emailIntent, "Select an Email Client"));
                                            }
                                            catch(ActivityNotFoundException ae)
                                            {
                                                Toast.makeText(AutoAlertActivity.this, "There are no email clients installed", Toast.LENGTH_LONG).show();
                                            }

                                            dialog.dismiss();
                                        }
                                    })
                                    .show();
                        }
                        catch (Exception e)
                        {
                            progressDialog.dismiss();

                            Toast.makeText(AutoAlertActivity.this, "Data Not Found", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {

                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }
}
