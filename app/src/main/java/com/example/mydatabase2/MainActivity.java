package com.example.mydatabase2;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    SQLiteDatabase database;

    EditText editText;
    EditText editText2;

    SingerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.editText);
        editText2 = (EditText) findViewById(R.id.editText2);

        ListView listView = findViewById(R.id.listView);

        adapter = new SingerAdapter();
        // adapter.addItem(new SingerItem("소녀시대", "010-1000-1000"));
        // adapter.addItem(new SingerItem("걸스데이", "010-2000-2000"));
        listView.setAdapter(adapter);

        DatabaseHelper helper = new DatabaseHelper(getApplicationContext(), "customer.db", null, 1);
        database = helper.getWritableDatabase();

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editText.getText().toString();
                String mobile = editText2.getText().toString();

                insert(name, mobile);

            }
        });
    }

    public void insert(String name, String mobile) {
        String sql = "insert into customer(name, mobile) values (?, ?)";

        try {
            Object[] params = {name, mobile};
            database.execSQL(sql, params);

            load();
            Toast.makeText(getApplicationContext(), "데이터 추가했습니다.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void load() {
        String sql = "select name, mobile from customer";
        Cursor cursor = database.rawQuery(sql, null);

        adapter.clear();

        for (int i = 0; i < cursor.getCount(); i++) {
            cursor.moveToNext();

            String name = cursor.getString(0);
            String mobile = cursor.getString(1);

            adapter .addItem(new SingerItem(name, mobile));
        }

        adapter.notifyDataSetChanged();
    }

    class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            Log.d("DBHelper", "onCreate 호출됨.");

            try {
                String sql = "create table customer(name text, mobile text)";
                sqLiteDatabase.execSQL(sql);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            Log.d("DBHelper", "onUpgrade 호출됨.");
        }
    }

    class SingerAdapter extends BaseAdapter {

        ArrayList<SingerItem> items = new ArrayList<SingerItem>();

        public void clear() {
            items.clear();
        }

        public void addItem(SingerItem item) {
            items.add(item);
        }

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            SingerItemView view = null;

            if (convertView == null) {
                view = new SingerItemView(getApplicationContext());
            } else {
                view = (SingerItemView) convertView;
            }

            SingerItem item = items.get(position);
            view.setName(item.getName());
            view.setMobile(item.getMobile());

            return view;
        }

    }
}
