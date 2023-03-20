package com.ecost.specter.support;

import static com.ecost.specter.Routing.authId;
import static com.ecost.specter.Routing.myDB;
import static com.ecost.specter.Routing.popup;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ecost.specter.R;
import com.ecost.specter.menu.SettingsMenuFragment;
import com.ecost.specter.models.FAQPost;
import com.ecost.specter.recyclers.FAQPostsAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.List;

public class FAQSupportFragment extends Fragment {

    FrameLayout senderMenu;
    List<FAQPost> faqPosts = new ArrayList<>();
    EditText eFAQText;
    LinearLayout bSendFAQPost;
    FAQPostsAdapter faqPostsAdapter;
    SupportActivity supportActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_faq_support, container, false);

        RecyclerView rFAQPostsList = inflaterView.findViewById(R.id.recycler_support_message_list);
        senderMenu = inflaterView.findViewById(R.id.sender_menu);
        eFAQText = inflaterView.findViewById(R.id.input_support_post);
        bSendFAQPost = inflaterView.findViewById(R.id.button_send);
        supportActivity = (SupportActivity) requireActivity();

        registerForContextMenu(bSendFAQPost);
        myDB.child("specter").child("users").child(authId.toString()).child("admin").get().addOnCompleteListener(task -> {
            if (Boolean.TRUE.equals(task.getResult().getValue(Boolean.class))) senderMenu.setVisibility(View.VISIBLE);
        });

        rFAQPostsList.setLayoutManager(new LinearLayoutManager(supportActivity));
        faqPostsAdapter = new FAQPostsAdapter(supportActivity, faqPosts, context -> {
            ((ClipboardManager) inflater.getContext().getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(ClipData.newPlainText("post", context));
            popup(supportActivity, requireView(), 2, getString(R.string.faq_support_post_copy));
            return false;
        });
        rFAQPostsList.setAdapter(faqPostsAdapter);

        ChildEventListener childEventListener = new ChildEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                faqPosts.add(snapshot.getValue(FAQPost.class));
                faqPostsAdapter.notifyDataSetChanged();
            }

            @Override public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override public void onChildRemoved(@NonNull DataSnapshot snapshot) {}
            @Override public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {}
            @Override public void onCancelled(@NonNull DatabaseError error) {}
        };
        myDB.child("specter").child("support").child("faq").addChildEventListener(childEventListener);

        inflaterView.findViewById(R.id.button_close).setOnClickListener(view -> supportActivity.finish());

        inflaterView.findViewById(R.id.button_contact_support).setOnClickListener(view -> supportActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new FormAppealSupportFragment()).addToBackStack(null).commit());

        inflaterView.findViewById(R.id.button_open_appeals).setOnClickListener(view -> supportActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new AppealsSupportFragment()).commit());

        bSendFAQPost.setOnClickListener(view -> {
             myDB.child("specter").child("support").child("faq").push().setValue(new FAQPost(2, eFAQText.getText().toString()));
             eFAQText.setText("");
        });

        return inflaterView;
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);
        menu.add(0, view.getId(), 0, R.string.faq_support_context_menu_item_send_question);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        myDB.child("specter").child("support").child("faq").push().setValue(new FAQPost(1, eFAQText.getText().toString()));
        eFAQText.setText("");
        return true;
    }

}