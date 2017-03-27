package com.moumou.locate;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

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

        addSlide(new SimpleSlide.Builder().title("Locate!")
                         .description(R.string.intro1desc)
                         .background(R.color.colorPrimary)
                         .backgroundDark(R.color.colorPrimaryDark)
                         .build());

        addSlide(new SimpleSlide.Builder().title("Location Reminders")
                         .description(R.string.intro2desc)
                         .background(R.color.colorPrimary)
                         .backgroundDark(R.color.colorPrimaryDark)
                         .image(R.drawable.ic_room_white_48dp)
                         .build());

        addSlide(new SimpleSlide.Builder().title("POI Reminders")
                         .description(R.string.intro3desc)
                         .background(R.color.colorPrimary)
                         .backgroundDark(R.color.colorPrimaryDark)
                         .image(R.drawable.ic_shopping_cart_white_48dp)
                         .build());

        if (ActivityCompat.checkSelfPermission(this,
                                               android.Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                                                                                    android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                                                 PackageManager.PERMISSION_GRANTED) {
            addSlide(new SimpleSlide.Builder().title("Last but not least..")
                             .description(
                                     "For obvious reasons, we need your permission for locations!")
                             .background(R.color.colorPrimary)
                             .backgroundDark(R.color.colorPrimaryDark)
                             .permissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION})
                             .build());
        }
    }
}
