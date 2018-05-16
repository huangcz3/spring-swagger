package com.asiainfo.iop.channel.interact.api;

import com.asiainfo.iop.channel.interact.domain.RecRequest;
import com.asiainfo.iop.channel.interact.domain.RecMsg;
import com.asiainfo.iop.channel.interact.domain.RecResponse;
import com.asiainfo.iop.channel.interact.service.RecService;
import com.asiainfo.iop.util.DigestUtil;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import io.swagger.annotations.*;
import org.apache.rocketmq.spring.starter.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;


@RestController
@Api(value = "API-RecController", description = "IOP平台-被动渠道接触服务")
public class RecController {
    private static final Logger logger = LoggerFactory.getLogger(RecController.class);

    @Autowired
    private RecService recService;

    @Resource
    private RocketMQTemplate rocketMQTemplate;

    public final static long TIME_RANGE = 5 * 60 * 1000; //可以允许正负5分钟的时间偏差

    @Value("${iop.channel.interact.event-topic}")
    private String interEventTopic;

    @HystrixCommand(fallbackMethod = "recError")
    @GetMapping("/rec")
    @ApiOperation(value = "营销推荐接口", notes = "为被动渠道返回指定用户的推荐内容", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "chl_id", required = true, dataType = "string", paramType = "query", value = "渠道ID"),
            @ApiImplicitParam(name = "tpt_id", required = false, dataType = "string", paramType = "query", value = "触点ID"),
            @ApiImplicitParam(name = "pos_id", required = false, dataType = "string", paramType = "query", value = "营销位ID"),
            @ApiImplicitParam(name = "uid", required = true, dataType = "string", paramType = "query", value = "用户ID"),
            @ApiImplicitParam(name = "uid_type", required = false, dataType = "string", paramType = "query", defaultValue = "10", value = "uid类型:10:手机号;20:IMEI;30:USERID; 11:md5后的手机号;21:md5后的IMEI;31:md5后的userid"),
            @ApiImplicitParam(name = "ts", required = true, dataType = "long", paramType = "query", value = "请求时间:Unix时间戳，单位毫秒；避免可能存在的时间误差，验证时取[-5m,+5m]内都算正常请求"),
            @ApiImplicitParam(name = "sign", required = true, dataType = "string", paramType = "query", value = "签名:用于验证请求发起侧的身份；通过md5(chl_id+uid+ts+渠道密钥)生成;渠道密钥会定期通知渠道侧更新;注:md5取32位")
    })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful — 请求已完成"),
            @ApiResponse(code = 400, message = "请求中有语法问题，或不能满足请求"),
            @ApiResponse(code = 401, message = "未授权客户机访问数据"),
            @ApiResponse(code = 404, message = "服务器找不到给定的资源；文档不存在"),
            @ApiResponse(code = 500, message = "服务器不能完成请求")}
    )
    //@ApiIgnore 忽略某类/方法/参数的文档
    private RecResponse rec(@RequestParam(required = true, value = "chl_id") String chlId,
                            @RequestParam(required = false, value = "tpt_id") String tptId,
                            @RequestParam(required = false, value = "pos_id") String posId,
                            @RequestParam(required = true, value = "uid") String uid,
                            @RequestParam(required = true, value = "uid_type", defaultValue = "10") String uidType,
                            @RequestParam(required = true, value = "ts") long ts,
                            @RequestParam(required = true, value = "sign") String sign,
                            HttpServletResponse response,
                            HttpServletRequest request
    ) {
        long cts = System.currentTimeMillis();
        logger.info("req detail:{}|{}|{}|{}|{}|{}|{}|{}|{}|{}", chlId,tptId, posId,uid, uidType,ts, cts, sign, Math.abs(ts - cts),request.getRemoteAddr());

        RecRequest req = new RecRequest(chlId, tptId, posId, uid, uidType, System.currentTimeMillis(),sign,request.getRemoteAddr());
        sendEventToMQ(req);
        RecResponse resp;
        if (Math.abs(ts - cts) > TIME_RANGE) {
            //logger.warn("请求时间验证不通过，疑似伪造请求chlid:{},uid:{},ts:{} cts:{},sign:{},ts-cts:{}", chlId, uid, ts, cts, sign, Math.abs(ts - cts));
            logger.warn("请求时间验证不通过，疑似伪造请求ts:{} cts:{} diff {}", ts, cts, Math.abs(ts - cts));
            response.setStatus(400);
            resp = new RecResponse(req,-1, "请核对请求参数");
            sendEventToMQ(resp);
            return resp;
        }

        //获取渠道密钥判断请求签名
        if (!getRecService().isSign(chlId, uid, ts, sign)) {
            logger.warn("请求签名验证不通过，疑似伪造请求");
            response.setStatus(401);
            resp = new RecResponse(req,-2, "请核对请求参数");
            sendEventToMQ(resp);
            return resp;
        }
        //获取推荐计算结果
        //获取渠道信息
        //判断渠道是否生效
        resp = getRecService().getResponse(req);
        sendEventToMQ(resp);
        return resp;

    }


    @GetMapping("/showtest")   //TODO:完善一下，提供一个签名验证服务，方便调用方测试，但需要控制频率，以免产生不必要的安全问题
    @ApiOperation(value = "sign验证接口", notes = "请求参数中sign验证服务，方便调用方测试", httpMethod = "GET", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Map<String, String> showtest() {
        Map<String, String> map = new HashMap<>();
        String chlId = "chl001";
        String uid = "13511011100";
        String ts = "" + System.currentTimeMillis();
        map.put("chl_id", chlId);
        map.put("uid", uid);
        map.put("ts", ts);
        map.put("sign", DigestUtil.md5(chlId + uid + ts + getRecService().getChlKey(chlId)));
        return map;
    }

//    private String recFb() {
//        //TODO
//        return "{'state':'0','msg':'3q'}";
//    }

    /*
     * 熔断处理方法
     */
    private RecResponse recError(String chlId,
                                 String tptId,
                                 String posId,
                                 String uid,
                                 String uidType,
                                 long ts,
                                 String sign,
                                 HttpServletResponse response
    ) {
        return new RecResponse(1, "no message!");
    }

    @GetMapping("/t001")
    public String test() {
        return "x:" + (recService == null);
    }

    public RecService getRecService() {
        return this.recService;
    }

    public RocketMQTemplate getRocketMQTemplate() {
        return rocketMQTemplate;
    }

    public String getInterEventTopic() {
        return interEventTopic;
    }

    public void setInterEventTopic(String interEventTopic) {
        this.interEventTopic = interEventTopic;
    }

    //发送交互内容到MQ做下一步统计分析
    //注意：在这里修改写入消息队列中的日志内容后需要通知水费此队列消息的相关人员做相应修改
    // private void sendEventToMQ(String traceId,String chlId, String tptId, String posId, String uid, String uidType){
    private void sendEventToMQ(RecMsg msg) {
        //getRocketMQTemplate().syncSend(getInterEventTopic(), MessageBuilder.withPayload("Hello, World! I'm from spring message").build());
        //异步发送，忽略失败
        getRocketMQTemplate().asyncSend(getInterEventTopic(), MessageBuilder.withPayload(msg.toJson()).build(),null);
    }
}
