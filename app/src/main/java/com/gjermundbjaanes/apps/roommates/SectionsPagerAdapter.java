package com.gjermundbjaanes.apps.roommates;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.gjermundbjaanes.apps.roommates.expenses.ExpensesFragment;
import com.gjermundbjaanes.apps.roommates.feed.FeedFragment;
import com.gjermundbjaanes.apps.roommates.me.MeFragment;
import com.gjermundbjaanes.apps.roommates.tasks.fragment.TaskListFragment;

import java.util.Locale;

import static com.gjermundbjaanes.apps.roommates.helpers.Constants.EXPENSES_INDEX;
import static com.gjermundbjaanes.apps.roommates.helpers.Constants.FEED_INDEX;
import static com.gjermundbjaanes.apps.roommates.helpers.Constants.ME_INDEX;
import static com.gjermundbjaanes.apps.roommates.helpers.Constants.NUMBER_OF_FRAGMENTS;
import static com.gjermundbjaanes.apps.roommates.helpers.Constants.TASKS_INDEX;

public class SectionsPagerAdapter extends FragmentPagerAdapter {
    private MainActivity mainActivity;
    private FeedFragment feedFragment;
    private MeFragment meFragment;
    private TaskListFragment taskListFragment;
    private ExpensesFragment expensesFragment;

    public SectionsPagerAdapter(MainActivity mainActivity, FragmentManager fm) {
        super(fm);
        this.mainActivity = mainActivity;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == FEED_INDEX) {
            if (feedFragment == null) {
                feedFragment = new FeedFragment();
            }
            return feedFragment;
        } else if (position == ME_INDEX) {
            if (meFragment == null) {
                meFragment = new MeFragment();
            }
            return meFragment;
        } else if (position == TASKS_INDEX) {
            if (taskListFragment == null) {
                taskListFragment = new TaskListFragment();
            }
            return taskListFragment;
        } else if (position == EXPENSES_INDEX) {
            if (expensesFragment == null) {
                expensesFragment = new ExpensesFragment();
            }
            return expensesFragment;
        } else {
            throw new IllegalArgumentException("Invalid section number");
        }
    }

    @Override
    public int getCount() {
        return NUMBER_OF_FRAGMENTS;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position) {
            case FEED_INDEX:
                return mainActivity.getString(R.string.title_section1).toUpperCase(l);
            case ME_INDEX:
                return mainActivity.getString(R.string.title_section2).toUpperCase(l);
            case TASKS_INDEX:
                return mainActivity.getString(R.string.title_section3).toUpperCase(l);
            case EXPENSES_INDEX:
                return mainActivity.getString(R.string.title_section4).toUpperCase();
        }
        return null;
    }
}
