package com.example.minicapstone390.Views;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.minicapstone390.Controllers.Database;
import com.example.minicapstone390.R;


public class NotificationsFragment extends DialogFragment {

    // Initialize variables
    private final Database dB = new Database();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notifications, container, false);

        //TODO:  Add fields

        return view;
    }
}