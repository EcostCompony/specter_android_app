package com.ecost.specter.support;

import static com.ecost.specter.Routing.authAdmin;
import static com.ecost.specter.Routing.authId;
import static com.ecost.specter.Routing.myDB;
import static com.ecost.specter.Routing.pluralForm;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ecost.specter.R;
import com.ecost.specter.models.Appeal;
import com.ecost.specter.models.Channel;
import com.ecost.specter.recyclers.AppealsAdapter;
import com.ecost.specter.recyclers.ChannelsAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AppealsSupportFragment extends Fragment {

    List<Appeal> appeals = new ArrayList<>();
    AppealsAdapter appealsAdapter;
    SupportActivity supportActivity;

    @Override
    public void onStart() {
        super.onStart();
        appeals.clear();
        ChildEventListener childEventListener = new ChildEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                Appeal appeal = Objects.requireNonNull(dataSnapshot.getValue(Appeal.class));
                if (appeal.getAuthor().equals(Integer.parseInt(String.valueOf(authId))) || authAdmin) {
                    appeals.add(appeal);
                    appealsAdapter.notifyDataSetChanged();
                }
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                Appeal appeal = Objects.requireNonNull(dataSnapshot.getValue(Appeal.class));
                for (int i = 0; i < appealsAdapter.getItemCount(); i++) {
                    if (appeals.get(i).getId().equals(appeal.getId())) {
                        appeals.set(i, appeal);
                        appealsAdapter.notifyDataSetChanged();
                        break;
                    }
                }
            }

            @Override public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
            @Override public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String previousChildName) { }
            @Override public void onCancelled(@NonNull DatabaseError databaseError) { }
        };
        myDB.child("specter").child("support").child("appeals").addChildEventListener(childEventListener);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_appeals_support, container, false);

        RecyclerView rAppealsList = inflaterView.findViewById(R.id.recycler_appeals_list);
        supportActivity = (SupportActivity) requireActivity();

        rAppealsList.setLayoutManager(new LinearLayoutManager(supportActivity));
        appealsAdapter = new AppealsAdapter(supportActivity, appeals, (appeal, position) -> {
            supportActivity.appealTopic = appeal.getTopic();
            supportActivity.appealId = appeal.getId();
            supportActivity.appealAuthor = appeal.getAuthor();
            supportActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new AppealSupportFragment()).addToBackStack(null).commit();
        });
        rAppealsList.setAdapter(appealsAdapter);

        inflaterView.findViewById(R.id.button_close).setOnClickListener(view -> supportActivity.finish());

        inflaterView.findViewById(R.id.button_open_faq).setOnClickListener(view -> supportActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new FAQSupportFragment()).commit());

        return inflaterView;
    }

}