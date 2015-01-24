package com.bitdubai.smartwallet.ui.os.android.app.subapp.marketplace.version_1.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import com.bitdubai.smartwallet.R;

public  class ShopsFragment extends Fragment {

    private static final String ARG_POSITION = "position";

    View rootView;

    ExpandableListView lv;

    private String[] item;

    private String[][] sub_item;




    public static ShopsFragment newInstance(int position) {
        ShopsFragment f = new ShopsFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        item = new String[]{""};

        sub_item = new String[][]{

                {},
                {},
                {},
                {},
                {}
        };


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.marketplace_inflater, container, false);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lv = (ExpandableListView) view.findViewById(R.id.expListView);
        lv.setAdapter(new ExpandableListAdapter(item, sub_item));
        lv.setGroupIndicator(null);

        lv.setOnItemClickListener(null);

    }

    public class ExpandableListAdapter extends BaseExpandableListAdapter {

        private final LayoutInflater inf;
        private String[] item;
        private String[][] sub_item;

        public ExpandableListAdapter(String[] item, String[][] sub_item) {
            this.item = item;
            this.sub_item = sub_item;
            inf = LayoutInflater.from(getActivity());
        }


        @Override
        public int getGroupCount() {
            return item.length;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return sub_item[groupPosition].length;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return item[groupPosition];
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return sub_item[groupPosition][childPosition];
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            convertView = inf.inflate(R.layout.marketplace_shops_detail, parent, false);


            return convertView;
        }

        @Override
        public View getGroupView(final int groupPosition, final boolean isExpanded, View convertView, final ViewGroup parent) {
            convertView = inf.inflate(R.layout.marketplace_shops_header, parent, false);



            return convertView;
        }
        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        private class ViewHolder {
            TextView text;
        }
    }
}
