package com.example.luis.usersbrowser.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.luis.usersbrowser.R;
import com.example.luis.usersbrowser.retrofit.model.Result;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;


public class SearchAdapter extends ArrayAdapter<Result> {
    private List<Result> usersListFull;

    private PublishSubject<Result> suggestionSelectedSubject = PublishSubject.create();
    public Observable<Result> suggestionSelectedEvent = suggestionSelectedSubject;

    private Filter usersFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<Result> suggestions = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                suggestions.addAll(usersListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Result item : usersListFull) {
                    if ((item.getName().getFirst() + " " + item.getName().getLast()).toLowerCase().contains(filterPattern)) {
                        suggestions.add(item);
                    }
                }
            }
            results.values = suggestions;
            results.count = suggestions.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            addAll((List) results.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((Result) resultValue).getName().getFirst() + " " + ((Result) resultValue).getName().getLast();
        }
    };

    public SearchAdapter(@NonNull Context context, @NonNull List<Result> usersList) {
        super(context, 0, usersList);
        usersListFull = new ArrayList<>(usersList);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return usersFilter;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.item_search, parent, false
            );
        }

        TextView textViewName = convertView.findViewById(R.id.txtUser);
        Result userItem = getItem(position);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                suggestionSelectedSubject.onNext(getItem(position));
            }
        });

        if (userItem != null) {
            textViewName.setText(userItem.getName().getFirst() + " " + userItem.getName().getLast());
        }

        return convertView;
    }
}