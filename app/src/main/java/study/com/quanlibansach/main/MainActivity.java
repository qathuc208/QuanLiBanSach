package study.com.quanlibansach.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Locale;

import study.com.quanlibansach.R;

import static java.util.Spliterator.DISTINCT;

public class MainActivity extends AppCompatActivity {


    Button mCreateDatabase = null;
    Button mInsertAuthor = null;
    Button mShowAuthorList = null;
    Button mShowAuthorList2 = null;
    Button mTransaction = null;
    Button mShowDetailsBook = null;
    Button mInsertBook = null;

    public static String TAG = "abc";

    public static final int OPEN_AUTHOR_DIALOG = 1111;
    public static final int SEND_DATA_FROM_AUTHOR_ACTIVITY = 3333;

    SQLiteDatabase database = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mInsertAuthor = (Button) findViewById(R.id.btnInsertAuthor);
        mShowDetailsBook = (Button) findViewById(R.id.buttonShowAuthorList);
        mInsertBook = (Button) findViewById(R.id.buttonInsertBook1);

        mInsertBook.setOnClickListener(new MyEvent());
        mShowDetailsBook.setOnClickListener(new MyEvent());
        mInsertAuthor.setOnClickListener(new MyEvent());

        getDatabase();
    }

    public boolean isTableExists(SQLiteDatabase database, String tableName) {
        Cursor cursor = database.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"+tableName+"'", null);
        if (cursor != null) {
            if(cursor.getCount() > 0) {
                cursor.close();
                return true;
            }
        }
        return false;
    }

    @SuppressLint("WrongConstant")
    public SQLiteDatabase getDatabase() {
        try {
            Log.d(TAG, "create database");
            database = openOrCreateDatabase("mydata.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
            if (database != null) {
                if (isTableExists(database, "tblAuthors")) {
                    return database;
                }

                database.setLocale(Locale.getDefault());
                database.setVersion(1);

                String sqlAuthor="create table tblAuthors ("
                        +"id integer primary key autoincrement,"
                        +"firstname text, "
                        +"lastname text)";
                database.execSQL(sqlAuthor);
                String sqlBook="create table tblBooks ("
                        +"id integer primary key autoincrement,"
                        +"title text, "
                        +"dateadded date,"
                        +"authorid integer not null constraint authorid references tblAuthors(id) on delete cascade)";
                database.execSQL(sqlBook);
                //Cách tạo trigger khi nhập dữ liệu sai ràng buộc quan hệ
                String sqlTrigger="create trigger fk_insert_book before insert on tblBooks "
                        +" for each row "
                        +" begin "
                        +" select raise(rollback,'them du lieu tren bang tblBooks bi sai') "
                        +" where (select id from tblAuthors where id=new.authorid) is null ;"
                        +" end;";
                database.execSQL(sqlTrigger);
                Toast.makeText(MainActivity.this, "OK OK", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e)
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
        return database;
    }

    public void showInsertAuthorDialog() {
        Intent intent = new Intent(MainActivity.this, CreateAuthorActivity.class);
        startActivityForResult(intent, OPEN_AUTHOR_DIALOG);
    }

    public void showAuthorList1()
    {
        Intent intent=new Intent(MainActivity.this, ShowListAuthorActivity.class);
        startActivity(intent);
    }

    public void showAuthorList2()
    {
        Intent intent=new Intent(MainActivity.this, ShowListAuthorActivity2.class);
        startActivity(intent);
    }

    public void  interactDBWithTransaction() {
        if (database != null) {
            database.beginTransaction();
            try {
                ContentValues contentValues = new ContentValues();
                contentValues.put("firstname", "xx");
                contentValues.put("lastName", "yyy");
                database.insert("tblAuthors", null, contentValues);
                database.delete("tblAuthors", "ma=?", new String[]{"x"});
                database.setTransactionSuccessful();
            }
            catch (Exception e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            } finally {
                database.endTransaction();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == SEND_DATA_FROM_AUTHOR_ACTIVITY) {
            Log.d(TAG, "xu li tao tac gia 1");
            Bundle bundle= data.getBundleExtra("DATA_AUTHOR");
            String firstname=bundle.getString("firstName");
            String lastname=bundle.getString("lastName");
            ContentValues content=new ContentValues();
            content.put("firstName", firstname);
            content.put("lastName", lastname);

            if(database != null) {
                long authorid=database.insert("tblAuthors", null, content);
                if(authorid==-1)
                {
                    Toast.makeText(MainActivity.this,authorid+" - "+ firstname +" - "+lastname +" ==> insert error!", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Toast.makeText(MainActivity.this, authorid+" - "+firstname +" - "+lastname +" ==>insert OK!", Toast.LENGTH_LONG).show();
                }
            }
            }
        }

    class MyEvent implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.btnInsertAuthor) {
                showInsertAuthorDialog();
            } else if (v.getId() == R.id.buttonShowAuthorList) {
                showAuthorList1();
            } else if (v.getId() == R.id.buttonInsertBook1) {
                Log.d("abc","Call insert");
                Intent intent = new Intent(MainActivity.this, InsertBookActivity.class);
                startActivity(intent);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_simple_database_main,menu);
        return true;
    }
}

