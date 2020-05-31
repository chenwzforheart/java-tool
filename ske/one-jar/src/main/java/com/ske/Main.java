package com.ske;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * @author csh9016
 * @date 2020/5/19
 */
public class Main {

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            System.out.println(StringUtils.wrap("one jar", "***"));
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
