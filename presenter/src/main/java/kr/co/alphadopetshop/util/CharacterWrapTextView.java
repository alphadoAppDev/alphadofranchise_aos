package kr.co.alphadopetshop.util;

import android.content.Context;
import android.util.AttributeSet;

public class CharacterWrapTextView extends androidx.appcompat.widget.AppCompatTextView {
    public CharacterWrapTextView(Context context) {
        super(context);
    }

    public CharacterWrapTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CharacterWrapTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override public void setText(CharSequence text, BufferType type) {
        super.setText(text.toString().replace(" ", "\u00A0"), type);
    }
}