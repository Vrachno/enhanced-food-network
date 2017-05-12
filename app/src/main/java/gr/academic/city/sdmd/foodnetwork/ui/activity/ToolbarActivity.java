package gr.academic.city.sdmd.foodnetwork.ui.activity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import gr.academic.city.sdmd.foodnetwork.R;

/**
 * Created by Alexandros on 12/5/2017.
 */

public abstract class ToolbarActivity extends AppCompatActivity {


    String ExternalFontPath;
    Typeface FontLoaderTypeface;
    TextView textViewNewFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentView());

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle(getCustomTitle());

        textViewNewFont = new TextView(this);
        RelativeLayout.LayoutParams layoutparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        textViewNewFont.setLayoutParams(layoutparams);
        textViewNewFont.setText(getCustomTitle());
        textViewNewFont.setTextColor(Color.BLACK);
        textViewNewFont.setGravity(Gravity.CENTER);
        textViewNewFont.setTextSize(30);

        ExternalFontPath = "ExternalFonts/font.ttf";
        FontLoaderTypeface = Typeface.createFromAsset(getAssets(), ExternalFontPath);

        textViewNewFont.setTypeface(FontLoaderTypeface);

        actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionbar.setCustomView(textViewNewFont);

        if (getContentView() != R.layout.activity_main) {
            actionbar.setDisplayHomeAsUpEnabled(true);
        } else {
            ImageView imageview = new ImageView(actionbar.getThemedContext());
            imageview.setImageResource(R.drawable.ic_app_bar_icon);
            ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(
                    ActionBar.LayoutParams.WRAP_CONTENT,
                    ActionBar.LayoutParams.WRAP_CONTENT, Gravity.RIGHT | Gravity.CENTER_VERTICAL);
            layoutParams.rightMargin = 50;
            imageview.setLayoutParams(layoutParams);
            toolbar.addView(imageview);
        }


    }


    protected  abstract int getContentView();

    protected  abstract String getCustomTitle();
}
