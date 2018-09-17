package com.racoders.racodersproject.classes;

import android.animation.Animator;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.racoders.racodersproject.R;

public class MapLocationToggleHandler {

    private static Context context;

    public static void setContext(Context mContext){
        context = mContext;
    }

    public static boolean toggleAll(final TextView allTextView, final TextView favTextView, final View animationRadioButton, boolean isFav){

        if(isFav){
            isFav = !isFav;
            final float scale = context.getResources().getDisplayMetrics().density;
            animationRadioButton.animate().translationXBy(96*scale).setDuration(150).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    animationRadioButton.setEnabled(false);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    allTextView.setTextColor(context.getResources().getColor(R.color.white));
                    favTextView.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
                    animationRadioButton.setEnabled(true);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        return isFav;
    }

    public static boolean toggleFavorite(final TextView allTextView, final TextView favTextView, final View animationRadioButton, boolean isFav){

        if(!isFav){
            isFav = !isFav;
            final float scale = context.getResources().getDisplayMetrics().density;
            animationRadioButton.animate().translationXBy(-96*scale).setDuration(150).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    animationRadioButton.setEnabled(false);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    allTextView.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
                    favTextView.setTextColor(context.getResources().getColor(R.color.white));
                    animationRadioButton.setEnabled(true);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        return isFav;
    }


}
