package com.mba2dna.apps.EmploiNet.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;

import com.mba2dna.apps.EmploiNet.R;
import com.mba2dna.apps.EmploiNet.model.Category;
import com.mba2dna.apps.EmploiNet.model.Header;
import com.mba2dna.apps.EmploiNet.model.Recruteur;
import com.mba2dna.apps.EmploiNet.utils.Tools;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * Created by BIDA on 11/5/2016.
 */

public class RecruteurAdapter extends RecyclerView.Adapter<RecruteurAdapter.MyViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    Header header;
    private Context mContext;
    private List<Recruteur> Recruteurlist;
    private OnItemClickListener onItemClickListener;
    private ImageLoader imgloader = ImageLoader.getInstance();


    private int lastPosition = -1;
    private boolean clicked = false;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count;
        public ImageView thumbnail, overflow;
        public MaterialRippleLayout lyt_parent;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            Typeface font = Typeface.createFromAsset(view.getContext().getAssets(), "nexalight.ttf");
            title.setTypeface(font);
            count = (TextView) view.findViewById(R.id.count);
            Typeface font1 = Typeface.createFromAsset(view.getContext().getAssets(), "nexabold.ttf");
            count.setTypeface(font1);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            lyt_parent = (MaterialRippleLayout) view.findViewById(R.id.lyt_parent);
            // overflow = (ImageView) view.findViewById(R.id.overflow);
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    public RecruteurAdapter(Context mContext, List<Recruteur> Recruteurlist) {
        this.mContext = mContext;
        this.Recruteurlist = Recruteurlist;
        if (!imgloader.isInited()) Tools.initImageLoader(mContext);
    }

    public RecruteurAdapter(Header header, List<Recruteur> listItems) {
        this.header = header;
        this.Recruteurlist = listItems;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recruteur, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Recruteur recruteur = Recruteurlist.get(position);
        holder.title.setText(recruteur.recruteur);
        holder.count.setText(recruteur.num + " " + mContext.getString(R.string.article));
        holder.thumbnail.setVisibility(View.VISIBLE);

        if (recruteur.photo != null) {
            imgloader.displayImage(recruteur.photo, holder.thumbnail, Tools.getGridOption());
            Log.e("Photo", recruteur.photo);
        }
        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (!clicked && onItemClickListener != null) {
                    clicked = true;
                    onItemClickListener.onItemClick(v, recruteur);
                }
            }
        });
        clicked = false;
   /*     holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow);
            }
        });*/
    }


    @Override
    public int getItemCount() {
        return Recruteurlist.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, Recruteur viewModel);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_bottom);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }
}

