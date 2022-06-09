package com.example.bookkeeping.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Insert;
import androidx.room.PrimaryKey;

import com.example.bookkeeping.db.DbHelper;

import io.reactivex.rxjava3.annotations.Nullable;

//datum
@Entity(tableName = DbHelper.MY_DATUM_TABLE_NAME)
public class MyData {
    @ColumnInfo(name = "img")
    public int img;
    @ColumnInfo(name = "category")
    public String category;
    @ColumnInfo(name = "remark")
    public String remark;
    @ColumnInfo(name = "money")
    public String money;
    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "date_time")
    public String dateTime;
    /**
     * 内置数据库升级，定义的对象和表结构的定义存在问题
     *
     * java创建的基本类型：int 默认是NotNull = true
     * 变量定义：
     * class TABLEA{
     * int ID;
     * }
     * name=‘ID’, type=‘INTEGER’, affinity=‘3’, notNull=true, primaryKeyPosition=1
     *
     * 但是内置数据库的INTEGER类型是
     * name=‘ID’, type=‘INTEGER’, affinity=‘3’, notNull=false, primaryKeyPosition=1
     *
     * 处理方法
     * 创建对象的时候不用int，用Integer
     * class TABLEA{
     * @Nullable
     * Integer ID;
     * }
     *
     *  原文链接：https://blog.csdn.net/qq183340093/article/details/105835702/
     *  所以变integer了，下次注意添@Nullable注解就可以用回 int
     */
    @ColumnInfo(name = "sign")
    public Integer sign;

    public MyData(int img, String category, String remark, String money, @NonNull String dateTime,int sign) {
        this.img = img;
        this.category = category;
        this.remark = remark;
        this.money = money;
        this.dateTime = dateTime;
        this.sign=sign;
    }

    @Override
    public String toString() {
        return "MyData{" +
                "img=" + img +
                ", category='" + category + '\'' +
                ", remark='" + remark + '\'' +
                ", money='" + money + '\'' +
                ", dateTime='" + dateTime + '\'' +
                ", sign=" + sign +
                '}';
    }
}
