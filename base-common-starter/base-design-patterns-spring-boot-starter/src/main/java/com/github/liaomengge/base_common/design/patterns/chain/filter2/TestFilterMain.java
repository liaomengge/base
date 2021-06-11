package com.github.liaomengge.base_common.design.patterns.chain.filter2;

/**
 * Created by liaomengge on 2021/6/11.
 */
public class TestFilterMain {

    public static void main(String[] args) throws Throwable {
        A a = new A();
        B b = new B();
        C c = new C();
        a.setNextFilter(b).setNextFilter(c);
        String result = a.doFilter("x");
        System.out.println("result ==> " + result);
    }

    public static class A extends Filter<String, String> {

        @Override
        public String apply(String s) throws Throwable {
            return s + 'A';
        }

        @Override
        public boolean isOwn(String s) {
            return s.length() == 1;
        }
    }

    public static class B extends Filter<String, String> {

        @Override
        public String apply(String s) throws Throwable {
            return s + 'B';
        }

        @Override
        public boolean isOwn(String s) {
            return s.length() == 2;
        }
    }

    public static class C extends Filter<String, String> {

        @Override
        public String apply(String s) throws Throwable {
            return s + 'C';
        }

        @Override
        public boolean isOwn(String s) {
            return s.length() == 3;
        }
    }
}
