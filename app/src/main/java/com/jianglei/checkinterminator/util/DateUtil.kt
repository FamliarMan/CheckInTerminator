package com.jianglei.checkinterminator.util

import java.lang.StringBuilder

/**
 *@author longyi created on 19-5-14
 */
class DateUtil {
    companion object {
        private val dayMap = mapOf(
            "1" to "星期日",
            "2" to "星期一",
            "3" to "星期二",
            "4" to "星期三",
            "5" to "星期四",
            "6" to "星期五",
            "7" to "星期六"

        )


        fun getDayStr(dayInt: List<Int>): String {
            val res = StringBuilder()
            for (d in dayInt) {
                res.append(dayMap[d.toString()]).append(",")
            }
            if (res.length != 0) {
                res.deleteCharAt(res.length - 1)
            }
            return res.toString()
        }

        /**
         * 将[1,2,3]转成 [星期日，星期以，星期二]
         */
        fun getDayIntStr(dayInt: List<Int>): String {
            val res = StringBuilder()
            for (d in dayInt) {
                res.append(d.toString()).append(",")
            }
            if (res.length != 0) {
                res.deleteCharAt(res.length - 1)
            }
            return res.toString()
        }


        /**
         * 将"1,2,3"转成 [星期日，星期以，星期二]
         */
        fun getDayIntStr(weeks: String): String {
            val days = weeks.split(",")
            if (days.size == 1) {
                return ""
            }
            val dayInt = days
                .map {
                    it.toInt()
                }
            return getDayIntStr(dayInt)
        }
    }
}