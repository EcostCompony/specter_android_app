package com.ecost.specter.channel;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ecost.specter.R;

public class CommentsFragment extends Fragment {

    ChannelActivity channelActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflaterView = inflater.inflate(R.layout.fragment_comments, container, false);

        channelActivity = (ChannelActivity) requireActivity();

        inflaterView.findViewById(R.id.button_close).setOnClickListener(view -> channelActivity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_view, new ChannelFragment()).commit());

        return inflaterView;
    }

}