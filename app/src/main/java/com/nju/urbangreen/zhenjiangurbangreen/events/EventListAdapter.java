package com.nju.urbangreen.zhenjiangurbangreen.events;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nju.urbangreen.zhenjiangurbangreen.R;
import com.nju.urbangreen.zhenjiangurbangreen.basisClass.BaseItemViewHolder;

import java.util.List;

/**
 * Created by tommy on 2017/8/17.
 */

public class EventListAdapter extends RecyclerView.Adapter<BaseItemViewHolder>{
    private List<OneEvent> eventList;
//    private BaseItemViewHolder viewHolder;

    public EventListAdapter(List<OneEvent> eventList) {
        this.eventList = eventList;
    }

    @Override
    public BaseItemViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_list_item, parent, false);
        final BaseItemViewHolder viewHolder = new BaseItemViewHolder(view);
        viewHolder.itemTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resolveClick(viewHolder,view);
            }
        });
        viewHolder.itemContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resolveClick(viewHolder,view);
            }
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BaseItemViewHolder viewHolder, int i) {
        OneEvent oneEvent = eventList.get(i);
        viewHolder.itemTitle.setText(oneEvent.getUGE_Code() + "/" + oneEvent.getUGE_Type());
        viewHolder.itemContent.setText(oneEvent.getUGE_Description());

    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    private void resolveClick(BaseItemViewHolder viewHolder, View view) {
        Intent intent;
        int position = viewHolder.getAdapterPosition();
//        Toast.makeText(view.getContext(), position + "", Toast.LENGTH_SHORT).show();
        OneEvent oneEvent = eventList.get(position);
        if(!oneEvent.isUGE_EventOrActivity()){
             intent = new Intent(view.getContext(), EventRegisterActivity.class);
        }else {
            intent = new Intent(view.getContext(), ActivityRegisterActivity.class);
        }

        intent.putExtra("event_object", oneEvent);
        view.getContext().startActivity(intent);
    }
}
