package com.asiainfo.iop.channel.interact.service;

import com.asiainfo.iop.channel.interact.domain.RecItem;
import com.asiainfo.iop.channel.interact.domain.RecRequest;
import com.asiainfo.iop.channel.interact.domain.RecResponse;
import com.asiainfo.iop.util.DigestUtil;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Service
public class RecService {
    private DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final Logger logger = LoggerFactory.getLogger(RecService.class);

    @Autowired
    private RestTemplate restTemplate;

    public RecResponse getResponse(RecRequest req) {

        //TODO:根据渠道配置和推荐计算结果组装&返回消息
        //Channel chl = restTemplate.getForObject("http://ms-iop-rec/", Channel.class, chlId);
        RecResponse rr = new RecResponse(req);

        RecItem item1 = new RecItem(req.getChlId(), "欢迎您订购不限流套餐，只要9.9哦详情<a href='www.baidu.com'><img src='http://www.10086.cn/gz_head//uploadBaseDir/content/png/20171109/20171109134540000Yax.png'></a>");
        RecItem item2 = new RecItem(req.getChlId(), "欢迎您订购不限流套餐，只要19.9哦，详情<a href='www.baidu.com'><img src='http://www.10086.cn/gz_head//uploadBaseDir/content/png/20171109/20171109134540000Yax.png'></a>");

        rr.setStatus(0);
        rr.setMsg("ok");
        rr.setUid("13511011100");
        rr.setRecTime(LocalDateTime.now().format(df));
        rr.setTags(Arrays.asList("高价值", "五星"));
        rr.setRecs(Lists.newArrayList(item1, item2));

        return rr;
    }


    public boolean isSign(String chlId, String uid, long ts, String sign) {
        //String mySign =  ; // DigestUtil.md5(chlId + uid + ts + getChlKey(chlId));   //后续有机会，整合验证服务
        return sign.equalsIgnoreCase(getSign(chlId, uid, ts));
    }

    public String getChlKey(String chlId) {
        //TODO:从渠道服务获取取
        return StringUtils.reverse(chlId) + "key0001";
    }

    /**
     * 生成签名：md5(chl_id+uid+ts+渠道密钥)
     *
     * @param chlId
     * @param uid
     * @param ts
     * @return
     */
    public String getSign(String chlId, String uid, long ts) {
        return DigestUtil.md5(chlId + uid + ts + getChlKey(chlId));
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
