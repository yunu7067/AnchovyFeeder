package com.example.anchovyfeeder.gallery;

import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.anchovyfeeder.R;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;

import me.relex.photodraweeview.PhotoDraweeView;

public class PhotoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // get Photo Uri
        Intent intent = getIntent();
        String uri = intent.getStringExtra("URI");
        Toast.makeText(this, "thisUri : " + uri, Toast.LENGTH_SHORT).show();
        Uri photoUri = Uri.parse("file://" + uri);
        // set Photo
        //SimpleDraweeView photo = findViewById(R.id.photo_view);
        PhotoDraweeView photoView = findViewById(R.id.photo_view);
        photoView.setImageURI(photoUri);

        PipelineDraweeControllerBuilder controller = Fresco.newDraweeControllerBuilder();
        controller.setUri(photoUri);
        controller.setOldController(photoView.getController());
        controller.setControllerListener(new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
                super.onFinalImageSet(id, imageInfo, animatable);
                if (imageInfo == null || photoView == null) {
                    return;
                }
                photoView.update(imageInfo.getWidth(), imageInfo.getHeight());
            }
        });
        photoView.setController(controller.build());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
