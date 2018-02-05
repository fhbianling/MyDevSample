package com.bian.mydevsample.ui.flower

/**
 * author 边凌
 * date 2018/2/5 13:12
 * 类描述：Kotlin语法测试
 */
object KotlinGrammarTest {
    init {
        println("开始测试 ${this.javaClass.name}")
    }

    val PI = 3.1415926
    fun add(a: Int, b: Int) = a + b
    fun circleArea(r: Double): Double {
        return Math.pow(r, 2.toDouble()) * PI
    }

    fun rectangleArea(width: Double, height: Double) = width * height
    fun checkNull(obj: Any?) = obj == null
    fun checkNull(list: List<Any>?) = list == null || list.isEmpty()


    data class Circle(var radius: Double)

    /**
     * 重写操作符使[Circle]圆形可以互相比较
     * [testRewriteOperator]该方法中进行了测试
     */
    private operator fun Circle.compareTo(b: Circle): Int {
        println("used rewrite-operator to compare the two circle's area")
        return (circleArea(radius) - circleArea(b.radius)).toInt()
    }

    fun testRewriteOperator(): Boolean {
        println("testRewriteOperator------->")
        val smallCircle = Circle(2.0)
        val bigCircle = Circle(3.0)
        val bigCircleHasBigArea = bigCircle > smallCircle
        println("big circle has big area :$bigCircleHasBigArea")
        println("rewrite operator compare success: $bigCircleHasBigArea")
        println("testRewriteOperator<-------")
        return bigCircleHasBigArea
    }

    fun testControlFlow(): Boolean {
        println("test control flow-------->")
        val ifTestResult: Boolean = ifTest(2, 3)
        val whenTestResult: Boolean = whenTest()
        val forTestResult: Boolean = forTest()
        println("result:if-$ifTestResult,when-$whenTestResult,for-$forTestResult")
        println("test control flow<--------")
        return ifTestResult && whenTestResult
    }

    private fun forTest(): Boolean {
        val listOf = listOf(1, 2, 3, 4, 5, 6, 7)
        val count1 = (listOf.indices step 2).count()
        println("count1:$count1,${count1 == 4}")
        println("filter test->")
        for (i in listOf.filter { it < 5 }) {
            println(i)
        }
        println("filter test<-")
        val map = listOf.filter { it >= 5 }.map { it + 1 }
        println(map.toString())
        return map.filter { it > 7 }.count() == 1
    }

    private fun whenTest(): Boolean {
        val origin = Math.random()
        val x = origin.toInt()
        println("origin:$origin,x:$x")
        val result1 = when (x) {
            0 -> true
            else -> false
        }
        val result2 = when (x) {
            in 0..1 -> true
            else -> false
        }
        val y: Any = x
        val result3 = when (y) {
            is Int -> true
            else -> false
        }
        val d = origin * 10
        val result4 = when (d) {
            in 0..5, in 5..10 -> {
                println("now value is $d")
                true
            }
            else -> false
        }

        return result1 && result2 && result3 && result4
    }

    private fun ifTest(i: Int, i1: Int): Boolean {
        val result = if (i > i1) 1 else 2
        return if (result == 1) i > i1 else (result == 2 && i1 >= i)
    }

    class InitOrderDemo(name: String) {
        val firstProperty = "First property: $name".also(::println)

        init {
            println("First initializer block that prints $name")
        }

        val secondProperty = "Second property: ${name.length}".also(::println)

        init {
            println("Second initializer block that prints ${name.length}")
        }
    }
}
