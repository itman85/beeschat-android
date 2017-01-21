package com.omebee.android.beeschat;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.omebee.android.beeschat.firebase.FirebaseHelper;
import com.omebee.android.beeschat.tab.SlidingTabLayout;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by phannguyen on 9/17/16.
 */
public class MainActivity extends AppCompatActivity {
    ViewPager mPager;
    SlidingTabLayout mTabs;
    Map<String,String> usersMap = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        mPager = (ViewPager)findViewById(R.id.pager);
        mTabs = (SlidingTabLayout)findViewById(R.id.tabs);
        mTabs.setCustomTabView(R.layout.custom_tab_view,R.id.tabText);
        mPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        mTabs.setDistributeEvenly(true);
        //  mTabs.setBackgroundColor(getResources().getColor(R.color.primary));
        mTabs.setSelectedIndicatorColors(getColorFromRes(R.color.primary));
        mTabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer(){

            @Override
            public int getIndicatorColor(int position) {
                return getColorFromRes(R.color.accentColor);
            }
        });
        mTabs.setViewPager(mPager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if(id== R.id.action_logout){
            FirebaseHelper.Instance().logout();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }
    private int getColorFromRes(int colorid){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            return getResources().getColor(colorid, null);
        else
            return getResources().getColor(colorid);
    }

    public Map<String, String> getUsersMap() {
        return usersMap;
    }

    public void setUsersMap(Map<String, String> usersMap) {
        this.usersMap = usersMap;
    }

    public String getUsername(String userId){
        if(this.usersMap!=null && this.usersMap.containsKey(userId)){
            return this.usersMap.get(userId);
        }else if(FirebaseHelper.Instance().getCurrentUserId().equals(userId)){
            return "You";
        }
        return null;
    }

    class MyPagerAdapter extends FragmentPagerAdapter {
        String[] tabText = getResources().getStringArray(R.array.tabs);
        int icons[] = {R.drawable.ic_action_personal, R.drawable.ic_action_articles};
        private FragmentRecentMessages mFragmentRecentMessages;
        private FragmentUserList mFragmentUserList;

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
            mFragmentRecentMessages = new FragmentRecentMessages();
            mFragmentUserList = new FragmentUserList();
        }

        @Override
        public Fragment getItem(int position) {
            if(position==0){
                return mFragmentUserList;
            }else{
                return mFragmentRecentMessages;
            }

        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Drawable drawable = getResources().getDrawable(icons[position]);
            Log.d("!!width", drawable.getIntrinsicWidth() + "");
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            ImageSpan imageSpan = new ImageSpan(drawable);
            SpannableString spannableString = new SpannableString(" ");
            spannableString.setSpan(imageSpan, 0, spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannableString;
            //return tabText[position];
        }
    }
}
