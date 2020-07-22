package com.axbg.crimson.ui.books.adapter;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.axbg.crimson.R;
import com.axbg.crimson.db.entity.BookEntity;

import java.io.File;
import java.util.List;
import java.util.function.BiConsumer;

public class BookRecyclerViewAdapter extends RecyclerView.Adapter<BookViewHolder> {
    private List<BookEntity> books;
    private FragmentActivity fragmentActivity;
    private BiConsumer<FragmentActivity, BookEntity> onClickConsumer;

    public BookRecyclerViewAdapter(List<BookEntity> books, FragmentActivity fragmentActivity,
                                   BiConsumer<FragmentActivity, BookEntity> onClickConsumer) {
        this.books = books;
        this.fragmentActivity = fragmentActivity;
        this.onClickConsumer = onClickConsumer;
    }

    @NonNull
    @Override
    @SuppressLint("InflateParams")
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_books, null);
        return new BookViewHolder(layoutView, fragmentActivity, onClickConsumer);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {
        BookEntity book = books.get(position);

        holder.setBook(book);

        holder.getTitle().setText(book.getTitle());
        holder.getCover().setImageBitmap(getImage(book.getCoverPath()));
    }

    @Override
    public int getItemCount() {
        return this.books.size();
    }

    private Bitmap getImage(String coverPath) {
        File coverFile = new File(coverPath);
        return BitmapFactory.decodeFile(coverFile.getAbsolutePath());
    }
}
