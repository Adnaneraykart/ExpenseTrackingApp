package com.example.uyg;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class ViewExpenseActivity extends AppCompatActivity {

    private ListView expenseListView;
    private Button clearExpensesButton;
    private DatabaseHelper databaseHelper;
    private ArrayAdapter<String> adapter;
    private ArrayList<Expense> expenseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_expense);

        expenseListView = findViewById(R.id.expense_list_view);
        clearExpensesButton = findViewById(R.id.clear_expenses_button);

        databaseHelper = new DatabaseHelper(this);
        expenseList = new ArrayList<>();


        loadExpenses();


        clearExpensesButton.setOnClickListener(v -> {

            clearAllExpenses();
        });





        expenseListView.setOnItemClickListener((parent, view, position, id) -> {

            Expense selectedExpense = expenseList.get(position);
            showOptionsDialog(selectedExpense);
        });
    }

    private void loadExpenses() {
        expenseList.clear();
        Cursor cursor = databaseHelper.getAllExpenses();

        if (cursor != null) {
            int idColumnIndex = cursor.getColumnIndex("id");
            int nameColumnIndex = cursor.getColumnIndex("name");
            int amountColumnIndex = cursor.getColumnIndex("amount");

            if (idColumnIndex != -1 && nameColumnIndex != -1 && amountColumnIndex != -1) {
                if (cursor.moveToFirst()) {
                    do {
                        int id = cursor.getInt(idColumnIndex);
                        String name = cursor.getString(nameColumnIndex);
                        double amount = cursor.getDouble(amountColumnIndex);
                        expenseList.add(new Expense(id, name, amount));
                    } while (cursor.moveToNext());
                }
            } else {
                Toast.makeText(ViewExpenseActivity.this, "Veritabanı sütunları hatalı!", Toast.LENGTH_SHORT).show();
            }
            cursor.close();
        }


        ArrayList<String> displayList = new ArrayList<>();
        for (Expense expense : expenseList) {
            displayList.add(expense.getName() + " - " + expense.getAmount());
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList);
        expenseListView.setAdapter(adapter);
    }

    private void clearAllExpenses() {
        boolean result = databaseHelper.clearAllExpenses();
        if (result) {
            loadExpenses();
            Toast.makeText(ViewExpenseActivity.this, "Tüm harcamalar sıfırlandı.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(ViewExpenseActivity.this, "Harcamalar sıfırlanırken bir hata oluştu.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showOptionsDialog(Expense expense) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Seçenekler")
                .setItems(new CharSequence[]{"Harcama Güncelle", "Harcama Sil"}, (dialog, which) -> {
                    if (which == 0) {

                        Intent intent = new Intent(ViewExpenseActivity.this, AddExpenseActivity.class);
                        intent.putExtra("expenseId", expense.getId());
                        startActivity(intent);
                    } else if (which == 1) {

                        confirmDelete(expense);
                    }
                })
                .show();
    }

    private void confirmDelete(Expense expense) {
        new AlertDialog.Builder(this)
                .setMessage("Bu harcamayı silmek istediğinizden emin misiniz?")
                .setPositiveButton("Evet", (dialog, which) -> {
                    boolean result = databaseHelper.deleteExpense(expense.getId());
                    if (result) {
                        Toast.makeText(ViewExpenseActivity.this, "Harcama silindi.", Toast.LENGTH_SHORT).show();
                        loadExpenses();
                    } else {
                        Toast.makeText(ViewExpenseActivity.this, "Harcama silinemedi.", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hayır", null)
                .show();
    }


    public class Expense {
        private int id;
        private String name;
        private double amount;

        public Expense(int id, String name, double amount) {
            this.id = id;
            this.name = name;
            this.amount = amount;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public double getAmount() {
            return amount;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadExpenses();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem SelectedItem = menu.findItem(R.id.add_expense);
        SelectedItem.setTitle("Ana Menü");
        MenuItem SelectedItem2 = menu.findItem(R.id.view_expense_list);
        SelectedItem2.setTitle("Harcama Ekle");
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

            Intent viewExpenseIntent = new Intent(this, AddExpenseActivity.class);
            startActivity(viewExpenseIntent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
