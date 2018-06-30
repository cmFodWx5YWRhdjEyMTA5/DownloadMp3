package com.mp3downloader.musicgo;

import android.util.Log;

import com.facebook.ads.AudienceNetworkActivity;

import org.junit.Test;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.util.Calendar;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
//        assertEquals(4, 2 + 2);

//        String str = "utm_medium=(not%20set)";
//        str = URLDecoder.decode(str, "utf-8");
//        System.out.println(str.contains("not set"));
        try {

            URL url = new URL("https://www.google.com");
            URLConnection uc = url.openConnection();
            uc.setConnectTimeout(10 * 1000);
            uc.setReadTimeout(10 * 1000);
            uc.connect();

            long ld = uc.getDate();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(ld);
            int week = calendar.get(Calendar.DAY_OF_WEEK) - 1;//0代表周日，6代表周六
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int mi = calendar.get(Calendar.MINUTE);
            Log.v("XX", "XXX");
        } catch (Throwable e) {

        }

    }

    public void kuaipai(int a[],int l, int h) {
        if (l >= h) {
            return;
        }

        int i = p(a, l, h);
        kuaipai(a, l, i - 1);
        kuaipai(a, i + 1, h);
    }

    public int p(int a[], int l, int h) {
        int k = a[l];
        while (l < h) {
            while (a[h] >= k && h > l) {
                h--;
            }
            a[l] =a[h];
            while (a[l] <= k && h > l) {
                l--;
            }
            a[h] = a[l];
        }
        a[h] = k;
        return h;
    }

    public void maopao(int a[]) {
        boolean flag = false;
        int temp;
        for (int i = 0; i < a.length; i++) {
            flag = false;
            for (int j = 0; j < a.length - i -1; j++) {
                if (a[j] > a[j+1]) {
                    temp = a[j];
                    a[j] = a[j+1];
                    a[j+1] = temp;
                    flag = true;
                }
            }

            if (!flag) {
                return;
            }
        }
    }

    public boolean search(int a[], int k) {
        int left = 0;
        int right = a.length - 1;

        while (left <= right) {
            int m = (left + right)/2;
            if (a[m] > k) {
                right = m - 1;
            } else if (a[m] < k) {
                left = m + 1;
            } else if (a[m] == k){
                return true;
            }
        }

        return false;
    }
}