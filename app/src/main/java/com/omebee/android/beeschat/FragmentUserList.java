package com.omebee.android.beeschat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.omebee.android.beeschat.firebase.FirebaseHelper;
import com.parse.FindCallback;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by phannguyen on 9/17/16.
 */
public class FragmentUserList extends Fragment {

    private String currentUserId;
    private ArrayAdapter<String> namesArrayAdapter;
    private ArrayList<String> names;
    private ListView usersListView;
    private MainActivity mMainActivity;
    private View mRootView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        if(mRootView==null) {
            mRootView = inflater.inflate(R.layout.fragment_users_list, container, false);
        }
        mMainActivity = (MainActivity) this.getActivity();
        return mRootView;
    }

    @Override
    public void onResume() {
        if(namesArrayAdapter == null || names == null) {
            setConversationsList();
        }
        super.onResume();

    }

    //display clickable a list of all users
    private void setConversationsList() {
        currentUserId = FirebaseHelper.Instance().getCurrentUserId();
        if(currentUserId==null)
            return;
        names = new ArrayList<String>();

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("objectId", currentUserId);
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> userList, com.parse.ParseException e) {
                if (e == null) {
                    for (int i=0; i<userList.size(); i++) {
                        names.add(userList.get(i).getUsername().toString());
                        mMainActivity.getUsersMap().put(userList.get(i).getObjectId(),userList.get(i).getUsername());
                    }

                    usersListView = (ListView)mRootView.findViewById(R.id.usersListView);
                    namesArrayAdapter =
                            new ArrayAdapter<String>(mRootView.getContext(),
                                    R.layout.user_list_item, names);
                    usersListView.setAdapter(namesArrayAdapter);

                    usersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> a, View v, int i, long l) {
                            openConversation(names, i);
                        }
                    });

                } else {
                    Toast.makeText(mRootView.getContext(),
                            "Error loading user list",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //open a conversation with one person
    public void openConversation(ArrayList<String> names, int pos) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", names.get(pos));
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> user, com.parse.ParseException e) {
                if (e == null) {
                    Intent intent = new Intent(mRootView.getContext(), ChattingActivity.class);
                    intent.putExtra("RECIPIENT_ID", user.get(0).getObjectId());
                    intent.putExtra("RECIPIENT_NAME", user.get(0).getUsername());
                    startActivity(intent);
                } else {
                    Toast.makeText(mRootView.getContext(),
                            "Error finding that user",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
