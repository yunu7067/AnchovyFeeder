package com.example.anchovyfeeder.gallery;

import android.net.Uri;

public class PhotoItem {
    private Uri src;

    public PhotoItem(Uri uri) {
        this.src = uri;
    }

    public void setSrc(Uri src) {
        this.src = src;
    }

    public Uri getSrc() {
        return src;
    }
}
