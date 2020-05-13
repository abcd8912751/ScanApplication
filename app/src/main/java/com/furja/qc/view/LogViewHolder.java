package com.furja.qc.view;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import com.furja.qc.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import q.rorbin.badgeview.QBadgeView;

/**
 * RecyclerView的View装载
 */
public class LogViewHolder extends RecyclerView.ViewHolder{
    @BindView(R.id.marker_button)
    Button markerButton;
    private int markNum;
    private QBadgeView badgeView;
    public LogViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
        badgeView=new QBadgeView(itemView.getContext());
        badgeView.bindTarget(markerButton);
        badgeView.setBadgeTextSize(15,true);
        badgeView.setExactMode(true);
    }

    public Button getMarkerButton() {
        return markerButton;
    }

    public void setMarkerButton(Button markerButton) {
        this.markerButton = markerButton;
    }

    public Context getContext()
    {
        return itemView.getContext();
    }

    public int getMarkNum() {
        return markNum;
    }
    public void setText(String title)
    {
        markerButton.setText(title);
    }
    public void setMarkNum(long num) {
        this.markNum = (int) num;
        badgeView.setBadgeNumber(markNum);
        if(markNum==0)
        {
            markerButton.setGravity(Gravity.CENTER);
        }
        else if(markerButton.getGravity()!=(Gravity.LEFT|Gravity.BOTTOM))
        {
            markerButton.setGravity(Gravity.LEFT|Gravity.BOTTOM);
        }
        int overColor
                =getContext().getResources().getColor(R.color.over_color);
        int middleColor
                =getContext().getResources().getColor(R.color.middleColor);
        int littleColor
                =getContext().getResources().getColor(R.color.littleColor);
        if(markNum<4)
            badgeView.setBadgeBackgroundColor(littleColor);
        else if(markNum<7)
            badgeView.setBadgeBackgroundColor(middleColor);
            else
                badgeView.setBadgeBackgroundColor(overColor);

    }
}
