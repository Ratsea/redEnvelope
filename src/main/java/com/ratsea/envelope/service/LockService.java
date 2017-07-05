package com.ratsea.envelope.service;

import com.ratsea.envelope.domain.ResultDto;

/**
 * Created by Ratsea on 2017/7/5.
 */
public interface LockService {

    /**
     * 获取锁业务操作
     * @return
     */
    ResultDto getLock();

}
