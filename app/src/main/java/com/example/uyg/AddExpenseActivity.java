package com.example.uyg;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddExpenseActivity extends AppCompatActivity {
    private EditText expenseNameEditText, expenseAmountEditText;
    private Button saveExpenseButton;
    private DatabaseHelper dbHelper;
    private int expenseId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        expenseNameEditText = findViewById(R.id.expense_name_edit_text);
        expenseAmountEditText = findViewById(R.id.expense_amount_edit_text);
        saveExpenseButton = findViewById(R.id.save_expense_button);
        dbHelper = new DatabaseHelper(this);

        expenseId = getIntent().getIntExtra("expenseId", -1);
        if (expenseId != -1) {
            loadExpenseData();
        }

        saveExpenseButton.setOnClickListener(v -> {
            String name = expenseNameEditText.getText().toString().trim();
            String amountText = expenseAmountEditText.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(amountText)) {
                Toast.makeText(this, "Lütfen tüm alanları doldurun.", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Double.parseDouble(amountText);

            if (expenseId == -1) {
                boolean isInserted = dbHelper.insertExpense(name, amount);
                if (isInserted) {
                    Toast.makeText(this, "Harcama eklendi.", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Harcama eklenemedi.", Toast.LENGTH_SHORT).show();
                }
            } else {
                boolean isUpdated = dbHelper.updateExpense(expenseId, name, amount);
                if (isUpdated) {
                    Toast.makeText(this, "Harcama güncellendi.", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "Harcama güncellenemedi.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadExpenseData() {
        Cursor cursor = dbHelper.getExpenseById(expenseId);

        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));

            expenseNameEditText.setText(name);
            expenseAmountEditText.setText(String.valueOf(amount));
            cursor.close();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem SelectedItem = menu.findItem(R.id.add_expense);
        SelectedItem.setTitle("Ana Menü");
        return true;
    }


    @Override

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.add_expense) {

            Intent addExpenseIntent = new Intent(this, MainActivity.class);
            startActivity(addExpenseIntent);
            return true;
        } else if (id == R.id.view_expense_list) {

            Intent viewExpenseIntent = new Intent(this, ViewExpenseActivity.class);
            startActivity(viewExpenseIntent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
