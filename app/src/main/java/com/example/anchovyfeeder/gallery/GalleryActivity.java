package com.example.anchovyfeeder.gallery;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.anchovyfeeder.MainViewModel;
import com.example.anchovyfeeder.R;
import com.example.anchovyfeeder.realmdb.PhotoObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.Sort;

public class GalleryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        setTitle("갤러리");

        Realm mRealm = MainViewModel.Photos.getRealm();
        MainViewModel.Photos = MainViewModel.Photos.sort("DATE", Sort.DESCENDING);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");

        ArrayList<PhotoObject> photos = new ArrayList<PhotoObject>();
        if (!MainViewModel.Photos.isEmpty())
            photos.addAll(mRealm.copyFromRealm(MainViewModel.Photos));

        //RecyclerView
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.gallery);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        //Your RecyclerView.Adapter
        SimpleAdapter mAdapter = new SimpleAdapter(this, photos);

        //This is the code to provide a sectioned grid
        List<SectionedGridRecyclerViewAdapter.Section> sections =
                new ArrayList<SectionedGridRecyclerViewAdapter.Section>();

        //Sections
        if(!photos.isEmpty()) {
            // 0번 인덱스
            String prevItemDateString = photos.get(0).getDateFormat(dateFormat);
            sections.add(new SectionedGridRecyclerViewAdapter.Section(0, prevItemDateString));
            // 1번 ~ 마지막 인덱스
            for (int index = 1; index < photos.size(); index++) {
                String thisItemDateString = photos.get(index).getDateFormat(dateFormat);
                if (!prevItemDateString.equals(thisItemDateString)) {
                    prevItemDateString = thisItemDateString;
                    sections.add(new SectionedGridRecyclerViewAdapter.Section(index, thisItemDateString));
                }

            }
        }

        //Add your adapter to the sectionAdapter
        SectionedGridRecyclerViewAdapter.Section[] dummy = new SectionedGridRecyclerViewAdapter.Section[sections.size()];
        SectionedGridRecyclerViewAdapter mSectionedAdapter = new
                SectionedGridRecyclerViewAdapter(this, R.layout.gallery_section, R.id.section_text, mRecyclerView, mAdapter);
        mSectionedAdapter.setSections(sections.toArray(dummy));

        //Apply this adapter to the RecyclerView
        mRecyclerView.setAdapter(mSectionedAdapter);
    }
}
