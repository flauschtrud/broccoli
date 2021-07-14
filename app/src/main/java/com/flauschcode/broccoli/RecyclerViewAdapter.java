package com.flauschcode.broccoli;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public abstract class RecyclerViewAdapter<T> extends ListAdapter<T, RecyclerViewAdapter<T>.Holder> {

    protected RecyclerViewAdapter() {
        super(new DiffCallback<>());
        registerAdapterDataObserver(new ItemCountObserver());
    }

    @NonNull
    @Override
    public RecyclerViewAdapter<T>.Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ViewDataBinding itemBinding = DataBindingUtil.inflate(layoutInflater, getLayoutResourceId(), parent, false);
        return new RecyclerViewAdapter<T>.Holder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.Holder holder, int position) {
        T currentItem = getItem(position);
        holder.bind(currentItem);
        holder.itemView.setOnClickListener(v -> onItemClick(currentItem));
    }

    protected abstract int getLayoutResourceId();
    protected abstract int getBindingVariableId();
    protected abstract void onItemClick(T item);
    protected abstract void onAdapterDataChanged(int itemCount);

    public class Holder extends RecyclerView.ViewHolder {

        private final ViewDataBinding binding;

        Holder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(T obj) {
            binding.setVariable(getBindingVariableId(), obj);
            binding.executePendingBindings();
        }
    }

    public static class DiffCallback<T> extends DiffUtil.ItemCallback<T> {

        @Override
        public boolean areItemsTheSame(@NonNull T oldItem, @NonNull T newItem) {
            return oldItem.hashCode() == newItem.hashCode();
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull T oldItem, @NonNull T newItem) {
            return oldItem.equals(newItem);
        }

    }

    private class ItemCountObserver extends RecyclerView.AdapterDataObserver {

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            notifyAdapterDataChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
            super.onItemRangeChanged(positionStart, itemCount, payload);
            notifyAdapterDataChanged();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            notifyAdapterDataChanged();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            notifyAdapterDataChanged();
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            notifyAdapterDataChanged();
        }

        private void notifyAdapterDataChanged() {
            RecyclerViewAdapter.this.onAdapterDataChanged(getItemCount());
        }

    }
}
