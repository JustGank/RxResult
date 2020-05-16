package com.xjl.rx_result_x.util;

import com.xjl.rx_result_x.R;

import java.util.List;

import androidx.annotation.AnimRes;
import androidx.annotation.AnimatorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * 帮助载体Activity控制Fragment
 */
public class ProcessHelper {

    private AppCompatActivity activity;

    private int frameLayoutId;

    public ProcessHelper(AppCompatActivity activity, int frameLayoutId) {
        this.activity = activity;
        this.frameLayoutId = frameLayoutId;
    }

    @AnimatorRes
    @AnimRes
    private int enter = R.anim.sq_push_in_left;
    @AnimatorRes
    @AnimRes
    private int exit = R.anim.sq_push_out_left;
    @AnimatorRes
    @AnimRes
    private int popEnter = R.anim.sq_push_in_right;
    @AnimatorRes
    @AnimRes
    private int popExit = R.anim.sq_push_out_right;

    public void setFragmentAnimation(@AnimatorRes @AnimRes int enter,
                                     @AnimatorRes @AnimRes int exit,
                                     @AnimatorRes @AnimRes int popEnter,
                                     @AnimatorRes @AnimRes int popExit) {
        this.enter = enter;
        this.exit = exit;
        this.popEnter = popEnter;
        this.popExit = popExit;
    }

    public static <T extends Fragment> T findOrCreateFragment(@NonNull Class<T> fragmentClass, AppCompatActivity activity) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        T fragment = (T) fragmentManager.findFragmentByTag(fragmentClass.getClass().getCanonicalName());
        if (fragment == null) {
            try {
                fragment = fragmentClass.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return fragment;
    }

    public <T extends Fragment> T findOrCreateFragment(Class<T> fragmentClass) {
       return findOrCreateFragment(fragmentClass, this.activity);
    }


    public void push(Fragment fragment) {
        push(fragment, fragment.getClass().getCanonicalName());
    }

    public void push(Fragment fragment, String tag) {


        List<Fragment> currentFragments = activity.getSupportFragmentManager().getFragments();
        FragmentTransaction transaction = activity.getSupportFragmentManager().beginTransaction();
        if (currentFragments.size() != 0) {
            transaction.setCustomAnimations(
                    enter,
                    exit,
                    popEnter,
                    popExit
            );
        }
        transaction.add(frameLayoutId, fragment, tag);
        if (currentFragments.size() != 0) {
            transaction
                    .hide(currentFragments.get(currentFragments.size() - 1))
                    .addToBackStack(tag);
        }
        transaction.commit();
    }
}
