package com.example.brian.vetlocator.historyRecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.brian.vetlocator.HistorySingleActivity;
import com.example.brian.vetlocator.R;

public class HistoryViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView treatmentId;
    public TextView time;
    public HistoryViewHolders(View itemView) {
        super(itemView);

        itemView.setOnClickListener(this);

        treatmentId = (TextView) itemView.findViewById(R.id.treatmentId);
        time = (TextView) itemView.findViewById(R.id.time);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(view.getContext(), HistorySingleActivity.class);
        Bundle b = new Bundle();
        b.putString("treatmentId", treatmentId.getText().toString());
        intent.putExtras(b);
        view.getContext().startActivity(intent);


    }
}
