package com.tools.pixart.effect.anim;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class SlideUpAnimation extends Animation {
    private final View view;
    private final int height;

    public SlideUpAnimation(View view) {
        this.view = view;
        this.height = view.getHeight();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        view.getLayoutParams().height = height - (int) (height * interpolatedTime);
        view.requestLayout();
        view.setVisibility(interpolatedTime == 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}
