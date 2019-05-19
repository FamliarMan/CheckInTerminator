package com.jianglei.checkinterminator

import com.jianglei.checkinterminator.util.DateUtil
import org.junit.Assert
import org.junit.Test

/**
 * @author jianglei on 5/19/19.
 */
class DateUtilsTest {
    @Test
    fun getFormatTest(){
        Assert.assertEquals("18:00",DateUtil.getFormatTime(18,0))
    }
}