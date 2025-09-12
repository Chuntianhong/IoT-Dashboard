package com.xsz.IoT.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class NunitoBoldTextView extends androidx.appcompat.widget.AppCompatTextView {

    public NunitoBoldTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public NunitoBoldTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NunitoBoldTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/nunito-bold.ttf");
            setTypeface(tf);
        }
    }
}