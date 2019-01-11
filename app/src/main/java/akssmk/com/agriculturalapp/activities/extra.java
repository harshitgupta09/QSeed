package akssmk.com.agriculturalapp.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import akssmk.com.agriculturalapp.R;
import akssmk.com.agriculturalapp.adapters.MainAdapter;
import akssmk.com.agriculturalapp.modals.MainListItem;

public class extra extends AppCompatActivity {

    private ArrayList<MainListItem> list;
    private RecyclerView recyclerView;
    private MainAdapter adapter;

    private Integer[] imageUrls = {R.raw.crop_production_opt, R.raw.treat, R.drawable.horticulture_main};

    private Integer[] hindiTexts = {R.string.crop_production_card_title_hi, R.string.treatment_card_title_hi,
            R.string.horticulture_card_title_hi};
    private Integer[] englishTexts = {R.string.crop_production_card_title_en, R.string.treatment_card_title_en,
            R.string.horticulture_card_title_en};

    private String[] backgroundColors = {"#35e372", "#a4f075", "#cef63c"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extra);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Intent[] links = {
                new Intent(extra.this, CropProductionActivity.class),
                new Intent(extra.this, SelectProblem.class),
                new Intent(extra.this, HorticultureActivity.class)
        };

        list = new ArrayList<>();
        for (int i = 0; i < imageUrls.length; i++) {
            MainListItem item = new MainListItem();

            item.setImageUrl(imageUrls[i]);
            item.setHindiText(hindiTexts[i]);
            item.setEnglishText(englishTexts[i]);
            item.setBackgroundColor(backgroundColors[i]);
            item.setIntent(links[i]);

            list.add(item);
        }

        adapter = new MainAdapter(this, list);

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        Log.v("version", Build.VERSION.SDK_INT + "");
        findViewById(R.id.progress).setVisibility(View.GONE);

    }
}
