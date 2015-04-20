package com.example.adrianb.contactlistview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adrianb on 20/04/15.
 */
public class AlphabetListAdapter extends BaseAdapter implements Filterable {

    public static abstract class Row {
        public final String text;

        protected Row(String text) {
            this.text = text;
        }
    }

    public static final class Header extends Row {
        public Header(String text) {
            super(text);
        }
    }

    public static final class Cell extends Row {
        public Cell(String text) {
            super(text);
        }
    }

    private List<Row> rows;
    private List<Row> tempRows;

    public void setRows(List<Row> rows) {
        this.rows = rows;
        this.tempRows = rows;
    }

    @Override
    public int getCount() {
        return rows.size();
    }

    @Override
    public Row getItem(int position) {
        return rows.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position) instanceof Header) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (getItemViewType(position) == 0) { // Cell
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = (LinearLayout) inflater.inflate(R.layout.row_contact_item, parent, false);
            }
            Cell item = (Cell) getItem(position);
            TextView textView = (TextView) view.findViewById(R.id.textView1);
            textView.setText(item.text);
            view.setTag(item);
        } else { // Header
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = (LinearLayout) inflater.inflate(R.layout.row_contact_section, parent, false);
            }

            Header section = (Header) getItem(position);
            TextView textView = (TextView) view.findViewById(R.id.textView1);
            textView.setText(section.text);
        }

        return view;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                rows = (List<Row>)results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                ArrayList<Row> filteredList= new ArrayList<Row>();
                if (constraint == null || constraint.length() == 0) {
                    // No filter implemented we return all the list
                    results.values = tempRows;
                    results.count = tempRows.size();
                }
                else {
                    for (int i = 0; i < tempRows.size(); i++) {
                        Row row = tempRows.get(i);
                        String rowText;
                        if(row instanceof Header) {
                            rowText = ((Header) row).text;
                            if (rowText.toLowerCase().substring(0, 1).equals(constraint.toString().toLowerCase().substring(0, 1)))  {
                                filteredList.add(row);
                            }
                        } else if (row instanceof Cell) {
                            rowText = ((Cell) row).text;
                            if (rowText.toLowerCase().startsWith(constraint.toString().toLowerCase()))  {
                                filteredList.add(row);
                            }
                        }
                    }

                    if(filteredList.size() <= 1) {
                        filteredList.clear();
                    }

                    results.values = filteredList;
                    results.count = filteredList.size();
                }
                return results;
            }
        };
        return filter;
    }

}
