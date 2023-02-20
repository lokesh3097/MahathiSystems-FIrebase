package msipl.com.mahathisystems;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class SearchActivity extends AppCompatActivity
{

    private EditText serialNumberET;

    private String serialNum;

    private FirebaseDatabase database;

    private DatabaseReference rootReference;

    private ProgressDialog progressDialog;

    private Analyser analyserResult;

    private Intent receivedIntent;

    private boolean fromUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        serialNumberET = (EditText) findViewById(R.id.search_sno);

        receivedIntent = getIntent();
        fromUpdate = receivedIntent.getBooleanExtra("key", false);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();

        finish();
    }

    public void onSearchPressed(View view)
    {
        serialNum = serialNumberET.getText().toString().trim();

        if(serialNum.isEmpty())
        {
            serialNumberET.setError("Serial Number Cannot be Empty");
            serialNumberET.requestFocus();
        }
        else
        {
            progressDialog = new ProgressDialog(SearchActivity.this);

            progressDialog.setCancelable(false);
            progressDialog.setTitle("Please wait...");
            progressDialog.setMessage("Searching...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setIndeterminate(true);
            progressDialog.show();


            database = FirebaseDatabase.getInstance();
            rootReference = database.getReference();

            DatabaseReference devicesReferences = rootReference.child("devices");

            serialNum = serialNum.toUpperCase();

            final DatabaseReference databaseResult = devicesReferences.child(serialNum);

            //Query query = devicesReferences.equalTo(serialNum);

            //final Analyser analyser;

            databaseResult.addListenerForSingleValueEvent(new ValueEventListener()
            {
                //Analyser analyser;

                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    final Analyser result = dataSnapshot.getValue(Analyser.class);

                    analyserResult = result;

                    if(fromUpdate)
                    {
                        progressDialog.dismiss();
                        receivedIntent.putExtra("analyser", analyserResult);
                        //receivedIntent.putExtra("ref", databaseResult);

                        if(analyserResult == null)
                        {
                            setResult(RESULT_CANCELED, receivedIntent);
                        }
                        else
                        {
                            setResult(RESULT_OK, receivedIntent);
                        }

                        finish();
                    }
                    else
                    {
                        try
                        {
                            String date = result.day+"/"+result.month+"/"+result.year;

                            String message = "Serial Number : " + result.serialNumber + "\n" + "Calibration Due Date : " + date + "\nInvoice No : " + result.invoiceNumber + "\nCustomer Name : " +
                                    result.customerName + "\nCustomer Contact : " + result.customerContact + "\nEmail ID : " + result.emailId;

                            progressDialog.dismiss();

                            new AlertDialog.Builder(SearchActivity.this)
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
                                                Toast.makeText(SearchActivity.this, "There are no email clients installed", Toast.LENGTH_LONG).show();
                                            }

                                            dialog.dismiss();
                                        }
                                    })
                                    .show();
                        }
                        catch (Exception e)
                        {
                            progressDialog.dismiss();

                            Toast.makeText(SearchActivity.this, "Data Not Found", Toast.LENGTH_LONG).show();
                        }
                    }

                    //Toast.makeText(SearchActivity.this, "Serial Number : "+result.serialNumber, Toast.LENGTH_SHORT).show();




                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {

                }
            });

            //progressDialog = null;

        }
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        progressDialog = null;
    }
}
