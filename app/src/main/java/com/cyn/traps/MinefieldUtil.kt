package com.cyn.traps

import android.util.Log
import java.util.*

/**
 * 雷区工具类
 */
object MinefieldUtil {

    private const val TAG = "log-trap"

    //是否已经建立了游戏
    var isEstablish = false

    //剩余小红旗数量
    var flagNum = 0

    //已排除的格子数量
    var turnedOnNum = 0

    //是否公开雷区，当公开雷区也就意味着游戏结束，不能再点击
    var isOpen = false

    //创建一张二维数组代表地雷布置
    //-1：地雷区域
    //0-8：周围地雷数量
    val gameMap = Array(16) { Array(30) { 0 } }

    //用户操作图记录，与地图大小相等
    //0：未开采
    //1：已开踩
    //2：标记小红旗
    //3：问号
    val operationMap = Array(16) { Array(30) { 0 } }

    //特殊坐标，该坐标不允许创建雷区
    private lateinit var specialCoordinate: MutableList<Int>

    /**
     * 创建雷区，当开采第一个方块后，才会开始布置雷区，防止用户上来就炸，并且用户点击处和周围1格不再布置雷区
     */
    fun establish(dTemp: Int, kTemp: Int) {

        if (!isEstablish) {
            isEstablish = true
        } else {
            return
        }

        //创建特殊坐标
        createSpecialCoordinates(dTemp, kTemp)

        //重置用户操作图
        for (d in operationMap.indices) {
            for (k in operationMap[d].indices) {
                operationMap[d][k] = 0
            }
        }

        //剩余小红旗数量重置
        flagNum = 99

        val random = Random()
        val temp = mutableSetOf<Int>()

        //生成要埋地雷的下标
        while (true) {
            val nextInt = random.nextInt(479)

            dTemp * 30 + kTemp


            //如果不是用户点击处以及周围1格，才会采取该坐标
            if (!specialCoordinate.contains(nextInt)) {
                temp.add(nextInt)
            }

            if (99 == temp.size) {
                break
            }
        }

        //埋下地雷
        for (i in temp) {
            val d = i / 30
            val k = i - 30 * d
            gameMap[d][k] = -1
        }

        //计算周围地雷数量
        createTrapsNumber()

        //====log
        Log.d(
            TAG,
            "\t\t\t0\t1\t2\t3\t4\t5\t6\t7\t8\t9\t10\t11\t12\t13\t14\t15\t16\t17\t18\t19\t20\t21\t22\t23\t24\t25\t26\t27\t28\t29"
        )
        Log.d(
            TAG,
            "\t\t--------------------------------------------------------------------------------------------------------------------------"
        )
        for ((c, i) in gameMap.withIndex()) {
            var str = "$c->\t\t"
            for (k in i) {
                str += k.toString() + "\t"
            }
            Log.d(TAG, str)
        }

    }

    /**
     * 获取陷阱数量
     */
    private fun createTrapsNumber() {
        for (i in gameMap.indices) {
            for (j in gameMap[i].indices) {
                //当此时坐标不是炸弹时候开始计算
                if (-1 != gameMap[i][j]) {
                    var trapNum = 0

                    //查询目标点左侧
                    if (j - 1 >= 0 && -1 == gameMap[i][j - 1]) {
                        trapNum++
                    }

                    //查询目标上侧
                    if (i - 1 >= 0 && -1 == gameMap[i - 1][j]) {
                        trapNum++
                    }

                    //查询目标右侧
                    if (j + 1 <= 29 && -1 == gameMap[i][j + 1]) {
                        trapNum++
                    }

                    //查询目标下侧
                    if (i + 1 <= 15 && -1 == gameMap[i + 1][j]) {
                        trapNum++
                    }

                    //查询左上角
                    if (j - 1 >= 0 && i - 1 >= 0 && -1 == gameMap[i - 1][j - 1]) {
                        trapNum++
                    }

                    //查询右上角
                    if (j + 1 <= 29 && i - 1 >= 0 && -1 == gameMap[i - 1][j + 1]) {
                        trapNum++
                    }

                    //查询右下角
                    if (j + 1 <= 29 && i + 1 <= 15 && -1 == gameMap[i + 1][j + 1]) {
                        trapNum++
                    }

                    //查询左下角
                    if (j - 1 >= 0 && i + 1 <= 15 && -1 == gameMap[i + 1][j - 1]) {
                        trapNum++
                    }

                    //赋值地雷个数
                    gameMap[i][j] = trapNum

                }
            }
        }
    }

    /**
     * 创建特殊坐标
     */
    private fun createSpecialCoordinates(dTemp: Int, kTemp: Int) {
        specialCoordinate = mutableListOf()

        //点击位置
        specialCoordinate.add(dTemp * 30 + kTemp)

        //点击坐标左侧
        if (kTemp >= 1) {
            specialCoordinate.add(dTemp * 30 + kTemp - 1)
        }

        //点击坐标上侧
        if (dTemp >= 1) {
            specialCoordinate.add((dTemp - 1) * 30 + kTemp)
        }

        //点击坐标右侧
        if (kTemp <= 28) {
            specialCoordinate.add(dTemp * 30 + kTemp + 1)
        }

        //点击坐标下侧
        if (dTemp <= 14) {
            specialCoordinate.add((dTemp + 1) * 30 + kTemp)
        }

        //点击坐标的左上
        if (dTemp >= 1 && kTemp >= 1) {
            specialCoordinate.add((dTemp - 1) * 30 + kTemp - 1)
        }

        //点击坐标的右上
        if (dTemp >= 1 && kTemp <= 28) {
            specialCoordinate.add((dTemp - 1) * 30 + kTemp + 1)
        }

        //点击坐标的右下
        if (dTemp <= 14 && kTemp <= 28) {
            specialCoordinate.add((dTemp + 1) * 30 + kTemp + 1)
        }

        //点击坐标的左下
        if (dTemp <= 14 && kTemp >= 1) {
            specialCoordinate.add((dTemp + 1) * 30 + kTemp - 1)
        }

        for (i in specialCoordinate) {
            Log.d(TAG, i.toString())
        }

    }

    /**
     * 重置
     */
    fun reset() {
        isEstablish = false

        isOpen = false

        turnedOnNum = 0

        for (d in gameMap.indices) {
            for (k in gameMap[d].indices) {
                gameMap[d][k] = 0
                operationMap[d][k] = 0
            }
        }

        flagNum = 0
    }


}