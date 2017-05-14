package gr.academic.city.sdmd.foodnetwork.ui.activity;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import gr.academic.city.sdmd.foodnetwork.R;
import gr.academic.city.sdmd.foodnetwork.db.FoodNetworkContract;
import gr.academic.city.sdmd.foodnetwork.service.MealService;
import gr.academic.city.sdmd.foodnetwork.ui.fragment.MealDetailsFragment;
import gr.academic.city.sdmd.foodnetwork.ui.fragment.MealsListFragment;


/**
 * Created by trumpets on 4/13/16.
 */
public class MealDetailsActivity extends ToolbarActivity {

    private static final String EXTRA_MEAL_ID = "meal_id";
    private static final String EXTRA_MEAL_SERVER_ID = "meal_server_id";
    private static final String EXTRA_MEAL_NAME = "meal_name";
    private static final String EXTRA_MEAL_TYPE_SERVER_ID = "meal_type_server_id";

    private static final int MEAL_LOADER = 20;

    private static MealDetailsActivity ins;

    public static MealDetailsActivity getInstance() {
        return ins;
    }

    public static Intent getStartIntent(Context context, long mealId, long mealServerId, long mealTypeServerId, String mealName) {
        Intent intent = new Intent(context, MealDetailsActivity.class);
        intent.putExtra(EXTRA_MEAL_ID, mealId);
        intent.putExtra(EXTRA_MEAL_SERVER_ID, mealServerId);
        intent.putExtra(EXTRA_MEAL_TYPE_SERVER_ID, mealTypeServerId);
        intent.putExtra(EXTRA_MEAL_NAME, mealName);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ins = this;

        Long mealId = getIntent().getLongExtra(EXTRA_MEAL_ID, 0);
        Long mealServerId = getIntent().getLongExtra(EXTRA_MEAL_SERVER_ID, 0);
        String mealName = getIntent().getStringExtra(EXTRA_MEAL_NAME);
        Long mealTypeServerId = getIntent().getLongExtra(EXTRA_MEAL_TYPE_SERVER_ID, 0);

        MealDetailsFragment mealDetailsFragment = MealDetailsFragment.newInstance(mealId, mealServerId, mealName, mealTypeServerId);
        mealDetailsFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.frag_meal_details_container, mealDetailsFragment).commit();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_meal_details;
    }

    @Override
    protected String getCustomTitle() {
        return getIntent().getStringExtra(EXTRA_MEAL_NAME).toUpperCase();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_meal_details_menu, menu);
        return true;
    }


    public void updateUpvotes (final String upvotes) {
        MealDetailsActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView textUpvotes = (TextView) findViewById(R.id.tv_meal_upvotes);
                textUpvotes.setText(upvotes);
            }
        });
    }
}


