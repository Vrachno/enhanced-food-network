package gr.academic.city.sdmd.foodnetwork.ui.fragment;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import gr.academic.city.sdmd.foodnetwork.ui.activity.MealDetailsActivity;
import gr.academic.city.sdmd.foodnetwork.ui.activity.MealsActivity;

/**
 * Created by Alexandros on 14/5/2017.
 */

public class MealDetailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final String ARG_MEAL_ID = "meal_id";
    private static final String ARG_MEAL_SERVER_ID = "meal_server_id";
    private static final String ARG_MEAL_NAME = "meal_name";
    private static final String ARG_MEAL_TYPE_SERVER_ID = "meal_type_server_id";

    private static final int MEAL_LOADER = 20;

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

    private boolean isUpvotePressed = false;
    final Handler handler = new Handler();

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public MealDetailsFragment() {
    }

    public static MealDetailsFragment newInstance(long mealId, long mealServerId, String mealName, long mealTypeServerId) {

        Bundle args = new Bundle();

        MealDetailsFragment fragment = new MealDetailsFragment();
        args.putLong(ARG_MEAL_ID, mealId);
        args.putLong(ARG_MEAL_SERVER_ID, mealServerId);
        args.putString(ARG_MEAL_NAME, mealName);
        args.putLong(ARG_MEAL_TYPE_SERVER_ID, mealTypeServerId);
        fragment.setArguments(args);
        return fragment;
    }

    private static MealDetailsFragment ins;

    public static MealDetailsFragment getInstance() {
        return ins;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ins = this;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_meal_details, container, false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mealId = getArguments().getLong(ARG_MEAL_ID);
        mealServerId = getArguments().getLong(ARG_MEAL_SERVER_ID);
        mealTypeServerId = getArguments().getLong(ARG_MEAL_TYPE_SERVER_ID);

        tvTitle = (TextView) getActivity().findViewById(R.id.tv_meal_title_details);
        tvUpvotes = (TextView) getActivity().findViewById(R.id.tv_meal_upvotes_details);
        ivPreview = (ImageView) getActivity().findViewById(R.id.iv_meal_preview);
        tvRecipe = (TextView) getActivity().findViewById(R.id.tv_meal_recipe);
        tvNumberOfServings = (TextView) getActivity().findViewById(R.id.tv_number_of_servings);
        tvPrepTime = (TextView) getActivity().findViewById(R.id.tv_prep_time);
        tvCreationDate = (TextView) getActivity().findViewById(R.id.tv_meal_creation_date);

        getActivity().getSupportLoaderManager().initLoader(MEAL_LOADER, null, this);


    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (isUpvotePressed){
            handler.removeCallbacksAndMessages(null);
            MealService.startUpvoteMeal(getActivity(), mealTypeServerId, mealServerId);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case MEAL_LOADER:
                return new CursorLoader(getActivity(),
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
            MealDetailsFragment.LoadImageFromURL loadImage = new MealDetailsFragment.LoadImageFromURL();
            loadImage.execute(preview);


        }
        getActivity().getSupportLoaderManager().destroyLoader(MEAL_LOADER);

    }

    public class LoadImageFromURL extends AsyncTask<String, Void, Bitmap> {

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ConnectivityManager connMgr = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        switch (item.getItemId()) {
            case R.id.action_upvote:
                if (networkInfo != null && networkInfo.isConnected()) {
                    isUpvotePressed = true;
                    final int previousUpvotes = Integer.parseInt(tvUpvotes.getText().toString());
                    tvUpvotes.setText(String.valueOf(previousUpvotes + 1));
                    final Activity activity = getActivity();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            MealService.startUpvoteMeal(activity, mealTypeServerId, mealServerId);
                        }
                    }, 10000);
                    Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.coordinator_layout),
                            getResources().getString(R.string.snackbar_message), 3000).setAction(getResources().getString(R.string.undo), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            handler.removeCallbacksAndMessages(null);
                            isUpvotePressed = false;
                            tvUpvotes.setText(String.valueOf(previousUpvotes));
                        }
                    });
                    snackbar.setActionTextColor(Color.RED);
                    TextView message = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
                    message.setTextColor(getResources().getColor(R.color.colorPrimary));
                    snackbar.show();

                } else {
                    Toast.makeText(getActivity(),getResources().getString(R.string.no_connectivity), Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
