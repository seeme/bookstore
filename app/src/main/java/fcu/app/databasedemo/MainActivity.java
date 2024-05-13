package fcu.app.databasedemo;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends AppCompatActivity {

  private static final String DATABASE_NAME = "my_store";
  private SQLiteDatabase productDatabase;
  private static final String TABLE_NAME = "product";
  private EditText etName;
  private EditText etPrice;
  private Button btnAddProduct;
  private Button btnCleanProducts;
  private Button btnUpdateProduct;
  private Button btnDeleteProduct;
  private ListView lvProducts;
  private static final String CREATE_PRODUCT_TABLE_SQL = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME +" (_id INTEGER PRIMARY KEY, name TEXT, price INTEGER)";

  private long selectedProductId;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    productDatabase = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
    productDatabase.execSQL(CREATE_PRODUCT_TABLE_SQL);

    etName = findViewById(R.id.et_name);
    etPrice = findViewById(R.id.et_price);
    btnAddProduct = findViewById(R.id.btn_add_product);
    btnCleanProducts = findViewById(R.id.btn_clean_products);
    btnUpdateProduct = findViewById(R.id.btn_update_product);
    btnDeleteProduct = findViewById(R.id.btn_delete_product);
    lvProducts = findViewById(R.id.lv_products);
    listAllProducts();

    View.OnClickListener clickListener = new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (view.getId() == R.id.btn_add_product) {
          String productName = etName.getText().toString();
          int productPrice = Integer.parseInt(etPrice.getText().toString());
          String insertSql = "INSERT INTO " + TABLE_NAME + "(name, price) VALUES ('" + productName + "'," + productPrice + " )";
          productDatabase.execSQL(insertSql);
        } else if (view.getId() == R.id.btn_clean_products) {
          String deleteAllSql = "DELETE FROM " +TABLE_NAME;
          productDatabase.execSQL(deleteAllSql);
        } else if (view.getId() == R.id.btn_update_product) {
          String productName = etName.getText().toString();
          int productPrice = Integer.parseInt(etPrice.getText().toString());
          String updateSql = "UPDATE " + TABLE_NAME + " SET name = '" + productName + "', price = "
            + productPrice +" WHERE _id = " + selectedProductId;
          productDatabase.execSQL(updateSql);
        } else if (view.getId() == R.id.btn_delete_product) {
          String deleteSql = "DELETE FROM " + TABLE_NAME + " WHERE _id = " + selectedProductId;
          productDatabase.execSQL(deleteSql);
        }
        etName.setText("");
        etPrice.setText("");
        listAllProducts();
      }
    };
    btnAddProduct.setOnClickListener(clickListener);
    btnCleanProducts.setOnClickListener(clickListener);
    btnUpdateProduct.setOnClickListener(clickListener);
    btnDeleteProduct.setOnClickListener(clickListener);

    AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int index, long id) {
        String querySql = "SELECT * FROM " + TABLE_NAME + " WHERE _id=" + id;
        Cursor cursor = productDatabase.rawQuery(querySql, null);
        cursor.moveToFirst();
        System.out.println("name:" + cursor.getString(1) + ", price: " + cursor.getInt(2));
        etName.setText(cursor.getString(1));
        etPrice.setText(String.valueOf(cursor.getInt(2)));
        selectedProductId = id;
      }
    };
    lvProducts.setOnItemClickListener(itemClickListener);
  }

  private void listAllProducts() {
    //Cursor cursor = productDatabase.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    Cursor cursor = productDatabase.query(TABLE_NAME, new String[] {"_id", "name", "price"},
      null, null, null, null, null, null);

    SimpleCursorAdapter scAdapter = new SimpleCursorAdapter(MainActivity.this, android.R.layout.simple_list_item_2,
      cursor, new String[]{"name", "price"}, new int[]{android.R.id.text1, android.R.id.text2}, 0);
    lvProducts.setAdapter(scAdapter);
  }


  @Override
  protected void onDestroy() {
    super.onDestroy();
    productDatabase.close();
  }
}