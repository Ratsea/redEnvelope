package com.ratsea.envelope.service;

import com.ratsea.envelope.domain.ResultDto;

import java.math.BigDecimal;

/**
 * 红包业务处理层
 */
public interface EnvelopeService {

    /**
     * 发送红包
     * @param userId
     * @param nickName
     * @param money
     * @param num
     * @return
     */
    ResultDto pushEnvelope(Long userId, String nickName, BigDecimal money,int num);


    /**
     * 抢红包
     * @param id:红包编号
     * @param userId:用户编号
     * @return
     */
    ResultDto getReadEnvelope(String id,Long userId,String nickName) throws Exception ;

}
