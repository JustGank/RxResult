package com.xjl.rxresult_x.fragment;


import android.content.Intent;

import com.xjl.rxresult_x.bean.ActivityResult;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class ActivityResultFragment extends Fragment {

    private static final String TAG = "ActivityResultFragment";

    private final BehaviorSubject<ActivityResult> mActivityResultSubject = BehaviorSubject.create();

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mActivityResultSubject.onNext(new ActivityResult(requestCode, resultCode, data));
    }

    public static Observable<ActivityResult> getActivityResultObservable(Fragment f) {
        FragmentManager fragmentManager = f.getChildFragmentManager();
        ActivityResultFragment fragment = (ActivityResultFragment) fragmentManager.findFragmentByTag(
                ActivityResultFragment.class.getCanonicalName());
        if (fragment == null) {
            fragment = new ActivityResultFragment();
            fragmentManager.beginTransaction()
                    .add(fragment, ActivityResultFragment.class.getCanonicalName())
                    .commit();
            fragmentManager.executePendingTransactions();
        }
        return fragment.mActivityResultSubject;
    }

    public static Observable<ActivityResult> getActivityResultObservable(FragmentActivity activity) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        ActivityResultFragment fragment = (ActivityResultFragment) fragmentManager.findFragmentByTag(
                ActivityResultFragment.class.getCanonicalName());
        if (fragment == null) {
            fragment = new ActivityResultFragment();
            fragmentManager.beginTransaction()
                    .add(fragment, ActivityResultFragment.class.getCanonicalName())
                    .commit();
            fragmentManager.executePendingTransactions();
        }
        return fragment.mActivityResultSubject;
    }

    public static void startActivityForResult(FragmentActivity activity, Intent intent, int requestCode) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        ActivityResultFragment fragment = (ActivityResultFragment) fragmentManager.findFragmentByTag(
                ActivityResultFragment.class.getCanonicalName());
        if (fragment == null) {
            fragment = new ActivityResultFragment();
            fragmentManager.beginTransaction()
                    .add(fragment, ActivityResultFragment.class.getCanonicalName())
                    .commit();
            fragmentManager.executePendingTransactions();
        }
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void startActivityForResult(Fragment f, Intent intent, int requestCode) {
        FragmentManager fragmentManager = f.getChildFragmentManager();
        ActivityResultFragment fragment = (ActivityResultFragment) fragmentManager.findFragmentByTag(
                ActivityResultFragment.class.getCanonicalName());
        if (fragment == null) {
            fragment = new ActivityResultFragment();
            fragmentManager.beginTransaction()
                    .add(fragment, ActivityResultFragment.class.getCanonicalName())
                    .commit();
            fragmentManager.executePendingTransactions();
        }
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void insertActivityResult(FragmentActivity activity, ActivityResult activityResult) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        ActivityResultFragment fragment = (ActivityResultFragment) fragmentManager.findFragmentByTag(
                ActivityResultFragment.class.getCanonicalName());
        if (fragment == null) {
            fragment = new ActivityResultFragment();
            fragmentManager.beginTransaction()
                    .add(fragment, ActivityResultFragment.class.getCanonicalName())
                    .commit();
            fragmentManager.executePendingTransactions();
        }
        fragment.mActivityResultSubject.onNext(activityResult);
    }

    public static void insertActivityResult(Fragment f, ActivityResult activityResult) {
        FragmentManager fragmentManager = f.getChildFragmentManager();
        ActivityResultFragment fragment = (ActivityResultFragment) fragmentManager.findFragmentByTag(
                ActivityResultFragment.class.getCanonicalName());
        if (fragment == null) {
            fragment = new ActivityResultFragment();
            fragmentManager.beginTransaction()
                    .add(fragment, ActivityResultFragment.class.getCanonicalName())
                    .commit();
            fragmentManager.executePendingTransactions();
        }
        fragment.mActivityResultSubject.onNext(activityResult);
    }

}
