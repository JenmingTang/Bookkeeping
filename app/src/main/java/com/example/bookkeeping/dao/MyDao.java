package com.example.bookkeeping.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.bookkeeping.db.DbHelper;
import com.example.bookkeeping.entities.MyData;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface MyDao {
    //询问全部MyData数据
    @Query("select * from " + DbHelper.MY_DATUM_TABLE_NAME + " order by date_time desc ")
    Single<List<MyData>> queryAllFromMyDatum();

    //询问日或月的数据
    @Query("select * from " + DbHelper.MY_DATUM_TABLE_NAME + " where " +
            DbHelper.DATE_TIME_COLUMN_NAME + " like :dayOrMonth  order by date_time desc ")
    Single<List<MyData>> queryDayOrMonthFromMyDatumByLike(String dayOrMonth);

    //删除单条数据或多条
    @Delete(entity = MyData.class)
    Completable deleteMyDatum(MyData... myDatum);

    //插入单条数据或多条
    @Insert(entity = MyData.class)
    Completable insertMyDatum(MyData... myDatum);
}
