package nl.graaf.patricksresume.view.projects.magic8ball;

import android.test.ActivityInstrumentationTestCase2;

import java.util.Random;

import nl.graaf.patricksresume.views.projects.magic8ball.Magic8BallActivity;

public class Magic8BallTest extends ActivityInstrumentationTestCase2<Magic8BallActivity> {
    private Magic8BallActivity mActivity;

    public Magic8BallTest() {
        super(Magic8BallActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mActivity = getActivity();
    }

    public void testPreconditions() {
        assertNotNull(String.format("%s is null", Magic8BallActivity.class.getSimpleName()),
                mActivity);
        assertNotNull("Button Ask is null", mActivity.getButtonAsk());
        assertNotNull("Image with Ball is null", mActivity.getImageViewBall());
    }

    /**
     * Test if the ballImage is updated when a shake occurs. Unfortunately, we can't recreate a
     * Shake Event, so we can only call the listeners onShake() method.
     */
    public void testShakeListener() {
        getInstrumentation().runOnMainSync(() -> {
            final int NULL_TAG = -1;
            mActivity.getImageViewBall().setTag(NULL_TAG);
            if (mActivity.getListener() != null) {
                mActivity.getListener().onShake();
                try {
                    wait(mActivity.getAnimationDuration());
                    assertNotSame(NULL_TAG, mActivity.getImageViewBall().getTag());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    fail();
                }
            }
        });
    }

    /**
     * Test if the button updates the ballImage.
     */
    public void testAskButton() {
        getInstrumentation().runOnMainSync(() -> {
            Random randomGen = new Random();
            int number = randomGen.nextInt(mActivity.getBallsArrayLength());
            mActivity.updateBallView(number);
            assertEquals(mActivity.getFromBallsArray(number),
                    mActivity.getImageViewBall().getTag());
        });
    }
}
