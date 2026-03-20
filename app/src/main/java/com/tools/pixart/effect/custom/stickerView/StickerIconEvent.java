package com.tools.pixart.effect.custom.stickerView;

import android.view.MotionEvent;

public interface StickerIconEvent {
  void onActionDown(StickerView stickerView, MotionEvent event);

  void onActionMove(StickerView stickerView, MotionEvent event);

  void onActionUp(StickerView stickerView, MotionEvent event);
}
