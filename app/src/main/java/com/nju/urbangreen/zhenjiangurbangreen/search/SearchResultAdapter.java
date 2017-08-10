package com.nju.urbangreen.zhenjiangurbangreen.search;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.nju.urbangreen.zhenjiangurbangreen.R;
import com.nju.urbangreen.zhenjiangurbangreen.basisClass.GreenObject;

import java.util.List;

/**
 * Created by lxs on 2016/9/30.
 */
public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultHolder>{
    private List<GreenObject> UGList;

    public SearchResultAdapter(List<GreenObject> list)
    {
        this.UGList=list;
    }

    @Override
    public SearchResultHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=View.inflate(parent.getContext(), R.layout.recycleritem_search_result,null);
        SearchResultHolder holder=new SearchResultHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(SearchResultHolder holder, int position) {
        holder.setID(UGList.get(position).UGO_ID);
        holder.setName(UGList.get(position).UGO_Name);
        holder.setAddress(UGList.get(position).UGO_Address);
        holder.setClassType_ID(UGList.get(position).UGO_ClassType_ID);
    }

    @Override
    public int getItemCount() {
        return UGList.size();
    }
}
