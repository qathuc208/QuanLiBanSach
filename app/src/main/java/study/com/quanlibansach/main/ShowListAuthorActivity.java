package study.com.quanlibansach.main;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import study.com.quanlibansach.R;
import study.com.quanlibansach.adapter.MySimpleArrayAdapter;
import study.com.quanlibansach.object.InforData;

public class ShowListAuthorActivity extends AppCompatActivity {
    List<InforData> list=new ArrayList<InforData>();
    InforData dataClick=null;
    SQLiteDatabase database=null;
    MySimpleArrayAdapter adapter=null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_show_data);

        updateUI();
        Button btnBack = (Button) findViewById(R.id.buttonBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowListAuthorActivity.this.finish();
            }
        });
    }

    @SuppressLint("WrongConstant")
    public void updateUI() {
        database=openOrCreateDatabase("mydata.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
        if(database!=null)
        {

            Cursor cursor=database.query("tblAuthors", null, null, null, null, null, null);
            startManagingCursor(cursor);
            InforData header=new InforData();
            header.setField1("STT");
            header.setField2("Mã tác giả");
            header.setField3("Tên tác giả");
            list.add(header);
            cursor.moveToFirst();
            while(!cursor.isAfterLast())
            {
                InforData data=new InforData();
                data.setField1(cursor.getInt(0));
                data.setField2(cursor.getString(1));
                data.setField3(cursor.getString(2));
                list.add(data);
                cursor.moveToNext();
            }
            stopManagingCursor(cursor);
            cursor.close();
            adapter=new MySimpleArrayAdapter(ShowListAuthorActivity.this, R.layout.my_layout_for_show_list_data, list);
            final ListView lv= (ListView) findViewById(R.id.listViewShowData);
            lv.setAdapter(adapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int arg2, long arg3) {
                    // TODO Auto-generated method stub
                    Toast.makeText(ShowListAuthorActivity.this,"View -->"+ list.get(arg2).toString(), Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(ShowListAuthorActivity.this, CreateAuthorActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putInt("KEY", 1);
                    bundle.putString("getField1", list.get(arg2).getField1().toString());
                    bundle.putString("getField2", list.get(arg2).getField2().toString());
                    bundle.putString("getField3", list.get(arg2).getField3().toString());
                    intent.putExtra("DATA", bundle);
                    dataClick=list.get(arg2);
                    startActivityForResult(intent, MainActivity.OPEN_AUTHOR_DIALOG);
                }
            });
            lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                               int arg2, long arg3) {
                    // TODO Auto-generated method stub
                    final InforData data=list.get(arg2);
                    final int pos=arg2;
                    Toast.makeText(ShowListAuthorActivity.this, "Edit-->"+data.toString(), Toast.LENGTH_LONG).show();
                    AlertDialog.Builder b=new AlertDialog.Builder(ShowListAuthorActivity.this);
                    b.setTitle("Remove");
                    b.setMessage("Xóa ["+data.getField2() +" - "+data.getField3() +"] hả?");
                    b.setPositiveButton("Có", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            int n=database.delete("tblAuthors", "id=?", new String[]{data.getField1().toString()});
                            if(n>0)
                            {
                                Toast.makeText(ShowListAuthorActivity.this, "Remove ok", Toast.LENGTH_LONG).show();
                                list.remove(pos);
                                adapter.notifyDataSetChanged();
                            }
                            else
                            {
                                Toast.makeText(ShowListAuthorActivity.this, "Remove not ok", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    b.setNegativeButton("Không", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            dialog.cancel();
                        }
                    });
                    b.show();
                    return true;
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == MainActivity.SEND_DATA_FROM_AUTHOR_ACTIVITY) {
            // Update Author theo ID
            Log.d(MainActivity.TAG, "Update Theo ID");
            Bundle bundle = data.getBundleExtra("DATA_AUTHOR");

            String f2 = bundle.getString("firstName");
            String f3 = bundle.getString("lastName");
            String f1 = dataClick.getField1().toString();

            ContentValues contentValues = new ContentValues();
            contentValues.put("firstName", f2);
            contentValues.put("lastName", f3);

            if(database != null) {
                int n=database.update("tblAuthors", contentValues, "id=?", new String[]{f1});
                if(n>0) {
                        Toast.makeText(ShowListAuthorActivity.this, "update ok ok ok ", Toast.LENGTH_LONG).show();
                        dataClick.setField2(f2);
                        dataClick.setField3(f3);
                        if(adapter!=null)
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }
}
