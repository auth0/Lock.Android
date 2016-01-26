package com.auth0.android.lock.views;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.auth0.android.lock.R;
import com.auth0.android.lock.utils.Strategies;
import com.auth0.android.lock.utils.Strategy;

import java.util.List;

/**
 * Created by lbalmaceda on 1/22/16.
 */
class SocialViewAdapter extends RecyclerView.Adapter<SocialViewAdapter.ViewHolder> {
    private static final String TAG = SocialViewAdapter.class.getSimpleName();

    private final Context context;
    private final List<Strategy> strategyList;
    private ConnectionAuthenticationListener callback;

    public SocialViewAdapter(Context context, List<Strategy> strategyList) {
        this.context = context;
        this.strategyList = strategyList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = new SocialButton(context);
        int width = (int) context.getResources().getDimension(R.dimen.com_auth0_btn_social_big_width);
        int height = (int) context.getResources().getDimension(R.dimen.com_auth0_btn_social_big_height);
        view.setLayoutParams(new RecyclerView.LayoutParams(width, height));
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Strategy item = strategyList.get(i);
        viewHolder.socialButton.setTitle(getTitleForStrategy(item));
        viewHolder.socialButton.setIcon(getIconForStrategy(item));
    }

    @Override
    public int getItemCount() {
        return strategyList.size();
    }


    public void setCallback(ConnectionAuthenticationListener callback) {
        this.callback = callback;
    }

    private String getTitleForStrategy(Strategy strategy) {
        return strategy.getName();
    }

    @DrawableRes
    private int getIconForStrategy(Strategy strategy) {
        String name = strategy.getName().toLowerCase();
        if (name.startsWith(Strategies.Facebook.getName())) {
            return android.R.drawable.ic_delete;
        } else if (name.startsWith(Strategies.Twitter.getName())) {
            return android.R.drawable.ic_input_add;
        } else {
            return android.R.drawable.ic_btn_speak_now;
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        SocialButton socialButton;

        public ViewHolder(View v) {
            super(v);
            socialButton = (SocialButton) v;
            socialButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (callback != null) {
                Strategy strategy = strategyList.get(getAdapterPosition());
                callback.onConnectionClicked(strategy.getName());       //returns the name of the first connection element
            } else {
                Log.w(TAG, ConnectionAuthenticationListener.class.getSimpleName() + " not configured");
            }
        }

    }

    public interface ConnectionAuthenticationListener {
        /**
         * Called when a SocialButton is clicked.
         *
         * @param connectionName the connectionName associated to the button.
         */
        void onConnectionClicked(String connectionName);
    }
}
