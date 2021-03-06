package com.josef.tv.ui;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.leanback.app.GuidedStepSupportFragment;

/**
 * Created by kurt on 1/28/14 - 4:48 PM.
 */
public abstract class BaseGuidedStepFragment<T> extends GuidedStepSupportFragment {
    protected Activity activity;
    protected boolean visible;
    private T contract;

    @SuppressWarnings("unchecked")
    @Override
    public void onAttach(Context context) {
        this.activity = getActivity();
        try {
            contract = (T) activity;
        } catch (ClassCastException e) {
            throw new IllegalStateException(activity.getClass().getSimpleName()
                    + " does not implement " + getClass().getSimpleName() + "'s contract interface.", e);
        }
        super.onAttach(context);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        visible = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        visible = false;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        contract = null;
    }

    public final T getContract() {
        if (contract == null) {
            contract = (T) activity;
        }
        return contract;
    }
}
