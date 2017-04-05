package com.example.potoyang.train;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by 71579 on 2017/3/24.
 *
 * 自定义Adapter,实现Item的绘制
 */

public class PopAdapter extends BaseAdapter {

    private List<String> list;
    private int headColor;
    private int tailColor;
    private List<Integer> boxColor;
    private Context context;
    private LayoutInflater layoutInflater;

    public PopAdapter(Context context, List<String> list, int headColor, int tailColor, List<Integer> boxColor) {
        this.context = context;
        this.list = list;
        this.headColor = headColor;
        this.tailColor = tailColor;
        this.boxColor = boxColor;
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        View view;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            view = layoutInflater.inflate(R.layout.detailinfo, null);
            viewHolder.tv_line_num = (TextView) view.findViewById(R.id.tv_line_num);
            viewHolder.myItemView = (MyItemView) view.findViewById(R.id.my_item);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.tv_line_num.setText(list.get(position));
        viewHolder.myItemView.setHeadColor(headColor);
        viewHolder.myItemView.setTailColor(tailColor);
        viewHolder.myItemView.setBoxColor(boxColor);
        viewHolder.myItemView.setText("换向");

        return view;

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 使用ViewHolder保存数据，不用重复刷新界面
     */
    class ViewHolder {
        TextView tv_line_num;
        MyItemView myItemView;
    }
}
