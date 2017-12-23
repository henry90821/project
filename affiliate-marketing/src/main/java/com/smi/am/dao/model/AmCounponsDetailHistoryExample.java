package com.smi.am.dao.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AmCounponsDetailHistoryExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public AmCounponsDetailHistoryExample() {
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

        public Criteria andCdhDetailidIsNull() {
            addCriterion("cdh_detailId is null");
            return (Criteria) this;
        }

        public Criteria andCdhDetailidIsNotNull() {
            addCriterion("cdh_detailId is not null");
            return (Criteria) this;
        }

        public Criteria andCdhDetailidEqualTo(Integer value) {
            addCriterion("cdh_detailId =", value, "cdhDetailid");
            return (Criteria) this;
        }

        public Criteria andCdhDetailidNotEqualTo(Integer value) {
            addCriterion("cdh_detailId <>", value, "cdhDetailid");
            return (Criteria) this;
        }

        public Criteria andCdhDetailidGreaterThan(Integer value) {
            addCriterion("cdh_detailId >", value, "cdhDetailid");
            return (Criteria) this;
        }

        public Criteria andCdhDetailidGreaterThanOrEqualTo(Integer value) {
            addCriterion("cdh_detailId >=", value, "cdhDetailid");
            return (Criteria) this;
        }

        public Criteria andCdhDetailidLessThan(Integer value) {
            addCriterion("cdh_detailId <", value, "cdhDetailid");
            return (Criteria) this;
        }

        public Criteria andCdhDetailidLessThanOrEqualTo(Integer value) {
            addCriterion("cdh_detailId <=", value, "cdhDetailid");
            return (Criteria) this;
        }

        public Criteria andCdhDetailidIn(List<Integer> values) {
            addCriterion("cdh_detailId in", values, "cdhDetailid");
            return (Criteria) this;
        }

        public Criteria andCdhDetailidNotIn(List<Integer> values) {
            addCriterion("cdh_detailId not in", values, "cdhDetailid");
            return (Criteria) this;
        }

        public Criteria andCdhDetailidBetween(Integer value1, Integer value2) {
            addCriterion("cdh_detailId between", value1, value2, "cdhDetailid");
            return (Criteria) this;
        }

        public Criteria andCdhDetailidNotBetween(Integer value1, Integer value2) {
            addCriterion("cdh_detailId not between", value1, value2, "cdhDetailid");
            return (Criteria) this;
        }

        public Criteria andCdhCounponsidIsNull() {
            addCriterion("cdh_counponsId is null");
            return (Criteria) this;
        }

        public Criteria andCdhCounponsidIsNotNull() {
            addCriterion("cdh_counponsId is not null");
            return (Criteria) this;
        }

        public Criteria andCdhCounponsidEqualTo(String value) {
            addCriterion("cdh_counponsId =", value, "cdhCounponsid");
            return (Criteria) this;
        }

        public Criteria andCdhCounponsidNotEqualTo(String value) {
            addCriterion("cdh_counponsId <>", value, "cdhCounponsid");
            return (Criteria) this;
        }

        public Criteria andCdhCounponsidGreaterThan(String value) {
            addCriterion("cdh_counponsId >", value, "cdhCounponsid");
            return (Criteria) this;
        }

        public Criteria andCdhCounponsidGreaterThanOrEqualTo(String value) {
            addCriterion("cdh_counponsId >=", value, "cdhCounponsid");
            return (Criteria) this;
        }

        public Criteria andCdhCounponsidLessThan(String value) {
            addCriterion("cdh_counponsId <", value, "cdhCounponsid");
            return (Criteria) this;
        }

        public Criteria andCdhCounponsidLessThanOrEqualTo(String value) {
            addCriterion("cdh_counponsId <=", value, "cdhCounponsid");
            return (Criteria) this;
        }

        public Criteria andCdhCounponsidLike(String value) {
            addCriterion("cdh_counponsId like", value, "cdhCounponsid");
            return (Criteria) this;
        }

        public Criteria andCdhCounponsidNotLike(String value) {
            addCriterion("cdh_counponsId not like", value, "cdhCounponsid");
            return (Criteria) this;
        }

        public Criteria andCdhCounponsidIn(List<String> values) {
            addCriterion("cdh_counponsId in", values, "cdhCounponsid");
            return (Criteria) this;
        }

        public Criteria andCdhCounponsidNotIn(List<String> values) {
            addCriterion("cdh_counponsId not in", values, "cdhCounponsid");
            return (Criteria) this;
        }

        public Criteria andCdhCounponsidBetween(String value1, String value2) {
            addCriterion("cdh_counponsId between", value1, value2, "cdhCounponsid");
            return (Criteria) this;
        }

        public Criteria andCdhCounponsidNotBetween(String value1, String value2) {
            addCriterion("cdh_counponsId not between", value1, value2, "cdhCounponsid");
            return (Criteria) this;
        }

        public Criteria andCdLoginmobileIsNull() {
            addCriterion("cd_loginMobile is null");
            return (Criteria) this;
        }

        public Criteria andCdLoginmobileIsNotNull() {
            addCriterion("cd_loginMobile is not null");
            return (Criteria) this;
        }

        public Criteria andCdLoginmobileEqualTo(String value) {
            addCriterion("cd_loginMobile =", value, "cdLoginmobile");
            return (Criteria) this;
        }

        public Criteria andCdLoginmobileNotEqualTo(String value) {
            addCriterion("cd_loginMobile <>", value, "cdLoginmobile");
            return (Criteria) this;
        }

        public Criteria andCdLoginmobileGreaterThan(String value) {
            addCriterion("cd_loginMobile >", value, "cdLoginmobile");
            return (Criteria) this;
        }

        public Criteria andCdLoginmobileGreaterThanOrEqualTo(String value) {
            addCriterion("cd_loginMobile >=", value, "cdLoginmobile");
            return (Criteria) this;
        }

        public Criteria andCdLoginmobileLessThan(String value) {
            addCriterion("cd_loginMobile <", value, "cdLoginmobile");
            return (Criteria) this;
        }

        public Criteria andCdLoginmobileLessThanOrEqualTo(String value) {
            addCriterion("cd_loginMobile <=", value, "cdLoginmobile");
            return (Criteria) this;
        }

        public Criteria andCdLoginmobileLike(String value) {
            addCriterion("cd_loginMobile like", value, "cdLoginmobile");
            return (Criteria) this;
        }

        public Criteria andCdLoginmobileNotLike(String value) {
            addCriterion("cd_loginMobile not like", value, "cdLoginmobile");
            return (Criteria) this;
        }

        public Criteria andCdLoginmobileIn(List<String> values) {
            addCriterion("cd_loginMobile in", values, "cdLoginmobile");
            return (Criteria) this;
        }

        public Criteria andCdLoginmobileNotIn(List<String> values) {
            addCriterion("cd_loginMobile not in", values, "cdLoginmobile");
            return (Criteria) this;
        }

        public Criteria andCdLoginmobileBetween(String value1, String value2) {
            addCriterion("cd_loginMobile between", value1, value2, "cdLoginmobile");
            return (Criteria) this;
        }

        public Criteria andCdLoginmobileNotBetween(String value1, String value2) {
            addCriterion("cd_loginMobile not between", value1, value2, "cdLoginmobile");
            return (Criteria) this;
        }

        public Criteria andCdhCustidIsNull() {
            addCriterion("cdh_custId is null");
            return (Criteria) this;
        }

        public Criteria andCdhCustidIsNotNull() {
            addCriterion("cdh_custId is not null");
            return (Criteria) this;
        }

        public Criteria andCdhCustidEqualTo(String value) {
            addCriterion("cdh_custId =", value, "cdhCustid");
            return (Criteria) this;
        }

        public Criteria andCdhCustidNotEqualTo(String value) {
            addCriterion("cdh_custId <>", value, "cdhCustid");
            return (Criteria) this;
        }

        public Criteria andCdhCustidGreaterThan(String value) {
            addCriterion("cdh_custId >", value, "cdhCustid");
            return (Criteria) this;
        }

        public Criteria andCdhCustidGreaterThanOrEqualTo(String value) {
            addCriterion("cdh_custId >=", value, "cdhCustid");
            return (Criteria) this;
        }

        public Criteria andCdhCustidLessThan(String value) {
            addCriterion("cdh_custId <", value, "cdhCustid");
            return (Criteria) this;
        }

        public Criteria andCdhCustidLessThanOrEqualTo(String value) {
            addCriterion("cdh_custId <=", value, "cdhCustid");
            return (Criteria) this;
        }

        public Criteria andCdhCustidLike(String value) {
            addCriterion("cdh_custId like", value, "cdhCustid");
            return (Criteria) this;
        }

        public Criteria andCdhCustidNotLike(String value) {
            addCriterion("cdh_custId not like", value, "cdhCustid");
            return (Criteria) this;
        }

        public Criteria andCdhCustidIn(List<String> values) {
            addCriterion("cdh_custId in", values, "cdhCustid");
            return (Criteria) this;
        }

        public Criteria andCdhCustidNotIn(List<String> values) {
            addCriterion("cdh_custId not in", values, "cdhCustid");
            return (Criteria) this;
        }

        public Criteria andCdhCustidBetween(String value1, String value2) {
            addCriterion("cdh_custId between", value1, value2, "cdhCustid");
            return (Criteria) this;
        }

        public Criteria andCdhCustidNotBetween(String value1, String value2) {
            addCriterion("cdh_custId not between", value1, value2, "cdhCustid");
            return (Criteria) this;
        }

        public Criteria andCdhCounponschanneltypeIsNull() {
            addCriterion("cdh_counponsChannelType is null");
            return (Criteria) this;
        }

        public Criteria andCdhCounponschanneltypeIsNotNull() {
            addCriterion("cdh_counponsChannelType is not null");
            return (Criteria) this;
        }

        public Criteria andCdhCounponschanneltypeEqualTo(Integer value) {
            addCriterion("cdh_counponsChannelType =", value, "cdhCounponschanneltype");
            return (Criteria) this;
        }

        public Criteria andCdhCounponschanneltypeNotEqualTo(Integer value) {
            addCriterion("cdh_counponsChannelType <>", value, "cdhCounponschanneltype");
            return (Criteria) this;
        }

        public Criteria andCdhCounponschanneltypeGreaterThan(Integer value) {
            addCriterion("cdh_counponsChannelType >", value, "cdhCounponschanneltype");
            return (Criteria) this;
        }

        public Criteria andCdhCounponschanneltypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("cdh_counponsChannelType >=", value, "cdhCounponschanneltype");
            return (Criteria) this;
        }

        public Criteria andCdhCounponschanneltypeLessThan(Integer value) {
            addCriterion("cdh_counponsChannelType <", value, "cdhCounponschanneltype");
            return (Criteria) this;
        }

        public Criteria andCdhCounponschanneltypeLessThanOrEqualTo(Integer value) {
            addCriterion("cdh_counponsChannelType <=", value, "cdhCounponschanneltype");
            return (Criteria) this;
        }

        public Criteria andCdhCounponschanneltypeIn(List<Integer> values) {
            addCriterion("cdh_counponsChannelType in", values, "cdhCounponschanneltype");
            return (Criteria) this;
        }

        public Criteria andCdhCounponschanneltypeNotIn(List<Integer> values) {
            addCriterion("cdh_counponsChannelType not in", values, "cdhCounponschanneltype");
            return (Criteria) this;
        }

        public Criteria andCdhCounponschanneltypeBetween(Integer value1, Integer value2) {
            addCriterion("cdh_counponsChannelType between", value1, value2, "cdhCounponschanneltype");
            return (Criteria) this;
        }

        public Criteria andCdhCounponschanneltypeNotBetween(Integer value1, Integer value2) {
            addCriterion("cdh_counponsChannelType not between", value1, value2, "cdhCounponschanneltype");
            return (Criteria) this;
        }

        public Criteria andCdhIsgitpackagecounponsIsNull() {
            addCriterion("cdh_isGitPackageCounpons is null");
            return (Criteria) this;
        }

        public Criteria andCdhIsgitpackagecounponsIsNotNull() {
            addCriterion("cdh_isGitPackageCounpons is not null");
            return (Criteria) this;
        }

        public Criteria andCdhIsgitpackagecounponsEqualTo(Integer value) {
            addCriterion("cdh_isGitPackageCounpons =", value, "cdhIsgitpackagecounpons");
            return (Criteria) this;
        }

        public Criteria andCdhIsgitpackagecounponsNotEqualTo(Integer value) {
            addCriterion("cdh_isGitPackageCounpons <>", value, "cdhIsgitpackagecounpons");
            return (Criteria) this;
        }

        public Criteria andCdhIsgitpackagecounponsGreaterThan(Integer value) {
            addCriterion("cdh_isGitPackageCounpons >", value, "cdhIsgitpackagecounpons");
            return (Criteria) this;
        }

        public Criteria andCdhIsgitpackagecounponsGreaterThanOrEqualTo(Integer value) {
            addCriterion("cdh_isGitPackageCounpons >=", value, "cdhIsgitpackagecounpons");
            return (Criteria) this;
        }

        public Criteria andCdhIsgitpackagecounponsLessThan(Integer value) {
            addCriterion("cdh_isGitPackageCounpons <", value, "cdhIsgitpackagecounpons");
            return (Criteria) this;
        }

        public Criteria andCdhIsgitpackagecounponsLessThanOrEqualTo(Integer value) {
            addCriterion("cdh_isGitPackageCounpons <=", value, "cdhIsgitpackagecounpons");
            return (Criteria) this;
        }

        public Criteria andCdhIsgitpackagecounponsIn(List<Integer> values) {
            addCriterion("cdh_isGitPackageCounpons in", values, "cdhIsgitpackagecounpons");
            return (Criteria) this;
        }

        public Criteria andCdhIsgitpackagecounponsNotIn(List<Integer> values) {
            addCriterion("cdh_isGitPackageCounpons not in", values, "cdhIsgitpackagecounpons");
            return (Criteria) this;
        }

        public Criteria andCdhIsgitpackagecounponsBetween(Integer value1, Integer value2) {
            addCriterion("cdh_isGitPackageCounpons between", value1, value2, "cdhIsgitpackagecounpons");
            return (Criteria) this;
        }

        public Criteria andCdhIsgitpackagecounponsNotBetween(Integer value1, Integer value2) {
            addCriterion("cdh_isGitPackageCounpons not between", value1, value2, "cdhIsgitpackagecounpons");
            return (Criteria) this;
        }

        public Criteria andCdhGitpackageidIsNull() {
            addCriterion("cdh_gitPackageId is null");
            return (Criteria) this;
        }

        public Criteria andCdhGitpackageidIsNotNull() {
            addCriterion("cdh_gitPackageId is not null");
            return (Criteria) this;
        }

        public Criteria andCdhGitpackageidEqualTo(Integer value) {
            addCriterion("cdh_gitPackageId =", value, "cdhGitpackageid");
            return (Criteria) this;
        }

        public Criteria andCdhGitpackageidNotEqualTo(Integer value) {
            addCriterion("cdh_gitPackageId <>", value, "cdhGitpackageid");
            return (Criteria) this;
        }

        public Criteria andCdhGitpackageidGreaterThan(Integer value) {
            addCriterion("cdh_gitPackageId >", value, "cdhGitpackageid");
            return (Criteria) this;
        }

        public Criteria andCdhGitpackageidGreaterThanOrEqualTo(Integer value) {
            addCriterion("cdh_gitPackageId >=", value, "cdhGitpackageid");
            return (Criteria) this;
        }

        public Criteria andCdhGitpackageidLessThan(Integer value) {
            addCriterion("cdh_gitPackageId <", value, "cdhGitpackageid");
            return (Criteria) this;
        }

        public Criteria andCdhGitpackageidLessThanOrEqualTo(Integer value) {
            addCriterion("cdh_gitPackageId <=", value, "cdhGitpackageid");
            return (Criteria) this;
        }

        public Criteria andCdhGitpackageidIn(List<Integer> values) {
            addCriterion("cdh_gitPackageId in", values, "cdhGitpackageid");
            return (Criteria) this;
        }

        public Criteria andCdhGitpackageidNotIn(List<Integer> values) {
            addCriterion("cdh_gitPackageId not in", values, "cdhGitpackageid");
            return (Criteria) this;
        }

        public Criteria andCdhGitpackageidBetween(Integer value1, Integer value2) {
            addCriterion("cdh_gitPackageId between", value1, value2, "cdhGitpackageid");
            return (Criteria) this;
        }

        public Criteria andCdhGitpackageidNotBetween(Integer value1, Integer value2) {
            addCriterion("cdh_gitPackageId not between", value1, value2, "cdhGitpackageid");
            return (Criteria) this;
        }

        public Criteria andCdhUsedtimeIsNull() {
            addCriterion("cdh_usedTime is null");
            return (Criteria) this;
        }

        public Criteria andCdhUsedtimeIsNotNull() {
            addCriterion("cdh_usedTime is not null");
            return (Criteria) this;
        }

        public Criteria andCdhUsedtimeEqualTo(Date value) {
            addCriterion("cdh_usedTime =", value, "cdhUsedtime");
            return (Criteria) this;
        }

        public Criteria andCdhUsedtimeNotEqualTo(Date value) {
            addCriterion("cdh_usedTime <>", value, "cdhUsedtime");
            return (Criteria) this;
        }

        public Criteria andCdhUsedtimeGreaterThan(Date value) {
            addCriterion("cdh_usedTime >", value, "cdhUsedtime");
            return (Criteria) this;
        }

        public Criteria andCdhUsedtimeGreaterThanOrEqualTo(Date value) {
            addCriterion("cdh_usedTime >=", value, "cdhUsedtime");
            return (Criteria) this;
        }

        public Criteria andCdhUsedtimeLessThan(Date value) {
            addCriterion("cdh_usedTime <", value, "cdhUsedtime");
            return (Criteria) this;
        }

        public Criteria andCdhUsedtimeLessThanOrEqualTo(Date value) {
            addCriterion("cdh_usedTime <=", value, "cdhUsedtime");
            return (Criteria) this;
        }

        public Criteria andCdhUsedtimeIn(List<Date> values) {
            addCriterion("cdh_usedTime in", values, "cdhUsedtime");
            return (Criteria) this;
        }

        public Criteria andCdhUsedtimeNotIn(List<Date> values) {
            addCriterion("cdh_usedTime not in", values, "cdhUsedtime");
            return (Criteria) this;
        }

        public Criteria andCdhUsedtimeBetween(Date value1, Date value2) {
            addCriterion("cdh_usedTime between", value1, value2, "cdhUsedtime");
            return (Criteria) this;
        }

        public Criteria andCdhUsedtimeNotBetween(Date value1, Date value2) {
            addCriterion("cdh_usedTime not between", value1, value2, "cdhUsedtime");
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