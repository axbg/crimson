package com.axbg.crimson.ui.books.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.axbg.crimson.R;
import com.axbg.crimson.network.NetworkUtil;
import com.axbg.crimson.network.object.OpenLibraryBook;
import com.squareup.picasso.Picasso;

import java.util.List;

public class OpenBookRecyclerViewAdapter extends RecyclerView.Adapter<OpenBookViewHolder> {
    private List<OpenLibraryBook> books;
    private NavController navController;

    public OpenBookRecyclerViewAdapter(List<OpenLibraryBook> books, NavController nav) {
        this.books = books;
        this.navController = nav;
    }

    @NonNull
    @Override
    @SuppressLint("InflateParams")
    public OpenBookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_books, null);
        return new OpenBookViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull OpenBookViewHolder holder, int position) {
        OpenLibraryBook book = books.get(position);

        holder.setNav(navController);
        holder.setOpenLibraryBook(books.get(position));

        holder.getTitle().setText(book.getTitle());
        Picasso.get().load(NetworkUtil.buildCoverUrl(book.getEditionKey())).into(holder.getCover());
    }

    @Override
    public int getItemCount() {
        return this.books.size();
    }
}
