package com.example.uyg;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.add_expense) {

            Intent addExpenseIntent = new Intent(MainActivity.this, AddExpenseActivity.class);
            startActivity(addExpenseIntent);
            return true;
        } else if (id == R.id.view_expense_list) {

            Intent viewExpenseIntent = new Intent(MainActivity.this, ViewExpenseActivity.class);
            startActivity(viewExpenseIntent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

}

