package com.ecost.specter.support;

import static com.ecost.specter.Routing.myDB;
import static com.ecost.specter.Routing.popup;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ecost.specter.R;
import com.ecost.specter.models.FAQPost;
import com.ecost.specter.models.Post;
import com.ecost.specter.recyclers.FAQPostsAdapter;
import com.ecost.specter.recyclers.PostsAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.awt.font.TextAttribute;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AppealSupportFragment extends Fragment {

    TextView tAppealTopic;
    RecyclerView rPostsList;
    List<FAQPost> appealPosts = new ArrayList<>();
    FAQPostsAdapter faqPostsAdapter;
    SupportActivity supportActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_appeal_support, container, false);

        tAppealTopic = inflaterView.findViewById(R.id.appeal_topic);
        rPostsList = inflaterView.findViewById(R.id.recycler_support_message_list);
        supportActivity = (SupportActivity) requireActivity();

        tAppealTopic.setText(supportActivity.appealTopic);
        rPostsList.setLayoutManager(new LinearLayoutManager(supportActivity));
        faqPostsAdapter = new FAQPostsAdapter(supportActivity, appealPosts, context -> {
            ((ClipboardManager) inflater.getContext().getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("post", context));
            popup(supportActivity, requireView(), 2, getString(R.string.faq_support_post_copy));
            return false;
        });
        rPostsList.setAdapter(faqPostsAdapter);
        inflaterView.postDelayed(() -> rPostsList.scrollToPosition(appealPosts.size() - 1), 10);

        @SuppressLint("NotifyDataSetChanged")
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                FAQPost post = Objects.requireNonNull(dataSnapshot.getValue(FAQPost.class));
                appealPosts.add(post);
                faqPostsAdapter.notifyDataSetChanged();
                rPostsList.scrollToPosition(appealPosts.size());
            }

            @Override public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
            @Override public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String previousChildName) {}
            @Override public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String previousChildName) {}
            @Override public void onCancelled(@NonNull DatabaseError databaseError) {}
        };
        myDB.child("specter").child("support").child("appeals").child(String.valueOf(supportActivity.appealId)).child("posts").addChildEventListener(childEventListener);

        inflaterView.findViewById(R.id.button_close).setOnClickListener(view -> supportActivity.getSupportFragmentManager().popBackStack());

        return inflaterView;
    }

}