package com.auth0.android.lock.views;

import android.graphics.Rect;
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
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (parent.getChildAdapterPosition(view) == 0) {
            return;
        }

        if (orientation == RecyclerView.HORIZONTAL) {
            outRect.left = space;
        } else {
            outRect.top = space;
        }
    }
}