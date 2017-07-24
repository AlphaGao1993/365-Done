package com.alphagao.done365.ui.model;

import com.alphagao.done365.ui.view.DataListener;

import java.util.List;
import java.util.Map;

/**
 * Created by Alpha on 2017/4/26.
 */

public interface SearchModel {
    void execQueryForSearch(String str, DataListener<Map<String, List>> listener);
}
