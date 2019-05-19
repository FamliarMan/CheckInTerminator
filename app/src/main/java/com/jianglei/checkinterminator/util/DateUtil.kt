package com.jianglei.checkinterminator.util

import java.lang.StringBuilder

/**
 *@author longyi created on 19-5-14
 */
class DateUtil {
    companion object {
        private val dayIntToStrgMap = mapOf(
            "1" to "星期日",
            "2" to "星期一",
            "3" to "星期二",
            "4" to "星期三",
            "5" to "星期四",
            "6" to "星期五",
            "7" to "星期六"

        )
        private val dayStrToIntMap = mapOf(
            "星期日" to "1",
            "星期一" to "2",
            "星期二" to "3",
            "星期三" to "4",
            "星期四" to "5",
            "星期五" to "6",
            "星期六" to "7"

        )

        fun getDayStr(dayIntStr: String): String {
            val dayInt = dayIntStr.split(",")
                .map {
                    it.toInt()

                }.toList()
            return getDayStr(dayInt)
        }

        fun getDayStr(dayInt: List<Int>): String {
            val res = StringBuilder()
            for (d in dayInt) {
                res.append(dayIntToStrgMap[d.toString()]).append(",")
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
            val dayInt = days
                .map {
                    it.toInt()
                }
            return getDayIntStr(dayInt)
        }

        /**
         * 拼装18:00格式
         */
        fun getFormatTime(hour :Int,minute:Int):String{
            if(minute< 10){
                return ("$hour:0$minute")
            }
            return "$hour:$minute"
        }
    }
}