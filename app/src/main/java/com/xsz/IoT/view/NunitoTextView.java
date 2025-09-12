package com.xsz.IoT.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class NunitoTextView extends androidx.appcompat.widget.AppCompatTextView {

    public NunitoTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public NunitoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NunitoTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/nunito-regular.ttf");
            setTypeface(tf);
        }
    }

}