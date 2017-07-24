package com.alphagao.done365.ui.activity;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.alphagao.done365.R;
import com.alphagao.done365.engines.Account;
import com.alphagao.done365.engines.PastTodayEngine;
import com.alphagao.done365.greendao.DaoUtil;
import com.alphagao.done365.greendao.bean.AgendaEvent;
import com.alphagao.done365.greendao.bean.ContextEvent;
import com.alphagao.done365.greendao.bean.InboxEvent;
import com.alphagao.done365.greendao.bean.MemoEvent;
import com.alphagao.done365.greendao.bean.ProjectEvent;
import com.alphagao.done365.greendao.bean.TodoEvent;
import com.alphagao.done365.holder.ProjectTreeViewHolder;
import com.alphagao.done365.service.AlarmService;
import com.alphagao.done365.ui.fragment.AgendaDoneFragment;
import com.alphagao.done365.ui.fragment.AgendaFragment;
import com.alphagao.done365.ui.fragment.ContextFragment;
import com.alphagao.done365.ui.fragment.EventFragment;
import com.alphagao.done365.ui.fragment.FinishFragment;
import com.alphagao.done365.ui.fragment.FragmentFactory;
import com.alphagao.done365.ui.fragment.InboxFragment;
import com.alphagao.done365.ui.fragment.MemoFragment;
import com.alphagao.done365.ui.fragment.NewProjectFragment;
import com.alphagao.done365.ui.fragment.NewProjectSetFragment;
import com.alphagao.done365.ui.fragment.ProjectFragment;
import com.alphagao.done365.ui.fragment.ToDoFragment;
import com.alphagao.done365.ui.fragment.TodoDoneFragment;
import com.alphagao.done365.ui.fragment.TrashFragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity {
    private static final String TAG = "MainActivity";
    public static final int CHANGE_MENU_GROUP = 12501;
    public static final int REQUEST_NEW_AGENDA = 101;
    public static final int REQUEST_NEW_TODO = 102;
    public static final int REQUEST_NEW_MEMO = 103;
    public static final int REQUEST_NEW_PROJ = 104;
    public static final int REQUEST_NEW_PROJ_SET = 105;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    private InboxFragment inboxFragment;
    private EventFragment eventFragment;
    private MemoFragment memoFragment;
    private ContextFragment contextFragment;
    private FinishFragment finishFragment;
    private TrashFragment trashFragment;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private List<Fragment> fragmentList;
    private int fragPosition = 0;
    private MenuItem inboxItem;
    private int menuState;
    private List<String> fragments;
    private CircleImageView headImage;
    private SimpleDateFormat shortFormat;
    private TextView userNameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (prefs.getLong("current_login_user") <= 0) {
            jumpToLogin();
            return;
        }

        if (userPrefs != null && !userPrefs.getBoolean("login_succeed")) {
            jumpToLogin();
            return;
        }

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //全局第一次初始化存储真实数据的数据库,必须在用户登录之后，不存储 user 表
        DaoUtil.generateDatabase(getApplication(), String.valueOf(userPrefs.getLong("user_id")));
        initActivityView();
        initFragment();
        initFragmentName();
        handleNewIntent(getIntent());
        pushIntentToService();
        showPastToday();
    }

    private void jumpToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void showPastToday() {
        shortFormat = new SimpleDateFormat("MM-dd");

        boolean needClearTodayInbox = userPrefs.getBoolean("startPastToday")
                && !userPrefs.getString("pastToady").equals(shortFormat.format(new Date()));
        //当开启那年今日并且今天尚未提醒过则进行通知
        if (needClearTodayInbox) {

            List<AgendaEvent> pastAgendas = PastTodayEngine.getPastTodayAgendas();
            if (pastAgendas.size() > 0) {
                Intent i = new Intent(this, PastTodayActivity.class);
                PendingIntent pi = PendingIntent.getActivity(this, 0, i,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                Notification notification = new NotificationCompat
                        .Builder(this)
                        .setSmallIcon(android.R.drawable.ic_menu_report_image)
                        .setContentTitle(getString(R.string.past_today_title))
                        .setContentText(getString(R.string.past_today_content, pastAgendas.size()))
                        .setAutoCancel(true)
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI,
                                NotificationCompat.DEFAULT_SOUND)
                        .setLights(Color.GREEN, 500, 500)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(pi)
                        .build();
                NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
                managerCompat.notify((int) (System.currentTimeMillis() % Integer.MAX_VALUE),
                        notification);
                userPrefs.putString("pastToday", shortFormat.format(new Date()));
            }
        }
    }

    private void initFragmentName() {
        fragments = new ArrayList<>();
        fragments.add(InboxFragment.class.getName());
        fragments.add(AgendaFragment.class.getName());
        fragments.add(ToDoFragment.class.getName());
        fragments.add(ProjectFragment.class.getName());
        fragments.add(MemoFragment.class.getName());
        fragments.add(ContextFragment.class.getName());
        fragments.add(AgendaDoneFragment.class.getName());
        fragments.add(TodoDoneFragment.class.getName());
        fragments.add(TrashFragment.class.getName());
    }

    /**
     * 处理接受的 intent 对象
     */
    private void handleNewIntent(Intent intent) {
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && "text/plain".equals(type)) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            hideAllFragment(transaction);
            transaction.show(inboxFragment);
            transaction.commit();
            if (inboxItem != null) {
                navView.setCheckedItem(inboxItem.getItemId());
            }
            fragPosition = 0;
            toolbar.setTitle(R.string.main_nav_inbox);

            Message msg = Message.obtain();
            msg.what = InboxEvent.INBOX_ADD;
            msg.obj = intent.getStringExtra(Intent.EXTRA_TEXT);
            messageManager.publishMessage(msg);
        }
    }

    private void initFragment() {
        //预加载所有fragment
        fragmentList = new ArrayList<>();
        inboxFragment = (InboxFragment) FragmentFactory.create(this, InboxFragment.class);
        eventFragment = (EventFragment) FragmentFactory.create(this, EventFragment.class);
        memoFragment = (MemoFragment) FragmentFactory.create(this, MemoFragment.class);
        contextFragment = (ContextFragment) FragmentFactory.create(this, ContextFragment.class);
        finishFragment = (FinishFragment) FragmentFactory.create(this, FinishFragment.class);
        trashFragment = (TrashFragment) FragmentFactory.create(this, TrashFragment.class);
        fragmentList.add(inboxFragment);
        fragmentList.add(eventFragment);
        fragmentList.add(memoFragment);
        fragmentList.add(contextFragment);
        fragmentList.add(finishFragment);
        fragmentList.add(trashFragment);
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.content_frame, inboxFragment);
        transaction.add(R.id.content_frame, eventFragment);
        transaction.add(R.id.content_frame, memoFragment);
        transaction.add(R.id.content_frame, contextFragment);
        transaction.add(R.id.content_frame, finishFragment);
        transaction.add(R.id.content_frame, trashFragment);
        hideAllFragment(transaction);
        transaction.show(inboxFragment);
        fragPosition = 0;
        transaction.commitAllowingStateLoss();
    }

    private void initActivityView() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        //显示导航按钮和图片
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.toolbar_menu_white_36dp);
            actionBar.setTitle(getString(R.string.main_nav_inbox));
        }
        navView.setCheckedItem(R.id.nav_inbox);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_inbox:
                        inboxItem = item;
                        switchFragment(inboxFragment, item.getTitle());
                        break;
                    case R.id.nav_event:
                        switchFragment(eventFragment, item.getTitle());
                        break;
                    case R.id.nav_memo:
                        switchFragment(memoFragment, item.getTitle());
                        break;
                    case R.id.nav_context:
                        switchFragment(contextFragment, item.getTitle());
                        break;
                    case R.id.nav_finish:
                        switchFragment(finishFragment, item.getTitle());
                        break;
                    case R.id.nav_trash:
                        switchFragment(trashFragment, item.getTitle());
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //为侧滑面板添加状态监听
        if (drawerLayout != null) {
            drawerLayout.addDrawerListener(drawerToggle);
        }
        //开启状态监听以执行导航动画
        drawerToggle.syncState();
        initHeadView();
    }

    private void initHeadView() {
        headImage = (CircleImageView) navView.getHeaderView(0).findViewById(R.id.head_image);
        userNameView = (TextView) navView.getHeaderView(0).findViewById(R.id.head_username);

        loadHeadView();

        headImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });


    }

    private void loadHeadView() {
        long userId = userPrefs.getLong("user_id");
        userNameView.setText(userPrefs.getString("user_name"));
        Glide.with(this).load(getFilesDir() + "/head_img_" + userId + ".jpg")
                .signature(new StringSignature(UUID.randomUUID().toString()))
                .error(R.drawable.m06)
                .into(headImage);
    }

    public void switchFragment(Fragment fragment, CharSequence activityTitle) {
        FragmentTransaction switchFragmentAction = fragmentManager.beginTransaction();
        hideAllFragment(switchFragmentAction);

        if (fragment == null) {
            Class<? extends Fragment> fragmentClass = fragment.getClass();
            fragment = FragmentFactory
                    .create(MainActivity.this, fragmentClass);
            switchFragmentAction.add(R.id.content_frame, fragment);
        } else {
            switchFragmentAction.show(fragment);
        }

        switchFragmentAction.commitAllowingStateLoss();
        toolbar.setTitle(activityTitle);
        drawerLayout.closeDrawers();
    }

    private void hideAllFragment(FragmentTransaction action) {
        for (Fragment fragment : fragmentList) {
            if (fragment != null) {
                action.hide(fragment);
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        switch (menuState) {
            case 0:
                menu.setGroupVisible(R.id.group_inbox, true);
                break;
            case 1:
                menu.setGroupVisible(R.id.group_agenda, true);
                break;
            case 2:
                menu.setGroupVisible(R.id.group_todo, true);
                break;
            case 3:
                menu.setGroupVisible(R.id.group_project, true);
                break;
            case 4:
                menu.setGroupVisible(R.id.group_memo, true);
                break;
            case 5:
                menu.setGroupVisible(R.id.group_context, true);
                break;
            case 6:
                menu.setGroupVisible(R.id.group_agendaDone, true);
                break;
            case 7:
                menu.setGroupVisible(R.id.group_todoDone, true);
                break;
            case 8:
                menu.setGroupVisible(R.id.group_trash, true);
                break;
            default:
                break;
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addProj:
                callAddProject();
                break;
            case R.id.newProFolder:
                callAddNewProSet();
                break;
            case R.id.agenda_sort:
                callAgendaSort();
                break;
            case R.id.todo_sort:
                callTodoSort();
                break;
            case R.id.agenda_calendar_today:
                callToTodayCalendar();
                break;
            case R.id.agenda_calendar_week:
                callToWeekCalendar();
                break;
            case R.id.agenda_calendar_month:
                callToMonthCalendar();
                break;
            case R.id.agenda_canlendar_list:
                callToListCalendar();
                break;
            case R.id.context_add:
                callToAddContext();
                break;
            case R.id.search:
                callToSerach();
                break;
            case R.id.sync:
                syncAllData();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void syncAllData() {
        // TODO: 2017/5/12 该方法只是模拟网络数据同步，在集成云存储以后需要重写该方法
        toast("正在同步数据中...");
        new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    toast("数据同步完成");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void callToSerach() {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    private void callToAddContext() {
        Message msg = Message.obtain();
        msg.what = ContextEvent.CONTEXT_ADD;
        messageManager.publishMessage(msg);
    }

    private void callToListCalendar() {
        Message msg = Message.obtain();
        msg.what = AgendaEvent.AGENDA_CALENDAR_LIST;
        messageManager.publishMessage(msg);
    }

    private void callToMonthCalendar() {
        Message msg = Message.obtain();
        msg.what = AgendaEvent.AGENDA_CALENDAR_MONTH;
        messageManager.publishMessage(msg);
    }

    private void callToWeekCalendar() {
        Message msg = Message.obtain();
        msg.what = AgendaEvent.AGENDA_CALENDAR_WEEK;
        messageManager.publishMessage(msg);
    }

    private void callToTodayCalendar() {
        Message msg = Message.obtain();
        msg.what = AgendaEvent.AGENDA_CALENDAR_TODAY;
        messageManager.publishMessage(msg);
    }

    private void callTodoSort() {
        Message msg = Message.obtain();
        msg.what = TodoEvent.TODO_SORT;
        messageManager.publishMessage(msg);
    }

    private void callAgendaSort() {
        Message msg = Message.obtain();
        msg.what = AgendaEvent.SORT;
        messageManager.publishMessage(msg);
    }

    private void callAddNewProSet() {
        Intent intent = new Intent(this, SingleFragmentActivity.class);
        intent.putExtra(SingleFragmentActivity.FRAGMENT_PARAM,
                NewProjectSetFragment.class);
        startActivityForResult(intent, REQUEST_NEW_PROJ_SET);
    }

    private void callAddProject() {
        Intent intent = new Intent(this, SingleFragmentActivity.class);
        intent.putExtra(SingleFragmentActivity.FRAGMENT_PARAM,
                NewProjectFragment.class);
        intent.putExtra("mode", ProjectTreeViewHolder.MODE_SELECT);
        startActivityForResult(intent, REQUEST_NEW_PROJ);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout != null) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleNewIntent(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("fragPosition", fragPosition);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        fragPosition = savedInstanceState.getInt("fragPosition");
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideAllFragment(transaction);
        transaction.show(fragmentList.get(fragPosition));
        transaction.commit();
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_NEW_AGENDA:
                    //更新日程表数据
                    Message msg2 = Message.obtain();
                    msg2.what = AgendaEvent.AGENDA_UPDATE;
                    messageManager.publishMessage(msg2);
                    break;
                case REQUEST_NEW_TODO:
                    //更新待办列表
                    Message msg4 = Message.obtain();
                    msg4.what = TodoEvent.TODO_UPDATE;
                    messageManager.publishMessage(msg4);
                    break;
                case REQUEST_NEW_MEMO:
                    //更新备忘列表
                    Message msg6 = Message.obtain();
                    msg6.what = MemoEvent.MEMO_UPDATE;
                    messageManager.publishMessage(msg6);
                    break;
                //添加项目、添加项目集打开同一个页面
                case REQUEST_NEW_PROJ:
                case REQUEST_NEW_PROJ_SET:
                    Message msg = Message.obtain();
                    msg.what = ProjectEvent.PROJECT_NEW;
                    messageManager.publishMessage(msg);
                    break;
                default:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onNewMessage(Message msg) {
        switch (msg.what) {
            case CHANGE_MENU_GROUP:
                changeMenuGroup(msg);
                break;
            case Account.HEAD_IMAGE_UPDATE:
                loadHeadView();
            default:
                break;
        }
        invalidateOptionsMenu();
    }

    private void changeMenuGroup(Message msg) {
        String className = (String) msg.obj;
        menuState = fragments.indexOf(className);
        invalidateOptionsMenu();
    }

    private void pushIntentToService() {
        if (userPrefs.getBoolean("startAgendaAlarm")) {
            Intent intent = AlarmService.newIntent(this);
            startService(intent);
            AlarmService.setServiceAlarm(this, true);
        } else {
            AlarmService.setServiceAlarm(this, false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        userNameView.setText(userPrefs.getString("user_name"));
    }
}
