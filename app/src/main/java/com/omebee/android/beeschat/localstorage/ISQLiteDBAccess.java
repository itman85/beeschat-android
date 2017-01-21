package com.omebee.android.beeschat.localstorage;

import com.omebee.android.beeschat.models.base.BaseModel;

import java.util.List;


/**
 * Created by phannguyen on 10/3/15.
 */
public interface ISQLiteDBAccess<T extends BaseModel>{
    boolean openDB();
    boolean closeDB();
    boolean addData(T data);
    boolean addDataBulk(List<T> datalist);
    boolean updateData(T data);
    boolean updateDataBulk(List<T> datalist);
    boolean deleteData(String ID);
    boolean deleteDataBulk(List<String> IDs);
    boolean deleteData(T data);
    boolean deleteDataBatch(List<T> datalist);
    boolean upsertData(T data);
    boolean upsertDataBatch(List<T> datalist);
    void notifySync();
    T findDataByID(String ID);
    List<T> findAllData();
    List<T> findSomeData(List<String> IDs);



}
