package study.com.quanlibansach.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import study.com.quanlibansach.R;
import study.com.quanlibansach.object.InforData;

public class MySimpleArrayAdapter extends ArrayAdapter<InforData> {

    private Activity mContext;
    private int layout;
    private List<InforData> list;
    private ArrayList<InforData> mList;

    public MySimpleArrayAdapter(@NonNull Context context, int textViewResourceId, List<InforData> objects) {
        super(context, textViewResourceId, objects);

        this.mContext = (Activity) context;
        this.layout = textViewResourceId;
        this.list = objects;
    }

    public MySimpleArrayAdapter(@NonNull Context context, int textViewResourceId, ArrayList<InforData> objects) {
        super(context, textViewResourceId, objects);

        this.mContext = (Activity) context;
        this.layout = textViewResourceId;
        this.mList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater flater=mContext.getLayoutInflater();
        View row=flater.inflate(layout, parent,false);
        TextView txt1=(TextView) row.findViewById(R.id.textView1);
        TextView txt2=(TextView) row.findViewById(R.id.textView2);
        TextView txt3=(TextView) row.findViewById(R.id.textView3);
        /*txt1.setTextAlignment(Gravity.LEFT);
        txt2.setTextAlignment(Gravity.LEFT);
        txt3.setTextAlignment(Gravity.LEFT);*/
        InforData data=list.get(position);
            txt1.setText(data.getField1() == null ? "":data.getField1().toString());
            txt2.setText(data.getField2() == null ? "":data.getField2().toString());
            txt3.setText(data.getField3() == null ?"":data.getField3().toString());
            if(position==0)
            {
                row.setBackgroundColor(Color.RED);
            }

        return row;
    }
}
