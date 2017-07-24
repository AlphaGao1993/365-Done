package com.alphagao.done365.ui.fragment;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.alphagao.done365.R;
import com.alphagao.done365.greendao.DaoUtil;
import com.alphagao.done365.greendao.bean.MemoEvent;
import com.alphagao.done365.greendao.gen.MemoEventDao;
import com.alphagao.done365.holder.MemoFolderTreeViewHolder;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Alpha on 2017/4/3.
 */

public class NewMemoryFolderFragment extends BaseFragment {


    @BindView(R.id.memo_dir_name)
    EditText memoDirName;
    @BindView(R.id.has_Parent_Folder)
    Switch hasParentFolder;
    @BindView(R.id.memoTreeLayout)
    LinearLayout memoTreeLayout;
    @BindView(R.id.addNewMemoFolder)
    Button addNewMemoFolder;
    Unbinder unbinder;
    private MemoEventDao memoEventDao;
    private MemoEvent checkedEvent;
    private static ImageView lastCheckedView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_memo_folder, null, false);
        unbinder = ButterKnife.bind(this, view);
        initToolbar(R.string.new_memo_folder_label);
        setHasOptionsMenu(true);

        memoEventDao = DaoUtil.daoSession.getMemoEventDao();
        List<MemoEvent> memoFolders = updateRootMemoFolder();

        TreeNode root = TreeNode.root();
        fillTreeData(root, memoFolders);

        AndroidTreeView treeView = new AndroidTreeView(getActivity(), root);
        treeView.setDefaultAnimation(true);
        treeView.setDefaultViewHolder(MemoFolderTreeViewHolder.class);
        treeView.setDefaultContainerStyle(R.style.TreeNodeStyle);
        treeView.setDefaultNodeClickListener(nodeClickListener);
        memoTreeLayout = (LinearLayout)view. findViewById(R.id.memoTreeLayout);
        memoTreeLayout.addView(treeView.getView());


        hasParentFolder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                memoTreeLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });
        return view;
    }

    private List<MemoEvent> updateRootMemoFolder() {
        return memoEventDao.queryBuilder()
                    .where(memoEventDao.queryBuilder().and(MemoEventDao.Properties.DirID.eq(0)
                            , MemoEventDao.Properties.IsDir.eq(MemoEvent.YES)))
                    .build()
                    .list();
    }

    private void fillTreeData(TreeNode root, List<MemoEvent> memoEvents) {
        for (MemoEvent event : memoEvents) {
            MemoFolderTreeViewHolder.TreeItem item =
                    new MemoFolderTreeViewHolder.TreeItem(0, event);
            TreeNode node = new TreeNode(item);
            root.addChild(node);
            List<MemoEvent> events = memoEventDao.queryBuilder()
                    .where(memoEventDao.queryBuilder()
                            .and(MemoEventDao.Properties.DirID.eq(event.getId())
                                    , MemoEventDao.Properties.IsDir.eq(MemoEvent.YES)))
                    .build()
                    .list();
            if (events.size() > 0) {
                fillTreeData(node, events);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.addNewMemoFolder)
    public void onViewClicked() {
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (!TextUtils.isEmpty(memoDirName.getText().toString())) {
            try {
                if (checkedEvent != null) {
                    memoEventDao.insert(
                            new MemoEvent(null,
                                    memoDirName.getText().toString(),
                                    checkedEvent.getId(),
                                    MemoEvent.YES,
                                    formater.format(new Date()),
                                    MemoEvent.NO,
                                    null));
                } else {
                    memoEventDao.insert(new MemoEvent(null,
                            memoDirName.getText().toString(),
                            0L,
                            MemoEvent.YES,
                            formater.format(new Date()),
                            MemoEvent.NO,
                            null));
                }
                toast("添加成功！");
                getActivity().setResult(RESULT_OK);
                getActivity().finish();
            } catch (Exception e) {
                toast("添加失败");
            }
        }
    }

    private TreeNode.TreeNodeClickListener nodeClickListener
            = new TreeNode.TreeNodeClickListener() {
        @Override
        public void onClick(TreeNode node, Object value) {
            if (lastCheckedView != null) {
                lastCheckedView.setImageDrawable(null);
            }
            MemoFolderTreeViewHolder.TreeItem item = (MemoFolderTreeViewHolder.TreeItem) value;
            ImageView imageView = (ImageView) node.getViewHolder().getView()
                    .findViewById(R.id.folderChecked);
            imageView.setImageResource(R.mipmap.ic_checked);
            checkedEvent = item.event;
            lastCheckedView = imageView;
        }
    };

    @Override
    public void onNewMessage(Message msg) {
    }
}
