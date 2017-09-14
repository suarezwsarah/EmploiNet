package com.mba2dna.apps.EmploiNet.adapter;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.mba2dna.apps.EmploiNet.R;
import com.mba2dna.apps.EmploiNet.data.Constant;
import com.mba2dna.apps.EmploiNet.model.Offre;
import com.mba2dna.apps.EmploiNet.utils.CommonUtils;
import com.mba2dna.apps.EmploiNet.utils.Tools;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BIDA on 11/12/2016.
 */

public class AdapterPopulair extends RecyclerView.Adapter<AdapterPopulair.PopulairViewHolder> {
    private List<Offre> items = new ArrayList<>();
    private Context context;
    private OnItemClickListener itemClickListener;
    private ImageLoader imgloader = ImageLoader.getInstance();

    public AdapterPopulair(Context context, List<Offre> List) {
        this.context = context;
        this.items = List;
        if (!imgloader.isInited()) Tools.initImageLoader(context);
    }

    /* public boolean add(Offre item) {
        boolean isAdded = items.add(item);
        if (isAdded) {
            notifyItemInserted(items.size() - 1);
        }
        return isAdded;
    }

   public boolean addAll(List<Offre> items) {
        int start = this.items.size();
        boolean isAdded = this.items.addAll(items);
        if (isAdded) {
            notifyItemRangeInserted(start, items.size());
        }
        return isAdded;
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }*/

    @Override
    public PopulairViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_populair, parent, false);
        return new PopulairViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PopulairViewHolder holder, int position) {
        final Offre item = items.get(position);
        holder.tvSportTitle.setText(item.title);
        CommonUtils.setRobotoThinFont(context, holder.tvSportTitle);
        holder.tvSportSubtitle.setText(item.type_activite);
        CommonUtils.setRobotoThinFont(context, holder.tvSportSubtitle);
        holder.rating_number.setText((items.indexOf(item) + 1) + "");
        CommonUtils.setRobotoThinFont(context, holder.rating_number);


        CommonUtils.setRobotoThinFont(context, holder.rating_text);
        holder.views_number.setText(item.views + "");
        //Log.e("Views",item.views+"");
        CommonUtils.setRobotoThinFont(context, holder.views_number);
        // holder.tvSportRound.setText(item.title);
        // CommonUtils.setRobotoThinFont(context, holder.tvSportRound);
        imgloader.displayImage(Constant.getURLimgPlace(item.photo), holder.ivSportPreview, Tools.getGridOption());
       /* holder.difficultyicon.setText(item.deficulty);
        holder.fra_single_recipe_prep_time.setText(item.prepair);
        holder.fra_single_recipe_portions.setText(item.portion);
        holder.fra_single_recipe_cook_time.setText(item.cocking);
        CommonUtils.setRobotoThinFont(context, holder.difficultyicon);
        CommonUtils.setRobotoThinFont(context, holder.fra_single_recipe_prep_time);
        CommonUtils.setRobotoThinFont(context, holder.fra_single_recipe_portions);
        CommonUtils.setRobotoThinFont(context, holder.fra_single_recipe_cook_time);*/

//        ((CardView) holder.itemView).setCardBackgroundColor(ContextCompat.getColor(context, item.id));


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.ivSportPreview.setTransitionName("shared" + String.valueOf(position));
        }

        holder.ivSportPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                itemClickListener.onItemClicked(holder.getAdapterPosition(), holder.ivSportPreview, item);

            }
        });
        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                itemClickListener.onItemClicked(holder.getAdapterPosition(), holder.ivSportPreview, item);

            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public OnItemClickListener getItemClickListener() {
        return itemClickListener;
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }


    public interface OnItemClickListener {
        void onItemClicked(int pos, View view, Offre r);
    }

    class PopulairViewHolder extends RecyclerView.ViewHolder {

        final TextView tvSportTitle;
        final TextView tvSportSubtitle;
        final TextView rating_number, rating_text, views_number;
        public MaterialRippleLayout lyt_parent;
        // final TextView tvSportRound;
      /*  final TextView fra_single_recipe_prep_time;
        final TextView difficultyicon;
        final TextView fra_single_recipe_portions;
        final TextView fra_single_recipe_cook_time;*/
        ImageView ivSportPreview;

        PopulairViewHolder(View itemView) {
            super(itemView);
            tvSportTitle = (TextView) itemView.findViewById(R.id.ReciepeTitle);
            tvSportSubtitle = (TextView) itemView.findViewById(R.id.ReciepeCategory);
            //  tvSportRound = (TextView) itemView.findViewById(R.id.tvSportRound);
            ivSportPreview = (ImageView) itemView.findViewById(R.id.ivSportPreview);
            rating_number = (TextView) itemView.findViewById(R.id.rating_number);
            views_number = (TextView) itemView.findViewById(R.id.views_number);
            rating_text = (TextView) itemView.findViewById(R.id.rating_text);
            lyt_parent = (MaterialRippleLayout) itemView.findViewById(R.id.lyt_parent);
           /* fra_single_recipe_portions = (TextView) itemView.findViewById(R.id.fra_single_recipe_portions);
            fra_single_recipe_cook_time = (TextView) itemView.findViewById(R.id.fra_single_recipe_cook_time);
            fra_single_recipe_prep_time = (TextView) itemView.findViewById(R.id.fra_single_recipe_prep_time);
            difficultyicon = (TextView) itemView.findViewById(R.id.difficultyicon);*/

        }
    }
}