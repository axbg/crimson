package com.axbg.crimson.ui.books.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.axbg.crimson.R;
import com.axbg.crimson.db.entity.BookEntity;

import java.util.function.BiConsumer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookViewHolder extends RecyclerView.ViewHolder {
    private ImageView cover;
    private TextView title;
    private BookEntity book;

    public BookViewHolder(@NonNull View itemView, FragmentActivity fragmentActivity,
                          BiConsumer<FragmentActivity, BookEntity> onItemClickConsumer) {
        super(itemView);

        cover = itemView.findViewById(R.id.adapter_books_image);
        title = itemView.findViewById(R.id.adapter_books_title);

        itemView.setOnClickListener(v -> onItemClickConsumer.accept(fragmentActivity, book));
    }
}
