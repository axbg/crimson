package com.axbg.crimson.ui.books.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.axbg.crimson.R;
import com.axbg.crimson.db.entity.BookEntity;

import java.io.File;
import java.util.List;

public class BooksAdapter extends BaseAdapter {
    private Context context;
    private List<BookEntity> books;
    private int resource;

    private static class ViewHolder {
        ImageView cover;
        TextView title;
    }

    public BooksAdapter(List<BookEntity> books, int resource, Context context) {
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
        return this.books.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BookEntity book = this.books.get(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.cover = convertView.findViewById(R.id.adapter_books_image);
            viewHolder.title = convertView.findViewById(R.id.adapter_books_title);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (book != null) {
            Bitmap image = getImage(book.getCoverPath());
            viewHolder.cover.setImageBitmap(image);
            viewHolder.title.setText(book.getTitle());
        }

        return convertView;
    }

    private Bitmap getImage(String coverPath) {
        File coverFile = new File(coverPath);
        return BitmapFactory.decodeFile(coverFile.getAbsolutePath());
    }
}