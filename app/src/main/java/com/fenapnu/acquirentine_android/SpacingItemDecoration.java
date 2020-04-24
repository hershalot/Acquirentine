package com.fenapnu.acquirentine_android;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by fenapnu on 2/6/18.
 */


public class SpacingItemDecoration extends RecyclerView.ItemDecoration {

    private final int verticalSpaceHeight;
    private final int sidePadding;

    public SpacingItemDecoration(int verticalSpaceHeight, int sidePadding) {
        this.verticalSpaceHeight = verticalSpaceHeight;
        this.sidePadding = sidePadding;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull  View view, @NonNull  RecyclerView parent,
                               @NonNull RecyclerView.State state) {
        outRect.bottom = verticalSpaceHeight;
        outRect.right = sidePadding;
        outRect.left = sidePadding;
    }
}

