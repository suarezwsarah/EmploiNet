package com.mba2dna.apps.EmploiNet.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
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
import com.makeramen.roundedimageview.RoundedImageView;
import com.mba2dna.apps.EmploiNet.R;
import com.mba2dna.apps.EmploiNet.activities.ActivityMain;
import com.mba2dna.apps.EmploiNet.fragment.OffresFragment;
import com.mba2dna.apps.EmploiNet.model.Offre;
import com.mba2dna.apps.EmploiNet.utils.CommonUtils;
import com.mba2dna.apps.EmploiNet.utils.Tools;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.google.android.gms.ads.NativeExpressAdView;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by BIDA on 11/11/2016.
 */

public class AdapterOffres extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private final int VIEW_ADS = 2;

    private Context ctx;


    private List<Offre> filtered_items = new ArrayList<>();
    private List<Object> itemList = new ArrayList<>();


    private LinearLayoutManager mLinearLayoutManager;

    private boolean isMoreLoading = false;
    private int visibleThreshold = 1;
    int firstVisibleItem, visibleItemCount, totalItemCount;
    private int lastPosition = -1;
    private ImageLoader imgloader = ImageLoader.getInstance();

    /* *
  **
  * ON CLICK LISTINER
  * *
   */
    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, Offre viewModel);
    }

    /* *
    **
    * ON LOAD LISTENER
    * *
     */
    private OnLoadMoreListener onLoadMoreListener;

    public interface OnLoadMoreListener {
        void onLoadMore();
    }

    public AdapterOffres(Context ctx, List<Object> items, OnLoadMoreListener onLoadMoreListener) {
        this.ctx = ctx;
        itemList = items;
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
            Log.e("Position", position + "");
            if(itemList.get(position) == null) return VIEW_PROG;
            return (position % OffresFragment.ITEMS_PER_AD == 0) ? VIEW_ADS: VIEW_ITEM;

        } catch (java.lang.IndexOutOfBoundsException e) {
            return VIEW_PROG;
        }


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       /* if (viewType == VIEW_ITEM) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_offre, parent, false));
        } else {
            return new ProgressViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_progress, parent, false));
        }*/
        switch (viewType) {
            case VIEW_ITEM:
                return new OffreViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_offre, parent, false));
            case VIEW_PROG:
                return new ProgressViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_progress, parent, false));
            case VIEW_ADS:
                // fall through
            default:

                return new NativeExpressAdViewHolder(LayoutInflater.from( parent.getContext()).inflate(R.layout.item_native_ads_offre,parent, false));
        }

    }

    public void addAll(List<Object> lst) {
        itemList.clear();
        itemList.addAll(lst);
        notifyDataSetChanged();
    }

    public void addItemMore(List<Object> lst) {
        itemList.addAll(lst);
        notifyItemRangeChanged(0, itemList.size());
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case VIEW_ITEM:
                OffreViewHolder menuItemHolder = (OffreViewHolder) holder;
                final Offre offre = (Offre) itemList.get(position);

                if (offre != null) {
                    menuItemHolder.name.setText(offre.title);
                    menuItemHolder.username.setText(offre.contact_info);
                    menuItemHolder.timestamp.setText(offre.pub_date);
                    if (offre.photo != null) {
                        if (!offre.photo.contains("s_d3802b1dc0d80d8a3c8ccc6ccc068e7c.jpg")) {
                            try {
                                imgloader.displayImage(offre.photo, menuItemHolder.image, Tools.getGridOption());
                            } catch (Exception e) {
                                imgloader.displayImage("drawable://noimage", menuItemHolder.image, Tools.getGridOption());
                            }
                        }
                    }

                    menuItemHolder.lyt_parent.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View v) {

                            if (onItemClickListener != null) {

                                onItemClickListener.onItemClick(v, offre);
                            }

                        }
                    });
                }
                break;
            case VIEW_PROG:

                break;
            case VIEW_ADS:
                // fall through
            default:
                NativeExpressAdViewHolder nativeExpressHolder =
                        (NativeExpressAdViewHolder) holder;
                NativeExpressAdView adView =
                        (NativeExpressAdView) itemList.get(position);
                ViewGroup adCardView = (ViewGroup) nativeExpressHolder.itemView;
                // The NativeExpressAdViewHolder recycled by the RecyclerView may be a different
                // instance than the one used previously for this position. Clear the
                // NativeExpressAdViewHolder of any subviews in case it has a different
                // AdView associated with it, and make sure the AdView for this position doesn't
                // already have a parent of a different recycled NativeExpressAdViewHolder.
                if (adCardView.getChildCount() > 0) {
                    adCardView.removeAllViews();
                }
                if (adView.getParent() != null) {
                    ((ViewGroup) adView.getParent()).removeView(adView);
                }

                // Add the Native Express ad to the native express ad view.
                adCardView.addView(adView);
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


    public class OffreViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView name, username, timestamp;
        public ImageView image;
        public RoundedImageView userpic;
        public MaterialRippleLayout lyt_parent;

        public OffreViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.title);
            CommonUtils.setRobotoBoldFont(ctx, name);

            username = (TextView) v.findViewById(R.id.username);
            CommonUtils.setRobotoThinFont(ctx, username);

            timestamp = (TextView) v.findViewById(R.id.timestamp);
            CommonUtils.setRobotoThinFont(ctx, timestamp);
            image = (ImageView) v.findViewById(R.id.image);
            userpic = (RoundedImageView) v.findViewById(R.id.profilePic);
            // distance = (TextView) v.findViewById(R.id.distance);
            // lyt_distance = (LinearLayout) v.findViewById(R.id.lyt_distance);
            lyt_parent = (MaterialRippleLayout) v.findViewById(R.id.lyt_parent);
        }
    }
    /**
     * The {@link NativeExpressAdViewHolder} class.
     */
    public class NativeExpressAdViewHolder extends RecyclerView.ViewHolder {

        NativeExpressAdViewHolder(View view) {
            super(view);
        }
    }
    static class ProgressViewHolder extends RecyclerView.ViewHolder {
        // public ProgressBar pBar;
        public TextView name;

        public ProgressViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.title);
            Typeface font = Typeface.createFromAsset(v.getContext().getAssets(), "nexalight.ttf");
            name.setTypeface(font);
            // pBar = (ProgressBar) v.findViewById(R.id.pBar);
        }
    }

}