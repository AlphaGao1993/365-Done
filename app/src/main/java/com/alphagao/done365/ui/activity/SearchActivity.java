package com.alphagao.done365.ui.activity;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alphagao.done365.R;
import com.alphagao.done365.adapter.AgendaEventAdapter;
import com.alphagao.done365.adapter.InboxEventAdapter;
import com.alphagao.done365.adapter.MemoEventAdapter;
import com.alphagao.done365.adapter.SearchHistoryAdapter;
import com.alphagao.done365.adapter.TodoEventAdapter;
import com.alphagao.done365.adapter.TrashEventAdapter;
import com.alphagao.done365.greendao.bean.AgendaEvent;
import com.alphagao.done365.greendao.bean.InboxEvent;
import com.alphagao.done365.greendao.bean.MemoEvent;
import com.alphagao.done365.greendao.bean.TodoEvent;
import com.alphagao.done365.ui.presenter.SearchPresenter;
import com.alphagao.done365.ui.view.SearchViewInterface;
import com.alphagao.done365.widget.StickyListView;
import com.alphagao.done365.widget.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Alpha on 2017/4/25.
 */

public class SearchActivity extends BaseActivity implements SearchViewInterface {
    private static final String TAG = "SearchActivity";

    @BindView(R.id.searchContent)
    EditText searchContent;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.searchHistoryLayout)
    LinearLayout searchHistoryLayout;
    @BindView(R.id.inboxResultIcon)
    ImageView inboxResultIcon;
    @BindView(R.id.inboxResultCount)
    TextView inboxResultCount;
    @BindView(R.id.inboxResultLayout)
    RelativeLayout inboxResultLayout;
    @BindView(R.id.inboxResultView)
    SwipeRecyclerView inboxResultView;
    @BindView(R.id.agendaResultIcon)
    ImageView agendaResultIcon;
    @BindView(R.id.agendaResultCount)
    TextView agendaResultCount;
    @BindView(R.id.agendaResultLayout)
    RelativeLayout agendaResultLayout;
    @BindView(R.id.agendaResultView)
    SwipeRecyclerView agendaResultView;
    @BindView(R.id.todoResultIcon)
    ImageView todoResultIcon;
    @BindView(R.id.todoResultCount)
    TextView todoResultCount;
    @BindView(R.id.todoResultLayout)
    RelativeLayout todoResultLayout;
    @BindView(R.id.todoResultView)
    SwipeRecyclerView todoResultView;
    @BindView(R.id.trashResultIcon)
    ImageView trashResultIcon;
    @BindView(R.id.trashResultCount)
    TextView trashResultCount;
    @BindView(R.id.trashResultLayout)
    RelativeLayout trashResultLayout;
    @BindView(R.id.trashResultView)
    SwipeRecyclerView trashResultView;
    @BindView(R.id.searchResultLayout)
    LinearLayout searchResultLayout;
    @BindView(R.id.searchHistoryView)
    StickyListView searchHistoryView;
    @BindView(R.id.clearSearchLayout)
    RelativeLayout clearSearchLayout;
    @BindView(R.id.memoResultIcon)
    ImageView memoResultIcon;
    @BindView(R.id.memoResultCount)
    TextView memoResultCount;
    @BindView(R.id.memoResultLayout)
    RelativeLayout memoResultLayout;
    @BindView(R.id.memoResultView)
    SwipeRecyclerView memoResultView;
    private List<String> historyStr;
    private SearchHistoryAdapter searchAdapter;
    private SearchPresenter mSearchPresenter;
    private String searchPattern = null;

    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        setupActionBar();
        init();
    }

    private void setupActionBar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.arrow_back_white_24dp);
        }
    }

    private void init() {
        initSearchHistory();
        mSearchPresenter = new SearchPresenter(this);

        searchContent.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    startSearch(v.getText().toString());
                }
                return true;
            }
        });
    }

    private void initSearchHistory() {
        Set<String> histories = userPrefs.getStringSet("searchHistory");
        historyStr = new ArrayList<>();
        if (histories != null) {
            historyStr.addAll(histories);
        }
        searchAdapter = new SearchHistoryAdapter(historyStr);
        searchHistoryView.setAdapter(searchAdapter);

        searchHistoryView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                searchContent.setText(historyStr.get(position));
                startSearch(historyStr.get(position));
            }
        });
        shouldHideSearchTip();
    }

    private void shouldHideSearchTip() {
        if (historyStr.size() > 0) {
            clearSearchLayout.setVisibility(View.VISIBLE);
        } else {
            clearSearchLayout.setVisibility(View.GONE);
        }
    }

    private void addSearchHistory(String str) {
        Set<String> set = new LinkedHashSet<>();
        set.addAll(historyStr);
        if (set.contains(str)) {
            set.remove(str);
        }
        set.add(str);
        historyStr.clear();
        historyStr.addAll(set);
        searchAdapter.notifyDataSetChanged();
        userPrefs.putStringSet("searchHistory", set);
        shouldHideSearchTip();
    }

    private void startSearch(String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                .hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        searchPattern = str;
        mSearchPresenter.searchFromDb(str);
        addSearchHistory(str);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_clear) {
            searchContent.setText("");
            shouldHideSearchTip();
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.inboxResultLayout, R.id.agendaResultLayout, R.id.todoResultLayout,
            R.id.trashResultLayout, R.id.clearSearchLayout, R.id.searchContent,
            R.id.memoResultLayout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.inboxResultLayout:
                switchViewVisibility(inboxResultView, inboxResultIcon);
                break;
            case R.id.agendaResultLayout:
                switchViewVisibility(agendaResultView, agendaResultIcon);
                break;
            case R.id.todoResultLayout:
                switchViewVisibility(todoResultView, todoResultIcon);
                break;
            case R.id.memoResultLayout:
                switchViewVisibility(memoResultView, memoResultIcon);
                break;
            case R.id.trashResultLayout:
                switchViewVisibility(trashResultView, trashResultIcon);
                break;
            case R.id.clearSearchLayout:
                clearSearchHistory();
                break;
            case R.id.searchContent:
                switchResultLayout(true);
                break;
            default:
                break;
        }
    }

    private void switchResultLayout(boolean showHistory) {
        searchHistoryLayout.setVisibility(showHistory ? View.VISIBLE : View.GONE);
        searchResultLayout.setVisibility(showHistory ? View.GONE : View.VISIBLE);
    }

    private void switchViewVisibility(SwipeRecyclerView view, ImageView icon) {
        view.setVisibility(view.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
        icon.setImageResource(view.getVisibility() ==
                View.VISIBLE ? R.mipmap.ic_arrow_down_grey : R.mipmap.ic_arrow_right_grey);
    }

    private void clearSearchHistory() {
        historyStr.clear();
        searchAdapter.notifyDataSetChanged();
        userPrefs.putStringSet("searchHistory", null);
        shouldHideSearchTip();
    }

    @Override
    public void onNewMessage(Message msg) {}

    @Override
    public void showResult(Map<String, List> resultMap) {
        switchResultLayout(false);
        showInboxResult(resultMap);
        showAgendaResult(resultMap);
        showTodoResult(resultMap);
        showMemoResult(resultMap);
        showTrashResult(resultMap);
    }

    private void showTrashResult(Map<String, List> resultMap) {
        List<InboxEvent> trashEvents = resultMap.get("trash");
        int count = trashEvents == null ? 0 : trashEvents.size();
        trashResultCount.setText("(" + count + ")");
        if (trashEvents == null) {
            return;
        }
        TrashEventAdapter trashAdapter = new TrashEventAdapter(trashEvents, searchPattern);
        trashResultView.setLayoutManager(new LinearLayoutManager(this));
        trashResultView.setAdapter(trashAdapter);
        trashResultIcon.setImageResource(R.mipmap.ic_arrow_down_grey);
    }

    private void showMemoResult(Map<String, List> resultMap) {
        List<MemoEvent> memoEvents = resultMap.get("memo");
        int count = memoEvents == null ? 0 : memoEvents.size();
        memoResultCount.setText("(" + count + ")");
        if (memoEvents == null) {
            return;
        }
        MemoEventAdapter memoAdapter = new MemoEventAdapter(this, memoEvents, searchPattern);
        memoResultView.setLayoutManager(new LinearLayoutManager(this));
        memoResultView.setAdapter(memoAdapter);
        memoResultIcon.setImageResource(R.mipmap.ic_arrow_down_grey);
    }

    private void showTodoResult(Map<String, List> resultMap) {
        List<TodoEvent> todoEvents = resultMap.get("todo");
        int count = todoEvents == null ? 0 : todoEvents.size();
        todoResultCount.setText("(" + count + ")");
        if (todoEvents == null) {
            return;
        }
        TodoEventAdapter todoAdapter = new TodoEventAdapter(todoEvents, searchPattern);
        todoResultView.setLayoutManager(new LinearLayoutManager(this));
        todoResultView.setAdapter(todoAdapter);
        todoResultIcon.setImageResource(R.mipmap.ic_arrow_down_grey);
    }

    private void showAgendaResult(Map<String, List> resultMap) {
        List<AgendaEvent> agendaEvents = resultMap.get("agenda");
        int count = agendaEvents == null ? 0 : agendaEvents.size();
        agendaResultCount.setText("(" + count + ")");
        if (agendaEvents == null) {
            return;
        }
        AgendaEventAdapter agendaAdapter = new AgendaEventAdapter(agendaEvents, 0, searchPattern);
        agendaResultView.setLayoutManager(new LinearLayoutManager(this));
        agendaResultView.setAdapter(agendaAdapter);
        agendaResultIcon.setImageResource(R.mipmap.ic_arrow_down_grey);
    }

    private void showInboxResult(Map<String, List> resultMap) {
        List<InboxEvent> inboxEvents = resultMap.get("inbox");
        int count = inboxEvents == null ? 0 : inboxEvents.size();
        inboxResultCount.setText("(" + count + ")");
        if (inboxEvents == null) {
            return;
        }
        InboxEventAdapter inboxAdapter = new InboxEventAdapter(inboxEvents, this, searchPattern);
        inboxResultView.setLayoutManager(new LinearLayoutManager(this));
        inboxResultView.setAdapter(inboxAdapter);
        inboxResultIcon.setImageResource(R.mipmap.ic_arrow_down_grey);
    }
}
