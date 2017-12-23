package com.smi.am.dao.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AmSyslogExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public AmSyslogExample() {
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

        public Criteria andSlIdIsNull() {
            addCriterion("sl_id is null");
            return (Criteria) this;
        }

        public Criteria andSlIdIsNotNull() {
            addCriterion("sl_id is not null");
            return (Criteria) this;
        }

        public Criteria andSlIdEqualTo(Integer value) {
            addCriterion("sl_id =", value, "slId");
            return (Criteria) this;
        }

        public Criteria andSlIdNotEqualTo(Integer value) {
            addCriterion("sl_id <>", value, "slId");
            return (Criteria) this;
        }

        public Criteria andSlIdGreaterThan(Integer value) {
            addCriterion("sl_id >", value, "slId");
            return (Criteria) this;
        }

        public Criteria andSlIdGreaterThanOrEqualTo(Integer value) {
            addCriterion("sl_id >=", value, "slId");
            return (Criteria) this;
        }

        public Criteria andSlIdLessThan(Integer value) {
            addCriterion("sl_id <", value, "slId");
            return (Criteria) this;
        }

        public Criteria andSlIdLessThanOrEqualTo(Integer value) {
            addCriterion("sl_id <=", value, "slId");
            return (Criteria) this;
        }

        public Criteria andSlIdIn(List<Integer> values) {
            addCriterion("sl_id in", values, "slId");
            return (Criteria) this;
        }

        public Criteria andSlIdNotIn(List<Integer> values) {
            addCriterion("sl_id not in", values, "slId");
            return (Criteria) this;
        }

        public Criteria andSlIdBetween(Integer value1, Integer value2) {
            addCriterion("sl_id between", value1, value2, "slId");
            return (Criteria) this;
        }

        public Criteria andSlIdNotBetween(Integer value1, Integer value2) {
            addCriterion("sl_id not between", value1, value2, "slId");
            return (Criteria) this;
        }

        public Criteria andSlUsernameIsNull() {
            addCriterion("sl_userName is null");
            return (Criteria) this;
        }

        public Criteria andSlUsernameIsNotNull() {
            addCriterion("sl_userName is not null");
            return (Criteria) this;
        }

        public Criteria andSlUsernameEqualTo(String value) {
            addCriterion("sl_userName =", value, "slUsername");
            return (Criteria) this;
        }

        public Criteria andSlUsernameNotEqualTo(String value) {
            addCriterion("sl_userName <>", value, "slUsername");
            return (Criteria) this;
        }

        public Criteria andSlUsernameGreaterThan(String value) {
            addCriterion("sl_userName >", value, "slUsername");
            return (Criteria) this;
        }

        public Criteria andSlUsernameGreaterThanOrEqualTo(String value) {
            addCriterion("sl_userName >=", value, "slUsername");
            return (Criteria) this;
        }

        public Criteria andSlUsernameLessThan(String value) {
            addCriterion("sl_userName <", value, "slUsername");
            return (Criteria) this;
        }

        public Criteria andSlUsernameLessThanOrEqualTo(String value) {
            addCriterion("sl_userName <=", value, "slUsername");
            return (Criteria) this;
        }

        public Criteria andSlUsernameLike(String value) {
            addCriterion("sl_userName like", value, "slUsername");
            return (Criteria) this;
        }

        public Criteria andSlUsernameNotLike(String value) {
            addCriterion("sl_userName not like", value, "slUsername");
            return (Criteria) this;
        }

        public Criteria andSlUsernameIn(List<String> values) {
            addCriterion("sl_userName in", values, "slUsername");
            return (Criteria) this;
        }

        public Criteria andSlUsernameNotIn(List<String> values) {
            addCriterion("sl_userName not in", values, "slUsername");
            return (Criteria) this;
        }

        public Criteria andSlUsernameBetween(String value1, String value2) {
            addCriterion("sl_userName between", value1, value2, "slUsername");
            return (Criteria) this;
        }

        public Criteria andSlUsernameNotBetween(String value1, String value2) {
            addCriterion("sl_userName not between", value1, value2, "slUsername");
            return (Criteria) this;
        }

        public Criteria andSlNameIsNull() {
            addCriterion("sl_name is null");
            return (Criteria) this;
        }

        public Criteria andSlNameIsNotNull() {
            addCriterion("sl_name is not null");
            return (Criteria) this;
        }

        public Criteria andSlNameEqualTo(String value) {
            addCriterion("sl_name =", value, "slName");
            return (Criteria) this;
        }

        public Criteria andSlNameNotEqualTo(String value) {
            addCriterion("sl_name <>", value, "slName");
            return (Criteria) this;
        }

        public Criteria andSlNameGreaterThan(String value) {
            addCriterion("sl_name >", value, "slName");
            return (Criteria) this;
        }

        public Criteria andSlNameGreaterThanOrEqualTo(String value) {
            addCriterion("sl_name >=", value, "slName");
            return (Criteria) this;
        }

        public Criteria andSlNameLessThan(String value) {
            addCriterion("sl_name <", value, "slName");
            return (Criteria) this;
        }

        public Criteria andSlNameLessThanOrEqualTo(String value) {
            addCriterion("sl_name <=", value, "slName");
            return (Criteria) this;
        }

        public Criteria andSlNameLike(String value) {
            addCriterion("sl_name like", value, "slName");
            return (Criteria) this;
        }

        public Criteria andSlNameNotLike(String value) {
            addCriterion("sl_name not like", value, "slName");
            return (Criteria) this;
        }

        public Criteria andSlNameIn(List<String> values) {
            addCriterion("sl_name in", values, "slName");
            return (Criteria) this;
        }

        public Criteria andSlNameNotIn(List<String> values) {
            addCriterion("sl_name not in", values, "slName");
            return (Criteria) this;
        }

        public Criteria andSlNameBetween(String value1, String value2) {
            addCriterion("sl_name between", value1, value2, "slName");
            return (Criteria) this;
        }

        public Criteria andSlNameNotBetween(String value1, String value2) {
            addCriterion("sl_name not between", value1, value2, "slName");
            return (Criteria) this;
        }

        public Criteria andSlRolenameIsNull() {
            addCriterion("sl_roleName is null");
            return (Criteria) this;
        }

        public Criteria andSlRolenameIsNotNull() {
            addCriterion("sl_roleName is not null");
            return (Criteria) this;
        }

        public Criteria andSlRolenameEqualTo(String value) {
            addCriterion("sl_roleName =", value, "slRolename");
            return (Criteria) this;
        }

        public Criteria andSlRolenameNotEqualTo(String value) {
            addCriterion("sl_roleName <>", value, "slRolename");
            return (Criteria) this;
        }

        public Criteria andSlRolenameGreaterThan(String value) {
            addCriterion("sl_roleName >", value, "slRolename");
            return (Criteria) this;
        }

        public Criteria andSlRolenameGreaterThanOrEqualTo(String value) {
            addCriterion("sl_roleName >=", value, "slRolename");
            return (Criteria) this;
        }

        public Criteria andSlRolenameLessThan(String value) {
            addCriterion("sl_roleName <", value, "slRolename");
            return (Criteria) this;
        }

        public Criteria andSlRolenameLessThanOrEqualTo(String value) {
            addCriterion("sl_roleName <=", value, "slRolename");
            return (Criteria) this;
        }

        public Criteria andSlRolenameLike(String value) {
            addCriterion("sl_roleName like", value, "slRolename");
            return (Criteria) this;
        }

        public Criteria andSlRolenameNotLike(String value) {
            addCriterion("sl_roleName not like", value, "slRolename");
            return (Criteria) this;
        }

        public Criteria andSlRolenameIn(List<String> values) {
            addCriterion("sl_roleName in", values, "slRolename");
            return (Criteria) this;
        }

        public Criteria andSlRolenameNotIn(List<String> values) {
            addCriterion("sl_roleName not in", values, "slRolename");
            return (Criteria) this;
        }

        public Criteria andSlRolenameBetween(String value1, String value2) {
            addCriterion("sl_roleName between", value1, value2, "slRolename");
            return (Criteria) this;
        }

        public Criteria andSlRolenameNotBetween(String value1, String value2) {
            addCriterion("sl_roleName not between", value1, value2, "slRolename");
            return (Criteria) this;
        }

        public Criteria andSlAreaidIsNull() {
            addCriterion("sl_areaId is null");
            return (Criteria) this;
        }

        public Criteria andSlAreaidIsNotNull() {
            addCriterion("sl_areaId is not null");
            return (Criteria) this;
        }

        public Criteria andSlAreaidEqualTo(Integer value) {
            addCriterion("sl_areaId =", value, "slAreaid");
            return (Criteria) this;
        }

        public Criteria andSlAreaidNotEqualTo(Integer value) {
            addCriterion("sl_areaId <>", value, "slAreaid");
            return (Criteria) this;
        }

        public Criteria andSlAreaidGreaterThan(Integer value) {
            addCriterion("sl_areaId >", value, "slAreaid");
            return (Criteria) this;
        }

        public Criteria andSlAreaidGreaterThanOrEqualTo(Integer value) {
            addCriterion("sl_areaId >=", value, "slAreaid");
            return (Criteria) this;
        }

        public Criteria andSlAreaidLessThan(Integer value) {
            addCriterion("sl_areaId <", value, "slAreaid");
            return (Criteria) this;
        }

        public Criteria andSlAreaidLessThanOrEqualTo(Integer value) {
            addCriterion("sl_areaId <=", value, "slAreaid");
            return (Criteria) this;
        }

        public Criteria andSlAreaidIn(List<Integer> values) {
            addCriterion("sl_areaId in", values, "slAreaid");
            return (Criteria) this;
        }

        public Criteria andSlAreaidNotIn(List<Integer> values) {
            addCriterion("sl_areaId not in", values, "slAreaid");
            return (Criteria) this;
        }

        public Criteria andSlAreaidBetween(Integer value1, Integer value2) {
            addCriterion("sl_areaId between", value1, value2, "slAreaid");
            return (Criteria) this;
        }

        public Criteria andSlAreaidNotBetween(Integer value1, Integer value2) {
            addCriterion("sl_areaId not between", value1, value2, "slAreaid");
            return (Criteria) this;
        }

        public Criteria andSlRoletypeIsNull() {
            addCriterion("sl_roleType is null");
            return (Criteria) this;
        }

        public Criteria andSlRoletypeIsNotNull() {
            addCriterion("sl_roleType is not null");
            return (Criteria) this;
        }

        public Criteria andSlRoletypeEqualTo(Integer value) {
            addCriterion("sl_roleType =", value, "slRoletype");
            return (Criteria) this;
        }

        public Criteria andSlRoletypeNotEqualTo(Integer value) {
            addCriterion("sl_roleType <>", value, "slRoletype");
            return (Criteria) this;
        }

        public Criteria andSlRoletypeGreaterThan(Integer value) {
            addCriterion("sl_roleType >", value, "slRoletype");
            return (Criteria) this;
        }

        public Criteria andSlRoletypeGreaterThanOrEqualTo(Integer value) {
            addCriterion("sl_roleType >=", value, "slRoletype");
            return (Criteria) this;
        }

        public Criteria andSlRoletypeLessThan(Integer value) {
            addCriterion("sl_roleType <", value, "slRoletype");
            return (Criteria) this;
        }

        public Criteria andSlRoletypeLessThanOrEqualTo(Integer value) {
            addCriterion("sl_roleType <=", value, "slRoletype");
            return (Criteria) this;
        }

        public Criteria andSlRoletypeIn(List<Integer> values) {
            addCriterion("sl_roleType in", values, "slRoletype");
            return (Criteria) this;
        }

        public Criteria andSlRoletypeNotIn(List<Integer> values) {
            addCriterion("sl_roleType not in", values, "slRoletype");
            return (Criteria) this;
        }

        public Criteria andSlRoletypeBetween(Integer value1, Integer value2) {
            addCriterion("sl_roleType between", value1, value2, "slRoletype");
            return (Criteria) this;
        }

        public Criteria andSlRoletypeNotBetween(Integer value1, Integer value2) {
            addCriterion("sl_roleType not between", value1, value2, "slRoletype");
            return (Criteria) this;
        }

        public Criteria andSlLoginipIsNull() {
            addCriterion("sl_loginIp is null");
            return (Criteria) this;
        }

        public Criteria andSlLoginipIsNotNull() {
            addCriterion("sl_loginIp is not null");
            return (Criteria) this;
        }

        public Criteria andSlLoginipEqualTo(String value) {
            addCriterion("sl_loginIp =", value, "slLoginip");
            return (Criteria) this;
        }

        public Criteria andSlLoginipNotEqualTo(String value) {
            addCriterion("sl_loginIp <>", value, "slLoginip");
            return (Criteria) this;
        }

        public Criteria andSlLoginipGreaterThan(String value) {
            addCriterion("sl_loginIp >", value, "slLoginip");
            return (Criteria) this;
        }

        public Criteria andSlLoginipGreaterThanOrEqualTo(String value) {
            addCriterion("sl_loginIp >=", value, "slLoginip");
            return (Criteria) this;
        }

        public Criteria andSlLoginipLessThan(String value) {
            addCriterion("sl_loginIp <", value, "slLoginip");
            return (Criteria) this;
        }

        public Criteria andSlLoginipLessThanOrEqualTo(String value) {
            addCriterion("sl_loginIp <=", value, "slLoginip");
            return (Criteria) this;
        }

        public Criteria andSlLoginipLike(String value) {
            addCriterion("sl_loginIp like", value, "slLoginip");
            return (Criteria) this;
        }

        public Criteria andSlLoginipNotLike(String value) {
            addCriterion("sl_loginIp not like", value, "slLoginip");
            return (Criteria) this;
        }

        public Criteria andSlLoginipIn(List<String> values) {
            addCriterion("sl_loginIp in", values, "slLoginip");
            return (Criteria) this;
        }

        public Criteria andSlLoginipNotIn(List<String> values) {
            addCriterion("sl_loginIp not in", values, "slLoginip");
            return (Criteria) this;
        }

        public Criteria andSlLoginipBetween(String value1, String value2) {
            addCriterion("sl_loginIp between", value1, value2, "slLoginip");
            return (Criteria) this;
        }

        public Criteria andSlLoginipNotBetween(String value1, String value2) {
            addCriterion("sl_loginIp not between", value1, value2, "slLoginip");
            return (Criteria) this;
        }

        public Criteria andSlLogtitleIsNull() {
            addCriterion("sl_logTitle is null");
            return (Criteria) this;
        }

        public Criteria andSlLogtitleIsNotNull() {
            addCriterion("sl_logTitle is not null");
            return (Criteria) this;
        }

        public Criteria andSlLogtitleEqualTo(String value) {
            addCriterion("sl_logTitle =", value, "slLogtitle");
            return (Criteria) this;
        }

        public Criteria andSlLogtitleNotEqualTo(String value) {
            addCriterion("sl_logTitle <>", value, "slLogtitle");
            return (Criteria) this;
        }

        public Criteria andSlLogtitleGreaterThan(String value) {
            addCriterion("sl_logTitle >", value, "slLogtitle");
            return (Criteria) this;
        }

        public Criteria andSlLogtitleGreaterThanOrEqualTo(String value) {
            addCriterion("sl_logTitle >=", value, "slLogtitle");
            return (Criteria) this;
        }

        public Criteria andSlLogtitleLessThan(String value) {
            addCriterion("sl_logTitle <", value, "slLogtitle");
            return (Criteria) this;
        }

        public Criteria andSlLogtitleLessThanOrEqualTo(String value) {
            addCriterion("sl_logTitle <=", value, "slLogtitle");
            return (Criteria) this;
        }

        public Criteria andSlLogtitleLike(String value) {
            addCriterion("sl_logTitle like", value, "slLogtitle");
            return (Criteria) this;
        }

        public Criteria andSlLogtitleNotLike(String value) {
            addCriterion("sl_logTitle not like", value, "slLogtitle");
            return (Criteria) this;
        }

        public Criteria andSlLogtitleIn(List<String> values) {
            addCriterion("sl_logTitle in", values, "slLogtitle");
            return (Criteria) this;
        }

        public Criteria andSlLogtitleNotIn(List<String> values) {
            addCriterion("sl_logTitle not in", values, "slLogtitle");
            return (Criteria) this;
        }

        public Criteria andSlLogtitleBetween(String value1, String value2) {
            addCriterion("sl_logTitle between", value1, value2, "slLogtitle");
            return (Criteria) this;
        }

        public Criteria andSlLogtitleNotBetween(String value1, String value2) {
            addCriterion("sl_logTitle not between", value1, value2, "slLogtitle");
            return (Criteria) this;
        }

        public Criteria andSlLogdetailIsNull() {
            addCriterion("sl_logDetail is null");
            return (Criteria) this;
        }

        public Criteria andSlLogdetailIsNotNull() {
            addCriterion("sl_logDetail is not null");
            return (Criteria) this;
        }

        public Criteria andSlLogdetailEqualTo(String value) {
            addCriterion("sl_logDetail =", value, "slLogdetail");
            return (Criteria) this;
        }

        public Criteria andSlLogdetailNotEqualTo(String value) {
            addCriterion("sl_logDetail <>", value, "slLogdetail");
            return (Criteria) this;
        }

        public Criteria andSlLogdetailGreaterThan(String value) {
            addCriterion("sl_logDetail >", value, "slLogdetail");
            return (Criteria) this;
        }

        public Criteria andSlLogdetailGreaterThanOrEqualTo(String value) {
            addCriterion("sl_logDetail >=", value, "slLogdetail");
            return (Criteria) this;
        }

        public Criteria andSlLogdetailLessThan(String value) {
            addCriterion("sl_logDetail <", value, "slLogdetail");
            return (Criteria) this;
        }

        public Criteria andSlLogdetailLessThanOrEqualTo(String value) {
            addCriterion("sl_logDetail <=", value, "slLogdetail");
            return (Criteria) this;
        }

        public Criteria andSlLogdetailLike(String value) {
            addCriterion("sl_logDetail like", value, "slLogdetail");
            return (Criteria) this;
        }

        public Criteria andSlLogdetailNotLike(String value) {
            addCriterion("sl_logDetail not like", value, "slLogdetail");
            return (Criteria) this;
        }

        public Criteria andSlLogdetailIn(List<String> values) {
            addCriterion("sl_logDetail in", values, "slLogdetail");
            return (Criteria) this;
        }

        public Criteria andSlLogdetailNotIn(List<String> values) {
            addCriterion("sl_logDetail not in", values, "slLogdetail");
            return (Criteria) this;
        }

        public Criteria andSlLogdetailBetween(String value1, String value2) {
            addCriterion("sl_logDetail between", value1, value2, "slLogdetail");
            return (Criteria) this;
        }

        public Criteria andSlLogdetailNotBetween(String value1, String value2) {
            addCriterion("sl_logDetail not between", value1, value2, "slLogdetail");
            return (Criteria) this;
        }

        public Criteria andSlOperatedateIsNull() {
            addCriterion("sl_operateDate is null");
            return (Criteria) this;
        }

        public Criteria andSlOperatedateIsNotNull() {
            addCriterion("sl_operateDate is not null");
            return (Criteria) this;
        }

        public Criteria andSlOperatedateEqualTo(Date value) {
            addCriterion("sl_operateDate =", value, "slOperatedate");
            return (Criteria) this;
        }

        public Criteria andSlOperatedateNotEqualTo(Date value) {
            addCriterion("sl_operateDate <>", value, "slOperatedate");
            return (Criteria) this;
        }

        public Criteria andSlOperatedateGreaterThan(Date value) {
            addCriterion("sl_operateDate >", value, "slOperatedate");
            return (Criteria) this;
        }

        public Criteria andSlOperatedateGreaterThanOrEqualTo(Date value) {
            addCriterion("sl_operateDate >=", value, "slOperatedate");
            return (Criteria) this;
        }

        public Criteria andSlOperatedateLessThan(Date value) {
            addCriterion("sl_operateDate <", value, "slOperatedate");
            return (Criteria) this;
        }

        public Criteria andSlOperatedateLessThanOrEqualTo(Date value) {
            addCriterion("sl_operateDate <=", value, "slOperatedate");
            return (Criteria) this;
        }

        public Criteria andSlOperatedateIn(List<Date> values) {
            addCriterion("sl_operateDate in", values, "slOperatedate");
            return (Criteria) this;
        }

        public Criteria andSlOperatedateNotIn(List<Date> values) {
            addCriterion("sl_operateDate not in", values, "slOperatedate");
            return (Criteria) this;
        }

        public Criteria andSlOperatedateBetween(Date value1, Date value2) {
            addCriterion("sl_operateDate between", value1, value2, "slOperatedate");
            return (Criteria) this;
        }

        public Criteria andSlOperatedateNotBetween(Date value1, Date value2) {
            addCriterion("sl_operateDate not between", value1, value2, "slOperatedate");
            return (Criteria) this;
        }

        public Criteria andSlRemarkIsNull() {
            addCriterion("sl_remark is null");
            return (Criteria) this;
        }

        public Criteria andSlRemarkIsNotNull() {
            addCriterion("sl_remark is not null");
            return (Criteria) this;
        }

        public Criteria andSlRemarkEqualTo(String value) {
            addCriterion("sl_remark =", value, "slRemark");
            return (Criteria) this;
        }

        public Criteria andSlRemarkNotEqualTo(String value) {
            addCriterion("sl_remark <>", value, "slRemark");
            return (Criteria) this;
        }

        public Criteria andSlRemarkGreaterThan(String value) {
            addCriterion("sl_remark >", value, "slRemark");
            return (Criteria) this;
        }

        public Criteria andSlRemarkGreaterThanOrEqualTo(String value) {
            addCriterion("sl_remark >=", value, "slRemark");
            return (Criteria) this;
        }

        public Criteria andSlRemarkLessThan(String value) {
            addCriterion("sl_remark <", value, "slRemark");
            return (Criteria) this;
        }

        public Criteria andSlRemarkLessThanOrEqualTo(String value) {
            addCriterion("sl_remark <=", value, "slRemark");
            return (Criteria) this;
        }

        public Criteria andSlRemarkLike(String value) {
            addCriterion("sl_remark like", value, "slRemark");
            return (Criteria) this;
        }

        public Criteria andSlRemarkNotLike(String value) {
            addCriterion("sl_remark not like", value, "slRemark");
            return (Criteria) this;
        }

        public Criteria andSlRemarkIn(List<String> values) {
            addCriterion("sl_remark in", values, "slRemark");
            return (Criteria) this;
        }

        public Criteria andSlRemarkNotIn(List<String> values) {
            addCriterion("sl_remark not in", values, "slRemark");
            return (Criteria) this;
        }

        public Criteria andSlRemarkBetween(String value1, String value2) {
            addCriterion("sl_remark between", value1, value2, "slRemark");
            return (Criteria) this;
        }

        public Criteria andSlRemarkNotBetween(String value1, String value2) {
            addCriterion("sl_remark not between", value1, value2, "slRemark");
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