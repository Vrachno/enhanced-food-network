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


/**
 * Created by trumpets on 4/13/16.
 */
public class MealDetailsActivity extends ToolbarActivity implements LoaderManager.LoaderCallbacks<Cursor> {

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

    private long mealId;
    private long mealServerId;
    private long mealTypeServerId;

    private TextView tvTitle;
    private TextView tvUpvotes;
    private ImageView ivPreview;
    private TextView tvRecipe;
    private TextView tvNumberOfServings;
    private TextView tvPrepTime;
    private TextView tvCreationDate;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ins = this;

        mealId = getIntent().getLongExtra(EXTRA_MEAL_ID, -1);
        mealServerId = getIntent().getLongExtra(EXTRA_MEAL_SERVER_ID, -1);
        mealTypeServerId = getIntent().getLongExtra(EXTRA_MEAL_TYPE_SERVER_ID, -1);

        tvTitle = (TextView) findViewById(R.id.tv_meal_title);
        tvUpvotes = (TextView) findViewById(R.id.tv_meal_upvotes);
        ivPreview = (ImageView) findViewById(R.id.iv_meal_preview);
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
            tvUpvotes.setText(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(FoodNetworkContract.Meal.COLUMN_UPVOTES))));
            tvRecipe.setText(cursor.getString(cursor.getColumnIndexOrThrow(FoodNetworkContract.Meal.COLUMN_RECIPE)));
            tvNumberOfServings.setText(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(FoodNetworkContract.Meal.COLUMN_NUMBER_OF_SERVINGS))));

            int prepTimeHour = cursor.getInt(cursor.getColumnIndexOrThrow(FoodNetworkContract.Meal.COLUMN_PREP_TIME_HOUR));
            int prepTimeMinute = cursor.getInt(cursor.getColumnIndexOrThrow(FoodNetworkContract.Meal.COLUMN_PREP_TIME_MINUTE));

            tvPrepTime.setText(getString(R.string.prep_time_w_placeholder, prepTimeHour, prepTimeMinute));
            tvCreationDate.setText(dateFormat.format(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(FoodNetworkContract.Meal.COLUMN_CREATED_AT)))));

            String preview = cursor.getString(cursor.getColumnIndexOrThrow(FoodNetworkContract.Meal.COLUMN_PREVIEW));
            LoadImageFromURL loadImage = new LoadImageFromURL();
            loadImage.execute(preview);

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
        ConnectivityManager connMgr = (ConnectivityManager)
        this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        final Handler handler = new Handler();
        switch (item.getItemId()) {
            case R.id.action_upvote:
                if (networkInfo != null && networkInfo.isConnected()) {
                    final int previousUpvotes = Integer.parseInt(tvUpvotes.getText().toString());
                    tvUpvotes.setText(String.valueOf(previousUpvotes + 1));
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MealService.startUpvoteMeal(MealDetailsActivity.this, mealTypeServerId, mealServerId);
                        }
                    }, 3000);

                    Snackbar snackbar = Snackbar.make(findViewById(R.id.coordinator_layout),
                            getResources().getString(R.string.snackbar_message), 3000).setAction(getResources().getString(R.string.undo), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            handler.removeCallbacksAndMessages(null);
                            updateUpvotes(String.valueOf(previousUpvotes));
                        }
                    });
                    snackbar.setActionTextColor(Color.RED);
                    TextView message = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                    message.setTextColor(getResources().getColor(R.color.colorPrimary));
                    snackbar.show();
                } else {
                    Toast.makeText(this,getResources().getString(R.string.no_connectivity), Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class LoadImageFromURL extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                InputStream is = url.openConnection().getInputStream();
                Bitmap bitMap = BitmapFactory.decodeStream(is);
                return bitMap;

            } catch (MalformedURLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            ivPreview.setImageBitmap(result);
        }

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


