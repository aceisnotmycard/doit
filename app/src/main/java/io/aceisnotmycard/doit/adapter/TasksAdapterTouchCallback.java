package io.aceisnotmycard.doit.adapter;

import android.graphics.Canvas;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

public class TasksAdapterTouchCallback extends ItemTouchHelper.Callback {

    private DraggableAdapter draggableAdapter;

    public interface DraggableAdapter {
        void onItemSwiped(RecyclerView.ViewHolder viewHolder);
        boolean onItemMoved(int fromPosition, int toPosition);
    }

    public TasksAdapterTouchCallback(DraggableAdapter draggableAdapter) {
        this.draggableAdapter = draggableAdapter;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return draggableAdapter.onItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
    }

    @Override
    public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        // todo get elevation values from resources
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG && isCurrentlyActive) {
            viewHolder.itemView.setElevation(10);
        } else {
            viewHolder.itemView.setElevation(2);
        }
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        draggableAdapter.onItemSwiped(viewHolder);
    }
}
