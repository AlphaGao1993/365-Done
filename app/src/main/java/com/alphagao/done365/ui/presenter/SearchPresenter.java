package com.alphagao.done365.ui.presenter;

import com.alphagao.done365.ui.model.SearchModel;
import com.alphagao.done365.ui.model.SearchModelImpl;
import com.alphagao.done365.ui.view.DataListener;
import com.alphagao.done365.ui.view.SearchViewInterface;

import java.util.List;
import java.util.Map;

/**
 * Created by Alpha on 2017/4/26.
 */

public class SearchPresenter {

    SearchViewInterface mSearchView;
    SearchModel mSearchModel;

    public SearchPresenter(SearchViewInterface mSearchView) {
        this.mSearchView = mSearchView;
        mSearchModel = new SearchModelImpl();
    }

    public void searchFromDb(String str) {
        mSearchModel.execQueryForSearch(str, new DataListener<Map<String, List>>() {
            @Override
            public void onComplete(Map<String, List> result) {
                mSearchView.showResult(result);
            }
        });
    }
}
