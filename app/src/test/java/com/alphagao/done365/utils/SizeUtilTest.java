package com.alphagao.done365.utils;

import org.junit.Test;

import java.util.Calendar;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * Created by Alpha on 2017/4/18.
 */
public class SizeUtilTest {

    @Test
    public void getRightTime() throws Exception {
        assertThat(SizeUtil.getRightTime(36000000L), is("10 小时"));
        assertThat(SizeUtil.getRightTime(1800000), is("30 分钟"));
        assertThat(SizeUtil.getRightTime(30000), is("30 秒"));
    }

    @Test
    public void getTimeMills() throws Exception {
        assertThat(SizeUtil.getTimeMills(Calendar.DAY_OF_YEAR, 10), is(864000000L));
        assertThat(SizeUtil.getTimeMills(Calendar.HOUR, 10), is(36000000L));
        assertThat(SizeUtil.getTimeMills(Calendar.MINUTE, 10), is(600000L));
    }

    @Test
    public void getTimeMills1() throws Exception {
        assertThat(SizeUtil.getTimeMills("天", 10), is(864000000L));
        assertThat(SizeUtil.getTimeMills("小时", 10), is(36000000L));
        assertThat(SizeUtil.getTimeMills("分钟", 10), is(600000L));
    }

    @Test
    public void getTimeType() throws Exception {
        assertThat(SizeUtil.getTimeType("天"), is(Calendar.DAY_OF_YEAR));
        assertThat(SizeUtil.getTimeType("小时"), is(Calendar.HOUR));
        assertThat(SizeUtil.getTimeType("分钟"), is(Calendar.MINUTE));
        assertThat(SizeUtil.getTimeType("分"), is(0));
    }
}