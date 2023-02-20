package msipl.com.mahathisystems;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity
{
    Button my_login_button;

    String mobile_number = "";

    EditText editText_number;

    PhoneAuthProvider phoneAuthProvider;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private FirebaseAuth mauth;

    private FirebaseUser user;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        my_login_button = (Button) findViewById(R.id.login_button);

        editText_number = (EditText) findViewById(R.id.mobile_text);

        phoneAuthProvider = PhoneAuthProvider.getInstance();

        mauth = FirebaseAuth.getInstance();

        user = mauth.getCurrentUser();

        if(user != null)
        {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);

            finish();
        }

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d("MSIPL", "onVerificationCompleted:" + credential);

                //Toast.makeText(MainActivity.this, "onVerificationComplete!!", Toast.LENGTH_SHORT).show();

                //signInWithPhoneAuthCredential(credential);

                signIn(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w("MSIPL", "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // ...
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // ...
                }

                // Show a message and update the UI
                // ...
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d("MSIPL", "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                //mVerificationId = verificationId;
                //mResendToken = token;

                // ...
            }
        };




        my_login_button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (editText_number.getText().toString().isEmpty())
                {
                    editText_number.setError("Enter Mobile Number");
                }
                else if (editText_number.getText().toString().trim().length() != 10)
                {
                    editText_number.setError("Mobile Number Should be 10 digits");

                    editText_number.setText("");
                }
                else
                {
                    mobile_number = editText_number.getText().toString().trim();

                    //Toast.makeText(MainActivity.this, "Entered Mobile Number : " + mobile_number, Toast.LENGTH_LONG).show();

                    progressDialog = new ProgressDialog(MainActivity.this);

                    progressDialog.setTitle("Please Wait...");
                    progressDialog.setMessage("Logging you in...");
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    phoneAuthProvider.verifyPhoneNumber(mobile_number, 120, TimeUnit.SECONDS, MainActivity.this, mCallbacks);
                }
            }
        });
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        progressDialog = null;
    }

    private void signIn(PhoneAuthCredential credential)
    {
        mauth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
                {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            //Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            startActivity(intent);

                            user = mauth.getCurrentUser();
                            progressDialog.dismiss();

                            finish();
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, "Please Check Your Internet Connection", Toast.LENGTH_LONG).show();

                            progressDialog.dismiss();
                        }
                    }
                });
    }
}
