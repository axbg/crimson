package com.axbg.crimson.ui.books;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.axbg.crimson.R;
import com.axbg.crimson.network.NetworkUtil;
import com.axbg.crimson.network.object.OpenLibraryBook;
import com.squareup.picasso.Picasso;

import java.util.List;

public class OpenLibraryBooksAdapter extends BaseAdapter {
    private Context context;
    private List<OpenLibraryBook> books;
    private int resource;

    OpenLibraryBooksAdapter(List<OpenLibraryBook> books, int resource, Context context) {
        this.context = context;
        this.resource = resource;
        this.books = books;
    }

    @Override
    public int getCount() {
        return books.size();
    }

    @Override
    public Object getItem(int position) {
        return this.books.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        OpenLibraryBook book = this.books.get(position);
        OpenLibraryBooksAdapter.ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);

            viewHolder = new OpenLibraryBooksAdapter.ViewHolder();
            viewHolder.cover = convertView.findViewById(R.id.bookList_image);
            viewHolder.title = convertView.findViewById(R.id.bookList_book);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (OpenLibraryBooksAdapter.ViewHolder) convertView.getTag();
        }

        if (book != null) {
            Picasso.get().load(NetworkUtil.buildCoverUrl(book.getEditionKey())).into(viewHolder.cover);
            viewHolder.cover.setAdjustViewBounds(true);
            viewHolder.title.setText(book.getTitle());
        }

        return convertView;
    }

    private static class ViewHolder {
        ImageView cover;
        TextView title;
    }
}
