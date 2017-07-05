package com.ratsea.envelope.service.impl;

import com.ratsea.envelope.domain.EnvelopePushDto;
import com.ratsea.envelope.domain.ResultDto;
import com.ratsea.envelope.domain.User;
import com.ratsea.envelope.service.EnvelopeService;
import com.ratsea.envelope.util.DbStorage;
import com.ratsea.envelope.util.DistributeLock;
import com.ratsea.envelope.util.ReadEnvelopeUtil;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Ratsea on 2017/7/5.
 */
@Service
public class EnvelopeServiceImpl implements EnvelopeService {


    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    private static final CountDownLatch threadSemaphore = new CountDownLatch(10);

    @Override
    public ResultDto pushEnvelope(Long userId, String nickName, BigDecimal money, int num) {


        if(ObjectUtils.isEmpty(userId)){
            return new ResultDto(103,"用户编号为空","请登录");
        }

        if(ObjectUtils.isEmpty(nickName)){
            return new ResultDto(103,"用户昵称为空","请登录");
        }

        if(ObjectUtils.isEmpty(money)||money.equals(new BigDecimal(0))){
            return new ResultDto(103,"红包为空","红包为空");
        }


        EnvelopePushDto envelopePushDto=new EnvelopePushDto();

        //发送红包
        CompletableFuture<List<BigDecimal>> future=CompletableFuture.supplyAsync(()->{
             return ReadEnvelopeUtil.math(money,num);
        },threadPoolTaskExecutor);

        envelopePushDto.setId(UUID.randomUUID().toString().replace("-",""));
        envelopePushDto.setMoney(money);
        envelopePushDto.setNum(num);
        envelopePushDto.setUseNum(0);

        List<User> user=new ArrayList<>();
        try{
        future.get().forEach(n->{
            User u=new User();
            u.setMoney(n);
            user.add(u);
        });
        }catch (Exception e){
            e.printStackTrace();
        }
        envelopePushDto.setUser(user);


        DbStorage.getStorage().put(envelopePushDto.getId(),envelopePushDto);



        return new ResultDto(0,"成功","成功",envelopePushDto);
    }

    @Override
    public ResultDto getReadEnvelope(String id, Long userId,String nickeName) throws Exception {

        if(ObjectUtils.isEmpty(id)){
            return new ResultDto(103,"红包编号没有传入","验证");
        }

            DistributeLock distributeLockDemo = new DistributeLock();
            distributeLockDemo.lock();

            EnvelopePushDto envelopePushDto=(EnvelopePushDto)DbStorage.getStorage().get(id);

            if(ObjectUtils.isEmpty(envelopePushDto)){
                distributeLockDemo.unlock();
                return new ResultDto(104,"红包不存在","红包不存在");
            }

            //验证红包是否被抢完
            if(envelopePushDto.getNum()==envelopePushDto.getUseNum()){
                distributeLockDemo.unlock();
                return new ResultDto(105,"红包已被抢完","红包已被抢完");
            }
            //验证用户是否抢过该红包
           Long num= envelopePushDto.getUser().stream().filter(n->n.getId()==userId).count();
            if(num>0){
                distributeLockDemo.unlock();
                return new ResultDto(106,"红包已被用户抢过","请勿重复抢红包");
            }



        User user=null;

        for (User u1:envelopePushDto.getUser()) {
            if(ObjectUtils.isEmpty(u1.getId())){
                    u1.setId(userId);
                    u1.setNickName(nickeName);
                    user=u1;
                    break;
            }
        }

        envelopePushDto.setUseNum(envelopePushDto.getUseNum()+1);
          /*  User user=  envelopePushDto.getUser().stream().filter(n->ObjectUtils.isEmpty(n.getId())).findFirst().get();
            user.setId(userId);
            user.setNickName(nickeName);*/
            DbStorage.getStorage().put(id,envelopePushDto);

        distributeLockDemo.unlock();
            return new ResultDto(0,"成功","成功","抢到红包金额为："+user.getMoney());

    }
}
