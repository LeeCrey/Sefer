package eth.social.like_button;

import android.widget.ImageView;

/**
 * @author varun on 07/07/16.
 */
public interface HeartEventListener {
    void onEvent(ImageView button, boolean buttonState);
    void onEventAnimationEnd(ImageView button,boolean buttonState);
    void onEventAnimationStart(ImageView button,boolean buttonState);
}
