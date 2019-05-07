package com.common.android.utils.maputil;

import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.common.android.R;
import com.common.android.adapter.AdapterHolder;
import com.common.android.adapter.BaseListAdapter;

import java.util.List;


public class MapSelectDialog extends Dialog {
    private TextView tv_title;
    private ListView listView;
    private String title;
    private List<IconListItem> list;
    private MapHelper.MapLocation startPoint, endPoint;


    public MapSelectDialog(Context context, String title, List<IconListItem> list, MapHelper.MapLocation start, MapHelper.MapLocation end) {
        super(context, R.style.dialog);
        this.title = title;
        this.list = list;
        this.startPoint = start;
        this.endPoint = end;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_map_select);

        tv_title = (TextView) findViewById(R.id.tv_title);
        listView = (ListView) findViewById(R.id.listview);

        tv_title.setText(title);

        bindData();
    }

    private void bindData() {
        final BaseListAdapter listAdapter = new BaseListAdapter<IconListItem>(listView, list, R.layout.item_map_selector) {
            @Override
            public void convert(AdapterHolder holder, IconListItem item, boolean isScrolling) {
                holder.setText(R.id.tv_title, item.getTitle());
                ImageView imageView = holder.getView(R.id.image);
                if (item.getResource() != null) {
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImageDrawable(item.getResource());
                } else {
                    imageView.setVisibility(View.GONE);
                }
            }
        };
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dismiss();
                IconListItem item = list.get(position);
                PackageInfo info = (PackageInfo) item.getAttach();
                MapHelper.navigate(getContext(), info, startPoint, endPoint);
            }
        });
    }
}
