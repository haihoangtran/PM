package com.haihoangtran.pm.activities;

import android.content.Intent;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.haihoangtran.pm.R;

public class NavigationBaseActivity extends AppCompatActivity {
    public void navigationHandle(int id){
        BottomNavigationView navView = findViewById(R.id.bottom_nav);
        navView.setSelectedItemId(id);
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        Intent homeIntent = new Intent(NavigationBaseActivity.this, HomeActivity.class);
                        startActivity(homeIntent);
                        break;

                    case R.id.nav_budget:
                        Intent budgetIntent = new Intent(NavigationBaseActivity.this, BudgetActivity.class);
                        startActivity(budgetIntent);
                        break;

                    case R.id.nav_payment:
                        Intent paymentIntent = new Intent(NavigationBaseActivity.this, PaymentActivity.class);
                        startActivity(paymentIntent);
                        break;
                }
                return true;
            }
        });
    }

}
