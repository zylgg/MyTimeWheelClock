package com.example.admin.mytimewheelclock.utils;

import android.text.SpannableStringBuilder;

public class NumberToChineseUtils {
    /**
     * 将数字转换为汉字
     *
     * @param number
     * @return
     */
    public static String intToChinese(int number) {
        String value = String.valueOf(number);
        SpannableStringBuilder spannable = new SpannableStringBuilder();
        int length = value.length();
        if (length > 1) {
            /**
             *  对于长度大于1的数，对首位进行赋值；
             *  对于两位数： 如果首位为“1”，则拼接的字符串为“”；
             */
            spannable.append(getChinese(number / (int) Math.pow(10, length - 1), length))
                    .append(getUnitChinese(length));
            // 如果该数值取余数为0，则直接返回已有字符（例如：100，直接返回一百）
            if (number % (int) Math.pow(10, length - 1) == 0) {
                return spannable.toString();
            }
        }
        // 数字为一位数
        if (length == 1) {
            spannable.append(getChinese(number, 1));
        }
        // 数字为两位数
        if (length == 2) {
            // 拼接个位的数值： 如果各位为“0”，则拼接的字符串为“”;
            spannable.append(getChinese(number % 10, 0));
        }
        // 数字为三位数
        if (length == 3) {
            if (number % 100 < 10) {
                spannable.append("零")
                        .append(getChinese(number % 100, 3));
            } else {
                spannable.append(getChinese(number % 100 / 10, 3))
                        .append("十")
                        .append(getChinese(number % 10, 0));
            }
        }
        // 数字为四位数
        if (length == 4) {
            if (number % 1000 < 10) {
                spannable.append("零").append(getChinese(number % 1000, 3));
            } else if (number % 1000 < 100) {
                spannable.append("零")
                        .append(getChinese(number % 1000 / 10, 3))
                        .append("十")
                        .append(getChinese(number % 10, 0));
            } else {
                number = number % 1000;
                spannable.append(intToChinese(number));
            }
        }
        return spannable.toString();
    }

    /**
     * 根据不同的情况获取对应的中文
     *
     * @param key
     * @param length
     * @return
     */

    public static String getChinese(int key, int length) {
        switch (key) {
            case 1:
                if (length == 2) {
                    return "";
                }
                return "一";
            case 2:
                return "二";
            case 3:
                return "三";
            case 4:
                return "四";
            case 5:
                return "五";
            case 6:
                return "六";
            case 7:
                return "七";
            case 8:
                return "八";
            case 9:
                return "九";
            case 0:
                if (length == 1) {
                    return "零";
                }
                return "";

        }
        return "";
    }

    /**
     * 根据数字的位数返回最大位数的单位
     *
     * @param length
     * @return
     */
    public static String getUnitChinese(int length) {
        switch (length) {
            case 2:
                return "十";
            case 3:
                return "百";
            case 4:
                return "千";
        }
        return "";
    }
}
