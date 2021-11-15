package com.github.liaomengge.base_common.design.patterns.chain.filter;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by liaomengge on 2021/6/11.
 */
public class TestFilterMain {

    public static void main(String[] args) throws Throwable {
        List<Filter<String, String>> list = Lists.newArrayList();
        list.add(new A());
        list.add(new B());
        list.add(new C());
        StringDefaultFilterChain defaultFilterChain = new StringDefaultFilterChain(list);
        String result = defaultFilterChain.filter("x");
        System.out.println("result ==> " + result);
    }

    public static class A implements Filter<String, String> {

        @Override
        public String doFilter(String s, FilterChain<String, String> filterChain) throws Throwable {
            System.out.println("A.doFilter ===> " + s);
            return filterChain.filter(s);
        }

        @Override
        public int getOrder() {
            return 1;
        }
    }

    public static class B implements Filter<String, String> {

        @Override
        public String doFilter(String s, FilterChain<String, String> filterChain) throws Throwable {
            System.out.println("B.doFilter ===> " + s);
            return filterChain.filter(s);
        }

        @Override
        public int getOrder() {
            return 2;
        }
    }

    public static class C implements Filter<String, String> {

        @Override
        public String doFilter(String s, FilterChain<String, String> filterChain) throws Throwable {
            System.out.println("C.doFilter ===> " + s);
            return filterChain.filter(s);
        }

        @Override
        public int getOrder() {
            return 3;
        }
    }

    public static class StringDefaultFilterChain extends DefaultFilterChain<String, String> {

        public StringDefaultFilterChain(List<Filter<String, String>> filters) {
            super(filters);
        }

        @Override
        public String doFilter(String s) throws Throwable {
            return s + " => default";
        }
    }

}
