package com.bian.mydevsample.ui.flower;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * author 边凌
 * date 2018/2/5 13:20
 * 类描述：
 */
public class KotlinGrammarTestTest {
    @Test
    public void test() {
        KotlinGrammarTest grammarTest = KotlinGrammarTest.INSTANCE;
        boolean a1 = grammarTest.add(1, 2) == 3;
        boolean a2 = grammarTest.rectangleArea(2, 4) == 2d * 4d;
        Object any = null;
        boolean a3 = grammarTest.checkNull(any);
        List<Object> data = new ArrayList<>();
        boolean a4 = grammarTest.checkNull(data);
        boolean a5 = grammarTest.circleArea(2) == grammarTest.getPI() * Math.pow(2, 2);
        boolean a6 = grammarTest.testRewriteOperator();
        boolean a7 = grammarTest.testControlFlow();
        boolean[] result = new boolean[]{a1, a2, a3, a4, a5, a6, a7};
        for (boolean b : result) {
            System.out.println(b);
        }
        KotlinGrammarTest.InitOrderDemo demo = new KotlinGrammarTest.InitOrderDemo("test");
        System.out.println(demo.getFirstProperty() + "," + demo.getSecondProperty());
        System.out.println("测试结束");
    }
}