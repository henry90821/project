package com.smi.am.dao.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AmUserExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public AmUserExample() {
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

        public Criteria andUIdIsNull() {
            addCriterion("u_id is null");
            return (Criteria) this;
        }

        public Criteria andUIdIsNotNull() {
            addCriterion("u_id is not null");
            return (Criteria) this;
        }

        public Criteria andUIdEqualTo(Integer value) {
            addCriterion("u_id =", value, "uId");
            return (Criteria) this;
        }

        public Criteria andUIdNotEqualTo(Integer value) {
            addCriterion("u_id <>", value, "uId");
            return (Criteria) this;
        }

        public Criteria andUIdGreaterThan(Integer value) {
            addCriterion("u_id >", value, "uId");
            return (Criteria) this;
        }

        public Criteria andUIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("u_id >=", value, "uId");
            return (Criteria) this;
        }

        public Criteria andUIdLessThan(Integer value) {
            addCriterion("u_id <", value, "uId");
            return (Criteria) this;
        }

        public Criteria andUIdLessThanOrEqualTo(Integer value) {
            addCriterion("u_id <=", value, "uId");
            return (Criteria) this;
        }

        public Criteria andUIdIn(List<Integer> values) {
            addCriterion("u_id in", values, "uId");
            return (Criteria) this;
        }

        public Criteria andUIdNotIn(List<Integer> values) {
            addCriterion("u_id not in", values, "uId");
            return (Criteria) this;
        }

        public Criteria andUIdBetween(Integer value1, Integer value2) {
            addCriterion("u_id between", value1, value2, "uId");
            return (Criteria) this;
        }

        public Criteria andUIdNotBetween(Integer value1, Integer value2) {
            addCriterion("u_id not between", value1, value2, "uId");
            return (Criteria) this;
        }

        public Criteria andURoletypeIsNull() {
            addCriterion("u_roleType is null");
            return (Criteria) this;
        }

        public Criteria andURoletypeIsNotNull() {
            addCriterion("u_roleType is not null");
            return (Criteria) this;
        }

        public Criteria andURoletypeEqualTo(Integer value) {
            addCriterion("u_roleType =", value, "uRoletype");
            return (Criteria) this;
        }

        public Criteria andURoletypeNotEqualTo(Integer value) {
            addCriterion("u_roleType <>", value, "uRoletype");
            return (Criteria) this;
        }

        public Criteria andURoletypeGreaterThan(Integer value) {
            addCriterion("u_roleType >", value, "uRoletype");
            return (Criteria) this;
        }

        public Criteria andURoletypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("u_roleType >=", value, "uRoletype");
            return (Criteria) this;
        }

        public Criteria andURoletypeLessThan(Integer value) {
            addCriterion("u_roleType <", value, "uRoletype");
            return (Criteria) this;
        }

        public Criteria andURoletypeLessThanOrEqualTo(Integer value) {
            addCriterion("u_roleType <=", value, "uRoletype");
            return (Criteria) this;
        }

        public Criteria andURoletypeIn(List<Integer> values) {
            addCriterion("u_roleType in", values, "uRoletype");
            return (Criteria) this;
        }

        public Criteria andURoletypeNotIn(List<Integer> values) {
            addCriterion("u_roleType not in", values, "uRoletype");
            return (Criteria) this;
        }

        public Criteria andURoletypeBetween(Integer value1, Integer value2) {
            addCriterion("u_roleType between", value1, value2, "uRoletype");
            return (Criteria) this;
        }

        public Criteria andURoletypeNotBetween(Integer value1, Integer value2) {
            addCriterion("u_roleType not between", value1, value2, "uRoletype");
            return (Criteria) this;
        }

        public Criteria andUJobnumIsNull() {
            addCriterion("u_jobNum is null");
            return (Criteria) this;
        }

        public Criteria andUJobnumIsNotNull() {
            addCriterion("u_jobNum is not null");
            return (Criteria) this;
        }

        public Criteria andUJobnumEqualTo(String value) {
            addCriterion("u_jobNum =", value, "uJobnum");
            return (Criteria) this;
        }

        public Criteria andUJobnumNotEqualTo(String value) {
            addCriterion("u_jobNum <>", value, "uJobnum");
            return (Criteria) this;
        }

        public Criteria andUJobnumGreaterThan(String value) {
            addCriterion("u_jobNum >", value, "uJobnum");
            return (Criteria) this;
        }

        public Criteria andUJobnumGreaterThanOrEqualTo(String value) {
            addCriterion("u_jobNum >=", value, "uJobnum");
            return (Criteria) this;
        }

        public Criteria andUJobnumLessThan(String value) {
            addCriterion("u_jobNum <", value, "uJobnum");
            return (Criteria) this;
        }

        public Criteria andUJobnumLessThanOrEqualTo(String value) {
            addCriterion("u_jobNum <=", value, "uJobnum");
            return (Criteria) this;
        }

        public Criteria andUJobnumLike(String value) {
            addCriterion("u_jobNum like", value, "uJobnum");
            return (Criteria) this;
        }

        public Criteria andUJobnumNotLike(String value) {
            addCriterion("u_jobNum not like", value, "uJobnum");
            return (Criteria) this;
        }

        public Criteria andUJobnumIn(List<String> values) {
            addCriterion("u_jobNum in", values, "uJobnum");
            return (Criteria) this;
        }

        public Criteria andUJobnumNotIn(List<String> values) {
            addCriterion("u_jobNum not in", values, "uJobnum");
            return (Criteria) this;
        }

        public Criteria andUJobnumBetween(String value1, String value2) {
            addCriterion("u_jobNum between", value1, value2, "uJobnum");
            return (Criteria) this;
        }

        public Criteria andUJobnumNotBetween(String value1, String value2) {
            addCriterion("u_jobNum not between", value1, value2, "uJobnum");
            return (Criteria) this;
        }

        public Criteria andUDepartidIsNull() {
            addCriterion("u_departId is null");
            return (Criteria) this;
        }

        public Criteria andUDepartidIsNotNull() {
            addCriterion("u_departId is not null");
            return (Criteria) this;
        }

        public Criteria andUDepartidEqualTo(Integer value) {
            addCriterion("u_departId =", value, "uDepartid");
            return (Criteria) this;
        }

        public Criteria andUDepartidNotEqualTo(Integer value) {
            addCriterion("u_departId <>", value, "uDepartid");
            return (Criteria) this;
        }

        public Criteria andUDepartidGreaterThan(Integer value) {
            addCriterion("u_departId >", value, "uDepartid");
            return (Criteria) this;
        }

        public Criteria andUDepartidGreaterThanOrEqualTo(Integer value) {
            addCriterion("u_departId >=", value, "uDepartid");
            return (Criteria) this;
        }

        public Criteria andUDepartidLessThan(Integer value) {
            addCriterion("u_departId <", value, "uDepartid");
            return (Criteria) this;
        }

        public Criteria andUDepartidLessThanOrEqualTo(Integer value) {
            addCriterion("u_departId <=", value, "uDepartid");
            return (Criteria) this;
        }

        public Criteria andUDepartidIn(List<Integer> values) {
            addCriterion("u_departId in", values, "uDepartid");
            return (Criteria) this;
        }

        public Criteria andUDepartidNotIn(List<Integer> values) {
            addCriterion("u_departId not in", values, "uDepartid");
            return (Criteria) this;
        }

        public Criteria andUDepartidBetween(Integer value1, Integer value2) {
            addCriterion("u_departId between", value1, value2, "uDepartid");
            return (Criteria) this;
        }

        public Criteria andUDepartidNotBetween(Integer value1, Integer value2) {
            addCriterion("u_departId not between", value1, value2, "uDepartid");
            return (Criteria) this;
        }

        public Criteria andUNameIsNull() {
            addCriterion("u_name is null");
            return (Criteria) this;
        }

        public Criteria andUNameIsNotNull() {
            addCriterion("u_name is not null");
            return (Criteria) this;
        }

        public Criteria andUNameEqualTo(String value) {
            addCriterion("u_name =", value, "uName");
            return (Criteria) this;
        }

        public Criteria andUNameNotEqualTo(String value) {
            addCriterion("u_name <>", value, "uName");
            return (Criteria) this;
        }

        public Criteria andUNameGreaterThan(String value) {
            addCriterion("u_name >", value, "uName");
            return (Criteria) this;
        }

        public Criteria andUNameGreaterThanOrEqualTo(String value) {
            addCriterion("u_name >=", value, "uName");
            return (Criteria) this;
        }

        public Criteria andUNameLessThan(String value) {
            addCriterion("u_name <", value, "uName");
            return (Criteria) this;
        }

        public Criteria andUNameLessThanOrEqualTo(String value) {
            addCriterion("u_name <=", value, "uName");
            return (Criteria) this;
        }

        public Criteria andUNameLike(String value) {
            addCriterion("u_name like", value, "uName");
            return (Criteria) this;
        }

        public Criteria andUNameNotLike(String value) {
            addCriterion("u_name not like", value, "uName");
            return (Criteria) this;
        }

        public Criteria andUNameIn(List<String> values) {
            addCriterion("u_name in", values, "uName");
            return (Criteria) this;
        }

        public Criteria andUNameNotIn(List<String> values) {
            addCriterion("u_name not in", values, "uName");
            return (Criteria) this;
        }

        public Criteria andUNameBetween(String value1, String value2) {
            addCriterion("u_name between", value1, value2, "uName");
            return (Criteria) this;
        }

        public Criteria andUNameNotBetween(String value1, String value2) {
            addCriterion("u_name not between", value1, value2, "uName");
            return (Criteria) this;
        }

        public Criteria andUUsernameIsNull() {
            addCriterion("u_userName is null");
            return (Criteria) this;
        }

        public Criteria andUUsernameIsNotNull() {
            addCriterion("u_userName is not null");
            return (Criteria) this;
        }

        public Criteria andUUsernameEqualTo(String value) {
            addCriterion("u_userName =", value, "uUsername");
            return (Criteria) this;
        }

        public Criteria andUUsernameNotEqualTo(String value) {
            addCriterion("u_userName <>", value, "uUsername");
            return (Criteria) this;
        }

        public Criteria andUUsernameGreaterThan(String value) {
            addCriterion("u_userName >", value, "uUsername");
            return (Criteria) this;
        }

        public Criteria andUUsernameGreaterThanOrEqualTo(String value) {
            addCriterion("u_userName >=", value, "uUsername");
            return (Criteria) this;
        }

        public Criteria andUUsernameLessThan(String value) {
            addCriterion("u_userName <", value, "uUsername");
            return (Criteria) this;
        }

        public Criteria andUUsernameLessThanOrEqualTo(String value) {
            addCriterion("u_userName <=", value, "uUsername");
            return (Criteria) this;
        }

        public Criteria andUUsernameLike(String value) {
            addCriterion("u_userName like", value, "uUsername");
            return (Criteria) this;
        }

        public Criteria andUUsernameNotLike(String value) {
            addCriterion("u_userName not like", value, "uUsername");
            return (Criteria) this;
        }

        public Criteria andUUsernameIn(List<String> values) {
            addCriterion("u_userName in", values, "uUsername");
            return (Criteria) this;
        }

        public Criteria andUUsernameNotIn(List<String> values) {
            addCriterion("u_userName not in", values, "uUsername");
            return (Criteria) this;
        }

        public Criteria andUUsernameBetween(String value1, String value2) {
            addCriterion("u_userName between", value1, value2, "uUsername");
            return (Criteria) this;
        }

        public Criteria andUUsernameNotBetween(String value1, String value2) {
            addCriterion("u_userName not between", value1, value2, "uUsername");
            return (Criteria) this;
        }

        public Criteria andUPasswordIsNull() {
            addCriterion("u_password is null");
            return (Criteria) this;
        }

        public Criteria andUPasswordIsNotNull() {
            addCriterion("u_password is not null");
            return (Criteria) this;
        }

        public Criteria andUPasswordEqualTo(String value) {
            addCriterion("u_password =", value, "uPassword");
            return (Criteria) this;
        }

        public Criteria andUPasswordNotEqualTo(String value) {
            addCriterion("u_password <>", value, "uPassword");
            return (Criteria) this;
        }

        public Criteria andUPasswordGreaterThan(String value) {
            addCriterion("u_password >", value, "uPassword");
            return (Criteria) this;
        }

        public Criteria andUPasswordGreaterThanOrEqualTo(String value) {
            addCriterion("u_password >=", value, "uPassword");
            return (Criteria) this;
        }

        public Criteria andUPasswordLessThan(String value) {
            addCriterion("u_password <", value, "uPassword");
            return (Criteria) this;
        }

        public Criteria andUPasswordLessThanOrEqualTo(String value) {
            addCriterion("u_password <=", value, "uPassword");
            return (Criteria) this;
        }

        public Criteria andUPasswordLike(String value) {
            addCriterion("u_password like", value, "uPassword");
            return (Criteria) this;
        }

        public Criteria andUPasswordNotLike(String value) {
            addCriterion("u_password not like", value, "uPassword");
            return (Criteria) this;
        }

        public Criteria andUPasswordIn(List<String> values) {
            addCriterion("u_password in", values, "uPassword");
            return (Criteria) this;
        }

        public Criteria andUPasswordNotIn(List<String> values) {
            addCriterion("u_password not in", values, "uPassword");
            return (Criteria) this;
        }

        public Criteria andUPasswordBetween(String value1, String value2) {
            addCriterion("u_password between", value1, value2, "uPassword");
            return (Criteria) this;
        }

        public Criteria andUPasswordNotBetween(String value1, String value2) {
            addCriterion("u_password not between", value1, value2, "uPassword");
            return (Criteria) this;
        }

        public Criteria andUCreateuserIsNull() {
            addCriterion("u_createUser is null");
            return (Criteria) this;
        }

        public Criteria andUCreateuserIsNotNull() {
            addCriterion("u_createUser is not null");
            return (Criteria) this;
        }

        public Criteria andUCreateuserEqualTo(String value) {
            addCriterion("u_createUser =", value, "uCreateuser");
            return (Criteria) this;
        }

        public Criteria andUCreateuserNotEqualTo(String value) {
            addCriterion("u_createUser <>", value, "uCreateuser");
            return (Criteria) this;
        }

        public Criteria andUCreateuserGreaterThan(String value) {
            addCriterion("u_createUser >", value, "uCreateuser");
            return (Criteria) this;
        }

        public Criteria andUCreateuserGreaterThanOrEqualTo(String value) {
            addCriterion("u_createUser >=", value, "uCreateuser");
            return (Criteria) this;
        }

        public Criteria andUCreateuserLessThan(String value) {
            addCriterion("u_createUser <", value, "uCreateuser");
            return (Criteria) this;
        }

        public Criteria andUCreateuserLessThanOrEqualTo(String value) {
            addCriterion("u_createUser <=", value, "uCreateuser");
            return (Criteria) this;
        }

        public Criteria andUCreateuserLike(String value) {
            addCriterion("u_createUser like", value, "uCreateuser");
            return (Criteria) this;
        }

        public Criteria andUCreateuserNotLike(String value) {
            addCriterion("u_createUser not like", value, "uCreateuser");
            return (Criteria) this;
        }

        public Criteria andUCreateuserIn(List<String> values) {
            addCriterion("u_createUser in", values, "uCreateuser");
            return (Criteria) this;
        }

        public Criteria andUCreateuserNotIn(List<String> values) {
            addCriterion("u_createUser not in", values, "uCreateuser");
            return (Criteria) this;
        }

        public Criteria andUCreateuserBetween(String value1, String value2) {
            addCriterion("u_createUser between", value1, value2, "uCreateuser");
            return (Criteria) this;
        }

        public Criteria andUCreateuserNotBetween(String value1, String value2) {
            addCriterion("u_createUser not between", value1, value2, "uCreateuser");
            return (Criteria) this;
        }

        public Criteria andUCreatedateIsNull() {
            addCriterion("u_createDate is null");
            return (Criteria) this;
        }

        public Criteria andUCreatedateIsNotNull() {
            addCriterion("u_createDate is not null");
            return (Criteria) this;
        }

        public Criteria andUCreatedateEqualTo(Date value) {
            addCriterion("u_createDate =", value, "uCreatedate");
            return (Criteria) this;
        }

        public Criteria andUCreatedateNotEqualTo(Date value) {
            addCriterion("u_createDate <>", value, "uCreatedate");
            return (Criteria) this;
        }

        public Criteria andUCreatedateGreaterThan(Date value) {
            addCriterion("u_createDate >", value, "uCreatedate");
            return (Criteria) this;
        }

        public Criteria andUCreatedateGreaterThanOrEqualTo(Date value) {
            addCriterion("u_createDate >=", value, "uCreatedate");
            return (Criteria) this;
        }

        public Criteria andUCreatedateLessThan(Date value) {
            addCriterion("u_createDate <", value, "uCreatedate");
            return (Criteria) this;
        }

        public Criteria andUCreatedateLessThanOrEqualTo(Date value) {
            addCriterion("u_createDate <=", value, "uCreatedate");
            return (Criteria) this;
        }

        public Criteria andUCreatedateIn(List<Date> values) {
            addCriterion("u_createDate in", values, "uCreatedate");
            return (Criteria) this;
        }

        public Criteria andUCreatedateNotIn(List<Date> values) {
            addCriterion("u_createDate not in", values, "uCreatedate");
            return (Criteria) this;
        }

        public Criteria andUCreatedateBetween(Date value1, Date value2) {
            addCriterion("u_createDate between", value1, value2, "uCreatedate");
            return (Criteria) this;
        }

        public Criteria andUCreatedateNotBetween(Date value1, Date value2) {
            addCriterion("u_createDate not between", value1, value2, "uCreatedate");
            return (Criteria) this;
        }

        public Criteria andULastmoddateIsNull() {
            addCriterion("u_lastModDate is null");
            return (Criteria) this;
        }

        public Criteria andULastmoddateIsNotNull() {
            addCriterion("u_lastModDate is not null");
            return (Criteria) this;
        }

        public Criteria andULastmoddateEqualTo(Date value) {
            addCriterion("u_lastModDate =", value, "uLastmoddate");
            return (Criteria) this;
        }

        public Criteria andULastmoddateNotEqualTo(Date value) {
            addCriterion("u_lastModDate <>", value, "uLastmoddate");
            return (Criteria) this;
        }

        public Criteria andULastmoddateGreaterThan(Date value) {
            addCriterion("u_lastModDate >", value, "uLastmoddate");
            return (Criteria) this;
        }

        public Criteria andULastmoddateGreaterThanOrEqualTo(Date value) {
            addCriterion("u_lastModDate >=", value, "uLastmoddate");
            return (Criteria) this;
        }

        public Criteria andULastmoddateLessThan(Date value) {
            addCriterion("u_lastModDate <", value, "uLastmoddate");
            return (Criteria) this;
        }

        public Criteria andULastmoddateLessThanOrEqualTo(Date value) {
            addCriterion("u_lastModDate <=", value, "uLastmoddate");
            return (Criteria) this;
        }

        public Criteria andULastmoddateIn(List<Date> values) {
            addCriterion("u_lastModDate in", values, "uLastmoddate");
            return (Criteria) this;
        }

        public Criteria andULastmoddateNotIn(List<Date> values) {
            addCriterion("u_lastModDate not in", values, "uLastmoddate");
            return (Criteria) this;
        }

        public Criteria andULastmoddateBetween(Date value1, Date value2) {
            addCriterion("u_lastModDate between", value1, value2, "uLastmoddate");
            return (Criteria) this;
        }

        public Criteria andULastmoddateNotBetween(Date value1, Date value2) {
            addCriterion("u_lastModDate not between", value1, value2, "uLastmoddate");
            return (Criteria) this;
        }

        public Criteria andULastmoduserIsNull() {
            addCriterion("u_lastModUser is null");
            return (Criteria) this;
        }

        public Criteria andULastmoduserIsNotNull() {
            addCriterion("u_lastModUser is not null");
            return (Criteria) this;
        }

        public Criteria andULastmoduserEqualTo(String value) {
            addCriterion("u_lastModUser =", value, "uLastmoduser");
            return (Criteria) this;
        }

        public Criteria andULastmoduserNotEqualTo(String value) {
            addCriterion("u_lastModUser <>", value, "uLastmoduser");
            return (Criteria) this;
        }

        public Criteria andULastmoduserGreaterThan(String value) {
            addCriterion("u_lastModUser >", value, "uLastmoduser");
            return (Criteria) this;
        }

        public Criteria andULastmoduserGreaterThanOrEqualTo(String value) {
            addCriterion("u_lastModUser >=", value, "uLastmoduser");
            return (Criteria) this;
        }

        public Criteria andULastmoduserLessThan(String value) {
            addCriterion("u_lastModUser <", value, "uLastmoduser");
            return (Criteria) this;
        }

        public Criteria andULastmoduserLessThanOrEqualTo(String value) {
            addCriterion("u_lastModUser <=", value, "uLastmoduser");
            return (Criteria) this;
        }

        public Criteria andULastmoduserLike(String value) {
            addCriterion("u_lastModUser like", value, "uLastmoduser");
            return (Criteria) this;
        }

        public Criteria andULastmoduserNotLike(String value) {
            addCriterion("u_lastModUser not like", value, "uLastmoduser");
            return (Criteria) this;
        }

        public Criteria andULastmoduserIn(List<String> values) {
            addCriterion("u_lastModUser in", values, "uLastmoduser");
            return (Criteria) this;
        }

        public Criteria andULastmoduserNotIn(List<String> values) {
            addCriterion("u_lastModUser not in", values, "uLastmoduser");
            return (Criteria) this;
        }

        public Criteria andULastmoduserBetween(String value1, String value2) {
            addCriterion("u_lastModUser between", value1, value2, "uLastmoduser");
            return (Criteria) this;
        }

        public Criteria andULastmoduserNotBetween(String value1, String value2) {
            addCriterion("u_lastModUser not between", value1, value2, "uLastmoduser");
            return (Criteria) this;
        }

        public Criteria andULastlogindateIsNull() {
            addCriterion("u_lastLoginDate is null");
            return (Criteria) this;
        }

        public Criteria andULastlogindateIsNotNull() {
            addCriterion("u_lastLoginDate is not null");
            return (Criteria) this;
        }

        public Criteria andULastlogindateEqualTo(Date value) {
            addCriterion("u_lastLoginDate =", value, "uLastlogindate");
            return (Criteria) this;
        }

        public Criteria andULastlogindateNotEqualTo(Date value) {
            addCriterion("u_lastLoginDate <>", value, "uLastlogindate");
            return (Criteria) this;
        }

        public Criteria andULastlogindateGreaterThan(Date value) {
            addCriterion("u_lastLoginDate >", value, "uLastlogindate");
            return (Criteria) this;
        }

        public Criteria andULastlogindateGreaterThanOrEqualTo(Date value) {
            addCriterion("u_lastLoginDate >=", value, "uLastlogindate");
            return (Criteria) this;
        }

        public Criteria andULastlogindateLessThan(Date value) {
            addCriterion("u_lastLoginDate <", value, "uLastlogindate");
            return (Criteria) this;
        }

        public Criteria andULastlogindateLessThanOrEqualTo(Date value) {
            addCriterion("u_lastLoginDate <=", value, "uLastlogindate");
            return (Criteria) this;
        }

        public Criteria andULastlogindateIn(List<Date> values) {
            addCriterion("u_lastLoginDate in", values, "uLastlogindate");
            return (Criteria) this;
        }

        public Criteria andULastlogindateNotIn(List<Date> values) {
            addCriterion("u_lastLoginDate not in", values, "uLastlogindate");
            return (Criteria) this;
        }

        public Criteria andULastlogindateBetween(Date value1, Date value2) {
            addCriterion("u_lastLoginDate between", value1, value2, "uLastlogindate");
            return (Criteria) this;
        }

        public Criteria andULastlogindateNotBetween(Date value1, Date value2) {
            addCriterion("u_lastLoginDate not between", value1, value2, "uLastlogindate");
            return (Criteria) this;
        }

        public Criteria andUDeletestateIsNull() {
            addCriterion("u_deleteState is null");
            return (Criteria) this;
        }

        public Criteria andUDeletestateIsNotNull() {
            addCriterion("u_deleteState is not null");
            return (Criteria) this;
        }

        public Criteria andUDeletestateEqualTo(Integer value) {
            addCriterion("u_deleteState =", value, "uDeletestate");
            return (Criteria) this;
        }

        public Criteria andUDeletestateNotEqualTo(Integer value) {
            addCriterion("u_deleteState <>", value, "uDeletestate");
            return (Criteria) this;
        }

        public Criteria andUDeletestateGreaterThan(Integer value) {
            addCriterion("u_deleteState >", value, "uDeletestate");
            return (Criteria) this;
        }

        public Criteria andUDeletestateGreaterThanOrEqualTo(Integer value) {
            addCriterion("u_deleteState >=", value, "uDeletestate");
            return (Criteria) this;
        }

        public Criteria andUDeletestateLessThan(Integer value) {
            addCriterion("u_deleteState <", value, "uDeletestate");
            return (Criteria) this;
        }

        public Criteria andUDeletestateLessThanOrEqualTo(Integer value) {
            addCriterion("u_deleteState <=", value, "uDeletestate");
            return (Criteria) this;
        }

        public Criteria andUDeletestateIn(List<Integer> values) {
            addCriterion("u_deleteState in", values, "uDeletestate");
            return (Criteria) this;
        }

        public Criteria andUDeletestateNotIn(List<Integer> values) {
            addCriterion("u_deleteState not in", values, "uDeletestate");
            return (Criteria) this;
        }

        public Criteria andUDeletestateBetween(Integer value1, Integer value2) {
            addCriterion("u_deleteState between", value1, value2, "uDeletestate");
            return (Criteria) this;
        }

        public Criteria andUDeletestateNotBetween(Integer value1, Integer value2) {
            addCriterion("u_deleteState not between", value1, value2, "uDeletestate");
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