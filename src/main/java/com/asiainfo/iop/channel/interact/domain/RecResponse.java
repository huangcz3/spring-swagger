package com.asiainfo.iop.channel.interact.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;
import java.util.Map;

/**
 * @author migle 2018-4-16
 */
@JsonPropertyOrder({ "status", "msg","uid","recTime","recs","tags","extInfo","traceId"}) //Json字段顺序
@JsonInclude(JsonInclude.Include.NON_EMPTY) //没有值的字段不展现，否则会展现null
public class RecResponse extends RecMsg{
    @JsonProperty("status")
    private int status;  //响应状态编码
    private String msg;  //响应消息
    private String uid;  //用户标识
    @JsonProperty("rec_time")
    private String recTime;  //推荐时间
    private List<RecItem> recs;  //推荐结果列表
    private List<String> tags;  //用户标签
    @JsonProperty("ext_info")
    private Map<String, Object> extInfo;  //扩展信息
    @JsonProperty("trace_id")
    private String traceId;  //追踪标识

    public RecResponse() {
    }
    public RecResponse(RecRequest req) {
        setUid(req.getUid());
        setTraceId(req.getTraceId());

    }
    public RecResponse(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public RecResponse(RecRequest req,int status, String msg) {
        this(req);
        this.status = status;
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getRecTime() {
        return recTime;
    }

    public void setRecTime(String recTime) {
        this.recTime = recTime;
    }

    public List<RecItem> getRecs() {
        return recs;
    }

    public void setRecs(List<RecItem> recs) {
        this.recs = recs;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Map<String, Object> getExtInfo() {
        return extInfo;
    }

    public void setExtInfo(Map<String, Object> extInfo) {
        this.extInfo = extInfo;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }


}
