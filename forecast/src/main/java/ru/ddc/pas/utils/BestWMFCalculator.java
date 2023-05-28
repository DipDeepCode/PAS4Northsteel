package ru.ddc.pas.utils;

import java.util.*;

public class BestWMFCalculator extends Thread {
    private Object[] best_wmf;
    private final List<Float> price;
    private final Map<String, List<Float>> wmf;
    private final int point_price;
    private final int m;

    public BestWMFCalculator(List<Float> price, Map<String, List<Float>> wmf, int point_price, int m) {
        this.price = price;
        this.wmf = wmf;
        this.point_price = point_price;
        this.m = m;
    }

    @Override
    public void run() {
        best_wmf = best_wmf_1();
    }

    private Object[] best_wmf_1() {
        float cmo_min = 10;
        int point_wmf = 0;
        float min_price = 0;
        float max_price = 0;
        String sort_wmf = null;

//        System.out.println("price" + price);
//        System.out.println("price.length" + price.size());
//        System.out.println("point_price" + point_price);
//        System.out.println("m" + m);

        for (String key : wmf.keySet()) {
            Object[] result = identification_1(price, wmf.get(key), point_price, m);

//            System.out.println(result[1]);

            if ((float) result[1] < cmo_min) {
                cmo_min = (float) result[1];
                point_wmf = (int) result[2];
                min_price = (float) result[3];
                max_price = (float) result[4];
                sort_wmf = key;
            }
        }


        Object[] objects = {m, cmo_min, point_wmf, min_price, max_price, sort_wmf};

//        System.out.println(Arrays.toString(objects));

        return objects;
    }

    private Object[] identification_1(List<Float> price, List<Float> wmf, int point_price, int m) {
//        System.out.println(price);
        int start_price = point_price - m;
        List<Float> price_part = price.subList(start_price, point_price);

//        System.out.println("price_part" + price_part);

        float max_price = Collections.max(price_part);
        float min_price = Collections.min(price_part);
        List<Float> price_norm = normalize(price_part);

//        System.out.println("price_norm" + price_norm);

        int index = 0;
        int index_cmo_min = 1010;
        float cmo_min = 10;
        for (int i = 0; i <= wmf.size() - m; i++) {
            List<Float> row = wmf.subList(i, i + m);
            List<Float> wmf_norm = normalize(row);
            float cmo_value = cmo_1(price_norm, wmf_norm, m);

//            System.out.print(cmo_value + " - ");

            if (cmo_value < cmo_min) {
                cmo_min = cmo_value;
                index_cmo_min = index;
            }
            index++;
        }
//        System.out.println();

        Object[] objects = {m, cmo_min, index_cmo_min, min_price, max_price};

//        System.out.println(Arrays.toString(objects));

        return objects;
    }

    private float cmo_1(List<Float> price, List<Float> wmf, int length) {
        float cmo_mean = 0;
        for (int i = 0; i < length; i++) {
//            System.out.print(price.get(i));
            float abs = Math.abs(price.get(i) - wmf.get(i));
//            System.out.println(abs);
            cmo_mean += abs;
        }
        float v = cmo_mean / length;
//        System.out.print(length);
        return v;
    }

    private List<Float> normalize(List<Float> series) {
        float max = Collections.max(series);
        float min = Collections.min(series);

//        System.out.println(max + " - " + min);

        List<Float> list = series.stream().map(x -> (x - min) / (max - min)).toList();
//        System.out.println(list);
        return list;
    }

    public Object[] getBest_wmf() {
        return best_wmf;
    }
}
