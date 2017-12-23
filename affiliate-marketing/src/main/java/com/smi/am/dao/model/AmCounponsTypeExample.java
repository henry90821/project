package com.smi.am.dao.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AmCounponsTypeExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public AmCounponsTypeExample() {
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

        public Criteria andCtIdIsNull() {
            addCriterion("ct_id is null");
            return (Criteria) this;
        }

        public Criteria andCtIdIsNotNull() {
            addCriterion("ct_id is not null");
            return (Criteria) this;
        }

        public Criteria andCtIdEqualTo(Integer value) {
            addCriterion("ct_id =", value, "ctId");
            return (Criteria) this;
        }

        public Criteria andCtIdNotEqualTo(Integer value) {
            addCriterion("ct_id <>", value, "ctId");
            return (Criteria) this;
        }

        public Criteria andCtIdGreaterThan(Integer value) {
            addCriterion("ct_id >", value, "ctId");
            return (Criteria) this;
        }

        public Criteria andCtIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("ct_id >=", value, "ctId");
            return (Criteria) this;
        }

        public Criteria andCtIdLessThan(Integer value) {
            addCriterion("ct_id <", value, "ctId");
            return (Criteria) this;
        }

        public Criteria andCtIdLessThanOrEqualTo(Integer value) {
            addCriterion("ct_id <=", value, "ctId");
            return (Criteria) this;
        }

        public Criteria andCtIdIn(List<Integer> values) {
            addCriterion("ct_id in", values, "ctId");
            return (Criteria) this;
        }

        public Criteria andCtIdNotIn(List<Integer> values) {
            addCriterion("ct_id not in", values, "ctId");
            return (Criteria) this;
        }

        public Criteria andCtIdBetween(Integer value1, Integer value2) {
            addCriterion("ct_id between", value1, value2, "ctId");
            return (Criteria) this;
        }

        public Criteria andCtIdNotBetween(Integer value1, Integer value2) {
            addCriterion("ct_id not between", value1, value2, "ctId");
            return (Criteria) this;
        }

        public Criteria andCtCounponsnameIsNull() {
            addCriterion("ct_counponsName is null");
            return (Criteria) this;
        }

        public Criteria andCtCounponsnameIsNotNull() {
            addCriterion("ct_counponsName is not null");
            return (Criteria) this;
        }

        public Criteria andCtCounponsnameEqualTo(String value) {
            addCriterion("ct_counponsName =", value, "ctCounponsname");
            return (Criteria) this;
        }

        public Criteria andCtCounponsnameNotEqualTo(String value) {
            addCriterion("ct_counponsName <>", value, "ctCounponsname");
            return (Criteria) this;
        }

        public Criteria andCtCounponsnameGreaterThan(String value) {
            addCriterion("ct_counponsName >", value, "ctCounponsname");
            return (Criteria) this;
        }

        public Criteria andCtCounponsnameGreaterThanOrEqualTo(String value) {
            addCriterion("ct_counponsName >=", value, "ctCounponsname");
            return (Criteria) this;
        }

        public Criteria andCtCounponsnameLessThan(String value) {
            addCriterion("ct_counponsName <", value, "ctCounponsname");
            return (Criteria) this;
        }

        public Criteria andCtCounponsnameLessThanOrEqualTo(String value) {
            addCriterion("ct_counponsName <=", value, "ctCounponsname");
            return (Criteria) this;
        }

        public Criteria andCtCounponsnameLike(String value) {
            addCriterion("ct_counponsName like", value, "ctCounponsname");
            return (Criteria) this;
        }

        public Criteria andCtCounponsnameNotLike(String value) {
            addCriterion("ct_counponsName not like", value, "ctCounponsname");
            return (Criteria) this;
        }

        public Criteria andCtCounponsnameIn(List<String> values) {
            addCriterion("ct_counponsName in", values, "ctCounponsname");
            return (Criteria) this;
        }

        public Criteria andCtCounponsnameNotIn(List<String> values) {
            addCriterion("ct_counponsName not in", values, "ctCounponsname");
            return (Criteria) this;
        }

        public Criteria andCtCounponsnameBetween(String value1, String value2) {
            addCriterion("ct_counponsName between", value1, value2, "ctCounponsname");
            return (Criteria) this;
        }

        public Criteria andCtCounponsnameNotBetween(String value1, String value2) {
            addCriterion("ct_counponsName not between", value1, value2, "ctCounponsname");
            return (Criteria) this;
        }

        public Criteria andCtCounponstypeIsNull() {
            addCriterion("ct_counponsType is null");
            return (Criteria) this;
        }

        public Criteria andCtCounponstypeIsNotNull() {
            addCriterion("ct_counponsType is not null");
            return (Criteria) this;
        }

        public Criteria andCtCounponstypeEqualTo(Integer value) {
            addCriterion("ct_counponsType =", value, "ctCounponstype");
            return (Criteria) this;
        }

        public Criteria andCtCounponstypeNotEqualTo(Integer value) {
            addCriterion("ct_counponsType <>", value, "ctCounponstype");
            return (Criteria) this;
        }

        public Criteria andCtCounponstypeGreaterThan(Integer value) {
            addCriterion("ct_counponsType >", value, "ctCounponstype");
            return (Criteria) this;
        }

        public Criteria andCtCounponstypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("ct_counponsType >=", value, "ctCounponstype");
            return (Criteria) this;
        }

        public Criteria andCtCounponstypeLessThan(Integer value) {
            addCriterion("ct_counponsType <", value, "ctCounponstype");
            return (Criteria) this;
        }

        public Criteria andCtCounponstypeLessThanOrEqualTo(Integer value) {
            addCriterion("ct_counponsType <=", value, "ctCounponstype");
            return (Criteria) this;
        }

        public Criteria andCtCounponstypeIn(List<Integer> values) {
            addCriterion("ct_counponsType in", values, "ctCounponstype");
            return (Criteria) this;
        }

        public Criteria andCtCounponstypeNotIn(List<Integer> values) {
            addCriterion("ct_counponsType not in", values, "ctCounponstype");
            return (Criteria) this;
        }

        public Criteria andCtCounponstypeBetween(Integer value1, Integer value2) {
            addCriterion("ct_counponsType between", value1, value2, "ctCounponstype");
            return (Criteria) this;
        }

        public Criteria andCtCounponstypeNotBetween(Integer value1, Integer value2) {
            addCriterion("ct_counponsType not between", value1, value2, "ctCounponstype");
            return (Criteria) this;
        }

        public Criteria andCtRemarkIsNull() {
            addCriterion("ct_remark is null");
            return (Criteria) this;
        }

        public Criteria andCtRemarkIsNotNull() {
            addCriterion("ct_remark is not null");
            return (Criteria) this;
        }

        public Criteria andCtRemarkEqualTo(String value) {
            addCriterion("ct_remark =", value, "ctRemark");
            return (Criteria) this;
        }

        public Criteria andCtRemarkNotEqualTo(String value) {
            addCriterion("ct_remark <>", value, "ctRemark");
            return (Criteria) this;
        }

        public Criteria andCtRemarkGreaterThan(String value) {
            addCriterion("ct_remark >", value, "ctRemark");
            return (Criteria) this;
        }

        public Criteria andCtRemarkGreaterThanOrEqualTo(String value) {
            addCriterion("ct_remark >=", value, "ctRemark");
            return (Criteria) this;
        }

        public Criteria andCtRemarkLessThan(String value) {
            addCriterion("ct_remark <", value, "ctRemark");
            return (Criteria) this;
        }

        public Criteria andCtRemarkLessThanOrEqualTo(String value) {
            addCriterion("ct_remark <=", value, "ctRemark");
            return (Criteria) this;
        }

        public Criteria andCtRemarkLike(String value) {
            addCriterion("ct_remark like", value, "ctRemark");
            return (Criteria) this;
        }

        public Criteria andCtRemarkNotLike(String value) {
            addCriterion("ct_remark not like", value, "ctRemark");
            return (Criteria) this;
        }

        public Criteria andCtRemarkIn(List<String> values) {
            addCriterion("ct_remark in", values, "ctRemark");
            return (Criteria) this;
        }

        public Criteria andCtRemarkNotIn(List<String> values) {
            addCriterion("ct_remark not in", values, "ctRemark");
            return (Criteria) this;
        }

        public Criteria andCtRemarkBetween(String value1, String value2) {
            addCriterion("ct_remark between", value1, value2, "ctRemark");
            return (Criteria) this;
        }

        public Criteria andCtRemarkNotBetween(String value1, String value2) {
            addCriterion("ct_remark not between", value1, value2, "ctRemark");
            return (Criteria) this;
        }

        public Criteria andCtCreatedateIsNull() {
            addCriterion("ct_createDate is null");
            return (Criteria) this;
        }

        public Criteria andCtCreatedateIsNotNull() {
            addCriterion("ct_createDate is not null");
            return (Criteria) this;
        }

        public Criteria andCtCreatedateEqualTo(Date value) {
            addCriterion("ct_createDate =", value, "ctCreatedate");
            return (Criteria) this;
        }

        public Criteria andCtCreatedateNotEqualTo(Date value) {
            addCriterion("ct_createDate <>", value, "ctCreatedate");
            return (Criteria) this;
        }

        public Criteria andCtCreatedateGreaterThan(Date value) {
            addCriterion("ct_createDate >", value, "ctCreatedate");
            return (Criteria) this;
        }

        public Criteria andCtCreatedateGreaterThanOrEqualTo(Date value) {
            addCriterion("ct_createDate >=", value, "ctCreatedate");
            return (Criteria) this;
        }

        public Criteria andCtCreatedateLessThan(Date value) {
            addCriterion("ct_createDate <", value, "ctCreatedate");
            return (Criteria) this;
        }

        public Criteria andCtCreatedateLessThanOrEqualTo(Date value) {
            addCriterion("ct_createDate <=", value, "ctCreatedate");
            return (Criteria) this;
        }

        public Criteria andCtCreatedateIn(List<Date> values) {
            addCriterion("ct_createDate in", values, "ctCreatedate");
            return (Criteria) this;
        }

        public Criteria andCtCreatedateNotIn(List<Date> values) {
            addCriterion("ct_createDate not in", values, "ctCreatedate");
            return (Criteria) this;
        }

        public Criteria andCtCreatedateBetween(Date value1, Date value2) {
            addCriterion("ct_createDate between", value1, value2, "ctCreatedate");
            return (Criteria) this;
        }

        public Criteria andCtCreatedateNotBetween(Date value1, Date value2) {
            addCriterion("ct_createDate not between", value1, value2, "ctCreatedate");
            return (Criteria) this;
        }

        public Criteria andCtCreateuserIsNull() {
            addCriterion("ct_createUser is null");
            return (Criteria) this;
        }

        public Criteria andCtCreateuserIsNotNull() {
            addCriterion("ct_createUser is not null");
            return (Criteria) this;
        }

        public Criteria andCtCreateuserEqualTo(String value) {
            addCriterion("ct_createUser =", value, "ctCreateuser");
            return (Criteria) this;
        }

        public Criteria andCtCreateuserNotEqualTo(String value) {
            addCriterion("ct_createUser <>", value, "ctCreateuser");
            return (Criteria) this;
        }

        public Criteria andCtCreateuserGreaterThan(String value) {
            addCriterion("ct_createUser >", value, "ctCreateuser");
            return (Criteria) this;
        }

        public Criteria andCtCreateuserGreaterThanOrEqualTo(String value) {
            addCriterion("ct_createUser >=", value, "ctCreateuser");
            return (Criteria) this;
        }

        public Criteria andCtCreateuserLessThan(String value) {
            addCriterion("ct_createUser <", value, "ctCreateuser");
            return (Criteria) this;
        }

        public Criteria andCtCreateuserLessThanOrEqualTo(String value) {
            addCriterion("ct_createUser <=", value, "ctCreateuser");
            return (Criteria) this;
        }

        public Criteria andCtCreateuserLike(String value) {
            addCriterion("ct_createUser like", value, "ctCreateuser");
            return (Criteria) this;
        }

        public Criteria andCtCreateuserNotLike(String value) {
            addCriterion("ct_createUser not like", value, "ctCreateuser");
            return (Criteria) this;
        }

        public Criteria andCtCreateuserIn(List<String> values) {
            addCriterion("ct_createUser in", values, "ctCreateuser");
            return (Criteria) this;
        }

        public Criteria andCtCreateuserNotIn(List<String> values) {
            addCriterion("ct_createUser not in", values, "ctCreateuser");
            return (Criteria) this;
        }

        public Criteria andCtCreateuserBetween(String value1, String value2) {
            addCriterion("ct_createUser between", value1, value2, "ctCreateuser");
            return (Criteria) this;
        }

        public Criteria andCtCreateuserNotBetween(String value1, String value2) {
            addCriterion("ct_createUser not between", value1, value2, "ctCreateuser");
            return (Criteria) this;
        }

        public Criteria andCtLastmoddateIsNull() {
            addCriterion("ct_lastModDate is null");
            return (Criteria) this;
        }

        public Criteria andCtLastmoddateIsNotNull() {
            addCriterion("ct_lastModDate is not null");
            return (Criteria) this;
        }

        public Criteria andCtLastmoddateEqualTo(Date value) {
            addCriterion("ct_lastModDate =", value, "ctLastmoddate");
            return (Criteria) this;
        }

        public Criteria andCtLastmoddateNotEqualTo(Date value) {
            addCriterion("ct_lastModDate <>", value, "ctLastmoddate");
            return (Criteria) this;
        }

        public Criteria andCtLastmoddateGreaterThan(Date value) {
            addCriterion("ct_lastModDate >", value, "ctLastmoddate");
            return (Criteria) this;
        }

        public Criteria andCtLastmoddateGreaterThanOrEqualTo(Date value) {
            addCriterion("ct_lastModDate >=", value, "ctLastmoddate");
            return (Criteria) this;
        }

        public Criteria andCtLastmoddateLessThan(Date value) {
            addCriterion("ct_lastModDate <", value, "ctLastmoddate");
            return (Criteria) this;
        }

        public Criteria andCtLastmoddateLessThanOrEqualTo(Date value) {
            addCriterion("ct_lastModDate <=", value, "ctLastmoddate");
            return (Criteria) this;
        }

        public Criteria andCtLastmoddateIn(List<Date> values) {
            addCriterion("ct_lastModDate in", values, "ctLastmoddate");
            return (Criteria) this;
        }

        public Criteria andCtLastmoddateNotIn(List<Date> values) {
            addCriterion("ct_lastModDate not in", values, "ctLastmoddate");
            return (Criteria) this;
        }

        public Criteria andCtLastmoddateBetween(Date value1, Date value2) {
            addCriterion("ct_lastModDate between", value1, value2, "ctLastmoddate");
            return (Criteria) this;
        }

        public Criteria andCtLastmoddateNotBetween(Date value1, Date value2) {
            addCriterion("ct_lastModDate not between", value1, value2, "ctLastmoddate");
            return (Criteria) this;
        }

        public Criteria andCtLastmoduserIsNull() {
            addCriterion("ct_lastModUser is null");
            return (Criteria) this;
        }

        public Criteria andCtLastmoduserIsNotNull() {
            addCriterion("ct_lastModUser is not null");
            return (Criteria) this;
        }

        public Criteria andCtLastmoduserEqualTo(String value) {
            addCriterion("ct_lastModUser =", value, "ctLastmoduser");
            return (Criteria) this;
        }

        public Criteria andCtLastmoduserNotEqualTo(String value) {
            addCriterion("ct_lastModUser <>", value, "ctLastmoduser");
            return (Criteria) this;
        }

        public Criteria andCtLastmoduserGreaterThan(String value) {
            addCriterion("ct_lastModUser >", value, "ctLastmoduser");
            return (Criteria) this;
        }

        public Criteria andCtLastmoduserGreaterThanOrEqualTo(String value) {
            addCriterion("ct_lastModUser >=", value, "ctLastmoduser");
            return (Criteria) this;
        }

        public Criteria andCtLastmoduserLessThan(String value) {
            addCriterion("ct_lastModUser <", value, "ctLastmoduser");
            return (Criteria) this;
        }

        public Criteria andCtLastmoduserLessThanOrEqualTo(String value) {
            addCriterion("ct_lastModUser <=", value, "ctLastmoduser");
            return (Criteria) this;
        }

        public Criteria andCtLastmoduserLike(String value) {
            addCriterion("ct_lastModUser like", value, "ctLastmoduser");
            return (Criteria) this;
        }

        public Criteria andCtLastmoduserNotLike(String value) {
            addCriterion("ct_lastModUser not like", value, "ctLastmoduser");
            return (Criteria) this;
        }

        public Criteria andCtLastmoduserIn(List<String> values) {
            addCriterion("ct_lastModUser in", values, "ctLastmoduser");
            return (Criteria) this;
        }

        public Criteria andCtLastmoduserNotIn(List<String> values) {
            addCriterion("ct_lastModUser not in", values, "ctLastmoduser");
            return (Criteria) this;
        }

        public Criteria andCtLastmoduserBetween(String value1, String value2) {
            addCriterion("ct_lastModUser between", value1, value2, "ctLastmoduser");
            return (Criteria) this;
        }

        public Criteria andCtLastmoduserNotBetween(String value1, String value2) {
            addCriterion("ct_lastModUser not between", value1, value2, "ctLastmoduser");
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