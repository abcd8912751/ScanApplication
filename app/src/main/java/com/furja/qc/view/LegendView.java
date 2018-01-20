package com.furja.qc.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.furja.qc.R;


/**
 * 图例View
 */

public class LegendView extends RelativeLayout {
    private TextView label_text;
    private View legendView;

    public LegendView(Context context)
    {
        this(context, null);
    }
    public LegendView(Context context, AttributeSet attrs)
    {
        this(context, attrs,0);
    }

    public LegendView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        legendView= LayoutInflater.from(context).inflate(R.layout.layout_legend,null);
        label_text=(TextView)legendView.findViewById(R.id.legend_label);
        label_text.setTextColor(Color.BLACK);
        addView(legendView);
    }


    public void setBackgroundColor(int color)
    {
        legendView.setBackgroundColor(color);
    }

    public void setText(String label)
    {
        label_text.setText(label);
    }
}
