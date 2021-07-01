package com.cyn.traps

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class RcAdapter(var context: Context) : RecyclerView.Adapter<RcAdapter.Holder>() {

    //当游戏失败后，失败处的坐标，此处要着重显示
    var overPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {

        return Holder(LayoutInflater.from(context).inflate(R.layout.item_lattice, parent, false))
    }

    override fun getItemCount(): Int {
        return 480
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val d = position / 30
        val k = position - 30 * d

        //-1：地雷区域
        //0-8：周围地雷数量
        val indexGame = MinefieldUtil.gameMap[d][k]

        //0：未开采
        //1：已开踩
        //2：标记小红旗
        //3：问号
        val indexOperation = MinefieldUtil.operationMap[d][k]

        //判断是否公开雷区
        if (MinefieldUtil.isOpen) {
            //公开雷区，游戏结束

            when (indexOperation) {
                0, 3 -> {
                    if (indexGame == -1) {
                        holder.itemText.setBackgroundResource(R.mipmap.icon_trap_open)
                        holder.itemText.text = ""
                    } else {
                        holder.itemText.setBackgroundResource(R.mipmap.icon_lattice)
                        holder.itemText.text = ""
                    }
                }
                1 -> {
                    holder.itemText.setBackgroundResource(R.mipmap.icon_empty)
                    holder.itemText.text = indexGame.toString()

                    if (0 != indexGame) {
                        holder.itemText.text = indexGame.toString()
                    } else {
                        holder.itemText.text = ""
                    }

                    holder.itemText.setTextColor(
                        when (indexGame) {
                            1 -> ContextCompat.getColor(context, R.color.index1)
                            2 -> ContextCompat.getColor(context, R.color.index2)
                            3 -> ContextCompat.getColor(context, R.color.index3)
                            4 -> ContextCompat.getColor(context, R.color.index4)
                            5 -> ContextCompat.getColor(context, R.color.index5)
                            6 -> ContextCompat.getColor(context, R.color.index6)
                            7 -> ContextCompat.getColor(context, R.color.index7)
                            else -> ContextCompat.getColor(context, R.color.index8)
                        }
                    )
                }
                2 -> {
                    if (indexGame == -1) {
                        holder.itemText.setBackgroundResource(R.mipmap.icon_flag)
                        holder.itemText.text = ""
                    } else {
                        holder.itemText.setBackgroundResource(R.mipmap.icon_flag_error)
                        holder.itemText.text = ""
                    }
                }
            }

            if (indexOperation == 0 && -1 == indexGame) {
                holder.itemText.setBackgroundResource(R.mipmap.icon_trap_open)
                holder.itemText.text = ""
            }

            if (overPosition == position) {
                holder.itemText.setBackgroundResource(R.mipmap.icon_trap)
                holder.itemText.text = ""
            }

        } else {
            //隐藏雷区
            when (indexOperation) {
                0 -> {
                    holder.itemText.setBackgroundResource(R.mipmap.icon_lattice)
                    holder.itemText.text = ""
                    holder.itemText.setOnClickListener {
                        //开采区域
                        if (-1 == indexGame) {
                            //踩到地雷，游戏结束
                            MinefieldUtil.isOpen = true
                            overPosition = position
                            notifyDataSetChanged()
                            dataCallBack?.gameOver()
                        } else {

//                            dataCallBack?.gameWins()

                            //回调游戏开始
                            if (!MinefieldUtil.isEstablish) {
                                dataCallBack?.gameStart()
                            }

                            //本次点击排除一个格子
                            MinefieldUtil.turnedOnNum++

                            //创建地雷，本局游戏只会执行一次，内部已封装好方法
                            MinefieldUtil.establish(d, k)

                            //递归开采其他模块
                            exploitation(d, k)


                            //判断是否已经排除完地雷
                            if (381 == MinefieldUtil.turnedOnNum) {
                                dataCallBack?.gameWins()
                            }

                            //刷新
                            notifyDataSetChanged()

                        }
                    }
                    holder.itemText.setOnLongClickListener {
                        //在该区域插上小红旗

                        //判断小红旗是否用完了
                        if (MinefieldUtil.flagNum <= 0) {
                            return@setOnLongClickListener true
                        }

                        MinefieldUtil.operationMap[d][k] = 2

                        //回调使用了小红旗
                        dataCallBack?.useFlag()

                        notifyDataSetChanged()
                        return@setOnLongClickListener true
                    }
                }

                1 -> {
                    if (0 == indexGame) {
                        //已开采周围没有地雷的方块
                        holder.itemText.setBackgroundResource(R.mipmap.icon_empty)
                        holder.itemText.text = ""
                    } else {
                        //已开采周围有地雷的方块
                        holder.itemText.setBackgroundResource(R.mipmap.icon_empty)
                        holder.itemText.text = indexGame.toString()
                        holder.itemText.setTextColor(
                            when (indexGame) {
                                1 -> ContextCompat.getColor(context, R.color.index1)
                                2 -> ContextCompat.getColor(context, R.color.index2)
                                3 -> ContextCompat.getColor(context, R.color.index3)
                                4 -> ContextCompat.getColor(context, R.color.index4)
                                5 -> ContextCompat.getColor(context, R.color.index5)
                                6 -> ContextCompat.getColor(context, R.color.index6)
                                7 -> ContextCompat.getColor(context, R.color.index7)
                                else -> ContextCompat.getColor(context, R.color.index8)
                            }
                        )
                    }
                }

                2 -> {
                    holder.itemText.setBackgroundResource(R.mipmap.icon_flag)
                    holder.itemText.text = ""
                    holder.itemText.setOnLongClickListener {

                        MinefieldUtil.operationMap[d][k] = 3

                        dataCallBack?.cancelFlag()

                        notifyDataSetChanged()



                        return@setOnLongClickListener true
                    }
                }

                3 -> {
                    holder.itemText.setBackgroundResource(R.mipmap.icon_doubt)
                    holder.itemText.text = ""
                    holder.itemText.setOnLongClickListener {
                        MinefieldUtil.operationMap[d][k] = 0
                        notifyDataSetChanged()
                        return@setOnLongClickListener true
                    }
                }
            }
        }
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemText: TextView = itemView.findViewById(R.id.itemText)
    }


    //==============================================================================================
    /**
     * 开采领域，递归调用
     */
    private fun exploitation(d: Int, k: Int) {
        if (MinefieldUtil.gameMap[d][k] >= 0) {
            MinefieldUtil.operationMap[d][k] = 1


            if (0 != MinefieldUtil.gameMap[d][k]) {
                return
            }

            //判断左侧是否开采
            if (k >= 1 && MinefieldUtil.gameMap[d][k - 1] >= 0 && MinefieldUtil.operationMap[d][k - 1] != 1) {
                MinefieldUtil.operationMap[d][k - 1] = 1
                MinefieldUtil.turnedOnNum++
                if (MinefieldUtil.gameMap[d][k - 1] == 0) {
                    exploitation(d, k - 1)
                }
            }

            //判断上侧是否开采
            if (d >= 1 && MinefieldUtil.gameMap[d - 1][k] >= 0 && MinefieldUtil.operationMap[d - 1][k] != 1) {
                MinefieldUtil.operationMap[d - 1][k] = 1
                MinefieldUtil.turnedOnNum++
                if (MinefieldUtil.gameMap[d - 1][k] == 0) {
                    exploitation(d - 1, k)
                }
            }

            //判断右侧是否开采
            if (k <= 28 && MinefieldUtil.gameMap[d][k + 1] >= 0 && MinefieldUtil.operationMap[d][k + 1] != 1) {
                MinefieldUtil.operationMap[d][k + 1] = 1
                MinefieldUtil.turnedOnNum++
                if (MinefieldUtil.gameMap[d][k + 1] == 0) {
                    exploitation(d, k + 1)
                }
            }

            //判断下侧是否开采
            if (d <= 14 && MinefieldUtil.gameMap[d + 1][k] >= 0 && MinefieldUtil.operationMap[d + 1][k] != 1) {
                MinefieldUtil.operationMap[d + 1][k] = 1
                MinefieldUtil.turnedOnNum++
                if (MinefieldUtil.gameMap[d + 1][k] == 0) {
                    exploitation(d + 1, k)
                }
            }

            //判断左上是否开采
            if (d >= 1 && k >= 1 && MinefieldUtil.gameMap[d - 1][k - 1] >= 0 && MinefieldUtil.operationMap[d - 1][k - 1] != 1) {
                MinefieldUtil.operationMap[d - 1][k - 1] = 1
                MinefieldUtil.turnedOnNum++
                if (MinefieldUtil.gameMap[d - 1][k - 1] == 0) {
                    exploitation(d - 1, k - 1)
                }
            }

            //判断右上是否开采
            if (d >= 1 && k <= 28 && MinefieldUtil.gameMap[d - 1][k + 1] >= 0 && MinefieldUtil.operationMap[d - 1][k + 1] != 1) {
                MinefieldUtil.operationMap[d - 1][k + 1] = 1
                MinefieldUtil.turnedOnNum++
                if (MinefieldUtil.gameMap[d - 1][k + 1] == 0) {
                    exploitation(d - 1, k + 1)
                }
            }

            //判断右下是否开采
            if (d <= 14 && k <= 28 && MinefieldUtil.gameMap[d + 1][k + 1] >= 0 && MinefieldUtil.operationMap[d + 1][k + 1] != 1) {
                MinefieldUtil.operationMap[d + 1][k + 1] = 1
                MinefieldUtil.turnedOnNum++
                if (MinefieldUtil.gameMap[d + 1][k + 1] == 0) {
                    exploitation(d + 1, k + 1)
                }
            }

            //判断左下是否开采
            if (d <= 14 && k >= 1 && MinefieldUtil.gameMap[d + 1][k - 1] >= 0 && MinefieldUtil.operationMap[d + 1][k - 1] != 1) {
                MinefieldUtil.operationMap[d + 1][k - 1] = 1
                MinefieldUtil.turnedOnNum++
                if (MinefieldUtil.gameMap[d + 1][k - 1] == 0) {
                    exploitation(d + 1, k - 1)
                }
            }

        }
    }

    //==============================================================================================
    //相关事件回调
    private var dataCallBack: DataCallBack? = null

    fun setDataCallBack(dataCallBack: DataCallBack) {
        this.dataCallBack = dataCallBack
    }

    interface DataCallBack {

        fun gameStart()

        //游戏结束
        fun gameOver()

        //使用小红旗
        fun useFlag()

        //取消使用小红旗
        fun cancelFlag()

        //游戏胜利
        fun gameWins()
    }


}