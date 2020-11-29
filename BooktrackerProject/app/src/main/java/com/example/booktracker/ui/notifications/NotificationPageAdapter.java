/**
*NotificationPageAdapter
* A FragmentPagerAdapter for fragments inside of fragments
* Handles the changes of fragments in the notifications tab
 */
package com.example.booktracker.ui.notifications;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class NotificationPageAdapter extends FragmentPagerAdapter {
    private int numOfTabs;

    public NotificationPageAdapter(@NonNull FragmentManager fm, int numOfTabs) {
        super(fm);
        this.numOfTabs = numOfTabs;
    }

    /**
     * Determines what tab to open when a tab is selected
     * @param position
     * @return Fragment clicked on position
     */
    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new NotificationMessagesTabFragment();
            case 1:
                return new NotificationRequestedTabFragment();
            default:
                return null;
        }
    }
    @Override
    public int getCount() {
        return numOfTabs;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}