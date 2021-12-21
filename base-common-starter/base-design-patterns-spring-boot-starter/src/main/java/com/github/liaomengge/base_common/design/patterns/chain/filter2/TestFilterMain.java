package com.github.liaomengge.base_common.design.patterns.chain.filter2;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by liaomengge on 2021/6/11.
 */
public class TestFilterMain {

    private static Filter<String, String> rootFilter;

    public static void main(String[] args) throws Throwable {
        A a = new A();
        B b = new B();
        C c = new C();
        //a.setNextFilter(b).setNextFilter(c);
        //String result = a.filter("x");
        //System.out.println("result ==> " + result);

        List<Filter<String, String>> filters = Lists.newArrayList();
        filters.add(a);
        filters.add(b);
        filters.add(c);
        init(filters);
        String result = rootFilter.filter("x");
        System.out.println("result ==> " + result);
    }

    private static void init(List<Filter<String, String>> filters) {
        for (int i = 0; i < filters.size(); i++) {
            if (i == 0) {
                rootFilter = filters.get(0);
            } else {
                Filter currentFilter = filters.get(i - 1);
                Filter nextFilter = filters.get(i);
                currentFilter.setNextFilter(nextFilter);
            }
        }
    }

    public static class A extends Filter<String, String> {

        @Override
        public String doFilter(String s) throws Throwable {
            System.out.println("A.doFilter");
            return s + 'A';
        }

        //@Override
        //public boolean skip(String s) {
        //    return s.length() == 1;
        //}
    }

    public static class B extends Filter<String, String> {

        @Override
        public String doFilter(String s) throws Throwable {
            System.out.println("B.doFilter");
            return s + 'B';
        }

        //@Override
        //public boolean skip(String s) {
        //    return s.length() == 1;
        //}
    }

    public static class C extends Filter<String, String> {

        @Override
        public String doFilter(String s) throws Throwable {
            System.out.println("C.doFilter");
            return s + 'C';
        }

        //@Override
        //public boolean skip(String s) {
        //    return s.length() == 1;
        //}
    }
}
