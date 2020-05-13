package com.furja.qc.view;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.furja.qc.R;
import com.furja.qc.databases.DaoSession;
import com.furja.qc.databases.ProduceNoDao;
import com.furja.qc.utils.LocalBadTypeQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangmeng on 2017/12/14.
 */

public class MyAutoAdapter extends ArrayAdapter {
    private ArrayList<String> autoPrompts;
    private int flag;
    String queryString;
    public MyAutoAdapter(@NonNull Context context, int falg) {
        super(context, R.layout.simple_list_item);
        autoPrompts=new ArrayList<String>();
        this.flag=falg;
    }

    public MyAutoAdapter(@NonNull Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);

    }

    public MyAutoAdapter(@NonNull Context context, int resource, @NonNull Object[] objects) {
        super(context, resource, objects);
    }

    public MyAutoAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull Object[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public MyAutoAdapter(@NonNull Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
    }

    public MyAutoAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @Override
    public int getCount() {
        return autoPrompts.size();
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return autoPrompts.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        AutoViewHolder autoViewHolder;
        if(convertView==null)
        {
            convertView= LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.simple_list_item,null);
            autoViewHolder=new AutoViewHolder();
            autoViewHolder.textView=(TextView)convertView.findViewById(R.id.simple_text_item);
            convertView.setTag(autoViewHolder);
        }
        else
            autoViewHolder=(AutoViewHolder)convertView.getTag();
        autoViewHolder.textView.setText(getItem(position));
        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
//        return super.getFilter();
        return new MyFilter();
    }

    /**
     * 自行定制的过滤器,根据输入去本地数据库寻找答案
     */
    private class MyFilter extends Filter
    {
        LocalBadTypeQuery badTypeQuery;
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            badTypeQuery=new LocalBadTypeQuery();
            if(TextUtils.isEmpty(charSequence))
                charSequence="";
            List<String> results=new ArrayList<>();
            switch (flag)
            {
                case 1:
                    results=badTypeQuery.query(charSequence.toString());
                    break;
                case 2:
                    results=badTypeQuery.queryProductModel(charSequence.toString());
                    break;
                case 3:
                    results=badTypeQuery.queryProduceNo(charSequence);
                    break;
            }
            FilterResults filterResults= new FilterResults();
            filterResults.count=results.size();
            filterResults.values=results;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            autoPrompts= (ArrayList<String>) filterResults.values;
            notifyDataSetChanged();
        }
    }

    class AutoViewHolder
    {

        TextView textView;
        public AutoViewHolder() {
        }
    }
}
