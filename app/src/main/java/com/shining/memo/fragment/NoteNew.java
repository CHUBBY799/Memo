package com.shining.memo.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shining.memo.R;

public class NoteNew extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.note_new, container, false);
    }
}