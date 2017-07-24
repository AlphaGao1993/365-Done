package com.alphagao.done365.widget;

import com.alphagao.done365.R;
import com.alphagao.done365.app.App;

import cn.aigestudio.datepicker.bizs.themes.DPCNTheme;

/**
 * Created by Alpha on 2017/3/13.
 * App 全局的日期展示视图使用同一个主题，for 农历日期选择器
 */

public class MyDateTheme extends DPCNTheme {

    @Override
    public int colorBG() {
        return 0xFFFFFFFF;
    }

    /*@Override
    public int colorBGCircle() {
        return App.getAppContext().getResources().getColor(R.color.date_picker_bg);
    }*/

    @Override
    public int colorTitleBG() {
        return App.getAppContext().getResources().getColor(R.color.colorPrimary);
    }

    @Override
    public int colorTitle() {
        return App.getAppContext().getResources().getColor(R.color.colorTitleText);
    }

    @Override
    public int colorToday() {
        return App.getAppContext().getResources().getColor(R.color.colorDivider);
    }

    @Override
    public int colorG() {
        return App.getAppContext().getResources().getColor(R.color.colorOrdinaryText);
    }

    @Override
    public int colorF() {
        return App.getAppContext().getResources().getColor(R.color.date_picker_rest_day);
    }

    @Override
    public int colorWeekend() {
        return App.getAppContext().getResources().getColor(R.color.date_picker_rest_day);
    }

    @Override
    public int colorHoliday() {
        return App.getAppContext().getResources().getColor(R.color.colorDivider);
    }
}
