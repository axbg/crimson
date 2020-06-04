package com.axbg.crimson.ui.quotes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.axbg.crimson.R;
import com.axbg.crimson.db.entity.QuoteEntity;

import java.util.List;

public class QuotesAdapter extends ArrayAdapter<QuoteEntity> {
    private Context context;
    private int resource;

    QuotesAdapter(@NonNull List<QuoteEntity> objects, int resource, @NonNull Context context) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    private static class ViewHolder {
        TextView quote;
        TextView book;
        TextView date;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        QuoteEntity quote = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.quote = convertView.findViewById(R.id.quoteList_quote);
            viewHolder.book = convertView.findViewById(R.id.quoteList_book);
            viewHolder.date = convertView.findViewById(R.id.quoteList_date);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (quote != null) {
            viewHolder.quote.setText(quote.getShortText());
            viewHolder.book.setText("");
            viewHolder.date.setText(quote.getAddedAt().toString());
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "element1", Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }
}
