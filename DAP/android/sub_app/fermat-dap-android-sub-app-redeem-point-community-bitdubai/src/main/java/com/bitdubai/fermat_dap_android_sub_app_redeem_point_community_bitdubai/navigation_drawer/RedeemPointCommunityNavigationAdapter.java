package com.bitdubai.fermat_dap_android_sub_app_redeem_point_community_bitdubai.navigation_drawer;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;

import com.bitdubai.fermat_android_api.ui.adapters.FermatAdapter;
import com.bitdubai.fermat_api.layer.all_definition.navigation_structure.MenuItem;
import com.bitdubai.fermat_dap_android_sub_app_redeem_point_community_bitdubai.R;

import java.util.List;

/**
 * Created by Matias Furszyfer on 2015.09.30..
 * Modified by Jose Manuel De Sousa
 */
public class RedeemPointCommunityNavigationAdapter extends FermatAdapter<MenuItem, RedeemPointNavigationHolder> {


    Typeface tf;

    protected RedeemPointCommunityNavigationAdapter(Context context) {
        super(context);
    }

    public RedeemPointCommunityNavigationAdapter(Context context, List<MenuItem> dataSet) {
        super(context, dataSet);
        tf = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf");
    }

    public void setOnClickListerAcceptButton(View.OnClickListener onClickListener){

    }

    public void setOnClickListerRefuseButton(View.OnClickListener onClickListener){

    }

    /**
     * Create a new holder instance
     *
     * @param itemView View object
     * @param type     int type
     * @return ViewHolder
     */
    @Override
    protected RedeemPointNavigationHolder createHolder(View itemView, int type) {
        return new RedeemPointNavigationHolder(itemView);
    }

    /**
     * Get custom layout to use it.
     *
     * @return int Layout Resource id: Example: R.layout.row_item
     */
    @Override
    protected int getCardViewResource() {
        return R.layout.dap_navigation_drawer_community_redeem_point_content;
    }

    /**
     * Bind ViewHolder
     *
     * @param holder   ViewHolder object
     * @param data     Object data to render
     * @param position position to render
     */
    @Override
    protected void bindHolder(RedeemPointNavigationHolder holder, MenuItem data, int position) {

        try {
            holder.getLabel().setText(data.getLabel());
            switch (position) {
                case 0:
                    holder.getIcon().setImageResource(R.drawable.ic_action_filters);
                    break;
                case 1:
                    holder.getIcon().setImageResource(R.drawable.ic_action_filters);
                    break;
                case 2:
                    holder.getIcon().setImageResource(R.drawable.ic_action_filters);
                    break;
                case 3:
                    holder.getIcon().setImageResource(R.drawable.ic_action_filters);
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
