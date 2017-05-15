package gr.academic.city.sdmd.foodnetwork.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import gr.academic.city.sdmd.foodnetwork.R;
import gr.academic.city.sdmd.foodnetwork.db.FoodNetworkContract;
import gr.academic.city.sdmd.foodnetwork.service.MealService;
import gr.academic.city.sdmd.foodnetwork.ui.activity.CreateMealActivity;
import gr.academic.city.sdmd.foodnetwork.ui.activity.MealDetailsActivity;
import gr.academic.city.sdmd.foodnetwork.ui.activity.MealsActivity;

/**
 * Created by Vrachno on 13/5/2017.
 */

public class MealsListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String ARG_MEAL_TYPE_SERVER_ID = "meal_type_server_id";

    public interface OnFragmentInteractionListener {
        void onMealSelected(int mealLocationInList, Cursor cursor, long id);
    }

    private OnFragmentInteractionListener mListener;

    private static final String[] PROJECTION = {
            FoodNetworkContract.Meal._ID,
            FoodNetworkContract.Meal.COLUMN_TITLE,
            FoodNetworkContract.Meal.COLUMN_UPVOTES,
            FoodNetworkContract.Meal.COLUMN_PREP_TIME_HOUR,
            FoodNetworkContract.Meal.COLUMN_PREP_TIME_MINUTE,
            FoodNetworkContract.Meal.COLUMN_SERVER_ID,
            FoodNetworkContract.Meal.COLUMN_MEAL_TYPE_SERVER_ID
    };

    private static final String SORT_ORDER = FoodNetworkContract.Meal.COLUMN_UPVOTES + " DESC";

    private static final int MEALS_LOADER = 10;

    private final static String[] FROM_COLUMNS = {
            FoodNetworkContract.Meal.COLUMN_TITLE,
            FoodNetworkContract.Meal.COLUMN_UPVOTES,
            FoodNetworkContract.Meal.COLUMN_PREP_TIME_HOUR,};

    private final static int[] TO_IDS = {
            R.id.tv_meal_title,
            R.id.tv_meal_upvotes,
            R.id.tv_meal_prep_time};


    private long mealTypeServerId;
    private CursorAdapter adapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    public static MealsListFragment newInstance(long mealTypeServerId) {
        MealsListFragment fragment = new MealsListFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_MEAL_TYPE_SERVER_ID, mealTypeServerId);
        fragment.setArguments(args);
        return fragment;
    }

    public MealsListFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_meals_list, container, false);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getArguments() != null)
            this.mealTypeServerId = getArguments().getLong(ARG_MEAL_TYPE_SERVER_ID);

        swipeRefreshLayout = (SwipeRefreshLayout) getActivity().findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initiateMealsRefresh();
            }
        });

        adapter = new SimpleCursorAdapter(getActivity(), R.layout.item_meal, null, FROM_COLUMNS, TO_IDS, 0);
        ((SimpleCursorAdapter) adapter).setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (columnIndex == cursor.getColumnIndexOrThrow(FoodNetworkContract.Meal.COLUMN_PREP_TIME_HOUR) && view instanceof TextView) {
                    // we have to build a human readable string of prep time

                    TextView textView = (TextView) view;
                    textView.setText(getString(
                            R.string.prep_time_w_placeholder,
                            cursor.getInt(columnIndex),  // we know this is prep time hour
                            cursor.getInt(cursor.getColumnIndexOrThrow(FoodNetworkContract.Meal.COLUMN_PREP_TIME_MINUTE))));
                    return true;
                } else {
                    return false;
                }
            }
        });


        ListView resultsListView = (ListView) getActivity().findViewById(android.R.id.list);
        resultsListView.setAdapter(adapter);
        resultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.notifyDataSetChanged();
                Cursor cursor = adapter.getCursor();
                mListener.onMealSelected(position, cursor, id);
            }
        });

        getActivity().getSupportLoaderManager().initLoader(MEALS_LOADER, null, this);

        MealService.startFetchMeals(getActivity(), mealTypeServerId);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case MEALS_LOADER:
                return new CursorLoader(getActivity(),
                        FoodNetworkContract.Meal.CONTENT_URI,
                        PROJECTION,
                        FoodNetworkContract.Meal.COLUMN_MEAL_TYPE_SERVER_ID + " = ?",
                        new String[]{String.valueOf(mealTypeServerId)},
                        SORT_ORDER
                );

            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.changeCursor(null);
    }

    private void initiateMealsRefresh() {
        if (!swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(true);
        }

        new FetchMealsAsyncTask().execute(mealTypeServerId);
    }

    private class FetchMealsAsyncTask extends AsyncTask<Long, Void, Void> {

        @Override
        protected Void doInBackground(Long... params) {
            MealService.startFetchMeals(getActivity(), params[0]);

            try {
                // giving the service ample time to finish
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        startTimer();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    Timer timer;
    TimerTask timerTask;

    public void startTimer() {
        timer = new Timer();
        initializeTimerTask();
        timer.schedule(timerTask, 3000, 3000);
    }

    public void initializeTimerTask(){
        timerTask = new TimerTask() {
            @Override
            public void run() {
                new FetchMealsAsyncTask().execute(mealTypeServerId);
            }
        };
    }


}
