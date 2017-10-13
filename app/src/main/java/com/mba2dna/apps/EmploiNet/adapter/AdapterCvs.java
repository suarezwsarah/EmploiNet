package com.mba2dna.apps.EmploiNet.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mba2dna.apps.EmploiNet.R;

import com.mba2dna.apps.EmploiNet.model.Candidats;

import com.mba2dna.apps.EmploiNet.utils.CommonUtils;
import com.mba2dna.apps.EmploiNet.utils.Tools;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BIDA on 11/12/2016.
 */

public class AdapterCvs extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private final int VIEW_ADS = 2;

    private Context ctx;


    private List<Candidats> filtered_items = new ArrayList<>();
    private List<Candidats> itemList = new ArrayList<>();


    private LinearLayoutManager mLinearLayoutManager;

    private boolean isMoreLoading = false;
    private int visibleThreshold = 3;
    int firstVisibleItem, visibleItemCount, totalItemCount;
    private int lastPosition = -1;
    private ImageLoader imgloader = ImageLoader.getInstance();

    /* *
  **
  * ON CLICK LISTINER
  * *
   */
    private AdapterCvs.OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(AdapterCvs.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public AdapterCvs.OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, Candidats viewModel);
    }

    /* *
    **
    * ON LOAD LISTENER
    * *
     */
    private AdapterCvs.OnLoadMoreListener onLoadMoreListener;

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public AdapterCvs(Context ctx, List<Candidats> items, AdapterCvs.OnLoadMoreListener onLoadMoreListener) {
        this.ctx = ctx;
        itemList = items;
        filtered_items = items;
        this.onLoadMoreListener = onLoadMoreListener;
        if (!imgloader.isInited()) Tools.initImageLoader(ctx);
    }


    public void setLinearLayoutManager(LinearLayoutManager linearLayoutManager) {
        this.mLinearLayoutManager = linearLayoutManager;
    }

    public void setRecyclerView(RecyclerView mView) {
        mView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                visibleItemCount = recyclerView.getChildCount();
                totalItemCount = mLinearLayoutManager.getItemCount();
                firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
                if (!isMoreLoading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
                    if (getItemCount() > 6) {
                        if (onLoadMoreListener != null) {
                            onLoadMoreListener.onLoadMore();
                        }
                        isMoreLoading = true;
                    }
                }
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        try {
            return (itemList.get(position) != null) ? VIEW_ITEM : VIEW_PROG;
        } catch (java.lang.IndexOutOfBoundsException e) {
            return VIEW_PROG;
        }


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_ITEM) {
            return new AdapterCvs.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_candidat, parent, false));
        } else {
            return new AdapterCvs.ProgressViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_progress, parent, false));
        }

    }

    public void addAll(List<Candidats> lst) {
        itemList.clear();
        itemList.addAll(lst);
        notifyDataSetChanged();
    }

    public void addItemMore(List<Candidats> lst) {
        itemList.addAll(lst);
        notifyItemRangeChanged(0, itemList.size());
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof AdapterCvs.ViewHolder) {

            final Candidats p = itemList.get(position);
            if (p != null) {
                ((ViewHolder) holder).name.setText(p.civilitee + " " + p.nom + " " + p.prenom.substring(0, 1) + "******");
                ((ViewHolder) holder).username.setText(p.cv_title);
                ((ViewHolder) holder).addres.setText(p.adresse);
                ((ViewHolder) holder).exper.setText(p.experience.replace("&agrave;", "à"));
                ((ViewHolder) holder).email.setText(p.email.substring(0, 7) + "******************");
                ((ViewHolder) holder).tele.setText(p.mobile);
                if (p.certified == "1") ((ViewHolder) holder).badge.setVisibility(View.VISIBLE);
                else ((ViewHolder) holder).badge.setVisibility(View.INVISIBLE);
                if (p.photo.length() > 0)
                    imgloader.displayImage(p.photo, ((ViewHolder) holder).userpic, Tools.getGridOption());
                else
                    ((ViewHolder) holder).userpic.setImageDrawable(ctx.getResources().getDrawable(R.drawable.noimage));
                ((AdapterCvs.ViewHolder) holder).lyt_parent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {

                        if (onItemClickListener != null) {

                            onItemClickListener.onItemClick(v, p);
                        }

                    }
                });
            }

        }
    }

    public void setMoreLoading(boolean isMoreLoading) {
        this.isMoreLoading = isMoreLoading;
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void setProgressMore(final boolean isProgress) {
        if (isProgress) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    itemList.add(null);
                    notifyItemInserted(itemList.size() - 1);
                }
            });
        } else {
            itemList.remove(itemList.size() - 1);
            notifyItemRemoved(itemList.size());
        }
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(ctx, R.anim.slide_in_bottom);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each config item is just a string in this case
        public TextView name, username, email, tele, exper, addres;
        public ImageView badge;
        public RoundedImageView userpic;
        public MaterialRippleLayout lyt_parent;

        public ViewHolder(View v) {
            super(v);
            badge = (ImageView) v.findViewById(R.id.badge);
            name = (TextView) v.findViewById(R.id.title);
            CommonUtils.setRobotoBoldFont(ctx, name);

            username = (TextView) v.findViewById(R.id.username);
            CommonUtils.setRobotoThinFont(ctx, username);

            email = (TextView) v.findViewById(R.id.EmailTxt);
            CommonUtils.setRobotoThinFont(ctx, email);
            tele = (TextView) v.findViewById(R.id.TeleTxt);
            CommonUtils.setRobotoThinFont(ctx, tele);
            exper = (TextView) v.findViewById(R.id.experienceTxt);
            CommonUtils.setRobotoThinFont(ctx, exper);

            addres = (TextView) v.findViewById(R.id.timestamp);
            CommonUtils.setRobotoThinFont(ctx, addres);
            userpic = (RoundedImageView) v.findViewById(R.id.image);
            // distance = (TextView) v.findViewById(R.id.distance);
            // lyt_distance = (LinearLayout) v.findViewById(R.id.lyt_distance);
            lyt_parent = (MaterialRippleLayout) v.findViewById(R.id.lyt_parent);
        }
    }

    static class ProgressViewHolder extends RecyclerView.ViewHolder {
        // public ProgressBar pBar;
        public TextView name;

        public ProgressViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.title);
            name.setText("Chargement d\'autres candidats, veuillez patienter …");
            Typeface font = Typeface.createFromAsset(v.getContext().getAssets(), "nexalight.ttf");
            name.setTypeface(font);
            // pBar = (ProgressBar) v.findViewById(R.id.pBar);
        }
    }

}