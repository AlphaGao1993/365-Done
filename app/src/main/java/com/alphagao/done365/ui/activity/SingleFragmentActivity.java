package com.alphagao.done365.ui.activity;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.alphagao.done365.R;
import com.alphagao.done365.ui.fragment.FragmentFactory;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Alpha on 2017/4/2.
 */

public class SingleFragmentActivity extends BaseActivity {
    public static final String FRAGMENT_PARAM = "fragment";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private Fragment fragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_fragment);
        ButterKnife.bind(this);

        setupToolbar();

        Bundle bundle = getIntent().getExtras();
        Class<?> fragmentClass = (Class<?>) bundle.get(FRAGMENT_PARAM);
        if (savedInstanceState == null) {
            fragment = FragmentFactory.create(this, fragmentClass);
            fragment.setArguments(bundle);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment, fragment, fragmentClass.getName())
                    .commit();
        }
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.arrow_back_white_24dp);
        }
    }

    @Override
    public void onNewMessage(Message msg) {

    }

    @Override
    protected void onDestroy() {
        fragment.onDestroy();
        super.onDestroy();
    }
}
