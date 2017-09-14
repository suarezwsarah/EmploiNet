package com.mba2dna.apps.EmploiNet.adapter;

/**
 * Created by UserSession on 10/11/2016.
 */
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mba2dna.apps.EmploiNet.model.Offre;
import com.mba2dna.apps.EmploiNet.utils.Tools;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import com.mba2dna.apps.EmploiNet.R;
import com.mba2dna.apps.EmploiNet.data.Constant;

public class AdapterSuggestion extends RecyclerView.Adapter<AdapterSuggestion.ViewHolder> {

    private List<Offre> items = new ArrayList<>();
    private ImageLoader imgloader = ImageLoader.getInstance();
    private OnItemClickListener onItemClickListener;
    private boolean clicked = false;

    private int lastPosition = -1;

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView image;
        public RoundedImageView userpic;
        public MaterialRippleLayout lyt_parent;
        public TextView name,category,username;


        public ViewHolder(View v) {
            super(v);
            username = (TextView) v.findViewById(R.id.username);
            category = (TextView) v.findViewById(R.id.category);
            name = (TextView) v.findViewById(R.id.title);
            Typeface font = Typeface.createFromAsset(v.getContext().getAssets(), "nexalight.ttf");
            name.setTypeface(font);
            category.setTypeface(font);
            username.setTypeface(font);
           // image = (ImageView) v.findViewById(R.id.image);
           // userpic = (RoundedImageView) v.findViewById(R.id.profilePic);
            lyt_parent = (MaterialRippleLayout) v.findViewById(R.id.lyt_parent);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AdapterSuggestion(List<Offre> items) {
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_suggestion, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // ReString the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Offre p = items.get(position);
        holder.name.setText(p.title);
        holder.category.setText(p.type_activite);
        holder.username.setText(p.contact_info);
      //  imgloader.displayImage(Constant.getURLimgPlace(p.photo), holder.image, Tools.getGridOption());
     //   imgloader.displayImage(Constant.getURLimgUser(p.email_candidature), holder.userpic, Tools.getGridOption());
        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
               /* if (!clicked && onItemClickListener != null) {
                    clicked = true;

                }*/
                onItemClickListener.onItemClick(v, p);
            }
        });
        clicked = false;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }


    public interface OnItemClickListener {
        void onItemClick(View view, Offre viewModel);
    }
}
