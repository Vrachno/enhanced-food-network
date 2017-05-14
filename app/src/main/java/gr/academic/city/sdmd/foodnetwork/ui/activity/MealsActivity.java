package gr.academic.city.sdmd.foodnetwork.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import gr.academic.city.sdmd.foodnetwork.R;
import gr.academic.city.sdmd.foodnetwork.db.FoodNetworkContract;
import gr.academic.city.sdmd.foodnetwork.service.MealService;
import gr.academic.city.sdmd.foodnetwork.ui.fragment.MealsListFragment;

/**
 * Created by trumpets on 4/24/17.
 */
public class MealsActivity extends ToolbarActivity  {

    private static final String EXTRA_MEAL_TYPE_SERVER_ID = "meal_type_server_id";
    private static final String EXTRA_MEAL_TYPE_NAME = "meal_type_title";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (findViewById(R.id.poutses)!= null) {
            Long sid = getIntent().getLongExtra(EXTRA_MEAL_TYPE_SERVER_ID, 0);
            System.out.print(sid);
        }
        MealsListFragment mealsListFragment = MealsListFragment.newInstance(getIntent().getLongExtra(EXTRA_MEAL_TYPE_SERVER_ID, 0));
        Bundle extras = getIntent().getExtras();
        mealsListFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.frag_meals_list_container, mealsListFragment).commit();

    }

    public static Intent getStartIntent(Context context, long mealTypeServerId, String mealTypeTitle) {
        Intent intent = new Intent(context, MealsActivity.class);
        intent.putExtra(EXTRA_MEAL_TYPE_SERVER_ID, mealTypeServerId);
        intent.putExtra(EXTRA_MEAL_TYPE_NAME, mealTypeTitle);

        return intent;
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_meals;
    }

    @Override
    protected String getCustomTitle() {
        return getResources().getString(R.string.food_category) + " " + getIntent().getStringExtra(EXTRA_MEAL_TYPE_NAME).toUpperCase();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_meals_menu, menu);
        return true;
    }

}
