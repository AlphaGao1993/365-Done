package com.alphagao.done365.engines;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by Alpha on 2017/5/15.
 */
public class InboxAlarmEngineTest {
    @Test
    public void getTodayIntStr() throws Exception {
        assertThat(InboxAlarmEngine.getTodayLongStr(), is(2017515L));
        System.out.println(InboxAlarmEngine.getTodayLongStr());
    }

}