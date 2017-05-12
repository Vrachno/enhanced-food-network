package gr.academic.city.sdmd.foodnetwork.ui.activity;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import gr.academic.city.sdmd.foodnetwork.R;
import gr.academic.city.sdmd.foodnetwork.db.FoodNetworkContract;


/**
 * Created by trumpets on 4/13/16.
 */
public class MealDetailsActivity extends ToolbarActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String EXTRA_MEAL_ID = "meal_id";
    private static final String EXTRA_MEAL_NAME = "meal_name";

    private static final int MEAL_LOADER = 20;

    public static Intent getStartIntent(Context context, long mealId, String mealName) {
        Intent intent = new Intent(context, MealDetailsActivity.class);
        intent.putExtra(EXTRA_MEAL_ID, mealId);
        intent.putExtra(EXTRA_MEAL_NAME, mealName);

        return intent;
    }

    private long mealId;

    private TextView tvTitle;
    private TextView tvRecipe;
    private TextView tvNumberOfServings;
    private TextView tvPrepTime;
    private TextView tvCreationDate;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mealId = getIntent().getLongExtra(EXTRA_MEAL_ID, -1);

        tvTitle = (TextView) findViewById(R.id.tv_meal_title);
        tvRecipe = (TextView) findViewById(R.id.tv_meal_recipe);
        tvNumberOfServings = (TextView) findViewById(R.id.tv_number_of_servings);
        tvPrepTime = (TextView) findViewById(R.id.tv_prep_time);
        tvCreationDate = (TextView) findViewById(R.id.tv_meal_creation_date);

        getSupportLoaderManager().initLoader(MEAL_LOADER, null, this);
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
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case MEAL_LOADER:
                return new CursorLoader(this,
                        ContentUris.withAppendedId(FoodNetworkContract.Meal.CONTENT_URI, mealId),
                        null, // NULL because we want every column
                        null,
                        null,
                        null
                );

            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        updateView(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        updateView(null);
    }

    private void updateView(Cursor cursor) {
        if (cursor != null && cursor.moveToFirst()) {
            tvTitle.setText(cursor.getString(cursor.getColumnIndexOrThrow(FoodNetworkContract.Meal.COLUMN_TITLE)));
            tvRecipe.setText(cursor.getString(cursor.getColumnIndexOrThrow(FoodNetworkContract.Meal.COLUMN_RECIPE)));
            tvNumberOfServings.setText(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(FoodNetworkContract.Meal.COLUMN_NUMBER_OF_SERVINGS))));

            int prepTimeHour = cursor.getInt(cursor.getColumnIndexOrThrow(FoodNetworkContract.Meal.COLUMN_PREP_TIME_HOUR));
            int prepTimeMinute = cursor.getInt(cursor.getColumnIndexOrThrow(FoodNetworkContract.Meal.COLUMN_PREP_TIME_MINUTE));

            tvPrepTime.setText(getString(R.string.prep_time_w_placeholder, prepTimeHour, prepTimeMinute));
            tvCreationDate.setText(dateFormat.format(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(FoodNetworkContract.Meal.COLUMN_CREATED_AT)))));

        }

        if (cursor != null) {
            cursor.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_meal_details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_upvote:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
