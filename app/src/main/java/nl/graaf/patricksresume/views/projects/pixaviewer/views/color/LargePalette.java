package nl.graaf.patricksresume.views.projects.pixaviewer.views.color;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.graphics.Palette;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by patrick on 12/7/17.
 * 10:43 AM, Notive B.V.
 *
 * © Copyright 2017
 */

public class LargePalette implements Parcelable {
    public static final Parcelable.Creator<LargePalette> CREATOR = new Parcelable.Creator<LargePalette>() {
        public LargePalette createFromParcel(Parcel source) {
            return new LargePalette(source);
        }

        public LargePalette[] newArray(int size) {
            return new LargePalette[size];
        }
    };
    private static final int DEFAULT_CALCULATE_NUMBER_COLORS = 16;
    private List<LargeSwatch> mSwatches = new ArrayList<>();
    private LargeSwatch mVibrantSwatch;
    private LargeSwatch mMutedSwatch;
    private LargeSwatch mDarkVibrantSwatch;
    private LargeSwatch mDarkMutedSwatch;
    private LargeSwatch mLightVibrantSwatch;
    private LargeSwatch mLightMutedColor;

    private LargePalette(List<LargeSwatch> swatches, LargeSwatch vibrantSwatch,
                         LargeSwatch mutedSwatch, LargeSwatch darkVibrantSwatch,
                         LargeSwatch darkMutedSwatch, LargeSwatch lightVibrantSwatch,
                         LargeSwatch lightMutedColor) {
        replaceWithPalette(swatches, vibrantSwatch, mutedSwatch, darkVibrantSwatch, darkMutedSwatch, lightVibrantSwatch, lightMutedColor);
    }

    private LargePalette(Parcel in) {
        in.readTypedList(mSwatches, LargeSwatch.CREATOR);
        this.mVibrantSwatch = in.readParcelable(LargeSwatch.class.getClassLoader());
        this.mMutedSwatch = in.readParcelable(LargeSwatch.class.getClassLoader());
        this.mDarkVibrantSwatch = in.readParcelable(LargeSwatch.class.getClassLoader());
        this.mDarkMutedSwatch = in.readParcelable(LargeSwatch.class.getClassLoader());
        this.mLightVibrantSwatch = in.readParcelable(LargeSwatch.class.getClassLoader());
        this.mLightMutedColor = in.readParcelable(LargeSwatch.class.getClassLoader());
    }

    public static LargePalette generate(Bitmap bitmap) {
        Palette smallerPalette = Palette.from(bitmap).generate();
        return generateLargerPalette(smallerPalette);
    }

    private static LargePalette generateLargerPalette(Palette smallerPalette) {
        List<LargeSwatch> swatches = generateLargerPaletteSwatches(smallerPalette);
        LargeSwatch vibrantSwatch = new LargeSwatch(smallerPalette.getVibrantSwatch());
        LargeSwatch mutedSwatch = new LargeSwatch(smallerPalette.getMutedSwatch());
        LargeSwatch darkVibrantSwatch = new LargeSwatch(smallerPalette.getDarkVibrantSwatch());
        LargeSwatch darkMutedSwatch = new LargeSwatch(smallerPalette.getDarkMutedSwatch());
        LargeSwatch lightVibrantSwatch = new LargeSwatch(smallerPalette.getLightVibrantSwatch());
        LargeSwatch lightMutedColor = new LargeSwatch(smallerPalette.getLightMutedSwatch());
        return new LargePalette(swatches, vibrantSwatch, mutedSwatch, darkVibrantSwatch, darkMutedSwatch, lightVibrantSwatch, lightMutedColor);

    }

    private static List<LargeSwatch> generateLargerPaletteSwatches(Palette smallerPalette) {
        List<Palette.Swatch> smallerSwatches = smallerPalette.getSwatches();
        List<LargeSwatch> largeSwatches = new ArrayList<>(smallerSwatches.size());
        for (Palette.Swatch small : smallerSwatches) {
            largeSwatches.add(new LargeSwatch(small));
        }
        return Collections.unmodifiableList(largeSwatches);
    }

    public static boolean validatePalette(Palette palette) {
        try {
            boolean isAnyNull = palette.getVibrantSwatch() != null && palette.getDarkMutedSwatch() != null && palette.getLightMutedSwatch() != null;
            if (isAnyNull) {
                palette.getVibrantSwatch().getBodyTextColor();
                palette.getDarkMutedSwatch().getBodyTextColor();
                palette.getLightMutedSwatch().getBodyTextColor();
                return true;
            } else return isAnyNull;
        } catch (IllegalArgumentException | AssertionError e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean validatePalette(LargePalette palette) {
        try {
            palette.getVibrantSwatch().getBodyTextColor();
            palette.getDarkMutedSwatch().getBodyTextColor();
            palette.getLightMutedSwatch().getBodyTextColor();
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private void replaceWithPalette(List<LargeSwatch> swatches, LargeSwatch vibrantSwatch,
                                    LargeSwatch mutedSwatch, LargeSwatch darkVibrantSwatch,
                                    LargeSwatch darkMutedSwatch, LargeSwatch lightVibrantSwatch,
                                    LargeSwatch lightMutedColor) {
        this.mSwatches = swatches;
        this.mVibrantSwatch = vibrantSwatch;
        this.mMutedSwatch = mutedSwatch;
        this.mDarkVibrantSwatch = darkVibrantSwatch;
        this.mDarkMutedSwatch = darkMutedSwatch;
        this.mLightVibrantSwatch = lightVibrantSwatch;
        this.mLightMutedColor = lightMutedColor;

    }

    public LargeSwatch getVibrantSwatch() {
        return mVibrantSwatch;
    }

    public void setVibrantSwatch(LargeSwatch vibrantSwatch) {
        mVibrantSwatch = vibrantSwatch;
    }

    public LargeSwatch getMutedSwatch() {
        return mMutedSwatch;
    }

    public void setMutedSwatch(LargeSwatch mutedSwatch) {
        mMutedSwatch = mutedSwatch;
    }

    public LargeSwatch getDarkVibrantSwatch() {
        return mDarkVibrantSwatch;
    }

    public void setDarkVibrantSwatch(LargeSwatch darkVibrantSwatch) {
        mDarkVibrantSwatch = darkVibrantSwatch;
    }

    public LargeSwatch getDarkMutedSwatch() {
        return mDarkMutedSwatch;
    }

    public void setDarkMutedSwatch(LargeSwatch darkMutedSwatch) {
        mDarkMutedSwatch = darkMutedSwatch;
    }

    public LargeSwatch getLightVibrantSwatch() {
        return mLightVibrantSwatch;
    }

    public void setLightVibrantSwatch(LargeSwatch lightVibrantSwatch) {
        mLightVibrantSwatch = lightVibrantSwatch;
    }

    public LargeSwatch getLightMutedColor() {
        return mLightMutedColor;
    }

    public void setLightMutedColor(LargeSwatch lightMutedColor) {
        mLightMutedColor = lightMutedColor;
    }

    public LargeSwatch getLightMutedSwatch() {
        return mLightMutedColor;
    }

    public int getVibrantColor(int defaultColor) {
        return mVibrantSwatch != null ? mVibrantSwatch.getRgb() : defaultColor;
    }

    public int getLightVibrantColor(int defaultColor) {
        return mLightVibrantSwatch != null ? mLightVibrantSwatch.getRgb() : defaultColor;
    }

    public int getDarkVibrantColor(int defaultColor) {
        return mDarkVibrantSwatch != null ? mDarkVibrantSwatch.getRgb() : defaultColor;
    }

    public int getMutedColor(int defaultColor) {
        return mMutedSwatch != null ? mMutedSwatch.getRgb() : defaultColor;
    }

    public int getLightMutedColor(int defaultColor) {
        return mLightMutedColor != null ? mLightMutedColor.getRgb() : defaultColor;
    }

    public int getDarkMutedColor(int defaultColor) {
        return mDarkMutedSwatch != null ? mDarkMutedSwatch.getRgb() : defaultColor;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(mSwatches);
        dest.writeParcelable(this.mVibrantSwatch, 0);
        dest.writeParcelable(this.mMutedSwatch, 0);
        dest.writeParcelable(this.mDarkVibrantSwatch, 0);
        dest.writeParcelable(this.mDarkMutedSwatch, 0);
        dest.writeParcelable(this.mLightVibrantSwatch, 0);
        dest.writeParcelable(this.mLightMutedColor, 0);
    }

    public interface LargePaletteAsyncListener {
        public void onGenerated(LargePalette largePalette);
    }
}
