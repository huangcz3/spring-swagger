package com.asiainfo.iop.channel.interact.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * @author migle 2018-4-16
 *
 */
@JsonPropertyOrder({ "chlId", "tptId","posId","act_id","recText","priority"}) //Json字段顺序
@JsonInclude(JsonInclude.Include.NON_EMPTY) //没有值的字段不展现
public class RecItem {
    @JsonProperty("chl_id")
    @JsonPropertyOrder
    private String chlId;//渠道标识
    @JsonProperty("tpt_id")
    private String tptId;//触点标识
    @JsonProperty("pos_id")
    private String posId;//运营位标识
    @JsonProperty("act_id")
    private String actId;//营销活动标识
    @JsonProperty("rec_text")
    private String recText;//推荐内容
    private int priority;//优先级

    public RecItem() {
    }

    public RecItem(String chlId, String recText) {
        this.chlId = chlId;
        this.recText = recText;
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

    public String getActId() {
        return actId;
    }

    public void setActId(String actId) {
        this.actId = actId;
    }

    public String getRecText() {
        return recText;
    }

    public void setRecText(String recText) {
        this.recText = recText;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
