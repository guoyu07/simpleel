package com.alibaba.simpleEL.dialect.ql.parser;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.simpleEL.dialect.ql.ast.QLExpr;

public class TestParser0 extends TestCase {

    public void test_0() throws Exception {
        QLExprParser parser = new QLExprParser("CASE F WHEN T THEN 1 ELSE 0 END");
        QLExpr expr = parser.expr();
        
        Assert.assertEquals("CASE F WHEN T THEN 1 ELSE 0 END", expr.toString());
    }
    
    public void test_1() throws Exception {
        QLExprParser parser = new QLExprParser("CASE WHEN T > 0 THEN 1 ELSE 0 END");
        QLExpr expr = parser.expr();
        
        Assert.assertEquals("CASE WHEN T > 0 THEN 1 ELSE 0 END", expr.toString());
    }
    
    public void test_between() throws Exception {
        QLExprParser parser = new QLExprParser("F1 BETWEEN @min AND @max");
        QLExpr expr = parser.expr();
        
        Assert.assertEquals("F1 BETWEEN @min AND @max", expr.toString());
    }
    
    public void test_between_not() throws Exception {
        QLExprParser parser = new QLExprParser("F1 NOT BETWEEN @min AND @max");
        QLExpr expr = parser.expr();
        
        Assert.assertEquals("F1 NOT BETWEEN @min AND @max", expr.toString());
    }
    
    public void test_method() throws Exception {
        QLExprParser parser = new QLExprParser("LEN(F1) > 10");
        QLExpr expr = parser.expr();
        
        Assert.assertEquals("LEN(F1) > 10", expr.toString());
    }
    
    public void test_method_2() throws Exception {
        QLExprParser parser = new QLExprParser("u.name.length() > 10");
        QLExpr expr = parser.expr();
        
        Assert.assertEquals("u.name.length() > 10", expr.toString());
    }
    
    public void test_aggregate() throws Exception {
        QLExprParser parser = new QLExprParser("MAX(AGE) > 10");
        QLExpr expr = parser.expr();
        
        Assert.assertEquals("MAX(AGE) > 10", expr.toString());
    }
}
