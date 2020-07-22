package com.axbg.crimson.ui.quotes.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.axbg.crimson.R;
import com.axbg.crimson.db.entity.BookEntity;
import com.axbg.crimson.db.entity.QuoteEntity;

import java.util.List;

public class QuotesAdapter extends ArrayAdapter<QuoteEntity> {
    private Context context;
    private int resource;

    public QuotesAdapter(@NonNull List<QuoteEntity> objects, int resource, @NonNull Context context) {
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
        final QuoteEntity quote = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.quote = convertView.findViewById(R.id.adapter_quotes_text);
            viewHolder.book = convertView.findViewById(R.id.adapter_quotes_book);
            viewHolder.date = convertView.findViewById(R.id.adapter_quotes_added);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (quote != null) {
            viewHolder.quote.setText(convertView.getResources().getString(R.string.QUOTE_MARKED_TEXT, quote.getShortText()));
            viewHolder.book.setText(getBookTitle(quote.getBook()));
            viewHolder.date.setText(quote.getAddedAt().toString());
        }

        return convertView;
    }

    private String getBookTitle(BookEntity bookEntity) {
        return bookEntity != null ? bookEntity.getTitle() : "";
    }
}
