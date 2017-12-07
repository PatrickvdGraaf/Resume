package nl.graaf.patricksresume.views.projects.pixaviewer.views.color;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;

import java.util.Arrays;

import nl.graaf.patricksresume.views.projects.pixaviewer.models.PixaImage;

/**
 * Created by patrick on 12/7/17.
 * 10:43 AM, Notive B.V.
 *
 * © Copyright 2017
 */

public class LargeSwatch implements Parcelable {
    public static final Parcelable.Creator<LargeSwatch> CREATOR = new Parcelable.Creator<LargeSwatch>() {
        public LargeSwatch createFromParcel(Parcel source) {
            return new LargeSwatch(source);
        }

        public LargeSwatch[] newArray(int size) {
            return new LargeSwatch[size];
        }
    };
    private static final float MIN_CONTRAST_TITLE_TEXT = 3.0f;
    private static final float MIN_CONTRAST_BODY_TEXT = 4.5f;
    private int mRed, mGreen, mBlue;
    private int mLargeRgb;
    @Nullable
    private int mLargePopulation;
    private boolean mGeneratedTextColors;
    @Nullable
    private int mTitleTextColor;
    @Nullable
    private int mBodyTextColor;
    @Nullable
    private float[] mHsl;

    /*
     Generate LargeSwatch from existing Swatch
     */
    LargeSwatch(@Nullable Palette.Swatch swatch) {
        if (swatch != null) {
            mLargeRgb = swatch.getRgb();
            mBodyTextColor = swatch.getBodyTextColor();
            mTitleTextColor = swatch.getTitleTextColor();
            mHsl = swatch.getHsl();
            mLargePopulation = swatch.getPopulation();
        }
        mRed = Color.red(mLargeRgb);
        mBlue = Color.blue(mLargeRgb);
        mGreen = Color.green(mLargeRgb);
        mGeneratedTextColors = true;
    }

    private LargeSwatch(Parcel in) {
        this.mRed = in.readInt();
        this.mGreen = in.readInt();
        this.mBlue = in.readInt();
        this.mLargeRgb = in.readInt();
        this.mLargePopulation = in.readInt();
        this.mGeneratedTextColors = in.readByte() != 0;
        this.mTitleTextColor = in.readInt();
        this.mBodyTextColor = in.readInt();
        this.mHsl = in.createFloatArray();
    }

    public int getRgb() {
        if (mLargeRgb >= 0) {
            return mLargeRgb;
        } else {
            return Color.argb(PixaImage.BACKGROUND_ALPHA, 0, 0, 0);
        }
    }

    private float[] getHsl() {
        if (mHsl == null) {
            mHsl = new float[3];
            ColorUtils.RGBtoHSL(mRed, mGreen, mBlue, mHsl);
        }
        return mHsl;
    }

    private int getPopulation() {
        return mLargePopulation;
    }

    public int getTitleTextColor() {
        ensureTextColorsGenerated();
        return mTitleTextColor;
    }

    public int getBodyTextColor() {
        ensureTextColorsGenerated();
        return mBodyTextColor;
    }

    private void ensureTextColorsGenerated() {
        if (!mGeneratedTextColors) {
            mTitleTextColor = ColorUtils.getTextColorForBackground(getRgb(),
                    MIN_CONTRAST_TITLE_TEXT);
            mBodyTextColor = ColorUtils.getTextColorForBackground(mLargeRgb,
                    MIN_CONTRAST_BODY_TEXT);
            mGeneratedTextColors = true;
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                " [RGB: #" + Integer.toHexString(getRgb()) + ']' +
                " [HSL: " + Arrays.toString(getHsl()) + ']' +
                " [Population: " + mLargePopulation + ']' +
                " [Title Text: #" + Integer.toHexString(mTitleTextColor) + ']' +
                " [Body Text: #" + Integer.toHexString(mBodyTextColor) + ']';
    }

    @Override
    public int hashCode() {
        return 31 * getRgb() + mLargePopulation;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o instanceof Palette.Swatch) {
            Palette.Swatch swatch = (Palette.Swatch) o;
            return mLargePopulation == swatch.getPopulation() && getRgb() == swatch.getRgb();
        } else if (o instanceof LargeSwatch) {
            LargeSwatch largeSwatch = (LargeSwatch) o;
            return mLargePopulation == largeSwatch.getPopulation() && getRgb() == largeSwatch.getRgb();
        }
        return false;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mRed);
        dest.writeInt(this.mGreen);
        dest.writeInt(this.mBlue);
        dest.writeInt(getRgb());
        dest.writeInt(this.mLargePopulation);
        dest.writeByte(mGeneratedTextColors ? (byte) 1 : (byte) 0);
        dest.writeInt(this.getTitleTextColor());
        dest.writeInt(this.getBodyTextColor());
        dest.writeFloatArray(this.getHsl());
    }
}