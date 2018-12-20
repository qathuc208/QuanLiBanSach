package study.com.quanlibansach.main;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import study.com.quanlibansach.R;
import study.com.quanlibansach.adapter.MySimpleArrayAdapter;
import study.com.quanlibansach.object.InforData;

public class InsertBookActivity extends AppCompatActivity {

    SQLiteDatabase database = null;
    List<InforData> listBook = null;
    List<InforData> listAuthor = null;

    InforData authorData = null;
    MySimpleArrayAdapter adapter = null;
    int mDay, mMonth, mYear;


    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_book);

        Spinner pinner=(Spinner) findViewById(R.id.spinner1);
        listAuthor = new ArrayList<InforData>();
        InforData d1 = new InforData();
        d1.setField1("_");
        d1.setField2("Show All");
        d1.setField3("_");

        listAuthor.add(d1);
        // dua du lieu author vao spiner
        database = openOrCreateDatabase("mydata.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
        if (database != null) {
            Cursor cursor = database.query("tblAuthors", null, null, null, null, null, null);
            cursor.moveToFirst();
            while (cursor.isAfterLast()==false) {
                InforData d=new InforData();
                d.setField1(cursor.getInt(0));     // STT
                d.setField2(cursor.getString(1));  // Ma TG
                d.setField3(cursor.getString(2));  // Ten Tac Gia

                listAuthor.add(d);
                cursor.moveToNext();
            }
            cursor.close();
        }

        adapter = new MySimpleArrayAdapter(InsertBookActivity.this, R.layout.my_layout_for_show_list_data, listAuthor);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pinner.setAdapter(adapter);

        pinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    authorData = null;
                    loadAllListBook();
                } else {
                    authorData = listAuthor.get(position);
                    loadListBookByAuthor(authorData.getField1().toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                authorData = null;
            }
        });

        setCurrentDateOnView();
        //Handle datepicker

        Button btnChangeDate = (Button) findViewById(R.id.buttonDate);
        btnChangeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(113);
            }
        });

        Button btnInsertBook = (Button) findViewById(R.id.buttonInsertBook);
        btnInsertBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(authorData==null)
                {
                    Toast.makeText(InsertBookActivity.this, "Please choose an author to insert", Toast.LENGTH_LONG).show();
                    return;
                }

                EditText txtTitle = (EditText) findViewById(R.id.editTextTitle);
                ContentValues values = new ContentValues();
                values.put("title", txtTitle.getText().toString());
                Calendar c = Calendar.getInstance();
                c.set(mYear, mMonth, mDay);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
                values.put("dateadded",simpleDateFormat.format(c.getTime()));
                values.put("authorid", authorData.getField1().toString());

                long bId = database.insert("tblBooks", null, values);
                if (bId > 0) {
                    Log.d(MainActivity.TAG, "Luu Database thanh cong");
                    loadListBookByAuthor(authorData.getField1().toString());
                } else {
                    Toast.makeText(InsertBookActivity.this, "Insert Book Failed", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void loadAllListBook() {
        Cursor cur=database.query("tblBooks", null, null, null, null, null, null);
        cur.moveToFirst();
        listBook=new ArrayList<InforData>();

        while (cur.isAfterLast() == false) {
            InforData d=new InforData();
            d.setField1(cur.getInt(0));
            d.setField2(cur.getString(1));
            d.setField3(cur.getString(2));
            listBook.add(d);
            cur.moveToNext();
        }
        cur.close();

        adapter = new MySimpleArrayAdapter(InsertBookActivity.this, R.layout.my_layout_for_show_list_data, listBook);
        ListView lv=(ListView) findViewById(R.id.listViewBook);
        lv.setAdapter(adapter);
    }

    public void loadListBookByAuthor(String authorid) {
        Cursor cur=database.query("tblBooks", null, "authorid=?", new String[]{authorid}, null, null, null);

        cur.moveToFirst();
        listBook=new ArrayList<InforData>();

        while (cur.isAfterLast() == false) {
            InforData d=new InforData();
            d.setField1(cur.getInt(0));
            d.setField2(cur.getString(1));
            d.setField3(cur.getString(2));
            listBook.add(d);
            cur.moveToNext();
        }
        cur.close();

        adapter = new MySimpleArrayAdapter(InsertBookActivity.this, R.layout.my_layout_for_show_list_data, listBook);
        ListView lv=(ListView) findViewById(R.id.listViewBook);
        lv.setAdapter(adapter);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 113) {
            return new DatePickerDialog(this, dateChange, mYear, mMonth, mDay);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener dateChange = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            mYear = year;
            mMonth = month;
            mDay= dayOfMonth;
            EditText eDate = (EditText) findViewById(R.id.editTextDate);
            eDate.setText(mDay+"-"+(mMonth+1)+"-"+mYear);
        }
    };

    // Thiet lap ngay thang nam nhien tai
    public void setCurrentDateOnView()
    {
        EditText eDate=(EditText) findViewById(R.id.editTextDate);
        Calendar cal=Calendar.getInstance();
        mDay=cal.get(Calendar.DAY_OF_MONTH);
        mMonth=cal.get(Calendar.MONTH);
        mYear=cal.get(Calendar.YEAR);
        eDate.setText(mDay+"-"+(mMonth+1)+"-"+mYear);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_insert_book, menu);
        return true;
    }
}
