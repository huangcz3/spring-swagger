package com.asiainfo.iop.channel.interact.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.UUID;

@JsonPropertyOrder({"status", "msg", "uid", "recTime", "recs", "tags", "extInfo", "traceId"}) //Json字段顺序
@JsonInclude(JsonInclude.Include.NON_EMPTY) //没有值的字段不展现，否则会展现null
public class RecRequest extends RecMsg {
    private String traceId;
    private String chlId;
    private String tptId;
    private String posId;
    private String uid;  //用户标识
    private String uidType;
    long rts; //请求时间
    String sign;
    String remoteAddr;

    public RecRequest(String chlId, String tptId, String posId, String uid, String uidType, long rts, String sign, String remoteAddr) {
        this.chlId = chlId;
        this.tptId = tptId;
        this.posId = posId;
        this.uid = uid;
        this.uidType = uidType;
        this.traceId = UUID.randomUUID().toString();
        this.rts = rts;
        this.sign = sign;
        this.remoteAddr = remoteAddr;
        this.traceId = UUID.randomUUID().toString();
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public String getChlId() {
        return chlId;
    }

    public void setChlId(String chlId) {
        this.chlId = chlId;
    }

    public String getTptId() {
        return tptId;
    }

    public void setTptId(String tptId) {
        this.tptId = tptId;
    }

    public String getPosId() {
        return posId;
    }

    public void setPosId(String posId) {
        this.posId = posId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUidType() {
        return uidType;
    }

    public void setUidType(String uidType) {
        this.uidType = uidType;
    }

    public long getRts() {
        return rts;
    }

    public void setRts(long rts) {
        this.rts = rts;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getRemoteAddr() {
        return remoteAddr;
    }

    public void setRemoteAddr(String remoteAddr) {
        this.remoteAddr = remoteAddr;
    }


}
