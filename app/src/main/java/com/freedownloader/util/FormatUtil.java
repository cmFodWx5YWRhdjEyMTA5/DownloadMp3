package com.freedownloader.util;

/**
 * Created by liyanju on 2018/5/23.
 */

public class FormatUtil {

    /**
     * time的单位是秒
     *
     * @param durationString
     * @return
     */
    public static String formatMusicTime(String durationString) {
        try {
            final long duration = Long.parseLong(durationString) * 1000;
            return formatMusicTime(duration);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "00:00";
    }
    /**
     * duration单位毫秒
     *
     * @param duration
     * @return
     */
    public static String formatMusicTime(long duration) {
        String time = "";
        long minute = duration / 60000;
        long seconds = duration % 60000;
        long second = Math.round((int) seconds / 1000);
        if (minute < 10) {
            time += "0";
        }
        time += minute + ":";
        if (second < 10) {
            time += "0";
        }
        time += second;
        return time;
    }

}
