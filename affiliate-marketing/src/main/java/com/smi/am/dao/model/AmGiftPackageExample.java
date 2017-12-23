package com.smi.am.dao.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AmGiftPackageExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public AmGiftPackageExample() {
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

        public Criteria andGpIdIsNull() {
            addCriterion("gp_id is null");
            return (Criteria) this;
        }

        public Criteria andGpIdIsNotNull() {
            addCriterion("gp_id is not null");
            return (Criteria) this;
        }

        public Criteria andGpIdEqualTo(Integer value) {
            addCriterion("gp_id =", value, "gpId");
            return (Criteria) this;
        }

        public Criteria andGpIdNotEqualTo(Integer value) {
            addCriterion("gp_id <>", value, "gpId");
            return (Criteria) this;
        }

        public Criteria andGpIdGreaterThan(Integer value) {
            addCriterion("gp_id >", value, "gpId");
            return (Criteria) this;
        }

        public Criteria andGpIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("gp_id >=", value, "gpId");
            return (Criteria) this;
        }

        public Criteria andGpIdLessThan(Integer value) {
            addCriterion("gp_id <", value, "gpId");
            return (Criteria) this;
        }

        public Criteria andGpIdLessThanOrEqualTo(Integer value) {
            addCriterion("gp_id <=", value, "gpId");
            return (Criteria) this;
        }

        public Criteria andGpIdIn(List<Integer> values) {
            addCriterion("gp_id in", values, "gpId");
            return (Criteria) this;
        }

        public Criteria andGpIdNotIn(List<Integer> values) {
            addCriterion("gp_id not in", values, "gpId");
            return (Criteria) this;
        }

        public Criteria andGpIdBetween(Integer value1, Integer value2) {
            addCriterion("gp_id between", value1, value2, "gpId");
            return (Criteria) this;
        }

        public Criteria andGpIdNotBetween(Integer value1, Integer value2) {
            addCriterion("gp_id not between", value1, value2, "gpId");
            return (Criteria) this;
        }

        public Criteria andGpNameIsNull() {
            addCriterion("gp_name is null");
            return (Criteria) this;
        }

        public Criteria andGpNameIsNotNull() {
            addCriterion("gp_name is not null");
            return (Criteria) this;
        }

        public Criteria andGpNameEqualTo(String value) {
            addCriterion("gp_name =", value, "gpName");
            return (Criteria) this;
        }

        public Criteria andGpNameNotEqualTo(String value) {
            addCriterion("gp_name <>", value, "gpName");
            return (Criteria) this;
        }

        public Criteria andGpNameGreaterThan(String value) {
            addCriterion("gp_name >", value, "gpName");
            return (Criteria) this;
        }

        public Criteria andGpNameGreaterThanOrEqualTo(String value) {
            addCriterion("gp_name >=", value, "gpName");
            return (Criteria) this;
        }

        public Criteria andGpNameLessThan(String value) {
            addCriterion("gp_name <", value, "gpName");
            return (Criteria) this;
        }

        public Criteria andGpNameLessThanOrEqualTo(String value) {
            addCriterion("gp_name <=", value, "gpName");
            return (Criteria) this;
        }

        public Criteria andGpNameLike(String value) {
            addCriterion("gp_name like", value, "gpName");
            return (Criteria) this;
        }

        public Criteria andGpNameNotLike(String value) {
            addCriterion("gp_name not like", value, "gpName");
            return (Criteria) this;
        }

        public Criteria andGpNameIn(List<String> values) {
            addCriterion("gp_name in", values, "gpName");
            return (Criteria) this;
        }

        public Criteria andGpNameNotIn(List<String> values) {
            addCriterion("gp_name not in", values, "gpName");
            return (Criteria) this;
        }

        public Criteria andGpNameBetween(String value1, String value2) {
            addCriterion("gp_name between", value1, value2, "gpName");
            return (Criteria) this;
        }

        public Criteria andGpNameNotBetween(String value1, String value2) {
            addCriterion("gp_name not between", value1, value2, "gpName");
            return (Criteria) this;
        }

        public Criteria andGpChannelIsNull() {
            addCriterion("gp_channel is null");
            return (Criteria) this;
        }

        public Criteria andGpChannelIsNotNull() {
            addCriterion("gp_channel is not null");
            return (Criteria) this;
        }

        public Criteria andGpChannelEqualTo(String value) {
            addCriterion("gp_channel =", value, "gpChannel");
            return (Criteria) this;
        }

        public Criteria andGpChannelNotEqualTo(String value) {
            addCriterion("gp_channel <>", value, "gpChannel");
            return (Criteria) this;
        }

        public Criteria andGpChannelGreaterThan(String value) {
            addCriterion("gp_channel >", value, "gpChannel");
            return (Criteria) this;
        }

        public Criteria andGpChannelGreaterThanOrEqualTo(String value) {
            addCriterion("gp_channel >=", value, "gpChannel");
            return (Criteria) this;
        }

        public Criteria andGpChannelLessThan(String value) {
            addCriterion("gp_channel <", value, "gpChannel");
            return (Criteria) this;
        }

        public Criteria andGpChannelLessThanOrEqualTo(String value) {
            addCriterion("gp_channel <=", value, "gpChannel");
            return (Criteria) this;
        }

        public Criteria andGpChannelLike(String value) {
            addCriterion("gp_channel like", value, "gpChannel");
            return (Criteria) this;
        }

        public Criteria andGpChannelNotLike(String value) {
            addCriterion("gp_channel not like", value, "gpChannel");
            return (Criteria) this;
        }

        public Criteria andGpChannelIn(List<String> values) {
            addCriterion("gp_channel in", values, "gpChannel");
            return (Criteria) this;
        }

        public Criteria andGpChannelNotIn(List<String> values) {
            addCriterion("gp_channel not in", values, "gpChannel");
            return (Criteria) this;
        }

        public Criteria andGpChannelBetween(String value1, String value2) {
            addCriterion("gp_channel between", value1, value2, "gpChannel");
            return (Criteria) this;
        }

        public Criteria andGpChannelNotBetween(String value1, String value2) {
            addCriterion("gp_channel not between", value1, value2, "gpChannel");
            return (Criteria) this;
        }

        public Criteria andGpSendnumIsNull() {
            addCriterion("gp_sendNum is null");
            return (Criteria) this;
        }

        public Criteria andGpSendnumIsNotNull() {
            addCriterion("gp_sendNum is not null");
            return (Criteria) this;
        }

        public Criteria andGpSendnumEqualTo(String value) {
            addCriterion("gp_sendNum =", value, "gpSendnum");
            return (Criteria) this;
        }

        public Criteria andGpSendnumNotEqualTo(String value) {
            addCriterion("gp_sendNum <>", value, "gpSendnum");
            return (Criteria) this;
        }

        public Criteria andGpSendnumGreaterThan(String value) {
            addCriterion("gp_sendNum >", value, "gpSendnum");
            return (Criteria) this;
        }

        public Criteria andGpSendnumGreaterThanOrEqualTo(String value) {
            addCriterion("gp_sendNum >=", value, "gpSendnum");
            return (Criteria) this;
        }

        public Criteria andGpSendnumLessThan(String value) {
            addCriterion("gp_sendNum <", value, "gpSendnum");
            return (Criteria) this;
        }

        public Criteria andGpSendnumLessThanOrEqualTo(String value) {
            addCriterion("gp_sendNum <=", value, "gpSendnum");
            return (Criteria) this;
        }

        public Criteria andGpSendnumLike(String value) {
            addCriterion("gp_sendNum like", value, "gpSendnum");
            return (Criteria) this;
        }

        public Criteria andGpSendnumNotLike(String value) {
            addCriterion("gp_sendNum not like", value, "gpSendnum");
            return (Criteria) this;
        }

        public Criteria andGpSendnumIn(List<String> values) {
            addCriterion("gp_sendNum in", values, "gpSendnum");
            return (Criteria) this;
        }

        public Criteria andGpSendnumNotIn(List<String> values) {
            addCriterion("gp_sendNum not in", values, "gpSendnum");
            return (Criteria) this;
        }

        public Criteria andGpSendnumBetween(String value1, String value2) {
            addCriterion("gp_sendNum between", value1, value2, "gpSendnum");
            return (Criteria) this;
        }

        public Criteria andGpSendnumNotBetween(String value1, String value2) {
            addCriterion("gp_sendNum not between", value1, value2, "gpSendnum");
            return (Criteria) this;
        }

        public Criteria andGpActivityareaIsNull() {
            addCriterion("gp_activityArea is null");
            return (Criteria) this;
        }

        public Criteria andGpActivityareaIsNotNull() {
            addCriterion("gp_activityArea is not null");
            return (Criteria) this;
        }

        public Criteria andGpActivityareaEqualTo(String value) {
            addCriterion("gp_activityArea =", value, "gpActivityarea");
            return (Criteria) this;
        }

        public Criteria andGpActivityareaNotEqualTo(String value) {
            addCriterion("gp_activityArea <>", value, "gpActivityarea");
            return (Criteria) this;
        }

        public Criteria andGpActivityareaGreaterThan(String value) {
            addCriterion("gp_activityArea >", value, "gpActivityarea");
            return (Criteria) this;
        }

        public Criteria andGpActivityareaGreaterThanOrEqualTo(String value) {
            addCriterion("gp_activityArea >=", value, "gpActivityarea");
            return (Criteria) this;
        }

        public Criteria andGpActivityareaLessThan(String value) {
            addCriterion("gp_activityArea <", value, "gpActivityarea");
            return (Criteria) this;
        }

        public Criteria andGpActivityareaLessThanOrEqualTo(String value) {
            addCriterion("gp_activityArea <=", value, "gpActivityarea");
            return (Criteria) this;
        }

        public Criteria andGpActivityareaLike(String value) {
            addCriterion("gp_activityArea like", value, "gpActivityarea");
            return (Criteria) this;
        }

        public Criteria andGpActivityareaNotLike(String value) {
            addCriterion("gp_activityArea not like", value, "gpActivityarea");
            return (Criteria) this;
        }

        public Criteria andGpActivityareaIn(List<String> values) {
            addCriterion("gp_activityArea in", values, "gpActivityarea");
            return (Criteria) this;
        }

        public Criteria andGpActivityareaNotIn(List<String> values) {
            addCriterion("gp_activityArea not in", values, "gpActivityarea");
            return (Criteria) this;
        }

        public Criteria andGpActivityareaBetween(String value1, String value2) {
            addCriterion("gp_activityArea between", value1, value2, "gpActivityarea");
            return (Criteria) this;
        }

        public Criteria andGpActivityareaNotBetween(String value1, String value2) {
            addCriterion("gp_activityArea not between", value1, value2, "gpActivityarea");
            return (Criteria) this;
        }

        public Criteria andGpActivityshopIsNull() {
            addCriterion("gp_activityShop is null");
            return (Criteria) this;
        }

        public Criteria andGpActivityshopIsNotNull() {
            addCriterion("gp_activityShop is not null");
            return (Criteria) this;
        }

        public Criteria andGpActivityshopEqualTo(String value) {
            addCriterion("gp_activityShop =", value, "gpActivityshop");
            return (Criteria) this;
        }

        public Criteria andGpActivityshopNotEqualTo(String value) {
            addCriterion("gp_activityShop <>", value, "gpActivityshop");
            return (Criteria) this;
        }

        public Criteria andGpActivityshopGreaterThan(String value) {
            addCriterion("gp_activityShop >", value, "gpActivityshop");
            return (Criteria) this;
        }

        public Criteria andGpActivityshopGreaterThanOrEqualTo(String value) {
            addCriterion("gp_activityShop >=", value, "gpActivityshop");
            return (Criteria) this;
        }

        public Criteria andGpActivityshopLessThan(String value) {
            addCriterion("gp_activityShop <", value, "gpActivityshop");
            return (Criteria) this;
        }

        public Criteria andGpActivityshopLessThanOrEqualTo(String value) {
            addCriterion("gp_activityShop <=", value, "gpActivityshop");
            return (Criteria) this;
        }

        public Criteria andGpActivityshopLike(String value) {
            addCriterion("gp_activityShop like", value, "gpActivityshop");
            return (Criteria) this;
        }

        public Criteria andGpActivityshopNotLike(String value) {
            addCriterion("gp_activityShop not like", value, "gpActivityshop");
            return (Criteria) this;
        }

        public Criteria andGpActivityshopIn(List<String> values) {
            addCriterion("gp_activityShop in", values, "gpActivityshop");
            return (Criteria) this;
        }

        public Criteria andGpActivityshopNotIn(List<String> values) {
            addCriterion("gp_activityShop not in", values, "gpActivityshop");
            return (Criteria) this;
        }

        public Criteria andGpActivityshopBetween(String value1, String value2) {
            addCriterion("gp_activityShop between", value1, value2, "gpActivityshop");
            return (Criteria) this;
        }

        public Criteria andGpActivityshopNotBetween(String value1, String value2) {
            addCriterion("gp_activityShop not between", value1, value2, "gpActivityshop");
            return (Criteria) this;
        }

        public Criteria andGpStarttimeIsNull() {
            addCriterion("gp_startTime is null");
            return (Criteria) this;
        }

        public Criteria andGpStarttimeIsNotNull() {
            addCriterion("gp_startTime is not null");
            return (Criteria) this;
        }

        public Criteria andGpStarttimeEqualTo(Date value) {
            addCriterion("gp_startTime =", value, "gpStarttime");
            return (Criteria) this;
        }

        public Criteria andGpStarttimeNotEqualTo(Date value) {
            addCriterion("gp_startTime <>", value, "gpStarttime");
            return (Criteria) this;
        }

        public Criteria andGpStarttimeGreaterThan(Date value) {
            addCriterion("gp_startTime >", value, "gpStarttime");
            return (Criteria) this;
        }

        public Criteria andGpStarttimeGreaterThanOrEqualTo(Date value) {
            addCriterion("gp_startTime >=", value, "gpStarttime");
            return (Criteria) this;
        }

        public Criteria andGpStarttimeLessThan(Date value) {
            addCriterion("gp_startTime <", value, "gpStarttime");
            return (Criteria) this;
        }

        public Criteria andGpStarttimeLessThanOrEqualTo(Date value) {
            addCriterion("gp_startTime <=", value, "gpStarttime");
            return (Criteria) this;
        }

        public Criteria andGpStarttimeIn(List<Date> values) {
            addCriterion("gp_startTime in", values, "gpStarttime");
            return (Criteria) this;
        }

        public Criteria andGpStarttimeNotIn(List<Date> values) {
            addCriterion("gp_startTime not in", values, "gpStarttime");
            return (Criteria) this;
        }

        public Criteria andGpStarttimeBetween(Date value1, Date value2) {
            addCriterion("gp_startTime between", value1, value2, "gpStarttime");
            return (Criteria) this;
        }

        public Criteria andGpStarttimeNotBetween(Date value1, Date value2) {
            addCriterion("gp_startTime not between", value1, value2, "gpStarttime");
            return (Criteria) this;
        }

        public Criteria andGpEndtimeIsNull() {
            addCriterion("gp_endTime is null");
            return (Criteria) this;
        }

        public Criteria andGpEndtimeIsNotNull() {
            addCriterion("gp_endTime is not null");
            return (Criteria) this;
        }

        public Criteria andGpEndtimeEqualTo(Date value) {
            addCriterion("gp_endTime =", value, "gpEndtime");
            return (Criteria) this;
        }

        public Criteria andGpEndtimeNotEqualTo(Date value) {
            addCriterion("gp_endTime <>", value, "gpEndtime");
            return (Criteria) this;
        }

        public Criteria andGpEndtimeGreaterThan(Date value) {
            addCriterion("gp_endTime >", value, "gpEndtime");
            return (Criteria) this;
        }

        public Criteria andGpEndtimeGreaterThanOrEqualTo(Date value) {
            addCriterion("gp_endTime >=", value, "gpEndtime");
            return (Criteria) this;
        }

        public Criteria andGpEndtimeLessThan(Date value) {
            addCriterion("gp_endTime <", value, "gpEndtime");
            return (Criteria) this;
        }

        public Criteria andGpEndtimeLessThanOrEqualTo(Date value) {
            addCriterion("gp_endTime <=", value, "gpEndtime");
            return (Criteria) this;
        }

        public Criteria andGpEndtimeIn(List<Date> values) {
            addCriterion("gp_endTime in", values, "gpEndtime");
            return (Criteria) this;
        }

        public Criteria andGpEndtimeNotIn(List<Date> values) {
            addCriterion("gp_endTime not in", values, "gpEndtime");
            return (Criteria) this;
        }

        public Criteria andGpEndtimeBetween(Date value1, Date value2) {
            addCriterion("gp_endTime between", value1, value2, "gpEndtime");
            return (Criteria) this;
        }

        public Criteria andGpEndtimeNotBetween(Date value1, Date value2) {
            addCriterion("gp_endTime not between", value1, value2, "gpEndtime");
            return (Criteria) this;
        }

        public Criteria andGpProvideallIsNull() {
            addCriterion("gp_provideAll is null");
            return (Criteria) this;
        }

        public Criteria andGpProvideallIsNotNull() {
            addCriterion("gp_provideAll is not null");
            return (Criteria) this;
        }

        public Criteria andGpProvideallEqualTo(Integer value) {
            addCriterion("gp_provideAll =", value, "gpProvideall");
            return (Criteria) this;
        }

        public Criteria andGpProvideallNotEqualTo(Integer value) {
            addCriterion("gp_provideAll <>", value, "gpProvideall");
            return (Criteria) this;
        }

        public Criteria andGpProvideallGreaterThan(Integer value) {
            addCriterion("gp_provideAll >", value, "gpProvideall");
            return (Criteria) this;
        }

        public Criteria andGpProvideallGreaterThanOrEqualTo(Integer value) {
            addCriterion("gp_provideAll >=", value, "gpProvideall");
            return (Criteria) this;
        }

        public Criteria andGpProvideallLessThan(Integer value) {
            addCriterion("gp_provideAll <", value, "gpProvideall");
            return (Criteria) this;
        }

        public Criteria andGpProvideallLessThanOrEqualTo(Integer value) {
            addCriterion("gp_provideAll <=", value, "gpProvideall");
            return (Criteria) this;
        }

        public Criteria andGpProvideallIn(List<Integer> values) {
            addCriterion("gp_provideAll in", values, "gpProvideall");
            return (Criteria) this;
        }

        public Criteria andGpProvideallNotIn(List<Integer> values) {
            addCriterion("gp_provideAll not in", values, "gpProvideall");
            return (Criteria) this;
        }

        public Criteria andGpProvideallBetween(Integer value1, Integer value2) {
            addCriterion("gp_provideAll between", value1, value2, "gpProvideall");
            return (Criteria) this;
        }

        public Criteria andGpProvideallNotBetween(Integer value1, Integer value2) {
            addCriterion("gp_provideAll not between", value1, value2, "gpProvideall");
            return (Criteria) this;
        }

        public Criteria andGpStatusIsNull() {
            addCriterion("gp_status is null");
            return (Criteria) this;
        }

        public Criteria andGpStatusIsNotNull() {
            addCriterion("gp_status is not null");
            return (Criteria) this;
        }

        public Criteria andGpStatusEqualTo(String value) {
            addCriterion("gp_status =", value, "gpStatus");
            return (Criteria) this;
        }

        public Criteria andGpStatusNotEqualTo(String value) {
            addCriterion("gp_status <>", value, "gpStatus");
            return (Criteria) this;
        }

        public Criteria andGpStatusGreaterThan(String value) {
            addCriterion("gp_status >", value, "gpStatus");
            return (Criteria) this;
        }

        public Criteria andGpStatusGreaterThanOrEqualTo(String value) {
            addCriterion("gp_status >=", value, "gpStatus");
            return (Criteria) this;
        }

        public Criteria andGpStatusLessThan(String value) {
            addCriterion("gp_status <", value, "gpStatus");
            return (Criteria) this;
        }

        public Criteria andGpStatusLessThanOrEqualTo(String value) {
            addCriterion("gp_status <=", value, "gpStatus");
            return (Criteria) this;
        }

        public Criteria andGpStatusLike(String value) {
            addCriterion("gp_status like", value, "gpStatus");
            return (Criteria) this;
        }

        public Criteria andGpStatusNotLike(String value) {
            addCriterion("gp_status not like", value, "gpStatus");
            return (Criteria) this;
        }

        public Criteria andGpStatusIn(List<String> values) {
            addCriterion("gp_status in", values, "gpStatus");
            return (Criteria) this;
        }

        public Criteria andGpStatusNotIn(List<String> values) {
            addCriterion("gp_status not in", values, "gpStatus");
            return (Criteria) this;
        }

        public Criteria andGpStatusBetween(String value1, String value2) {
            addCriterion("gp_status between", value1, value2, "gpStatus");
            return (Criteria) this;
        }

        public Criteria andGpStatusNotBetween(String value1, String value2) {
            addCriterion("gp_status not between", value1, value2, "gpStatus");
            return (Criteria) this;
        }

        public Criteria andGpCreatedateIsNull() {
            addCriterion("gp_createDate is null");
            return (Criteria) this;
        }

        public Criteria andGpCreatedateIsNotNull() {
            addCriterion("gp_createDate is not null");
            return (Criteria) this;
        }

        public Criteria andGpCreatedateEqualTo(Date value) {
            addCriterion("gp_createDate =", value, "gpCreatedate");
            return (Criteria) this;
        }

        public Criteria andGpCreatedateNotEqualTo(Date value) {
            addCriterion("gp_createDate <>", value, "gpCreatedate");
            return (Criteria) this;
        }

        public Criteria andGpCreatedateGreaterThan(Date value) {
            addCriterion("gp_createDate >", value, "gpCreatedate");
            return (Criteria) this;
        }

        public Criteria andGpCreatedateGreaterThanOrEqualTo(Date value) {
            addCriterion("gp_createDate >=", value, "gpCreatedate");
            return (Criteria) this;
        }

        public Criteria andGpCreatedateLessThan(Date value) {
            addCriterion("gp_createDate <", value, "gpCreatedate");
            return (Criteria) this;
        }

        public Criteria andGpCreatedateLessThanOrEqualTo(Date value) {
            addCriterion("gp_createDate <=", value, "gpCreatedate");
            return (Criteria) this;
        }

        public Criteria andGpCreatedateIn(List<Date> values) {
            addCriterion("gp_createDate in", values, "gpCreatedate");
            return (Criteria) this;
        }

        public Criteria andGpCreatedateNotIn(List<Date> values) {
            addCriterion("gp_createDate not in", values, "gpCreatedate");
            return (Criteria) this;
        }

        public Criteria andGpCreatedateBetween(Date value1, Date value2) {
            addCriterion("gp_createDate between", value1, value2, "gpCreatedate");
            return (Criteria) this;
        }

        public Criteria andGpCreatedateNotBetween(Date value1, Date value2) {
            addCriterion("gp_createDate not between", value1, value2, "gpCreatedate");
            return (Criteria) this;
        }

        public Criteria andGpCreateuserIsNull() {
            addCriterion("gp_createUser is null");
            return (Criteria) this;
        }

        public Criteria andGpCreateuserIsNotNull() {
            addCriterion("gp_createUser is not null");
            return (Criteria) this;
        }

        public Criteria andGpCreateuserEqualTo(String value) {
            addCriterion("gp_createUser =", value, "gpCreateuser");
            return (Criteria) this;
        }

        public Criteria andGpCreateuserNotEqualTo(String value) {
            addCriterion("gp_createUser <>", value, "gpCreateuser");
            return (Criteria) this;
        }

        public Criteria andGpCreateuserGreaterThan(String value) {
            addCriterion("gp_createUser >", value, "gpCreateuser");
            return (Criteria) this;
        }

        public Criteria andGpCreateuserGreaterThanOrEqualTo(String value) {
            addCriterion("gp_createUser >=", value, "gpCreateuser");
            return (Criteria) this;
        }

        public Criteria andGpCreateuserLessThan(String value) {
            addCriterion("gp_createUser <", value, "gpCreateuser");
            return (Criteria) this;
        }

        public Criteria andGpCreateuserLessThanOrEqualTo(String value) {
            addCriterion("gp_createUser <=", value, "gpCreateuser");
            return (Criteria) this;
        }

        public Criteria andGpCreateuserLike(String value) {
            addCriterion("gp_createUser like", value, "gpCreateuser");
            return (Criteria) this;
        }

        public Criteria andGpCreateuserNotLike(String value) {
            addCriterion("gp_createUser not like", value, "gpCreateuser");
            return (Criteria) this;
        }

        public Criteria andGpCreateuserIn(List<String> values) {
            addCriterion("gp_createUser in", values, "gpCreateuser");
            return (Criteria) this;
        }

        public Criteria andGpCreateuserNotIn(List<String> values) {
            addCriterion("gp_createUser not in", values, "gpCreateuser");
            return (Criteria) this;
        }

        public Criteria andGpCreateuserBetween(String value1, String value2) {
            addCriterion("gp_createUser between", value1, value2, "gpCreateuser");
            return (Criteria) this;
        }

        public Criteria andGpCreateuserNotBetween(String value1, String value2) {
            addCriterion("gp_createUser not between", value1, value2, "gpCreateuser");
            return (Criteria) this;
        }

        public Criteria andGpLastmoddateIsNull() {
            addCriterion("gp_lastModDate is null");
            return (Criteria) this;
        }

        public Criteria andGpLastmoddateIsNotNull() {
            addCriterion("gp_lastModDate is not null");
            return (Criteria) this;
        }

        public Criteria andGpLastmoddateEqualTo(Date value) {
            addCriterion("gp_lastModDate =", value, "gpLastmoddate");
            return (Criteria) this;
        }

        public Criteria andGpLastmoddateNotEqualTo(Date value) {
            addCriterion("gp_lastModDate <>", value, "gpLastmoddate");
            return (Criteria) this;
        }

        public Criteria andGpLastmoddateGreaterThan(Date value) {
            addCriterion("gp_lastModDate >", value, "gpLastmoddate");
            return (Criteria) this;
        }

        public Criteria andGpLastmoddateGreaterThanOrEqualTo(Date value) {
            addCriterion("gp_lastModDate >=", value, "gpLastmoddate");
            return (Criteria) this;
        }

        public Criteria andGpLastmoddateLessThan(Date value) {
            addCriterion("gp_lastModDate <", value, "gpLastmoddate");
            return (Criteria) this;
        }

        public Criteria andGpLastmoddateLessThanOrEqualTo(Date value) {
            addCriterion("gp_lastModDate <=", value, "gpLastmoddate");
            return (Criteria) this;
        }

        public Criteria andGpLastmoddateIn(List<Date> values) {
            addCriterion("gp_lastModDate in", values, "gpLastmoddate");
            return (Criteria) this;
        }

        public Criteria andGpLastmoddateNotIn(List<Date> values) {
            addCriterion("gp_lastModDate not in", values, "gpLastmoddate");
            return (Criteria) this;
        }

        public Criteria andGpLastmoddateBetween(Date value1, Date value2) {
            addCriterion("gp_lastModDate between", value1, value2, "gpLastmoddate");
            return (Criteria) this;
        }

        public Criteria andGpLastmoddateNotBetween(Date value1, Date value2) {
            addCriterion("gp_lastModDate not between", value1, value2, "gpLastmoddate");
            return (Criteria) this;
        }

        public Criteria andGpLastmoduserIsNull() {
            addCriterion("gp_lastModUser is null");
            return (Criteria) this;
        }

        public Criteria andGpLastmoduserIsNotNull() {
            addCriterion("gp_lastModUser is not null");
            return (Criteria) this;
        }

        public Criteria andGpLastmoduserEqualTo(String value) {
            addCriterion("gp_lastModUser =", value, "gpLastmoduser");
            return (Criteria) this;
        }

        public Criteria andGpLastmoduserNotEqualTo(String value) {
            addCriterion("gp_lastModUser <>", value, "gpLastmoduser");
            return (Criteria) this;
        }

        public Criteria andGpLastmoduserGreaterThan(String value) {
            addCriterion("gp_lastModUser >", value, "gpLastmoduser");
            return (Criteria) this;
        }

        public Criteria andGpLastmoduserGreaterThanOrEqualTo(String value) {
            addCriterion("gp_lastModUser >=", value, "gpLastmoduser");
            return (Criteria) this;
        }

        public Criteria andGpLastmoduserLessThan(String value) {
            addCriterion("gp_lastModUser <", value, "gpLastmoduser");
            return (Criteria) this;
        }

        public Criteria andGpLastmoduserLessThanOrEqualTo(String value) {
            addCriterion("gp_lastModUser <=", value, "gpLastmoduser");
            return (Criteria) this;
        }

        public Criteria andGpLastmoduserLike(String value) {
            addCriterion("gp_lastModUser like", value, "gpLastmoduser");
            return (Criteria) this;
        }

        public Criteria andGpLastmoduserNotLike(String value) {
            addCriterion("gp_lastModUser not like", value, "gpLastmoduser");
            return (Criteria) this;
        }

        public Criteria andGpLastmoduserIn(List<String> values) {
            addCriterion("gp_lastModUser in", values, "gpLastmoduser");
            return (Criteria) this;
        }

        public Criteria andGpLastmoduserNotIn(List<String> values) {
            addCriterion("gp_lastModUser not in", values, "gpLastmoduser");
            return (Criteria) this;
        }

        public Criteria andGpLastmoduserBetween(String value1, String value2) {
            addCriterion("gp_lastModUser between", value1, value2, "gpLastmoduser");
            return (Criteria) this;
        }

        public Criteria andGpLastmoduserNotBetween(String value1, String value2) {
            addCriterion("gp_lastModUser not between", value1, value2, "gpLastmoduser");
            return (Criteria) this;
        }

        public Criteria andGpRemarkIsNull() {
            addCriterion("gp_remark is null");
            return (Criteria) this;
        }

        public Criteria andGpRemarkIsNotNull() {
            addCriterion("gp_remark is not null");
            return (Criteria) this;
        }

        public Criteria andGpRemarkEqualTo(String value) {
            addCriterion("gp_remark =", value, "gpRemark");
            return (Criteria) this;
        }

        public Criteria andGpRemarkNotEqualTo(String value) {
            addCriterion("gp_remark <>", value, "gpRemark");
            return (Criteria) this;
        }

        public Criteria andGpRemarkGreaterThan(String value) {
            addCriterion("gp_remark >", value, "gpRemark");
            return (Criteria) this;
        }

        public Criteria andGpRemarkGreaterThanOrEqualTo(String value) {
            addCriterion("gp_remark >=", value, "gpRemark");
            return (Criteria) this;
        }

        public Criteria andGpRemarkLessThan(String value) {
            addCriterion("gp_remark <", value, "gpRemark");
            return (Criteria) this;
        }

        public Criteria andGpRemarkLessThanOrEqualTo(String value) {
            addCriterion("gp_remark <=", value, "gpRemark");
            return (Criteria) this;
        }

        public Criteria andGpRemarkLike(String value) {
            addCriterion("gp_remark like", value, "gpRemark");
            return (Criteria) this;
        }

        public Criteria andGpRemarkNotLike(String value) {
            addCriterion("gp_remark not like", value, "gpRemark");
            return (Criteria) this;
        }

        public Criteria andGpRemarkIn(List<String> values) {
            addCriterion("gp_remark in", values, "gpRemark");
            return (Criteria) this;
        }

        public Criteria andGpRemarkNotIn(List<String> values) {
            addCriterion("gp_remark not in", values, "gpRemark");
            return (Criteria) this;
        }

        public Criteria andGpRemarkBetween(String value1, String value2) {
            addCriterion("gp_remark between", value1, value2, "gpRemark");
            return (Criteria) this;
        }

        public Criteria andGpRemarkNotBetween(String value1, String value2) {
            addCriterion("gp_remark not between", value1, value2, "gpRemark");
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