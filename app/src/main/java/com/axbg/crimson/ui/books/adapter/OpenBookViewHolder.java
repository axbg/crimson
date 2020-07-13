package com.axbg.crimson.ui.books.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.axbg.crimson.R;
import com.axbg.crimson.network.object.OpenLibraryBook;

import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OpenBookViewHolder extends RecyclerView.ViewHolder {
    private ImageView cover;
    private TextView title;

    private OpenLibraryBook openLibraryBook;
    private NavController nav;

    public OpenBookViewHolder(@NonNull View itemView) {
        super(itemView);

        cover = itemView.findViewById(R.id.adapter_books_image);
        title = itemView.findViewById(R.id.adapter_books_title);

        itemView.setOnClickListener(v -> {
            Objects.requireNonNull(nav.getPreviousBackStackEntry())
                    .getSavedStateHandle()
                    .set("OPEN_BOOK", openLibraryBook);
            nav.popBackStack();
        });
    }
}
