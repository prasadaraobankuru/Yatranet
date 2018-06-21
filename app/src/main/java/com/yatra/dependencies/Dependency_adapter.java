package com.yatra.dependencies;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Dependency_adapter extends BaseAdapter  {

    public  ArrayList<dependencies> cateLists;
    private LayoutInflater lwInflater;
    Context aContext;


    public static List<String> BlogsRecentViewList = new ArrayList<String>();
    public static List<String> BlogsViewList = new ArrayList<String>();
    public static Set<String> Blogshs = new HashSet<>();
    public Dependency_adapter(Context context, ArrayList<dependencies> playlist){
        lwInflater = LayoutInflater.from(context);
        cateLists = playlist;
        aContext = context;

    }

    public int getCount() {
        // TODO Auto-generated method stub
        return cateLists.size();
    }

    public dependencies getItem(int position) {
        // TODO Auto-generated method stub
        return cateLists.get(position);
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return cateLists.get(position).getSno();
    }

    @Override
    public int getViewTypeCount() {
        return cateLists.size();
    }
    @Override
    public int getItemViewType(int pos){
        return pos;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final ModHolder holder;
        View rowView = convertView;
        if(rowView == null)
        {
            rowView = lwInflater.inflate(R.layout.dependency_list_item, null);
            holder = new ModHolder();

            holder.title = (TextView) rowView.findViewById(R.id.devicename);
            holder.card_view=(CardView)rowView.findViewById(R.id.cardView);
            holder.dev_image = (ImageView) rowView.findViewById(R.id.dev_image);
            holder.dep_download = (ImageView) rowView.findViewById(R.id.dep_download);
            holder.id = (TextView) rowView.findViewById(R.id.id);
            holder.type= (TextView) rowView.findViewById(R.id.type);
            holder.size=(TextView) rowView.findViewById(R.id.size);
            holder.rl_layout= (RelativeLayout) rowView.findViewById(R.id.rl_layout);

            rowView.setTag(holder);

        }
        else
        {
            holder =  (ModHolder) rowView.getTag();
        }





        try {
                  holder.title.setText(cateLists.get(position).getName());
                  holder.id.setText("ID :"+cateLists.get(position).getId());
                  holder.size.setText(cateLists.get(position).getSizeInBytes()+" "+"Bytes");
                  holder.type.setText(cateLists.get(position).getType());
                // animate(holder);

                if (cateLists.get(position).getType().equalsIgnoreCase("IMAGE") ) {

                    Picasso.with(holder.dev_image.getContext())
                            .load(cateLists.get(position).getCdn_path())
                            .placeholder(R.drawable.no_image) // can also be a drawable
                            // will be displayed if the image cannot be loaded
                            .into(holder.dev_image);

                   // holder.dep_download.setVisibility(View.INVISIBLE);

                }

            holder.card_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(cateLists.get(position).getType().equalsIgnoreCase("IMAGE") ){

                        Intent i = new Intent(aContext, ImagePreviewActivity.class);
                        i.putExtra("imgName", cateLists.get(position).getCdn_path());

                        aContext.startActivity(i);
                    }
                    else {

                        Intent i = new Intent(aContext, Dependency_VideoView.class);

                        i.putExtra("name", cateLists.get(position).getName());
                        i.putExtra("path", cateLists.get(position).getCdn_path());

                        aContext.startActivity(i);
                    }

                }
            });

            holder.dep_download.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent Intent = new Intent(aContext, Download_Dependency_File.class);
                    Intent.putExtra("URL", cateLists.get(position).getCdn_path());
                    Intent.putExtra("Name", cateLists.get(position).getName());
                    Intent.putExtra("type", cateLists.get(position).getType());

                    aContext.startActivity(Intent);

                }

            });


        }

        catch (Exception e){

        }





        return rowView;
    }

    public void reset(){
        cateLists.clear();
        notifyDataSetChanged();
    }

    public void addData(dependencies dataModel){

        cateLists.add(dataModel);
        notifyDataSetChanged();
    }


    public static class ModHolder{
        ImageView dev_image,dep_download;
        TextView title,id,type,size;
        CardView card_view;
        RelativeLayout rl_layout;

    }



}
