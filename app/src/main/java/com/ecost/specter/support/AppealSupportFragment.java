package com.ecost.specter.support;

import static com.ecost.specter.BuildConfig.VERSION_CODE;
import static com.ecost.specter.Routing.authAdmin;
import static com.ecost.specter.Routing.authUserName;
import static com.ecost.specter.Routing.myDB;
import static com.ecost.specter.Routing.popup;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ecost.specter.R;
import com.ecost.specter.menu.MainMenuActivity;
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
    EditText ePost;
    LinearLayout bSendPost;
    List<FAQPost> appealPosts = new ArrayList<>();
    FAQPostsAdapter faqPostsAdapter;
    SupportActivity supportActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_appeal_support, container, false);

        tAppealTopic = inflaterView.findViewById(R.id.appeal_topic);
        rPostsList = inflaterView.findViewById(R.id.recycler_support_message_list);
        ePost = inflaterView.findViewById(R.id.input_support_post);
        bSendPost = inflaterView.findViewById(R.id.button_send);
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

        bSendPost.setOnClickListener(view -> {
            String text = ePost.getText().toString().trim();
            if (!text.equals("")) {
                myDB.child("specter").child("support").child("appeals").child(String.valueOf(supportActivity.appealId)).child("posts_number").get().addOnCompleteListener(task -> {
                    myDB.child("specter").child("support").child("appeals").child(String.valueOf(supportActivity.appealId)).child("body").setValue(text);
                    myDB.child("specter").child("support").child("appeals").child(String.valueOf(supportActivity.appealId)).child("posts").child(String.valueOf(task.getResult().getValue(Integer.class))).setValue(new FAQPost(authAdmin ? 2 : 1, text));
                    myDB.child("specter").child("support").child("appeals").child(String.valueOf(supportActivity.appealId)).child("posts_number").setValue(Objects.requireNonNull(task.getResult().getValue(Integer.class)) + 1);
                    rPostsList.smoothScrollToPosition(appealPosts.size());
                });
            }
            ePost.setText("");
        });

        return inflaterView;
    }

}