package com.auth0.android.lock.views;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;

class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private final int orientation;
    private final int space;

    public SpacesItemDecoration(int space, @LinearLayoutCompat.OrientationMode int orientation) {
        this.space = space;
        this.orientation = orientation;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        if (orientation == RecyclerView.HORIZONTAL) {
            outRect.left = space;
            outRect.right = space;
        } else {
            boolean firstItem = parent.getChildAdapterPosition(view) == 0;
            boolean lastItem = parent.getChildAdapterPosition(view) == parent.getChildCount() - 1;
            outRect.top = firstItem ? 0 : space;
            outRect.bottom = lastItem ? 0 : space;
        }
    }
}