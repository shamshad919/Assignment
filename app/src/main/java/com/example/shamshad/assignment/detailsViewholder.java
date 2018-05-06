package com.example.shamshad.assignment;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by shamshad on 5/6/18.
 */

public class detailsViewholder extends RecyclerView.ViewHolder {
    TextView address;
    public detailsViewholder(View itemView) {
        super(itemView);
        address=itemView.findViewById(R.id.address_text);
    }
}
