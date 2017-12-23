package com.smi.am.dao.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AmCouponsExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public AmCouponsExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andCCouponsidIsNull() {
            addCriterion("c_couponsId is null");
            return (Criteria) this;
        }

        public Criteria andCCouponsidIsNotNull() {
            addCriterion("c_couponsId is not null");
            return (Criteria) this;
        }

        public Criteria andCCouponsidEqualTo(String value) {
            addCriterion("c_couponsId =", value, "cCouponsid");
            return (Criteria) this;
        }

        public Criteria andCCouponsidNotEqualTo(String value) {
            addCriterion("c_couponsId <>", value, "cCouponsid");
            return (Criteria) this;
        }

        public Criteria andCCouponsidGreaterThan(String value) {
            addCriterion("c_couponsId >", value, "cCouponsid");
            return (Criteria) this;
        }

        public Criteria andCCouponsidGreaterThanOrEqualTo(String value) {
            addCriterion("c_couponsId >=", value, "cCouponsid");
            return (Criteria) this;
        }

        public Criteria andCCouponsidLessThan(String value) {
            addCriterion("c_couponsId <", value, "cCouponsid");
            return (Criteria) this;
        }

        public Criteria andCCouponsidLessThanOrEqualTo(String value) {
            addCriterion("c_couponsId <=", value, "cCouponsid");
            return (Criteria) this;
        }

        public Criteria andCCouponsidLike(String value) {
            addCriterion("c_couponsId like", value, "cCouponsid");
            return (Criteria) this;
        }

        public Criteria andCCouponsidNotLike(String value) {
            addCriterion("c_couponsId not like", value, "cCouponsid");
            return (Criteria) this;
        }

        public Criteria andCCouponsidIn(List<String> values) {
            addCriterion("c_couponsId in", values, "cCouponsid");
            return (Criteria) this;
        }

        public Criteria andCCouponsidNotIn(List<String> values) {
            addCriterion("c_couponsId not in", values, "cCouponsid");
            return (Criteria) this;
        }

        public Criteria andCCouponsidBetween(String value1, String value2) {
            addCriterion("c_couponsId between", value1, value2, "cCouponsid");
            return (Criteria) this;
        }

        public Criteria andCCouponsidNotBetween(String value1, String value2) {
            addCriterion("c_couponsId not between", value1, value2, "cCouponsid");
            return (Criteria) this;
        }

        public Criteria andCCouponsnameIsNull() {
            addCriterion("c_couponsName is null");
            return (Criteria) this;
        }

        public Criteria andCCouponsnameIsNotNull() {
            addCriterion("c_couponsName is not null");
            return (Criteria) this;
        }

        public Criteria andCCouponsnameEqualTo(String value) {
            addCriterion("c_couponsName =", value, "cCouponsname");
            return (Criteria) this;
        }

        public Criteria andCCouponsnameNotEqualTo(String value) {
            addCriterion("c_couponsName <>", value, "cCouponsname");
            return (Criteria) this;
        }

        public Criteria andCCouponsnameGreaterThan(String value) {
            addCriterion("c_couponsName >", value, "cCouponsname");
            return (Criteria) this;
        }

        public Criteria andCCouponsnameGreaterThanOrEqualTo(String value) {
            addCriterion("c_couponsName >=", value, "cCouponsname");
            return (Criteria) this;
        }

        public Criteria andCCouponsnameLessThan(String value) {
            addCriterion("c_couponsName <", value, "cCouponsname");
            return (Criteria) this;
        }

        public Criteria andCCouponsnameLessThanOrEqualTo(String value) {
            addCriterion("c_couponsName <=", value, "cCouponsname");
            return (Criteria) this;
        }

        public Criteria andCCouponsnameLike(String value) {
            addCriterion("c_couponsName like", value, "cCouponsname");
            return (Criteria) this;
        }

        public Criteria andCCouponsnameNotLike(String value) {
            addCriterion("c_couponsName not like", value, "cCouponsname");
            return (Criteria) this;
        }

        public Criteria andCCouponsnameIn(List<String> values) {
            addCriterion("c_couponsName in", values, "cCouponsname");
            return (Criteria) this;
        }

        public Criteria andCCouponsnameNotIn(List<String> values) {
            addCriterion("c_couponsName not in", values, "cCouponsname");
            return (Criteria) this;
        }

        public Criteria andCCouponsnameBetween(String value1, String value2) {
            addCriterion("c_couponsName between", value1, value2, "cCouponsname");
            return (Criteria) this;
        }

        public Criteria andCCouponsnameNotBetween(String value1, String value2) {
            addCriterion("c_couponsName not between", value1, value2, "cCouponsname");
            return (Criteria) this;
        }

        public Criteria andCCouponstypeIsNull() {
            addCriterion("c_couponsType is null");
            return (Criteria) this;
        }

        public Criteria andCCouponstypeIsNotNull() {
            addCriterion("c_couponsType is not null");
            return (Criteria) this;
        }

        public Criteria andCCouponstypeEqualTo(Integer value) {
            addCriterion("c_couponsType =", value, "cCouponstype");
            return (Criteria) this;
        }

        public Criteria andCCouponstypeNotEqualTo(Integer value) {
            addCriterion("c_couponsType <>", value, "cCouponstype");
            return (Criteria) this;
        }

        public Criteria andCCouponstypeGreaterThan(Integer value) {
            addCriterion("c_couponsType >", value, "cCouponstype");
            return (Criteria) this;
        }

        public Criteria andCCouponstypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("c_couponsType >=", value, "cCouponstype");
            return (Criteria) this;
        }

        public Criteria andCCouponstypeLessThan(Integer value) {
            addCriterion("c_couponsType <", value, "cCouponstype");
            return (Criteria) this;
        }

        public Criteria andCCouponstypeLessThanOrEqualTo(Integer value) {
            addCriterion("c_couponsType <=", value, "cCouponstype");
            return (Criteria) this;
        }

        public Criteria andCCouponstypeIn(List<Integer> values) {
            addCriterion("c_couponsType in", values, "cCouponstype");
            return (Criteria) this;
        }

        public Criteria andCCouponstypeNotIn(List<Integer> values) {
            addCriterion("c_couponsType not in", values, "cCouponstype");
            return (Criteria) this;
        }

        public Criteria andCCouponstypeBetween(Integer value1, Integer value2) {
            addCriterion("c_couponsType between", value1, value2, "cCouponstype");
            return (Criteria) this;
        }

        public Criteria andCCouponstypeNotBetween(Integer value1, Integer value2) {
            addCriterion("c_couponsType not between", value1, value2, "cCouponstype");
            return (Criteria) this;
        }

        public Criteria andCDeliveringwayIsNull() {
            addCriterion("c_deliveringWay is null");
            return (Criteria) this;
        }

        public Criteria andCDeliveringwayIsNotNull() {
            addCriterion("c_deliveringWay is not null");
            return (Criteria) this;
        }

        public Criteria andCDeliveringwayEqualTo(Integer value) {
            addCriterion("c_deliveringWay =", value, "cDeliveringway");
            return (Criteria) this;
        }

        public Criteria andCDeliveringwayNotEqualTo(Integer value) {
            addCriterion("c_deliveringWay <>", value, "cDeliveringway");
            return (Criteria) this;
        }

        public Criteria andCDeliveringwayGreaterThan(Integer value) {
            addCriterion("c_deliveringWay >", value, "cDeliveringway");
            return (Criteria) this;
        }

        public Criteria andCDeliveringwayGreaterThanOrEqualTo(Integer value) {
            addCriterion("c_deliveringWay >=", value, "cDeliveringway");
            return (Criteria) this;
        }

        public Criteria andCDeliveringwayLessThan(Integer value) {
            addCriterion("c_deliveringWay <", value, "cDeliveringway");
            return (Criteria) this;
        }

        public Criteria andCDeliveringwayLessThanOrEqualTo(Integer value) {
            addCriterion("c_deliveringWay <=", value, "cDeliveringway");
            return (Criteria) this;
        }

        public Criteria andCDeliveringwayIn(List<Integer> values) {
            addCriterion("c_deliveringWay in", values, "cDeliveringway");
            return (Criteria) this;
        }

        public Criteria andCDeliveringwayNotIn(List<Integer> values) {
            addCriterion("c_deliveringWay not in", values, "cDeliveringway");
            return (Criteria) this;
        }

        public Criteria andCDeliveringwayBetween(Integer value1, Integer value2) {
            addCriterion("c_deliveringWay between", value1, value2, "cDeliveringway");
            return (Criteria) this;
        }

        public Criteria andCDeliveringwayNotBetween(Integer value1, Integer value2) {
            addCriterion("c_deliveringWay not between", value1, value2, "cDeliveringway");
            return (Criteria) this;
        }

        public Criteria andCChannelIsNull() {
            addCriterion("c_channel is null");
            return (Criteria) this;
        }

        public Criteria andCChannelIsNotNull() {
            addCriterion("c_channel is not null");
            return (Criteria) this;
        }

        public Criteria andCChannelEqualTo(String value) {
            addCriterion("c_channel =", value, "cChannel");
            return (Criteria) this;
        }

        public Criteria andCChannelNotEqualTo(String value) {
            addCriterion("c_channel <>", value, "cChannel");
            return (Criteria) this;
        }

        public Criteria andCChannelGreaterThan(String value) {
            addCriterion("c_channel >", value, "cChannel");
            return (Criteria) this;
        }

        public Criteria andCChannelGreaterThanOrEqualTo(String value) {
            addCriterion("c_channel >=", value, "cChannel");
            return (Criteria) this;
        }

        public Criteria andCChannelLessThan(String value) {
            addCriterion("c_channel <", value, "cChannel");
            return (Criteria) this;
        }

        public Criteria andCChannelLessThanOrEqualTo(String value) {
            addCriterion("c_channel <=", value, "cChannel");
            return (Criteria) this;
        }

        public Criteria andCChannelLike(String value) {
            addCriterion("c_channel like", value, "cChannel");
            return (Criteria) this;
        }

        public Criteria andCChannelNotLike(String value) {
            addCriterion("c_channel not like", value, "cChannel");
            return (Criteria) this;
        }

        public Criteria andCChannelIn(List<String> values) {
            addCriterion("c_channel in", values, "cChannel");
            return (Criteria) this;
        }

        public Criteria andCChannelNotIn(List<String> values) {
            addCriterion("c_channel not in", values, "cChannel");
            return (Criteria) this;
        }

        public Criteria andCChannelBetween(String value1, String value2) {
            addCriterion("c_channel between", value1, value2, "cChannel");
            return (Criteria) this;
        }

        public Criteria andCChannelNotBetween(String value1, String value2) {
            addCriterion("c_channel not between", value1, value2, "cChannel");
            return (Criteria) this;
        }

        public Criteria andCUserchannelIsNull() {
            addCriterion("c_userChannel is null");
            return (Criteria) this;
        }

        public Criteria andCUserchannelIsNotNull() {
            addCriterion("c_userChannel is not null");
            return (Criteria) this;
        }

        public Criteria andCUserchannelEqualTo(Integer value) {
            addCriterion("c_userChannel =", value, "cUserchannel");
            return (Criteria) this;
        }

        public Criteria andCUserchannelNotEqualTo(Integer value) {
            addCriterion("c_userChannel <>", value, "cUserchannel");
            return (Criteria) this;
        }

        public Criteria andCUserchannelGreaterThan(Integer value) {
            addCriterion("c_userChannel >", value, "cUserchannel");
            return (Criteria) this;
        }

        public Criteria andCUserchannelGreaterThanOrEqualTo(Integer value) {
            addCriterion("c_userChannel >=", value, "cUserchannel");
            return (Criteria) this;
        }

        public Criteria andCUserchannelLessThan(Integer value) {
            addCriterion("c_userChannel <", value, "cUserchannel");
            return (Criteria) this;
        }

        public Criteria andCUserchannelLessThanOrEqualTo(Integer value) {
            addCriterion("c_userChannel <=", value, "cUserchannel");
            return (Criteria) this;
        }

        public Criteria andCUserchannelIn(List<Integer> values) {
            addCriterion("c_userChannel in", values, "cUserchannel");
            return (Criteria) this;
        }

        public Criteria andCUserchannelNotIn(List<Integer> values) {
            addCriterion("c_userChannel not in", values, "cUserchannel");
            return (Criteria) this;
        }

        public Criteria andCUserchannelBetween(Integer value1, Integer value2) {
            addCriterion("c_userChannel between", value1, value2, "cUserchannel");
            return (Criteria) this;
        }

        public Criteria andCUserchannelNotBetween(Integer value1, Integer value2) {
            addCriterion("c_userChannel not between", value1, value2, "cUserchannel");
            return (Criteria) this;
        }

        public Criteria andCCouponsmsgIsNull() {
            addCriterion("c_couponsMsg is null");
            return (Criteria) this;
        }

        public Criteria andCCouponsmsgIsNotNull() {
            addCriterion("c_couponsMsg is not null");
            return (Criteria) this;
        }

        public Criteria andCCouponsmsgEqualTo(String value) {
            addCriterion("c_couponsMsg =", value, "cCouponsmsg");
            return (Criteria) this;
        }

        public Criteria andCCouponsmsgNotEqualTo(String value) {
            addCriterion("c_couponsMsg <>", value, "cCouponsmsg");
            return (Criteria) this;
        }

        public Criteria andCCouponsmsgGreaterThan(String value) {
            addCriterion("c_couponsMsg >", value, "cCouponsmsg");
            return (Criteria) this;
        }

        public Criteria andCCouponsmsgGreaterThanOrEqualTo(String value) {
            addCriterion("c_couponsMsg >=", value, "cCouponsmsg");
            return (Criteria) this;
        }

        public Criteria andCCouponsmsgLessThan(String value) {
            addCriterion("c_couponsMsg <", value, "cCouponsmsg");
            return (Criteria) this;
        }

        public Criteria andCCouponsmsgLessThanOrEqualTo(String value) {
            addCriterion("c_couponsMsg <=", value, "cCouponsmsg");
            return (Criteria) this;
        }

        public Criteria andCCouponsmsgLike(String value) {
            addCriterion("c_couponsMsg like", value, "cCouponsmsg");
            return (Criteria) this;
        }

        public Criteria andCCouponsmsgNotLike(String value) {
            addCriterion("c_couponsMsg not like", value, "cCouponsmsg");
            return (Criteria) this;
        }

        public Criteria andCCouponsmsgIn(List<String> values) {
            addCriterion("c_couponsMsg in", values, "cCouponsmsg");
            return (Criteria) this;
        }

        public Criteria andCCouponsmsgNotIn(List<String> values) {
            addCriterion("c_couponsMsg not in", values, "cCouponsmsg");
            return (Criteria) this;
        }

        public Criteria andCCouponsmsgBetween(String value1, String value2) {
            addCriterion("c_couponsMsg between", value1, value2, "cCouponsmsg");
            return (Criteria) this;
        }

        public Criteria andCCouponsmsgNotBetween(String value1, String value2) {
            addCriterion("c_couponsMsg not between", value1, value2, "cCouponsmsg");
            return (Criteria) this;
        }

        public Criteria andCAvaliabledorderamtIsNull() {
            addCriterion("c_avaliabledOrderAmt is null");
            return (Criteria) this;
        }

        public Criteria andCAvaliabledorderamtIsNotNull() {
            addCriterion("c_avaliabledOrderAmt is not null");
            return (Criteria) this;
        }

        public Criteria andCAvaliabledorderamtEqualTo(Double value) {
            addCriterion("c_avaliabledOrderAmt =", value, "cAvaliabledorderamt");
            return (Criteria) this;
        }

        public Criteria andCAvaliabledorderamtNotEqualTo(Double value) {
            addCriterion("c_avaliabledOrderAmt <>", value, "cAvaliabledorderamt");
            return (Criteria) this;
        }

        public Criteria andCAvaliabledorderamtGreaterThan(Double value) {
            addCriterion("c_avaliabledOrderAmt >", value, "cAvaliabledorderamt");
            return (Criteria) this;
        }

        public Criteria andCAvaliabledorderamtGreaterThanOrEqualTo(Double value) {
            addCriterion("c_avaliabledOrderAmt >=", value, "cAvaliabledorderamt");
            return (Criteria) this;
        }

        public Criteria andCAvaliabledorderamtLessThan(Double value) {
            addCriterion("c_avaliabledOrderAmt <", value, "cAvaliabledorderamt");
            return (Criteria) this;
        }

        public Criteria andCAvaliabledorderamtLessThanOrEqualTo(Double value) {
            addCriterion("c_avaliabledOrderAmt <=", value, "cAvaliabledorderamt");
            return (Criteria) this;
        }

        public Criteria andCAvaliabledorderamtIn(List<Double> values) {
            addCriterion("c_avaliabledOrderAmt in", values, "cAvaliabledorderamt");
            return (Criteria) this;
        }

        public Criteria andCAvaliabledorderamtNotIn(List<Double> values) {
            addCriterion("c_avaliabledOrderAmt not in", values, "cAvaliabledorderamt");
            return (Criteria) this;
        }

        public Criteria andCAvaliabledorderamtBetween(Double value1, Double value2) {
            addCriterion("c_avaliabledOrderAmt between", value1, value2, "cAvaliabledorderamt");
            return (Criteria) this;
        }

        public Criteria andCAvaliabledorderamtNotBetween(Double value1, Double value2) {
            addCriterion("c_avaliabledOrderAmt not between", value1, value2, "cAvaliabledorderamt");
            return (Criteria) this;
        }

        public Criteria andCDailylimitIsNull() {
            addCriterion("c_dailyLimit is null");
            return (Criteria) this;
        }

        public Criteria andCDailylimitIsNotNull() {
            addCriterion("c_dailyLimit is not null");
            return (Criteria) this;
        }

        public Criteria andCDailylimitEqualTo(Integer value) {
            addCriterion("c_dailyLimit =", value, "cDailylimit");
            return (Criteria) this;
        }

        public Criteria andCDailylimitNotEqualTo(Integer value) {
            addCriterion("c_dailyLimit <>", value, "cDailylimit");
            return (Criteria) this;
        }

        public Criteria andCDailylimitGreaterThan(Integer value) {
            addCriterion("c_dailyLimit >", value, "cDailylimit");
            return (Criteria) this;
        }

        public Criteria andCDailylimitGreaterThanOrEqualTo(Integer value) {
            addCriterion("c_dailyLimit >=", value, "cDailylimit");
            return (Criteria) this;
        }

        public Criteria andCDailylimitLessThan(Integer value) {
            addCriterion("c_dailyLimit <", value, "cDailylimit");
            return (Criteria) this;
        }

        public Criteria andCDailylimitLessThanOrEqualTo(Integer value) {
            addCriterion("c_dailyLimit <=", value, "cDailylimit");
            return (Criteria) this;
        }

        public Criteria andCDailylimitIn(List<Integer> values) {
            addCriterion("c_dailyLimit in", values, "cDailylimit");
            return (Criteria) this;
        }

        public Criteria andCDailylimitNotIn(List<Integer> values) {
            addCriterion("c_dailyLimit not in", values, "cDailylimit");
            return (Criteria) this;
        }

        public Criteria andCDailylimitBetween(Integer value1, Integer value2) {
            addCriterion("c_dailyLimit between", value1, value2, "cDailylimit");
            return (Criteria) this;
        }

        public Criteria andCDailylimitNotBetween(Integer value1, Integer value2) {
            addCriterion("c_dailyLimit not between", value1, value2, "cDailylimit");
            return (Criteria) this;
        }

        public Criteria andCPresendcountIsNull() {
            addCriterion("c_preSendCount is null");
            return (Criteria) this;
        }

        public Criteria andCPresendcountIsNotNull() {
            addCriterion("c_preSendCount is not null");
            return (Criteria) this;
        }

        public Criteria andCPresendcountEqualTo(Integer value) {
            addCriterion("c_preSendCount =", value, "cPresendcount");
            return (Criteria) this;
        }

        public Criteria andCPresendcountNotEqualTo(Integer value) {
            addCriterion("c_preSendCount <>", value, "cPresendcount");
            return (Criteria) this;
        }

        public Criteria andCPresendcountGreaterThan(Integer value) {
            addCriterion("c_preSendCount >", value, "cPresendcount");
            return (Criteria) this;
        }

        public Criteria andCPresendcountGreaterThanOrEqualTo(Integer value) {
            addCriterion("c_preSendCount >=", value, "cPresendcount");
            return (Criteria) this;
        }

        public Criteria andCPresendcountLessThan(Integer value) {
            addCriterion("c_preSendCount <", value, "cPresendcount");
            return (Criteria) this;
        }

        public Criteria andCPresendcountLessThanOrEqualTo(Integer value) {
            addCriterion("c_preSendCount <=", value, "cPresendcount");
            return (Criteria) this;
        }

        public Criteria andCPresendcountIn(List<Integer> values) {
            addCriterion("c_preSendCount in", values, "cPresendcount");
            return (Criteria) this;
        }

        public Criteria andCPresendcountNotIn(List<Integer> values) {
            addCriterion("c_preSendCount not in", values, "cPresendcount");
            return (Criteria) this;
        }

        public Criteria andCPresendcountBetween(Integer value1, Integer value2) {
            addCriterion("c_preSendCount between", value1, value2, "cPresendcount");
            return (Criteria) this;
        }

        public Criteria andCPresendcountNotBetween(Integer value1, Integer value2) {
            addCriterion("c_preSendCount not between", value1, value2, "cPresendcount");
            return (Criteria) this;
        }

        public Criteria andCPermanentIsNull() {
            addCriterion("c_permanent is null");
            return (Criteria) this;
        }

        public Criteria andCPermanentIsNotNull() {
            addCriterion("c_permanent is not null");
            return (Criteria) this;
        }

        public Criteria andCPermanentEqualTo(Integer value) {
            addCriterion("c_permanent =", value, "cPermanent");
            return (Criteria) this;
        }

        public Criteria andCPermanentNotEqualTo(Integer value) {
            addCriterion("c_permanent <>", value, "cPermanent");
            return (Criteria) this;
        }

        public Criteria andCPermanentGreaterThan(Integer value) {
            addCriterion("c_permanent >", value, "cPermanent");
            return (Criteria) this;
        }

        public Criteria andCPermanentGreaterThanOrEqualTo(Integer value) {
            addCriterion("c_permanent >=", value, "cPermanent");
            return (Criteria) this;
        }

        public Criteria andCPermanentLessThan(Integer value) {
            addCriterion("c_permanent <", value, "cPermanent");
            return (Criteria) this;
        }

        public Criteria andCPermanentLessThanOrEqualTo(Integer value) {
            addCriterion("c_permanent <=", value, "cPermanent");
            return (Criteria) this;
        }

        public Criteria andCPermanentIn(List<Integer> values) {
            addCriterion("c_permanent in", values, "cPermanent");
            return (Criteria) this;
        }

        public Criteria andCPermanentNotIn(List<Integer> values) {
            addCriterion("c_permanent not in", values, "cPermanent");
            return (Criteria) this;
        }

        public Criteria andCPermanentBetween(Integer value1, Integer value2) {
            addCriterion("c_permanent between", value1, value2, "cPermanent");
            return (Criteria) this;
        }

        public Criteria andCPermanentNotBetween(Integer value1, Integer value2) {
            addCriterion("c_permanent not between", value1, value2, "cPermanent");
            return (Criteria) this;
        }

        public Criteria andCLimitstartIsNull() {
            addCriterion("c_limitStart is null");
            return (Criteria) this;
        }

        public Criteria andCLimitstartIsNotNull() {
            addCriterion("c_limitStart is not null");
            return (Criteria) this;
        }

        public Criteria andCLimitstartEqualTo(Date value) {
            addCriterion("c_limitStart =", value, "cLimitstart");
            return (Criteria) this;
        }

        public Criteria andCLimitstartNotEqualTo(Date value) {
            addCriterion("c_limitStart <>", value, "cLimitstart");
            return (Criteria) this;
        }

        public Criteria andCLimitstartGreaterThan(Date value) {
            addCriterion("c_limitStart >", value, "cLimitstart");
            return (Criteria) this;
        }

        public Criteria andCLimitstartGreaterThanOrEqualTo(Date value) {
            addCriterion("c_limitStart >=", value, "cLimitstart");
            return (Criteria) this;
        }

        public Criteria andCLimitstartLessThan(Date value) {
            addCriterion("c_limitStart <", value, "cLimitstart");
            return (Criteria) this;
        }

        public Criteria andCLimitstartLessThanOrEqualTo(Date value) {
            addCriterion("c_limitStart <=", value, "cLimitstart");
            return (Criteria) this;
        }

        public Criteria andCLimitstartIn(List<Date> values) {
            addCriterion("c_limitStart in", values, "cLimitstart");
            return (Criteria) this;
        }

        public Criteria andCLimitstartNotIn(List<Date> values) {
            addCriterion("c_limitStart not in", values, "cLimitstart");
            return (Criteria) this;
        }

        public Criteria andCLimitstartBetween(Date value1, Date value2) {
            addCriterion("c_limitStart between", value1, value2, "cLimitstart");
            return (Criteria) this;
        }

        public Criteria andCLimitstartNotBetween(Date value1, Date value2) {
            addCriterion("c_limitStart not between", value1, value2, "cLimitstart");
            return (Criteria) this;
        }

        public Criteria andCLimitendIsNull() {
            addCriterion("c_limitEnd is null");
            return (Criteria) this;
        }

        public Criteria andCLimitendIsNotNull() {
            addCriterion("c_limitEnd is not null");
            return (Criteria) this;
        }

        public Criteria andCLimitendEqualTo(Date value) {
            addCriterion("c_limitEnd =", value, "cLimitend");
            return (Criteria) this;
        }

        public Criteria andCLimitendNotEqualTo(Date value) {
            addCriterion("c_limitEnd <>", value, "cLimitend");
            return (Criteria) this;
        }

        public Criteria andCLimitendGreaterThan(Date value) {
            addCriterion("c_limitEnd >", value, "cLimitend");
            return (Criteria) this;
        }

        public Criteria andCLimitendGreaterThanOrEqualTo(Date value) {
            addCriterion("c_limitEnd >=", value, "cLimitend");
            return (Criteria) this;
        }

        public Criteria andCLimitendLessThan(Date value) {
            addCriterion("c_limitEnd <", value, "cLimitend");
            return (Criteria) this;
        }

        public Criteria andCLimitendLessThanOrEqualTo(Date value) {
            addCriterion("c_limitEnd <=", value, "cLimitend");
            return (Criteria) this;
        }

        public Criteria andCLimitendIn(List<Date> values) {
            addCriterion("c_limitEnd in", values, "cLimitend");
            return (Criteria) this;
        }

        public Criteria andCLimitendNotIn(List<Date> values) {
            addCriterion("c_limitEnd not in", values, "cLimitend");
            return (Criteria) this;
        }

        public Criteria andCLimitendBetween(Date value1, Date value2) {
            addCriterion("c_limitEnd between", value1, value2, "cLimitend");
            return (Criteria) this;
        }

        public Criteria andCLimitendNotBetween(Date value1, Date value2) {
            addCriterion("c_limitEnd not between", value1, value2, "cLimitend");
            return (Criteria) this;
        }

        public Criteria andCCumulativeIsNull() {
            addCriterion("c_cumulative is null");
            return (Criteria) this;
        }

        public Criteria andCCumulativeIsNotNull() {
            addCriterion("c_cumulative is not null");
            return (Criteria) this;
        }

        public Criteria andCCumulativeEqualTo(Integer value) {
            addCriterion("c_cumulative =", value, "cCumulative");
            return (Criteria) this;
        }

        public Criteria andCCumulativeNotEqualTo(Integer value) {
            addCriterion("c_cumulative <>", value, "cCumulative");
            return (Criteria) this;
        }

        public Criteria andCCumulativeGreaterThan(Integer value) {
            addCriterion("c_cumulative >", value, "cCumulative");
            return (Criteria) this;
        }

        public Criteria andCCumulativeGreaterThanOrEqualTo(Integer value) {
            addCriterion("c_cumulative >=", value, "cCumulative");
            return (Criteria) this;
        }

        public Criteria andCCumulativeLessThan(Integer value) {
            addCriterion("c_cumulative <", value, "cCumulative");
            return (Criteria) this;
        }

        public Criteria andCCumulativeLessThanOrEqualTo(Integer value) {
            addCriterion("c_cumulative <=", value, "cCumulative");
            return (Criteria) this;
        }

        public Criteria andCCumulativeIn(List<Integer> values) {
            addCriterion("c_cumulative in", values, "cCumulative");
            return (Criteria) this;
        }

        public Criteria andCCumulativeNotIn(List<Integer> values) {
            addCriterion("c_cumulative not in", values, "cCumulative");
            return (Criteria) this;
        }

        public Criteria andCCumulativeBetween(Integer value1, Integer value2) {
            addCriterion("c_cumulative between", value1, value2, "cCumulative");
            return (Criteria) this;
        }

        public Criteria andCCumulativeNotBetween(Integer value1, Integer value2) {
            addCriterion("c_cumulative not between", value1, value2, "cCumulative");
            return (Criteria) this;
        }

        public Criteria andCActivityareaIsNull() {
            addCriterion("c_activityArea is null");
            return (Criteria) this;
        }

        public Criteria andCActivityareaIsNotNull() {
            addCriterion("c_activityArea is not null");
            return (Criteria) this;
        }

        public Criteria andCActivityareaEqualTo(String value) {
            addCriterion("c_activityArea =", value, "cActivityarea");
            return (Criteria) this;
        }

        public Criteria andCActivityareaNotEqualTo(String value) {
            addCriterion("c_activityArea <>", value, "cActivityarea");
            return (Criteria) this;
        }

        public Criteria andCActivityareaGreaterThan(String value) {
            addCriterion("c_activityArea >", value, "cActivityarea");
            return (Criteria) this;
        }

        public Criteria andCActivityareaGreaterThanOrEqualTo(String value) {
            addCriterion("c_activityArea >=", value, "cActivityarea");
            return (Criteria) this;
        }

        public Criteria andCActivityareaLessThan(String value) {
            addCriterion("c_activityArea <", value, "cActivityarea");
            return (Criteria) this;
        }

        public Criteria andCActivityareaLessThanOrEqualTo(String value) {
            addCriterion("c_activityArea <=", value, "cActivityarea");
            return (Criteria) this;
        }

        public Criteria andCActivityareaLike(String value) {
            addCriterion("c_activityArea like", value, "cActivityarea");
            return (Criteria) this;
        }

        public Criteria andCActivityareaNotLike(String value) {
            addCriterion("c_activityArea not like", value, "cActivityarea");
            return (Criteria) this;
        }

        public Criteria andCActivityareaIn(List<String> values) {
            addCriterion("c_activityArea in", values, "cActivityarea");
            return (Criteria) this;
        }

        public Criteria andCActivityareaNotIn(List<String> values) {
            addCriterion("c_activityArea not in", values, "cActivityarea");
            return (Criteria) this;
        }

        public Criteria andCActivityareaBetween(String value1, String value2) {
            addCriterion("c_activityArea between", value1, value2, "cActivityarea");
            return (Criteria) this;
        }

        public Criteria andCActivityareaNotBetween(String value1, String value2) {
            addCriterion("c_activityArea not between", value1, value2, "cActivityarea");
            return (Criteria) this;
        }

        public Criteria andCActivityzoneIsNull() {
            addCriterion("c_activityZone is null");
            return (Criteria) this;
        }

        public Criteria andCActivityzoneIsNotNull() {
            addCriterion("c_activityZone is not null");
            return (Criteria) this;
        }

        public Criteria andCActivityzoneEqualTo(String value) {
            addCriterion("c_activityZone =", value, "cActivityzone");
            return (Criteria) this;
        }

        public Criteria andCActivityzoneNotEqualTo(String value) {
            addCriterion("c_activityZone <>", value, "cActivityzone");
            return (Criteria) this;
        }

        public Criteria andCActivityzoneGreaterThan(String value) {
            addCriterion("c_activityZone >", value, "cActivityzone");
            return (Criteria) this;
        }

        public Criteria andCActivityzoneGreaterThanOrEqualTo(String value) {
            addCriterion("c_activityZone >=", value, "cActivityzone");
            return (Criteria) this;
        }

        public Criteria andCActivityzoneLessThan(String value) {
            addCriterion("c_activityZone <", value, "cActivityzone");
            return (Criteria) this;
        }

        public Criteria andCActivityzoneLessThanOrEqualTo(String value) {
            addCriterion("c_activityZone <=", value, "cActivityzone");
            return (Criteria) this;
        }

        public Criteria andCActivityzoneLike(String value) {
            addCriterion("c_activityZone like", value, "cActivityzone");
            return (Criteria) this;
        }

        public Criteria andCActivityzoneNotLike(String value) {
            addCriterion("c_activityZone not like", value, "cActivityzone");
            return (Criteria) this;
        }

        public Criteria andCActivityzoneIn(List<String> values) {
            addCriterion("c_activityZone in", values, "cActivityzone");
            return (Criteria) this;
        }

        public Criteria andCActivityzoneNotIn(List<String> values) {
            addCriterion("c_activityZone not in", values, "cActivityzone");
            return (Criteria) this;
        }

        public Criteria andCActivityzoneBetween(String value1, String value2) {
            addCriterion("c_activityZone between", value1, value2, "cActivityzone");
            return (Criteria) this;
        }

        public Criteria andCActivityzoneNotBetween(String value1, String value2) {
            addCriterion("c_activityZone not between", value1, value2, "cActivityzone");
            return (Criteria) this;
        }

        public Criteria andCActivitystarttimeIsNull() {
            addCriterion("c_activityStartTime is null");
            return (Criteria) this;
        }

        public Criteria andCActivitystarttimeIsNotNull() {
            addCriterion("c_activityStartTime is not null");
            return (Criteria) this;
        }

        public Criteria andCActivitystarttimeEqualTo(Date value) {
            addCriterion("c_activityStartTime =", value, "cActivitystarttime");
            return (Criteria) this;
        }

        public Criteria andCActivitystarttimeNotEqualTo(Date value) {
            addCriterion("c_activityStartTime <>", value, "cActivitystarttime");
            return (Criteria) this;
        }

        public Criteria andCActivitystarttimeGreaterThan(Date value) {
            addCriterion("c_activityStartTime >", value, "cActivitystarttime");
            return (Criteria) this;
        }

        public Criteria andCActivitystarttimeGreaterThanOrEqualTo(Date value) {
            addCriterion("c_activityStartTime >=", value, "cActivitystarttime");
            return (Criteria) this;
        }

        public Criteria andCActivitystarttimeLessThan(Date value) {
            addCriterion("c_activityStartTime <", value, "cActivitystarttime");
            return (Criteria) this;
        }

        public Criteria andCActivitystarttimeLessThanOrEqualTo(Date value) {
            addCriterion("c_activityStartTime <=", value, "cActivitystarttime");
            return (Criteria) this;
        }

        public Criteria andCActivitystarttimeIn(List<Date> values) {
            addCriterion("c_activityStartTime in", values, "cActivitystarttime");
            return (Criteria) this;
        }

        public Criteria andCActivitystarttimeNotIn(List<Date> values) {
            addCriterion("c_activityStartTime not in", values, "cActivitystarttime");
            return (Criteria) this;
        }

        public Criteria andCActivitystarttimeBetween(Date value1, Date value2) {
            addCriterion("c_activityStartTime between", value1, value2, "cActivitystarttime");
            return (Criteria) this;
        }

        public Criteria andCActivitystarttimeNotBetween(Date value1, Date value2) {
            addCriterion("c_activityStartTime not between", value1, value2, "cActivitystarttime");
            return (Criteria) this;
        }

        public Criteria andCProvideallIsNull() {
            addCriterion("c_provideAll is null");
            return (Criteria) this;
        }

        public Criteria andCProvideallIsNotNull() {
            addCriterion("c_provideAll is not null");
            return (Criteria) this;
        }

        public Criteria andCProvideallEqualTo(Integer value) {
            addCriterion("c_provideAll =", value, "cProvideall");
            return (Criteria) this;
        }

        public Criteria andCProvideallNotEqualTo(Integer value) {
            addCriterion("c_provideAll <>", value, "cProvideall");
            return (Criteria) this;
        }

        public Criteria andCProvideallGreaterThan(Integer value) {
            addCriterion("c_provideAll >", value, "cProvideall");
            return (Criteria) this;
        }

        public Criteria andCProvideallGreaterThanOrEqualTo(Integer value) {
            addCriterion("c_provideAll >=", value, "cProvideall");
            return (Criteria) this;
        }

        public Criteria andCProvideallLessThan(Integer value) {
            addCriterion("c_provideAll <", value, "cProvideall");
            return (Criteria) this;
        }

        public Criteria andCProvideallLessThanOrEqualTo(Integer value) {
            addCriterion("c_provideAll <=", value, "cProvideall");
            return (Criteria) this;
        }

        public Criteria andCProvideallIn(List<Integer> values) {
            addCriterion("c_provideAll in", values, "cProvideall");
            return (Criteria) this;
        }

        public Criteria andCProvideallNotIn(List<Integer> values) {
            addCriterion("c_provideAll not in", values, "cProvideall");
            return (Criteria) this;
        }

        public Criteria andCProvideallBetween(Integer value1, Integer value2) {
            addCriterion("c_provideAll between", value1, value2, "cProvideall");
            return (Criteria) this;
        }

        public Criteria andCProvideallNotBetween(Integer value1, Integer value2) {
            addCriterion("c_provideAll not between", value1, value2, "cProvideall");
            return (Criteria) this;
        }

        public Criteria andCAuditstatusIsNull() {
            addCriterion("c_auditStatus is null");
            return (Criteria) this;
        }

        public Criteria andCAuditstatusIsNotNull() {
            addCriterion("c_auditStatus is not null");
            return (Criteria) this;
        }

        public Criteria andCAuditstatusEqualTo(Integer value) {
            addCriterion("c_auditStatus =", value, "cAuditstatus");
            return (Criteria) this;
        }

        public Criteria andCAuditstatusNotEqualTo(Integer value) {
            addCriterion("c_auditStatus <>", value, "cAuditstatus");
            return (Criteria) this;
        }

        public Criteria andCAuditstatusGreaterThan(Integer value) {
            addCriterion("c_auditStatus >", value, "cAuditstatus");
            return (Criteria) this;
        }

        public Criteria andCAuditstatusGreaterThanOrEqualTo(Integer value) {
            addCriterion("c_auditStatus >=", value, "cAuditstatus");
            return (Criteria) this;
        }

        public Criteria andCAuditstatusLessThan(Integer value) {
            addCriterion("c_auditStatus <", value, "cAuditstatus");
            return (Criteria) this;
        }

        public Criteria andCAuditstatusLessThanOrEqualTo(Integer value) {
            addCriterion("c_auditStatus <=", value, "cAuditstatus");
            return (Criteria) this;
        }

        public Criteria andCAuditstatusIn(List<Integer> values) {
            addCriterion("c_auditStatus in", values, "cAuditstatus");
            return (Criteria) this;
        }

        public Criteria andCAuditstatusNotIn(List<Integer> values) {
            addCriterion("c_auditStatus not in", values, "cAuditstatus");
            return (Criteria) this;
        }

        public Criteria andCAuditstatusBetween(Integer value1, Integer value2) {
            addCriterion("c_auditStatus between", value1, value2, "cAuditstatus");
            return (Criteria) this;
        }

        public Criteria andCAuditstatusNotBetween(Integer value1, Integer value2) {
            addCriterion("c_auditStatus not between", value1, value2, "cAuditstatus");
            return (Criteria) this;
        }

        public Criteria andCCreatedateIsNull() {
            addCriterion("c_createDate is null");
            return (Criteria) this;
        }

        public Criteria andCCreatedateIsNotNull() {
            addCriterion("c_createDate is not null");
            return (Criteria) this;
        }

        public Criteria andCCreatedateEqualTo(Date value) {
            addCriterion("c_createDate =", value, "cCreatedate");
            return (Criteria) this;
        }

        public Criteria andCCreatedateNotEqualTo(Date value) {
            addCriterion("c_createDate <>", value, "cCreatedate");
            return (Criteria) this;
        }

        public Criteria andCCreatedateGreaterThan(Date value) {
            addCriterion("c_createDate >", value, "cCreatedate");
            return (Criteria) this;
        }

        public Criteria andCCreatedateGreaterThanOrEqualTo(Date value) {
            addCriterion("c_createDate >=", value, "cCreatedate");
            return (Criteria) this;
        }

        public Criteria andCCreatedateLessThan(Date value) {
            addCriterion("c_createDate <", value, "cCreatedate");
            return (Criteria) this;
        }

        public Criteria andCCreatedateLessThanOrEqualTo(Date value) {
            addCriterion("c_createDate <=", value, "cCreatedate");
            return (Criteria) this;
        }

        public Criteria andCCreatedateIn(List<Date> values) {
            addCriterion("c_createDate in", values, "cCreatedate");
            return (Criteria) this;
        }

        public Criteria andCCreatedateNotIn(List<Date> values) {
            addCriterion("c_createDate not in", values, "cCreatedate");
            return (Criteria) this;
        }

        public Criteria andCCreatedateBetween(Date value1, Date value2) {
            addCriterion("c_createDate between", value1, value2, "cCreatedate");
            return (Criteria) this;
        }

        public Criteria andCCreatedateNotBetween(Date value1, Date value2) {
            addCriterion("c_createDate not between", value1, value2, "cCreatedate");
            return (Criteria) this;
        }

        public Criteria andCCreateuserIsNull() {
            addCriterion("c_createUser is null");
            return (Criteria) this;
        }

        public Criteria andCCreateuserIsNotNull() {
            addCriterion("c_createUser is not null");
            return (Criteria) this;
        }

        public Criteria andCCreateuserEqualTo(String value) {
            addCriterion("c_createUser =", value, "cCreateuser");
            return (Criteria) this;
        }

        public Criteria andCCreateuserNotEqualTo(String value) {
            addCriterion("c_createUser <>", value, "cCreateuser");
            return (Criteria) this;
        }

        public Criteria andCCreateuserGreaterThan(String value) {
            addCriterion("c_createUser >", value, "cCreateuser");
            return (Criteria) this;
        }

        public Criteria andCCreateuserGreaterThanOrEqualTo(String value) {
            addCriterion("c_createUser >=", value, "cCreateuser");
            return (Criteria) this;
        }

        public Criteria andCCreateuserLessThan(String value) {
            addCriterion("c_createUser <", value, "cCreateuser");
            return (Criteria) this;
        }

        public Criteria andCCreateuserLessThanOrEqualTo(String value) {
            addCriterion("c_createUser <=", value, "cCreateuser");
            return (Criteria) this;
        }

        public Criteria andCCreateuserLike(String value) {
            addCriterion("c_createUser like", value, "cCreateuser");
            return (Criteria) this;
        }

        public Criteria andCCreateuserNotLike(String value) {
            addCriterion("c_createUser not like", value, "cCreateuser");
            return (Criteria) this;
        }

        public Criteria andCCreateuserIn(List<String> values) {
            addCriterion("c_createUser in", values, "cCreateuser");
            return (Criteria) this;
        }

        public Criteria andCCreateuserNotIn(List<String> values) {
            addCriterion("c_createUser not in", values, "cCreateuser");
            return (Criteria) this;
        }

        public Criteria andCCreateuserBetween(String value1, String value2) {
            addCriterion("c_createUser between", value1, value2, "cCreateuser");
            return (Criteria) this;
        }

        public Criteria andCCreateuserNotBetween(String value1, String value2) {
            addCriterion("c_createUser not between", value1, value2, "cCreateuser");
            return (Criteria) this;
        }

        public Criteria andCLastmoddateIsNull() {
            addCriterion("c_lastModDate is null");
            return (Criteria) this;
        }

        public Criteria andCLastmoddateIsNotNull() {
            addCriterion("c_lastModDate is not null");
            return (Criteria) this;
        }

        public Criteria andCLastmoddateEqualTo(Date value) {
            addCriterion("c_lastModDate =", value, "cLastmoddate");
            return (Criteria) this;
        }

        public Criteria andCLastmoddateNotEqualTo(Date value) {
            addCriterion("c_lastModDate <>", value, "cLastmoddate");
            return (Criteria) this;
        }

        public Criteria andCLastmoddateGreaterThan(Date value) {
            addCriterion("c_lastModDate >", value, "cLastmoddate");
            return (Criteria) this;
        }

        public Criteria andCLastmoddateGreaterThanOrEqualTo(Date value) {
            addCriterion("c_lastModDate >=", value, "cLastmoddate");
            return (Criteria) this;
        }

        public Criteria andCLastmoddateLessThan(Date value) {
            addCriterion("c_lastModDate <", value, "cLastmoddate");
            return (Criteria) this;
        }

        public Criteria andCLastmoddateLessThanOrEqualTo(Date value) {
            addCriterion("c_lastModDate <=", value, "cLastmoddate");
            return (Criteria) this;
        }

        public Criteria andCLastmoddateIn(List<Date> values) {
            addCriterion("c_lastModDate in", values, "cLastmoddate");
            return (Criteria) this;
        }

        public Criteria andCLastmoddateNotIn(List<Date> values) {
            addCriterion("c_lastModDate not in", values, "cLastmoddate");
            return (Criteria) this;
        }

        public Criteria andCLastmoddateBetween(Date value1, Date value2) {
            addCriterion("c_lastModDate between", value1, value2, "cLastmoddate");
            return (Criteria) this;
        }

        public Criteria andCLastmoddateNotBetween(Date value1, Date value2) {
            addCriterion("c_lastModDate not between", value1, value2, "cLastmoddate");
            return (Criteria) this;
        }

        public Criteria andCLastmoduserIsNull() {
            addCriterion("c_lastModUser is null");
            return (Criteria) this;
        }

        public Criteria andCLastmoduserIsNotNull() {
            addCriterion("c_lastModUser is not null");
            return (Criteria) this;
        }

        public Criteria andCLastmoduserEqualTo(String value) {
            addCriterion("c_lastModUser =", value, "cLastmoduser");
            return (Criteria) this;
        }

        public Criteria andCLastmoduserNotEqualTo(String value) {
            addCriterion("c_lastModUser <>", value, "cLastmoduser");
            return (Criteria) this;
        }

        public Criteria andCLastmoduserGreaterThan(String value) {
            addCriterion("c_lastModUser >", value, "cLastmoduser");
            return (Criteria) this;
        }

        public Criteria andCLastmoduserGreaterThanOrEqualTo(String value) {
            addCriterion("c_lastModUser >=", value, "cLastmoduser");
            return (Criteria) this;
        }

        public Criteria andCLastmoduserLessThan(String value) {
            addCriterion("c_lastModUser <", value, "cLastmoduser");
            return (Criteria) this;
        }

        public Criteria andCLastmoduserLessThanOrEqualTo(String value) {
            addCriterion("c_lastModUser <=", value, "cLastmoduser");
            return (Criteria) this;
        }

        public Criteria andCLastmoduserLike(String value) {
            addCriterion("c_lastModUser like", value, "cLastmoduser");
            return (Criteria) this;
        }

        public Criteria andCLastmoduserNotLike(String value) {
            addCriterion("c_lastModUser not like", value, "cLastmoduser");
            return (Criteria) this;
        }

        public Criteria andCLastmoduserIn(List<String> values) {
            addCriterion("c_lastModUser in", values, "cLastmoduser");
            return (Criteria) this;
        }

        public Criteria andCLastmoduserNotIn(List<String> values) {
            addCriterion("c_lastModUser not in", values, "cLastmoduser");
            return (Criteria) this;
        }

        public Criteria andCLastmoduserBetween(String value1, String value2) {
            addCriterion("c_lastModUser between", value1, value2, "cLastmoduser");
            return (Criteria) this;
        }

        public Criteria andCLastmoduserNotBetween(String value1, String value2) {
            addCriterion("c_lastModUser not between", value1, value2, "cLastmoduser");
            return (Criteria) this;
        }

        public Criteria andCActivityendtimeIsNull() {
            addCriterion("c_activityEndTime is null");
            return (Criteria) this;
        }

        public Criteria andCActivityendtimeIsNotNull() {
            addCriterion("c_activityEndTime is not null");
            return (Criteria) this;
        }

        public Criteria andCActivityendtimeEqualTo(Date value) {
            addCriterion("c_activityEndTime =", value, "cActivityendtime");
            return (Criteria) this;
        }

        public Criteria andCActivityendtimeNotEqualTo(Date value) {
            addCriterion("c_activityEndTime <>", value, "cActivityendtime");
            return (Criteria) this;
        }

        public Criteria andCActivityendtimeGreaterThan(Date value) {
            addCriterion("c_activityEndTime >", value, "cActivityendtime");
            return (Criteria) this;
        }

        public Criteria andCActivityendtimeGreaterThanOrEqualTo(Date value) {
            addCriterion("c_activityEndTime >=", value, "cActivityendtime");
            return (Criteria) this;
        }

        public Criteria andCActivityendtimeLessThan(Date value) {
            addCriterion("c_activityEndTime <", value, "cActivityendtime");
            return (Criteria) this;
        }

        public Criteria andCActivityendtimeLessThanOrEqualTo(Date value) {
            addCriterion("c_activityEndTime <=", value, "cActivityendtime");
            return (Criteria) this;
        }

        public Criteria andCActivityendtimeIn(List<Date> values) {
            addCriterion("c_activityEndTime in", values, "cActivityendtime");
            return (Criteria) this;
        }

        public Criteria andCActivityendtimeNotIn(List<Date> values) {
            addCriterion("c_activityEndTime not in", values, "cActivityendtime");
            return (Criteria) this;
        }

        public Criteria andCActivityendtimeBetween(Date value1, Date value2) {
            addCriterion("c_activityEndTime between", value1, value2, "cActivityendtime");
            return (Criteria) this;
        }

        public Criteria andCActivityendtimeNotBetween(Date value1, Date value2) {
            addCriterion("c_activityEndTime not between", value1, value2, "cActivityendtime");
            return (Criteria) this;
        }

        public Criteria andCDetailsinfoIsNull() {
            addCriterion("c_detailsInfo is null");
            return (Criteria) this;
        }

        public Criteria andCDetailsinfoIsNotNull() {
            addCriterion("c_detailsInfo is not null");
            return (Criteria) this;
        }

        public Criteria andCDetailsinfoEqualTo(String value) {
            addCriterion("c_detailsInfo =", value, "cDetailsinfo");
            return (Criteria) this;
        }

        public Criteria andCDetailsinfoNotEqualTo(String value) {
            addCriterion("c_detailsInfo <>", value, "cDetailsinfo");
            return (Criteria) this;
        }

        public Criteria andCDetailsinfoGreaterThan(String value) {
            addCriterion("c_detailsInfo >", value, "cDetailsinfo");
            return (Criteria) this;
        }

        public Criteria andCDetailsinfoGreaterThanOrEqualTo(String value) {
            addCriterion("c_detailsInfo >=", value, "cDetailsinfo");
            return (Criteria) this;
        }

        public Criteria andCDetailsinfoLessThan(String value) {
            addCriterion("c_detailsInfo <", value, "cDetailsinfo");
            return (Criteria) this;
        }

        public Criteria andCDetailsinfoLessThanOrEqualTo(String value) {
            addCriterion("c_detailsInfo <=", value, "cDetailsinfo");
            return (Criteria) this;
        }

        public Criteria andCDetailsinfoLike(String value) {
            addCriterion("c_detailsInfo like", value, "cDetailsinfo");
            return (Criteria) this;
        }

        public Criteria andCDetailsinfoNotLike(String value) {
            addCriterion("c_detailsInfo not like", value, "cDetailsinfo");
            return (Criteria) this;
        }

        public Criteria andCDetailsinfoIn(List<String> values) {
            addCriterion("c_detailsInfo in", values, "cDetailsinfo");
            return (Criteria) this;
        }

        public Criteria andCDetailsinfoNotIn(List<String> values) {
            addCriterion("c_detailsInfo not in", values, "cDetailsinfo");
            return (Criteria) this;
        }

        public Criteria andCDetailsinfoBetween(String value1, String value2) {
            addCriterion("c_detailsInfo between", value1, value2, "cDetailsinfo");
            return (Criteria) this;
        }

        public Criteria andCDetailsinfoNotBetween(String value1, String value2) {
            addCriterion("c_detailsInfo not between", value1, value2, "cDetailsinfo");
            return (Criteria) this;
        }

        public Criteria andCIsdeteleIsNull() {
            addCriterion("c_isDetele is null");
            return (Criteria) this;
        }

        public Criteria andCIsdeteleIsNotNull() {
            addCriterion("c_isDetele is not null");
            return (Criteria) this;
        }

        public Criteria andCIsdeteleEqualTo(Integer value) {
            addCriterion("c_isDetele =", value, "cIsdetele");
            return (Criteria) this;
        }

        public Criteria andCIsdeteleNotEqualTo(Integer value) {
            addCriterion("c_isDetele <>", value, "cIsdetele");
            return (Criteria) this;
        }

        public Criteria andCIsdeteleGreaterThan(Integer value) {
            addCriterion("c_isDetele >", value, "cIsdetele");
            return (Criteria) this;
        }

        public Criteria andCIsdeteleGreaterThanOrEqualTo(Integer value) {
            addCriterion("c_isDetele >=", value, "cIsdetele");
            return (Criteria) this;
        }

        public Criteria andCIsdeteleLessThan(Integer value) {
            addCriterion("c_isDetele <", value, "cIsdetele");
            return (Criteria) this;
        }

        public Criteria andCIsdeteleLessThanOrEqualTo(Integer value) {
            addCriterion("c_isDetele <=", value, "cIsdetele");
            return (Criteria) this;
        }

        public Criteria andCIsdeteleIn(List<Integer> values) {
            addCriterion("c_isDetele in", values, "cIsdetele");
            return (Criteria) this;
        }

        public Criteria andCIsdeteleNotIn(List<Integer> values) {
            addCriterion("c_isDetele not in", values, "cIsdetele");
            return (Criteria) this;
        }

        public Criteria andCIsdeteleBetween(Integer value1, Integer value2) {
            addCriterion("c_isDetele between", value1, value2, "cIsdetele");
            return (Criteria) this;
        }

        public Criteria andCIsdeteleNotBetween(Integer value1, Integer value2) {
            addCriterion("c_isDetele not between", value1, value2, "cIsdetele");
            return (Criteria) this;
        }

        public Criteria andCRemainnumIsNull() {
            addCriterion("c_remainNum is null");
            return (Criteria) this;
        }

        public Criteria andCRemainnumIsNotNull() {
            addCriterion("c_remainNum is not null");
            return (Criteria) this;
        }

        public Criteria andCRemainnumEqualTo(Integer value) {
            addCriterion("c_remainNum =", value, "cRemainnum");
            return (Criteria) this;
        }

        public Criteria andCRemainnumNotEqualTo(Integer value) {
            addCriterion("c_remainNum <>", value, "cRemainnum");
            return (Criteria) this;
        }

        public Criteria andCRemainnumGreaterThan(Integer value) {
            addCriterion("c_remainNum >", value, "cRemainnum");
            return (Criteria) this;
        }

        public Criteria andCRemainnumGreaterThanOrEqualTo(Integer value) {
            addCriterion("c_remainNum >=", value, "cRemainnum");
            return (Criteria) this;
        }

        public Criteria andCRemainnumLessThan(Integer value) {
            addCriterion("c_remainNum <", value, "cRemainnum");
            return (Criteria) this;
        }

        public Criteria andCRemainnumLessThanOrEqualTo(Integer value) {
            addCriterion("c_remainNum <=", value, "cRemainnum");
            return (Criteria) this;
        }

        public Criteria andCRemainnumIn(List<Integer> values) {
            addCriterion("c_remainNum in", values, "cRemainnum");
            return (Criteria) this;
        }

        public Criteria andCRemainnumNotIn(List<Integer> values) {
            addCriterion("c_remainNum not in", values, "cRemainnum");
            return (Criteria) this;
        }

        public Criteria andCRemainnumBetween(Integer value1, Integer value2) {
            addCriterion("c_remainNum between", value1, value2, "cRemainnum");
            return (Criteria) this;
        }

        public Criteria andCRemainnumNotBetween(Integer value1, Integer value2) {
            addCriterion("c_remainNum not between", value1, value2, "cRemainnum");
            return (Criteria) this;
        }

        public Criteria andCAccumulatecouponsidsIsNull() {
            addCriterion("c_accumulateCouponsIds is null");
            return (Criteria) this;
        }

        public Criteria andCAccumulatecouponsidsIsNotNull() {
            addCriterion("c_accumulateCouponsIds is not null");
            return (Criteria) this;
        }

        public Criteria andCAccumulatecouponsidsEqualTo(String value) {
            addCriterion("c_accumulateCouponsIds =", value, "cAccumulatecouponsids");
            return (Criteria) this;
        }

        public Criteria andCAccumulatecouponsidsNotEqualTo(String value) {
            addCriterion("c_accumulateCouponsIds <>", value, "cAccumulatecouponsids");
            return (Criteria) this;
        }

        public Criteria andCAccumulatecouponsidsGreaterThan(String value) {
            addCriterion("c_accumulateCouponsIds >", value, "cAccumulatecouponsids");
            return (Criteria) this;
        }

        public Criteria andCAccumulatecouponsidsGreaterThanOrEqualTo(String value) {
            addCriterion("c_accumulateCouponsIds >=", value, "cAccumulatecouponsids");
            return (Criteria) this;
        }

        public Criteria andCAccumulatecouponsidsLessThan(String value) {
            addCriterion("c_accumulateCouponsIds <", value, "cAccumulatecouponsids");
            return (Criteria) this;
        }

        public Criteria andCAccumulatecouponsidsLessThanOrEqualTo(String value) {
            addCriterion("c_accumulateCouponsIds <=", value, "cAccumulatecouponsids");
            return (Criteria) this;
        }

        public Criteria andCAccumulatecouponsidsLike(String value) {
            addCriterion("c_accumulateCouponsIds like", value, "cAccumulatecouponsids");
            return (Criteria) this;
        }

        public Criteria andCAccumulatecouponsidsNotLike(String value) {
            addCriterion("c_accumulateCouponsIds not like", value, "cAccumulatecouponsids");
            return (Criteria) this;
        }

        public Criteria andCAccumulatecouponsidsIn(List<String> values) {
            addCriterion("c_accumulateCouponsIds in", values, "cAccumulatecouponsids");
            return (Criteria) this;
        }

        public Criteria andCAccumulatecouponsidsNotIn(List<String> values) {
            addCriterion("c_accumulateCouponsIds not in", values, "cAccumulatecouponsids");
            return (Criteria) this;
        }

        public Criteria andCAccumulatecouponsidsBetween(String value1, String value2) {
            addCriterion("c_accumulateCouponsIds between", value1, value2, "cAccumulatecouponsids");
            return (Criteria) this;
        }

        public Criteria andCAccumulatecouponsidsNotBetween(String value1, String value2) {
            addCriterion("c_accumulateCouponsIds not between", value1, value2, "cAccumulatecouponsids");
            return (Criteria) this;
        }

        public Criteria andCRemarkIsNull() {
            addCriterion("c_remark is null");
            return (Criteria) this;
        }

        public Criteria andCRemarkIsNotNull() {
            addCriterion("c_remark is not null");
            return (Criteria) this;
        }

        public Criteria andCRemarkEqualTo(String value) {
            addCriterion("c_remark =", value, "cRemark");
            return (Criteria) this;
        }

        public Criteria andCRemarkNotEqualTo(String value) {
            addCriterion("c_remark <>", value, "cRemark");
            return (Criteria) this;
        }

        public Criteria andCRemarkGreaterThan(String value) {
            addCriterion("c_remark >", value, "cRemark");
            return (Criteria) this;
        }

        public Criteria andCRemarkGreaterThanOrEqualTo(String value) {
            addCriterion("c_remark >=", value, "cRemark");
            return (Criteria) this;
        }

        public Criteria andCRemarkLessThan(String value) {
            addCriterion("c_remark <", value, "cRemark");
            return (Criteria) this;
        }

        public Criteria andCRemarkLessThanOrEqualTo(String value) {
            addCriterion("c_remark <=", value, "cRemark");
            return (Criteria) this;
        }

        public Criteria andCRemarkLike(String value) {
            addCriterion("c_remark like", value, "cRemark");
            return (Criteria) this;
        }

        public Criteria andCRemarkNotLike(String value) {
            addCriterion("c_remark not like", value, "cRemark");
            return (Criteria) this;
        }

        public Criteria andCRemarkIn(List<String> values) {
            addCriterion("c_remark in", values, "cRemark");
            return (Criteria) this;
        }

        public Criteria andCRemarkNotIn(List<String> values) {
            addCriterion("c_remark not in", values, "cRemark");
            return (Criteria) this;
        }

        public Criteria andCRemarkBetween(String value1, String value2) {
            addCriterion("c_remark between", value1, value2, "cRemark");
            return (Criteria) this;
        }

        public Criteria andCRemarkNotBetween(String value1, String value2) {
            addCriterion("c_remark not between", value1, value2, "cRemark");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}