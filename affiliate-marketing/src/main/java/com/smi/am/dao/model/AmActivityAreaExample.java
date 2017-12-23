package com.smi.am.dao.model;

import java.util.ArrayList;
import java.util.List;

public class AmActivityAreaExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public AmActivityAreaExample() {
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

        public Criteria andAIdIsNull() {
            addCriterion("a_id is null");
            return (Criteria) this;
        }

        public Criteria andAIdIsNotNull() {
            addCriterion("a_id is not null");
            return (Criteria) this;
        }

        public Criteria andAIdEqualTo(Integer value) {
            addCriterion("a_id =", value, "aId");
            return (Criteria) this;
        }

        public Criteria andAIdNotEqualTo(Integer value) {
            addCriterion("a_id <>", value, "aId");
            return (Criteria) this;
        }

        public Criteria andAIdGreaterThan(Integer value) {
            addCriterion("a_id >", value, "aId");
            return (Criteria) this;
        }

        public Criteria andAIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("a_id >=", value, "aId");
            return (Criteria) this;
        }

        public Criteria andAIdLessThan(Integer value) {
            addCriterion("a_id <", value, "aId");
            return (Criteria) this;
        }

        public Criteria andAIdLessThanOrEqualTo(Integer value) {
            addCriterion("a_id <=", value, "aId");
            return (Criteria) this;
        }

        public Criteria andAIdIn(List<Integer> values) {
            addCriterion("a_id in", values, "aId");
            return (Criteria) this;
        }

        public Criteria andAIdNotIn(List<Integer> values) {
            addCriterion("a_id not in", values, "aId");
            return (Criteria) this;
        }

        public Criteria andAIdBetween(Integer value1, Integer value2) {
            addCriterion("a_id between", value1, value2, "aId");
            return (Criteria) this;
        }

        public Criteria andAIdNotBetween(Integer value1, Integer value2) {
            addCriterion("a_id not between", value1, value2, "aId");
            return (Criteria) this;
        }

        public Criteria andAActivityareaIsNull() {
            addCriterion("a_activityArea is null");
            return (Criteria) this;
        }

        public Criteria andAActivityareaIsNotNull() {
            addCriterion("a_activityArea is not null");
            return (Criteria) this;
        }

        public Criteria andAActivityareaEqualTo(String value) {
            addCriterion("a_activityArea =", value, "aActivityarea");
            return (Criteria) this;
        }

        public Criteria andAActivityareaNotEqualTo(String value) {
            addCriterion("a_activityArea <>", value, "aActivityarea");
            return (Criteria) this;
        }

        public Criteria andAActivityareaGreaterThan(String value) {
            addCriterion("a_activityArea >", value, "aActivityarea");
            return (Criteria) this;
        }

        public Criteria andAActivityareaGreaterThanOrEqualTo(String value) {
            addCriterion("a_activityArea >=", value, "aActivityarea");
            return (Criteria) this;
        }

        public Criteria andAActivityareaLessThan(String value) {
            addCriterion("a_activityArea <", value, "aActivityarea");
            return (Criteria) this;
        }

        public Criteria andAActivityareaLessThanOrEqualTo(String value) {
            addCriterion("a_activityArea <=", value, "aActivityarea");
            return (Criteria) this;
        }

        public Criteria andAActivityareaLike(String value) {
            addCriterion("a_activityArea like", value, "aActivityarea");
            return (Criteria) this;
        }

        public Criteria andAActivityareaNotLike(String value) {
            addCriterion("a_activityArea not like", value, "aActivityarea");
            return (Criteria) this;
        }

        public Criteria andAActivityareaIn(List<String> values) {
            addCriterion("a_activityArea in", values, "aActivityarea");
            return (Criteria) this;
        }

        public Criteria andAActivityareaNotIn(List<String> values) {
            addCriterion("a_activityArea not in", values, "aActivityarea");
            return (Criteria) this;
        }

        public Criteria andAActivityareaBetween(String value1, String value2) {
            addCriterion("a_activityArea between", value1, value2, "aActivityarea");
            return (Criteria) this;
        }

        public Criteria andAActivityareaNotBetween(String value1, String value2) {
            addCriterion("a_activityArea not between", value1, value2, "aActivityarea");
            return (Criteria) this;
        }

        public Criteria andAAreapidIsNull() {
            addCriterion("a_areaPid is null");
            return (Criteria) this;
        }

        public Criteria andAAreapidIsNotNull() {
            addCriterion("a_areaPid is not null");
            return (Criteria) this;
        }

        public Criteria andAAreapidEqualTo(Integer value) {
            addCriterion("a_areaPid =", value, "aAreapid");
            return (Criteria) this;
        }

        public Criteria andAAreapidNotEqualTo(Integer value) {
            addCriterion("a_areaPid <>", value, "aAreapid");
            return (Criteria) this;
        }

        public Criteria andAAreapidGreaterThan(Integer value) {
            addCriterion("a_areaPid >", value, "aAreapid");
            return (Criteria) this;
        }

        public Criteria andAAreapidGreaterThanOrEqualTo(Integer value) {
            addCriterion("a_areaPid >=", value, "aAreapid");
            return (Criteria) this;
        }

        public Criteria andAAreapidLessThan(Integer value) {
            addCriterion("a_areaPid <", value, "aAreapid");
            return (Criteria) this;
        }

        public Criteria andAAreapidLessThanOrEqualTo(Integer value) {
            addCriterion("a_areaPid <=", value, "aAreapid");
            return (Criteria) this;
        }

        public Criteria andAAreapidIn(List<Integer> values) {
            addCriterion("a_areaPid in", values, "aAreapid");
            return (Criteria) this;
        }

        public Criteria andAAreapidNotIn(List<Integer> values) {
            addCriterion("a_areaPid not in", values, "aAreapid");
            return (Criteria) this;
        }

        public Criteria andAAreapidBetween(Integer value1, Integer value2) {
            addCriterion("a_areaPid between", value1, value2, "aAreapid");
            return (Criteria) this;
        }

        public Criteria andAAreapidNotBetween(Integer value1, Integer value2) {
            addCriterion("a_areaPid not between", value1, value2, "aAreapid");
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