package com.haihoangtran.pm.components;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.ArrayAdapter;

import androidx.arch.core.util.Function;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.haihoangtran.pm.R;

import java.util.ArrayList;

public class SwipeListViewBuilder{

    public static <T> SwipeMenuListView build(ArrayList<T> records, ArrayAdapter<T> adapter, SwipeMenuListView listView,
                                              final Context context,
                                              final int titleSize,
                                              final int widthSize){
        // Add adapter to list view
        listView.setAdapter(adapter);

        // Create swipe Menu
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                // Create Edit button
                SwipeMenuItem editItem = new SwipeMenuItem(context);
                editItem.setBackground(new ColorDrawable(Color.rgb(0x30, 0xB1, 0xF5)));
                editItem.setWidth(170);
                editItem.setTitle(R.string.edit);
                editItem.setTitleSize(titleSize);
                editItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(editItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(context);
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,0x3F, 0x25)));
                deleteItem.setWidth(widthSize);
                deleteItem.setIcon(R.drawable.ic_delete);
                menu.addMenuItem(deleteItem);
            }

        };

        //Add menu item to list view
        listView.setMenuCreator(creator);
        return listView;
    }

}
