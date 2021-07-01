package com.cyn.traps

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView


class MainActivity : AppCompatActivity() {

    lateinit var rcAdapter: RcAdapter
    lateinit var flagNum: TextView
    lateinit var time: TextView
    lateinit var reset: TextView

    private var time1 = 0L
    private var time2 = 0L

    private val handler: Handler = Handler()

    //胜利弹框
    private lateinit var boxDialog: BoxDialog

    //计时器
    private val mCounter: Runnable = object : Runnable {
        @SuppressLint("SetTextI18n")
        override fun run() {
            handler.postDelayed(this, 1000)
            time2++
            if (60L == time2) {
                time1++
                time2 = 0
            }
            time.text =
                (if (time1 < 10) "0$time1" else time1.toString()) + ":" + if (time2 < 10) "0$time2" else time2.toString()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //状态栏设置
        val titleBar = findViewById<LinearLayout>(R.id.titleBar)
        StatusBarUtil.immersive(this)
        StatusBarUtil.darkMode(this)
        StatusBarUtil.setPaddingSmart(this, titleBar)

        //控件实例化
        flagNum = findViewById(R.id.flagNum)
        time = findViewById(R.id.time)
        reset = findViewById(R.id.reset)

        //加载游戏布局
        initView()

        //重置点击
        reset.setOnClickListener {

            //重新创建游戏
            MinefieldUtil.reset()

            //计时器重置
            handler.removeCallbacks(mCounter)
            time.text = "00:00"

            //小红旗重置
            flagNum.text = "--"

            //列表刷新
            rcAdapter.notifyDataSetChanged()

        }


    }

    @SuppressLint("ClickableViewAccessibility")
    fun initView() {
        val rc = findViewById<RecyclerView>(R.id.rc)
        val layoutParams = rc.layoutParams
        val pxValue = dip2px(this, 38F)
        layoutParams.width = pxValue * 30
        layoutParams.height = pxValue * 16

        rc.layoutManager = GridLayoutManager(this, 30)
        rcAdapter = RcAdapter(this)
        rc.adapter = rcAdapter

        rcAdapter.setDataCallBack(object : RcAdapter.DataCallBack {

            //游戏开始
            override fun gameStart() {
                time1 = 0
                time2 = 0
                handler.post(mCounter)
            }

            //游戏结束
            override fun gameOver() {
                MinefieldUtil.isEstablish = false
                flagNum.text = "--"

                //停止计时
                handler.removeCallbacks(mCounter)
            }

            //使用小红旗
            override fun useFlag() {
                if (MinefieldUtil.isEstablish) {
                    MinefieldUtil.flagNum--
                    flagNum.text = MinefieldUtil.flagNum.toString()
                }
            }

            //取消使用小红旗
            override fun cancelFlag() {
                if (MinefieldUtil.isEstablish) {
                    MinefieldUtil.flagNum++
                    flagNum.text = MinefieldUtil.flagNum.toString()
                }
            }

            //游戏胜利
            @SuppressLint("SetTextI18n")
            override fun gameWins() {
                //停止计时
                handler.removeCallbacks(mCounter)

                //弹出游戏胜利
                val inflate: View = LayoutInflater.from(this@MainActivity)
                    .inflate(R.layout.dialog_win, null, false)

                val consume = inflate.findViewById<TextView>(R.id.consume)

                consume.text =
                    "用时：" + (if (time1 < 10) "0$time1" else time1.toString()) + ":" + if (time2 < 10) "0$time2" else time2.toString()

                val again = inflate.findViewById<TextView>(R.id.again)
                again.setOnClickListener {
                    //重新创建游戏
                    time1 = 0
                    time2 = 0
                    MinefieldUtil.reset()
                    rcAdapter.notifyDataSetChanged()
                    boxDialog.dismiss()
                }

                boxDialog = BoxDialog(this@MainActivity, inflate, BoxDialog.LocationView.CENTER)
                boxDialog.setCancelable(false)//是否可以点击DialogView外关闭Dialog
                boxDialog.setCanceledOnTouchOutside(false)//是否可以按返回按钮关闭Dialog
                boxDialog.show()

            }

        })

    }

    /**
     * dp转px
     */
    private fun dip2px(context: Context, dpValue: Float): Int {
        val scale: Float = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }
}