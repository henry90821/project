package com.smi.am.dao.model;

import java.util.ArrayList;
import java.util.List;

public class AmDepartmentExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public AmDepartmentExample() {
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

        public Criteria andDDepartmentidIsNull() {
            addCriterion("d_departmentId is null");
            return (Criteria) this;
        }

        public Criteria andDDepartmentidIsNotNull() {
            addCriterion("d_departmentId is not null");
            return (Criteria) this;
        }

        public Criteria andDDepartmentidEqualTo(Integer value) {
            addCriterion("d_departmentId =", value, "dDepartmentid");
            return (Criteria) this;
        }

        public Criteria andDDepartmentidNotEqualTo(Integer value) {
            addCriterion("d_departmentId <>", value, "dDepartmentid");
            return (Criteria) this;
        }

        public Criteria andDDepartmentidGreaterThan(Integer value) {
            addCriterion("d_departmentId >", value, "dDepartmentid");
            return (Criteria) this;
        }

        public Criteria andDDepartmentidGreaterThanOrEqualTo(Integer value) {
            addCriterion("d_departmentId >=", value, "dDepartmentid");
            return (Criteria) this;
        }

        public Criteria andDDepartmentidLessThan(Integer value) {
            addCriterion("d_departmentId <", value, "dDepartmentid");
            return (Criteria) this;
        }

        public Criteria andDDepartmentidLessThanOrEqualTo(Integer value) {
            addCriterion("d_departmentId <=", value, "dDepartmentid");
            return (Criteria) this;
        }

        public Criteria andDDepartmentidIn(List<Integer> values) {
            addCriterion("d_departmentId in", values, "dDepartmentid");
            return (Criteria) this;
        }

        public Criteria andDDepartmentidNotIn(List<Integer> values) {
            addCriterion("d_departmentId not in", values, "dDepartmentid");
            return (Criteria) this;
        }

        public Criteria andDDepartmentidBetween(Integer value1, Integer value2) {
            addCriterion("d_departmentId between", value1, value2, "dDepartmentid");
            return (Criteria) this;
        }

        public Criteria andDDepartmentidNotBetween(Integer value1, Integer value2) {
            addCriterion("d_departmentId not between", value1, value2, "dDepartmentid");
            return (Criteria) this;
        }

        public Criteria andDDepartmentnameIsNull() {
            addCriterion("d_departmentName is null");
            return (Criteria) this;
        }

        public Criteria andDDepartmentnameIsNotNull() {
            addCriterion("d_departmentName is not null");
            return (Criteria) this;
        }

        public Criteria andDDepartmentnameEqualTo(String value) {
            addCriterion("d_departmentName =", value, "dDepartmentname");
            return (Criteria) this;
        }

        public Criteria andDDepartmentnameNotEqualTo(String value) {
            addCriterion("d_departmentName <>", value, "dDepartmentname");
            return (Criteria) this;
        }

        public Criteria andDDepartmentnameGreaterThan(String value) {
            addCriterion("d_departmentName >", value, "dDepartmentname");
            return (Criteria) this;
        }

        public Criteria andDDepartmentnameGreaterThanOrEqualTo(String value) {
            addCriterion("d_departmentName >=", value, "dDepartmentname");
            return (Criteria) this;
        }

        public Criteria andDDepartmentnameLessThan(String value) {
            addCriterion("d_departmentName <", value, "dDepartmentname");
            return (Criteria) this;
        }

        public Criteria andDDepartmentnameLessThanOrEqualTo(String value) {
            addCriterion("d_departmentName <=", value, "dDepartmentname");
            return (Criteria) this;
        }

        public Criteria andDDepartmentnameLike(String value) {
            addCriterion("d_departmentName like", value, "dDepartmentname");
            return (Criteria) this;
        }

        public Criteria andDDepartmentnameNotLike(String value) {
            addCriterion("d_departmentName not like", value, "dDepartmentname");
            return (Criteria) this;
        }

        public Criteria andDDepartmentnameIn(List<String> values) {
            addCriterion("d_departmentName in", values, "dDepartmentname");
            return (Criteria) this;
        }

        public Criteria andDDepartmentnameNotIn(List<String> values) {
            addCriterion("d_departmentName not in", values, "dDepartmentname");
            return (Criteria) this;
        }

        public Criteria andDDepartmentnameBetween(String value1, String value2) {
            addCriterion("d_departmentName between", value1, value2, "dDepartmentname");
            return (Criteria) this;
        }

        public Criteria andDDepartmentnameNotBetween(String value1, String value2) {
            addCriterion("d_departmentName not between", value1, value2, "dDepartmentname");
            return (Criteria) this;
        }

        public Criteria andDAreaidIsNull() {
            addCriterion("d_areaId is null");
            return (Criteria) this;
        }

        public Criteria andDAreaidIsNotNull() {
            addCriterion("d_areaId is not null");
            return (Criteria) this;
        }

        public Criteria andDAreaidEqualTo(Integer value) {
            addCriterion("d_areaId =", value, "dAreaid");
            return (Criteria) this;
        }

        public Criteria andDAreaidNotEqualTo(Integer value) {
            addCriterion("d_areaId <>", value, "dAreaid");
            return (Criteria) this;
        }

        public Criteria andDAreaidGreaterThan(Integer value) {
            addCriterion("d_areaId >", value, "dAreaid");
            return (Criteria) this;
        }

        public Criteria andDAreaidGreaterThanOrEqualTo(Integer value) {
            addCriterion("d_areaId >=", value, "dAreaid");
            return (Criteria) this;
        }

        public Criteria andDAreaidLessThan(Integer value) {
            addCriterion("d_areaId <", value, "dAreaid");
            return (Criteria) this;
        }

        public Criteria andDAreaidLessThanOrEqualTo(Integer value) {
            addCriterion("d_areaId <=", value, "dAreaid");
            return (Criteria) this;
        }

        public Criteria andDAreaidIn(List<Integer> values) {
            addCriterion("d_areaId in", values, "dAreaid");
            return (Criteria) this;
        }

        public Criteria andDAreaidNotIn(List<Integer> values) {
            addCriterion("d_areaId not in", values, "dAreaid");
            return (Criteria) this;
        }

        public Criteria andDAreaidBetween(Integer value1, Integer value2) {
            addCriterion("d_areaId between", value1, value2, "dAreaid");
            return (Criteria) this;
        }

        public Criteria andDAreaidNotBetween(Integer value1, Integer value2) {
            addCriterion("d_areaId not between", value1, value2, "dAreaid");
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