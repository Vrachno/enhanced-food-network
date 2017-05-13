package gr.academic.city.sdmd.foodnetwork.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import gr.academic.city.sdmd.foodnetwork.ui.activity.MealDetailsActivity;

/**
 * Created by Alexandros on 13/5/2017.
 */

public class UpdateUpvoteBroadcastReceiver extends BroadcastReceiver {

    public static final String ACTION_UPVOTE = "gr.academic.city.sdmd.foodnetwork.UPVOTE";
    public static final String EXTRA_UPVOTES = "upvotes";


    @Override
    public void onReceive(Context context, Intent intent) {
        MealDetailsActivity.getInstance().updateUpvotes(intent.getStringExtra(EXTRA_UPVOTES));
    }
}
