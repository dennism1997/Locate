package com.moumou.locate;

import android.Manifest;
import android.os.Bundle;

import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;

/**
 * Created by MouMou on 02-02-17.
 */

public class IntroActivity extends com.heinrichreimersoftware.materialintro.app.IntroActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setFullscreen(true);

        super.onCreate(savedInstanceState);

        setButtonBackVisible(false);

        addSlide(new SimpleSlide.Builder().title("Locate")
                         .description(R.string.intro1desc)
                         .background(R.color.colorPrimary)
                         .backgroundDark(R.color.colorPrimaryDark)
                         .build());

        addSlide(new SimpleSlide.Builder().title("Last but not least..")
                         .description("For obvious reasons, we need your permission for locations!")
                         .background(R.color.colorPrimary)
                         .backgroundDark(R.color.colorPrimaryDark)
                         .permissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
                         .build());
    }
}
