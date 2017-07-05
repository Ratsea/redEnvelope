import com.ratsea.envelope.Application;
import com.ratsea.envelope.domain.EnvelopePushDto;
import com.ratsea.envelope.domain.ResultDto;
import com.ratsea.envelope.service.EnvelopeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;

/**
 * Created by Ratsea on 2017/7/5.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes=Application.class)// 指定spring-boot的启动类
public class T {

    @Resource
    private EnvelopeService envelopeService;

    @Resource
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    private CountDownLatch latch = new CountDownLatch(100);
    @Test
    public void t(){
        //发红包
        ResultDto r=envelopeService.pushEnvelope(1l,"王胖子",new BigDecimal(200.00),20);

        EnvelopePushDto er=(EnvelopePushDto)r.getObj();
        System.err.println(r.getObj());


        //抢红包
        for(long i=10;i<100;i++){
            Long uId=i;
            threadPoolTaskExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try{
                     ResultDto r1= envelopeService.getReadEnvelope(er.getId(),uId,"张"+uId);

                        System.err.println(r1);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });


            latch.countDown();

        }

        try {
            latch.await(); // 主线程等待
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



    }
}
