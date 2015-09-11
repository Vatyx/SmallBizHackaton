package com.trinew.easytime.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.trinew.easytime.R;
import com.trinew.easytime.models.ParseStamp;
import com.trinew.easytime.modules.stamps.StampCollection;
import com.trinew.easytime.utils.PrettyTime;
import com.trinew.easytime.views.AnimatedExpandableListView;
import com.trinew.easytime.views.viewholders.StampCollectionChildHolder;
import com.trinew.easytime.views.viewholders.StampCollectionGroupHolder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StampCollectionAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {

    private LayoutInflater inflater;

    private Context mContext;
    private List<StampCollection> mItems;

    public StampCollectionAdapter(Context context) {
        inflater = LayoutInflater.from(context);

        mContext = context;
        mItems = new ArrayList<>();
    }

    public void setCollections(List<StampCollection> collections) {
        mItems = collections;
        notifyDataSetChanged();
    }

    public void addCollection(StampCollection collection) {
        mItems.add(collection);
        notifyDataSetChanged();
    }

    public void clearCollections() {
        mItems.clear();
        notifyDataSetChanged();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mItems.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return mItems.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mItems.get(groupPosition).getStamps().get(childPosition);
    }

    @Override
    public int getRealChildrenCount(int groupPosition) {
        return mItems.get(groupPosition).getStamps().size();
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getRealChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        // init views
        StampCollectionChildHolder holder;

        if (convertView == null) {
            holder = new StampCollectionChildHolder();
            convertView = inflater.inflate(R.layout.row_stamp, parent, false);
            holder.thumbImage = (ImageView) convertView.findViewById(R.id.thumbImage);
            holder.thumbText = (TextView) convertView.findViewById(R.id.thumbText);
            holder.timeText = (TextView) convertView.findViewById(R.id.timeText);
            holder.commentText = (TextView) convertView.findViewById(R.id.commentText);
            convertView.setTag(holder);
        } else {
            holder = (StampCollectionChildHolder) convertView.getTag();
        }

        ParseStamp stamp = (ParseStamp) getChild(groupPosition, childPosition);
        Date stampDate = stamp.getLogDate();
        int flag = stamp.getFlag();

        int color = 0xffffffff;

        switch (flag) {
            case ParseStamp.FLAG_CHECK_IN:
                holder.thumbText.setText("IN");
                color = mContext.getResources().getColor(R.color.green_light);
                break;
            case ParseStamp.FLAG_CHECK_OUT:
                holder.thumbText.setText("OUT");
                color = mContext.getResources().getColor(R.color.red_light);
                break;
        }

        holder.thumbImage.setColorFilter(color);

        String commentStr = stamp.getComment();

        holder.timeText.setText(PrettyTime.getPrettyTime(stampDate));
        holder.commentText.setText(commentStr);

        return convertView;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {

        // init views
        StampCollectionGroupHolder holder;

        if (convertView == null) {
            holder = new StampCollectionGroupHolder();
            convertView = inflater.inflate(R.layout.row_stamp_collection, parent, false);
            holder.dateText = (TextView) convertView.findViewById(R.id.dateText);
            holder.durationText = (TextView) convertView.findViewById(R.id.durationText);
            convertView.setTag(holder);
        } else {
            holder = (StampCollectionGroupHolder) convertView.getTag();
        }

        StampCollection stampCollection = (StampCollection) getGroup(groupPosition);

        Date stampDate = stampCollection.getCollectionDate();
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("M/d");
        String collectionDateStr = outputDateFormat.format(stampDate);

        long duration = stampCollection.getDuration();
        String durationStr = PrettyTime.getPrettyDuration(duration);

        // fill views\
        holder.dateText.setText(collectionDateStr);
        holder.durationText.setText(durationStr);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
