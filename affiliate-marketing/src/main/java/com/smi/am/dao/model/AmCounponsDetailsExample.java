package com.smi.am.dao.model;

import java.util.ArrayList;
import java.util.List;

public class AmCounponsDetailsExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public AmCounponsDetailsExample() {
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

        public Criteria andCdDetailidIsNull() {
            addCriterion("cd_detailId is null");
            return (Criteria) this;
        }

        public Criteria andCdDetailidIsNotNull() {
            addCriterion("cd_detailId is not null");
            return (Criteria) this;
        }

        public Criteria andCdDetailidEqualTo(String value) {
            addCriterion("cd_detailId =", value, "cdDetailid");
            return (Criteria) this;
        }

        public Criteria andCdDetailidNotEqualTo(String value) {
            addCriterion("cd_detailId <>", value, "cdDetailid");
            return (Criteria) this;
        }

        public Criteria andCdDetailidGreaterThan(String value) {
            addCriterion("cd_detailId >", value, "cdDetailid");
            return (Criteria) this;
        }

        public Criteria andCdDetailidGreaterThanOrEqualTo(String value) {
            addCriterion("cd_detailId >=", value, "cdDetailid");
            return (Criteria) this;
        }

        public Criteria andCdDetailidLessThan(String value) {
            addCriterion("cd_detailId <", value, "cdDetailid");
            return (Criteria) this;
        }

        public Criteria andCdDetailidLessThanOrEqualTo(String value) {
            addCriterion("cd_detailId <=", value, "cdDetailid");
            return (Criteria) this;
        }

        public Criteria andCdDetailidLike(String value) {
            addCriterion("cd_detailId like", value, "cdDetailid");
            return (Criteria) this;
        }

        public Criteria andCdDetailidNotLike(String value) {
            addCriterion("cd_detailId not like", value, "cdDetailid");
            return (Criteria) this;
        }

        public Criteria andCdDetailidIn(List<String> values) {
            addCriterion("cd_detailId in", values, "cdDetailid");
            return (Criteria) this;
        }

        public Criteria andCdDetailidNotIn(List<String> values) {
            addCriterion("cd_detailId not in", values, "cdDetailid");
            return (Criteria) this;
        }

        public Criteria andCdDetailidBetween(String value1, String value2) {
            addCriterion("cd_detailId between", value1, value2, "cdDetailid");
            return (Criteria) this;
        }

        public Criteria andCdDetailidNotBetween(String value1, String value2) {
            addCriterion("cd_detailId not between", value1, value2, "cdDetailid");
            return (Criteria) this;
        }

        public Criteria andCdCounponsidIsNull() {
            addCriterion("cd_counponsId is null");
            return (Criteria) this;
        }

        public Criteria andCdCounponsidIsNotNull() {
            addCriterion("cd_counponsId is not null");
            return (Criteria) this;
        }

        public Criteria andCdCounponsidEqualTo(String value) {
            addCriterion("cd_counponsId =", value, "cdCounponsid");
            return (Criteria) this;
        }

        public Criteria andCdCounponsidNotEqualTo(String value) {
            addCriterion("cd_counponsId <>", value, "cdCounponsid");
            return (Criteria) this;
        }

        public Criteria andCdCounponsidGreaterThan(String value) {
            addCriterion("cd_counponsId >", value, "cdCounponsid");
            return (Criteria) this;
        }

        public Criteria andCdCounponsidGreaterThanOrEqualTo(String value) {
            addCriterion("cd_counponsId >=", value, "cdCounponsid");
            return (Criteria) this;
        }

        public Criteria andCdCounponsidLessThan(String value) {
            addCriterion("cd_counponsId <", value, "cdCounponsid");
            return (Criteria) this;
        }

        public Criteria andCdCounponsidLessThanOrEqualTo(String value) {
            addCriterion("cd_counponsId <=", value, "cdCounponsid");
            return (Criteria) this;
        }

        public Criteria andCdCounponsidLike(String value) {
            addCriterion("cd_counponsId like", value, "cdCounponsid");
            return (Criteria) this;
        }

        public Criteria andCdCounponsidNotLike(String value) {
            addCriterion("cd_counponsId not like", value, "cdCounponsid");
            return (Criteria) this;
        }

        public Criteria andCdCounponsidIn(List<String> values) {
            addCriterion("cd_counponsId in", values, "cdCounponsid");
            return (Criteria) this;
        }

        public Criteria andCdCounponsidNotIn(List<String> values) {
            addCriterion("cd_counponsId not in", values, "cdCounponsid");
            return (Criteria) this;
        }

        public Criteria andCdCounponsidBetween(String value1, String value2) {
            addCriterion("cd_counponsId between", value1, value2, "cdCounponsid");
            return (Criteria) this;
        }

        public Criteria andCdCounponsidNotBetween(String value1, String value2) {
            addCriterion("cd_counponsId not between", value1, value2, "cdCounponsid");
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

        public Criteria andCdCustidIsNull() {
            addCriterion("cd_custId is null");
            return (Criteria) this;
        }

        public Criteria andCdCustidIsNotNull() {
            addCriterion("cd_custId is not null");
            return (Criteria) this;
        }

        public Criteria andCdCustidEqualTo(String value) {
            addCriterion("cd_custId =", value, "cdCustid");
            return (Criteria) this;
        }

        public Criteria andCdCustidNotEqualTo(String value) {
            addCriterion("cd_custId <>", value, "cdCustid");
            return (Criteria) this;
        }

        public Criteria andCdCustidGreaterThan(String value) {
            addCriterion("cd_custId >", value, "cdCustid");
            return (Criteria) this;
        }

        public Criteria andCdCustidGreaterThanOrEqualTo(String value) {
            addCriterion("cd_custId >=", value, "cdCustid");
            return (Criteria) this;
        }

        public Criteria andCdCustidLessThan(String value) {
            addCriterion("cd_custId <", value, "cdCustid");
            return (Criteria) this;
        }

        public Criteria andCdCustidLessThanOrEqualTo(String value) {
            addCriterion("cd_custId <=", value, "cdCustid");
            return (Criteria) this;
        }

        public Criteria andCdCustidLike(String value) {
            addCriterion("cd_custId like", value, "cdCustid");
            return (Criteria) this;
        }

        public Criteria andCdCustidNotLike(String value) {
            addCriterion("cd_custId not like", value, "cdCustid");
            return (Criteria) this;
        }

        public Criteria andCdCustidIn(List<String> values) {
            addCriterion("cd_custId in", values, "cdCustid");
            return (Criteria) this;
        }

        public Criteria andCdCustidNotIn(List<String> values) {
            addCriterion("cd_custId not in", values, "cdCustid");
            return (Criteria) this;
        }

        public Criteria andCdCustidBetween(String value1, String value2) {
            addCriterion("cd_custId between", value1, value2, "cdCustid");
            return (Criteria) this;
        }

        public Criteria andCdCustidNotBetween(String value1, String value2) {
            addCriterion("cd_custId not between", value1, value2, "cdCustid");
            return (Criteria) this;
        }

        public Criteria andCdCounponschanneltypeIsNull() {
            addCriterion("cd_counponsChannelType is null");
            return (Criteria) this;
        }

        public Criteria andCdCounponschanneltypeIsNotNull() {
            addCriterion("cd_counponsChannelType is not null");
            return (Criteria) this;
        }

        public Criteria andCdCounponschanneltypeEqualTo(Integer value) {
            addCriterion("cd_counponsChannelType =", value, "cdCounponschanneltype");
            return (Criteria) this;
        }

        public Criteria andCdCounponschanneltypeNotEqualTo(Integer value) {
            addCriterion("cd_counponsChannelType <>", value, "cdCounponschanneltype");
            return (Criteria) this;
        }

        public Criteria andCdCounponschanneltypeGreaterThan(Integer value) {
            addCriterion("cd_counponsChannelType >", value, "cdCounponschanneltype");
            return (Criteria) this;
        }

        public Criteria andCdCounponschanneltypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("cd_counponsChannelType >=", value, "cdCounponschanneltype");
            return (Criteria) this;
        }

        public Criteria andCdCounponschanneltypeLessThan(Integer value) {
            addCriterion("cd_counponsChannelType <", value, "cdCounponschanneltype");
            return (Criteria) this;
        }

        public Criteria andCdCounponschanneltypeLessThanOrEqualTo(Integer value) {
            addCriterion("cd_counponsChannelType <=", value, "cdCounponschanneltype");
            return (Criteria) this;
        }

        public Criteria andCdCounponschanneltypeIn(List<Integer> values) {
            addCriterion("cd_counponsChannelType in", values, "cdCounponschanneltype");
            return (Criteria) this;
        }

        public Criteria andCdCounponschanneltypeNotIn(List<Integer> values) {
            addCriterion("cd_counponsChannelType not in", values, "cdCounponschanneltype");
            return (Criteria) this;
        }

        public Criteria andCdCounponschanneltypeBetween(Integer value1, Integer value2) {
            addCriterion("cd_counponsChannelType between", value1, value2, "cdCounponschanneltype");
            return (Criteria) this;
        }

        public Criteria andCdCounponschanneltypeNotBetween(Integer value1, Integer value2) {
            addCriterion("cd_counponsChannelType not between", value1, value2, "cdCounponschanneltype");
            return (Criteria) this;
        }

        public Criteria andCdUsedIsNull() {
            addCriterion("cd_used is null");
            return (Criteria) this;
        }

        public Criteria andCdUsedIsNotNull() {
            addCriterion("cd_used is not null");
            return (Criteria) this;
        }

        public Criteria andCdUsedEqualTo(Integer value) {
            addCriterion("cd_used =", value, "cdUsed");
            return (Criteria) this;
        }

        public Criteria andCdUsedNotEqualTo(Integer value) {
            addCriterion("cd_used <>", value, "cdUsed");
            return (Criteria) this;
        }

        public Criteria andCdUsedGreaterThan(Integer value) {
            addCriterion("cd_used >", value, "cdUsed");
            return (Criteria) this;
        }

        public Criteria andCdUsedGreaterThanOrEqualTo(Integer value) {
            addCriterion("cd_used >=", value, "cdUsed");
            return (Criteria) this;
        }

        public Criteria andCdUsedLessThan(Integer value) {
            addCriterion("cd_used <", value, "cdUsed");
            return (Criteria) this;
        }

        public Criteria andCdUsedLessThanOrEqualTo(Integer value) {
            addCriterion("cd_used <=", value, "cdUsed");
            return (Criteria) this;
        }

        public Criteria andCdUsedIn(List<Integer> values) {
            addCriterion("cd_used in", values, "cdUsed");
            return (Criteria) this;
        }

        public Criteria andCdUsedNotIn(List<Integer> values) {
            addCriterion("cd_used not in", values, "cdUsed");
            return (Criteria) this;
        }

        public Criteria andCdUsedBetween(Integer value1, Integer value2) {
            addCriterion("cd_used between", value1, value2, "cdUsed");
            return (Criteria) this;
        }

        public Criteria andCdUsedNotBetween(Integer value1, Integer value2) {
            addCriterion("cd_used not between", value1, value2, "cdUsed");
            return (Criteria) this;
        }

        public Criteria andCdIsgitpackagecounponsIsNull() {
            addCriterion("cd_isGitPackageCounpons is null");
            return (Criteria) this;
        }

        public Criteria andCdIsgitpackagecounponsIsNotNull() {
            addCriterion("cd_isGitPackageCounpons is not null");
            return (Criteria) this;
        }

        public Criteria andCdIsgitpackagecounponsEqualTo(Integer value) {
            addCriterion("cd_isGitPackageCounpons =", value, "cdIsgitpackagecounpons");
            return (Criteria) this;
        }

        public Criteria andCdIsgitpackagecounponsNotEqualTo(Integer value) {
            addCriterion("cd_isGitPackageCounpons <>", value, "cdIsgitpackagecounpons");
            return (Criteria) this;
        }

        public Criteria andCdIsgitpackagecounponsGreaterThan(Integer value) {
            addCriterion("cd_isGitPackageCounpons >", value, "cdIsgitpackagecounpons");
            return (Criteria) this;
        }

        public Criteria andCdIsgitpackagecounponsGreaterThanOrEqualTo(Integer value) {
            addCriterion("cd_isGitPackageCounpons >=", value, "cdIsgitpackagecounpons");
            return (Criteria) this;
        }

        public Criteria andCdIsgitpackagecounponsLessThan(Integer value) {
            addCriterion("cd_isGitPackageCounpons <", value, "cdIsgitpackagecounpons");
            return (Criteria) this;
        }

        public Criteria andCdIsgitpackagecounponsLessThanOrEqualTo(Integer value) {
            addCriterion("cd_isGitPackageCounpons <=", value, "cdIsgitpackagecounpons");
            return (Criteria) this;
        }

        public Criteria andCdIsgitpackagecounponsIn(List<Integer> values) {
            addCriterion("cd_isGitPackageCounpons in", values, "cdIsgitpackagecounpons");
            return (Criteria) this;
        }

        public Criteria andCdIsgitpackagecounponsNotIn(List<Integer> values) {
            addCriterion("cd_isGitPackageCounpons not in", values, "cdIsgitpackagecounpons");
            return (Criteria) this;
        }

        public Criteria andCdIsgitpackagecounponsBetween(Integer value1, Integer value2) {
            addCriterion("cd_isGitPackageCounpons between", value1, value2, "cdIsgitpackagecounpons");
            return (Criteria) this;
        }

        public Criteria andCdIsgitpackagecounponsNotBetween(Integer value1, Integer value2) {
            addCriterion("cd_isGitPackageCounpons not between", value1, value2, "cdIsgitpackagecounpons");
            return (Criteria) this;
        }

        public Criteria andCdGitpackageidIsNull() {
            addCriterion("cd_gitPackageId is null");
            return (Criteria) this;
        }

        public Criteria andCdGitpackageidIsNotNull() {
            addCriterion("cd_gitPackageId is not null");
            return (Criteria) this;
        }

        public Criteria andCdGitpackageidEqualTo(Integer value) {
            addCriterion("cd_gitPackageId =", value, "cdGitpackageid");
            return (Criteria) this;
        }

        public Criteria andCdGitpackageidNotEqualTo(Integer value) {
            addCriterion("cd_gitPackageId <>", value, "cdGitpackageid");
            return (Criteria) this;
        }

        public Criteria andCdGitpackageidGreaterThan(Integer value) {
            addCriterion("cd_gitPackageId >", value, "cdGitpackageid");
            return (Criteria) this;
        }

        public Criteria andCdGitpackageidGreaterThanOrEqualTo(Integer value) {
            addCriterion("cd_gitPackageId >=", value, "cdGitpackageid");
            return (Criteria) this;
        }

        public Criteria andCdGitpackageidLessThan(Integer value) {
            addCriterion("cd_gitPackageId <", value, "cdGitpackageid");
            return (Criteria) this;
        }

        public Criteria andCdGitpackageidLessThanOrEqualTo(Integer value) {
            addCriterion("cd_gitPackageId <=", value, "cdGitpackageid");
            return (Criteria) this;
        }

        public Criteria andCdGitpackageidIn(List<Integer> values) {
            addCriterion("cd_gitPackageId in", values, "cdGitpackageid");
            return (Criteria) this;
        }

        public Criteria andCdGitpackageidNotIn(List<Integer> values) {
            addCriterion("cd_gitPackageId not in", values, "cdGitpackageid");
            return (Criteria) this;
        }

        public Criteria andCdGitpackageidBetween(Integer value1, Integer value2) {
            addCriterion("cd_gitPackageId between", value1, value2, "cdGitpackageid");
            return (Criteria) this;
        }

        public Criteria andCdGitpackageidNotBetween(Integer value1, Integer value2) {
            addCriterion("cd_gitPackageId not between", value1, value2, "cdGitpackageid");
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