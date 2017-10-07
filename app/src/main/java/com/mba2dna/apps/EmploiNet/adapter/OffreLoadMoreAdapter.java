package com.mba2dna.apps.EmploiNet.adapter;

import android.content.Context;
import android.graphics.Typeface;
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
import com.google.android.gms.ads.NativeExpressAdView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mba2dna.apps.EmploiNet.R;
import com.mba2dna.apps.EmploiNet.data.Constant;
import com.mba2dna.apps.EmploiNet.fragment.OffresFragment;
import com.mba2dna.apps.EmploiNet.fragment.OffresLoadMoreFragment;
import com.mba2dna.apps.EmploiNet.model.Offre;
import com.mba2dna.apps.EmploiNet.utils.CommonUtils;
import com.mba2dna.apps.EmploiNet.utils.Tools;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BIDA Mohamed Amine on 05,October,2017
 * MBA2DNA Network,
 * Boumerdes, Algeria.
 */

public class OffreLoadMoreAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;
    private final int VIEW_ADS = 2;
    private boolean loading;

    private Context ctx;
    private List<Offre> items = new ArrayList<>();
    private ImageLoader imgloader = ImageLoader.getInstance();

    private OnLoadMoreListener onLoadMoreListener;
    private OnItemClickListener onItemClickListener;

    private int lastPosition = -1;
    private boolean clicked = false;


    public interface OnItemClickListener {
        void onItemClick(View view, Offre viewModel);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView title;
        public ImageView image;
        public MaterialRippleLayout lyt_parent;

        public ViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.title);
            image = (ImageView) v.findViewById(R.id.image);

            lyt_parent = (MaterialRippleLayout) v.findViewById(R.id.lyt_parent);
        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public TextView name;

        public ProgressViewHolder(View v) {
            super(v);
            name = (TextView) v.findViewById(R.id.title);
            Typeface font = Typeface.createFromAsset(v.getContext().getAssets(), "nexalight.ttf");
            name.setTypeface(font);
        }
    }

    public class NativeExpressAdViewHolder extends RecyclerView.ViewHolder {

        NativeExpressAdViewHolder(View view) {
            super(view);
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

    public OffreLoadMoreAdapter(Context ctx, RecyclerView view, List<Offre> items) {
        this.ctx = ctx;
        this.items = items;
        if (!imgloader.isInited()) Tools.initImageLoader(ctx);
        lastItemViewDetector(view);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_ITEM:
                return new OffreLoadMoreAdapter.OffreViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_offre, parent, false));
            case VIEW_PROG:
                return new OffreLoadMoreAdapter.ProgressViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_progress, parent, false));
            case VIEW_ADS:
                // fall through
            default:

                return new OffreLoadMoreAdapter.NativeExpressAdViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_native_ads_offre, parent, false));
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        switch (viewType) {
            case VIEW_ITEM:
                OffreLoadMoreAdapter.OffreViewHolder menuItemHolder = (OffreLoadMoreAdapter.OffreViewHolder) holder;
                final Offre offre = (Offre) items.get(position);

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
                                clicked = true;
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
             /*   AdapterOffres.NativeExpressAdViewHolder nativeExpressHolder =
                        (AdapterOffres.NativeExpressAdViewHolder) holder;
                NativeExpressAdView adView =
                        (NativeExpressAdView) items.get(position);
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
                adCardView.addView(adView);*/
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        try {
            Log.e("Position", position + "");
            if (position == 0) return VIEW_ITEM;
            if (items.get(position) == null) return VIEW_PROG;
            return (position % OffresLoadMoreFragment.ITEMS_PER_AD == 0) ? VIEW_ADS : VIEW_ITEM;

        } catch (java.lang.IndexOutOfBoundsException e) {
            return VIEW_PROG;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // Here is the key method to apply the animation
    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(ctx, R.anim.slide_in_bottom);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    public void insertData(List<Offre> items) {
        setLoaded();
        int positionStart = getItemCount();
        int itemCount = items.size();
        this.items.addAll(items);
        notifyItemRangeInserted(positionStart, itemCount);
    }

    public void setLoaded() {
        loading = false;
        for (int i = 0; i < getItemCount(); i++) {
            if (items.get(i) == null) {
                items.remove(i);
                notifyItemRemoved(i);
            }
        }
    }

    public void setLoading() {
        if (getItemCount() != 0) {
            this.items.add(null);
            notifyItemInserted(getItemCount() - 1);
        }
    }

    public void resetListData() {
        this.items = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    private void lastItemViewDetector(RecyclerView recyclerView) {
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int lastPos = layoutManager.findLastVisibleItemPosition();
                    //Log.e("SCROLLED", lastPos + " == " + (getItemCount() - 1));
                    if (!loading && lastPos == getItemCount() - 1 && onLoadMoreListener != null) {
                        int current_page = getItemCount() / Constant.LIMIT_LOADMORE;
                        onLoadMoreListener.onLoadMore(current_page);
                        loading = true;
                    }
                }
            });
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore(int current_page);
    }

}