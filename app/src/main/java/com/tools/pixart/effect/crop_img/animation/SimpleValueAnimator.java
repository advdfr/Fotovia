package com.tools.pixart.effect.crop_img.animation;

@SuppressWarnings("unused") public interface SimpleValueAnimator {
  void startAnimation(long duration);

  void cancelAnimation();

  boolean isAnimationStarted();

  void addAnimatorListener(SimpleValueAnimatorListenerElegantPhoto animatorListener);
}
