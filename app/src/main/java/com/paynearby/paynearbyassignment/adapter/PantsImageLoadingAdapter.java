package com.paynearby.paynearbyassignment.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.paynearby.paynearbyassignment.R;
import com.paynearby.paynearbyassignment.model.PantsItem;
import com.paynearby.paynearbyassignment.model.ShirtsItem;
import com.paynearby.paynearbyassignment.utils.GlideApp;

import java.util.List;

import static android.view.View.GONE;

public class PantsImageLoadingAdapter extends PagerAdapter {

    private Context context;
    private List<PantsItem>  pantsItemList;

    public PantsImageLoadingAdapter(Context context, List<PantsItem> pantsItemList){
        this.context = context;
        this.pantsItemList = pantsItemList;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPager vp = (ViewPager)container;
        View view = (View)object;
        vp.removeView(view);
    }

    @Override
    public int getCount() {
        return pantsItemList.size();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert mLayoutInflater != null;
        View pageView = mLayoutInflater.inflate(R.layout.image_loading_view,container,false);
        final ImageView imageView = pageView.findViewById(R.id.image_view);
        final ProgressBar loader = pageView.findViewById(R.id.loader);

        loader.setVisibility(View.VISIBLE);
        GlideApp.with(context)
                .load(pantsItemList.get(position).getPantImageURL())
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        loader.setVisibility(GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        loader.setVisibility(GONE);
                        return false;
                    }
                })
                .centerCrop()
                .placeholder(R.drawable.shirts_placeholder)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);
        ViewPager vp = (ViewPager)container;
        vp.addView(pageView,0);
        return pageView;
    }

    public void updatePantsList(List<PantsItem> newlist) {
        pantsItemList.clear();
        pantsItemList.addAll(newlist);
        this.notifyDataSetChanged();
    }
}
