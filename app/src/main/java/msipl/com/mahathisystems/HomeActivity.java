package msipl.com.mahathisystems;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity
{
    FirebaseAuth firebaseAuth;

    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseUser = firebaseAuth.getCurrentUser();
    }

    public void logoutUser(View view)
    {
        if (firebaseUser != null)
        {
            firebaseAuth.signOut();

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

            finish();
        }
    }

    public void newRecord(View view)
    {
        Intent intent = new Intent(this, NewRecordActivity.class);
        startActivity(intent);

        //finish();
    }

    public void searchActivity(View view)
    {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    public void updateActivity(View view)
    {
        Intent intent = new Intent(this, UpdateActivity.class);
        startActivity(intent);
    }

    public void autoalertActivity(View view)
    {
        Intent intent = new Intent(this, AutoAlertActivity.class);
        startActivity(intent);
    }
}
