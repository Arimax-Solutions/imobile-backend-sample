package com.I_Mobile_Crazy.I_Mobile_Crazy.service;

import java.util.List;

/**
 * @author : Chanuka Weerakkody
 * @since : 20.1.1
 **/

public interface SuperService <T, ID> {
    void save(T data);
    void update(T data);
    void delete(ID id);
    T findById(ID id);
    List<T> findAll();
}
