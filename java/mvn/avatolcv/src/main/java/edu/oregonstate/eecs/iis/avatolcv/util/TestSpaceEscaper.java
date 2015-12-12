package edu.oregonstate.eecs.iis.avatolcv.util;

public class TestSpaceEscaper {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        String foo = "a b c d";
        String escape1 = foo.replaceAll(" ", "\\\\ ");
        System.out.println(escape1);
    }

}
