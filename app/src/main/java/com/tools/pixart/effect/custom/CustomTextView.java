
package com.tools.pixart.effect.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.tools.pixart.R;
import com.tools.pixart.effect.support.SupportedClass;



public class CustomTextView extends androidx.appcompat.widget.AppCompatTextView {

	public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(attrs);
	}

	public CustomTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);

	}

	public CustomTextView(Context context) {
		super(context);
		init(null);
	}

	private void init(AttributeSet attrs) {
		if (attrs != null) {
			TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CustomTextView);
			String fontName = a.getString(R.styleable.CustomTextView_fontName);
			if (fontName != null) {
				Typeface myTypeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/" + fontName);
				setTypeface(myTypeface);
			}else {
				Typeface regular = SupportedClass.getTypeFace(getContext(), 1);
				setTypeface(regular);
			}
			a.recycle();
		}
	}

}
