package com.smi.mc.model.cust;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class InfoCertiExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public InfoCertiExample() {
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

        protected void addCriterionForJDBCDate(String condition, Date value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            addCriterion(condition, new java.sql.Date(value.getTime()), property);
        }

        protected void addCriterionForJDBCDate(String condition, List<Date> values, String property) {
            if (values == null || values.size() == 0) {
                throw new RuntimeException("Value list for " + property + " cannot be null or empty");
            }
            List<java.sql.Date> dateList = new ArrayList<java.sql.Date>();
            Iterator<Date> iter = values.iterator();
            while (iter.hasNext()) {
                dateList.add(new java.sql.Date(iter.next().getTime()));
            }
            addCriterion(condition, dateList, property);
        }

        protected void addCriterionForJDBCDate(String condition, Date value1, Date value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            addCriterion(condition, new java.sql.Date(value1.getTime()), new java.sql.Date(value2.getTime()), property);
        }

        public Criteria andCertiIdIsNull() {
            addCriterion("CERTI_ID is null");
            return (Criteria) this;
        }

        public Criteria andCertiIdIsNotNull() {
            addCriterion("CERTI_ID is not null");
            return (Criteria) this;
        }

        public Criteria andCertiIdEqualTo(String value) {
            addCriterion("CERTI_ID =", value, "certiId");
            return (Criteria) this;
        }

        public Criteria andCertiIdNotEqualTo(String value) {
            addCriterion("CERTI_ID <>", value, "certiId");
            return (Criteria) this;
        }

        public Criteria andCertiIdGreaterThan(String value) {
            addCriterion("CERTI_ID >", value, "certiId");
            return (Criteria) this;
        }

        public Criteria andCertiIdGreaterThanOrEqualTo(String value) {
            addCriterion("CERTI_ID >=", value, "certiId");
            return (Criteria) this;
        }

        public Criteria andCertiIdLessThan(String value) {
            addCriterion("CERTI_ID <", value, "certiId");
            return (Criteria) this;
        }

        public Criteria andCertiIdLessThanOrEqualTo(String value) {
            addCriterion("CERTI_ID <=", value, "certiId");
            return (Criteria) this;
        }

        public Criteria andCertiIdLike(String value) {
            addCriterion("CERTI_ID like", value, "certiId");
            return (Criteria) this;
        }

        public Criteria andCertiIdNotLike(String value) {
            addCriterion("CERTI_ID not like", value, "certiId");
            return (Criteria) this;
        }

        public Criteria andCertiIdIn(List<String> values) {
            addCriterion("CERTI_ID in", values, "certiId");
            return (Criteria) this;
        }

        public Criteria andCertiIdNotIn(List<String> values) {
            addCriterion("CERTI_ID not in", values, "certiId");
            return (Criteria) this;
        }

        public Criteria andCertiIdBetween(String value1, String value2) {
            addCriterion("CERTI_ID between", value1, value2, "certiId");
            return (Criteria) this;
        }

        public Criteria andCertiIdNotBetween(String value1, String value2) {
            addCriterion("CERTI_ID not between", value1, value2, "certiId");
            return (Criteria) this;
        }

        public Criteria andCustIdIsNull() {
            addCriterion("CUST_ID is null");
            return (Criteria) this;
        }

        public Criteria andCustIdIsNotNull() {
            addCriterion("CUST_ID is not null");
            return (Criteria) this;
        }

        public Criteria andCustIdEqualTo(String value) {
            addCriterion("CUST_ID =", value, "custId");
            return (Criteria) this;
        }

        public Criteria andCustIdNotEqualTo(String value) {
            addCriterion("CUST_ID <>", value, "custId");
            return (Criteria) this;
        }

        public Criteria andCustIdGreaterThan(String value) {
            addCriterion("CUST_ID >", value, "custId");
            return (Criteria) this;
        }

        public Criteria andCustIdGreaterThanOrEqualTo(String value) {
            addCriterion("CUST_ID >=", value, "custId");
            return (Criteria) this;
        }

        public Criteria andCustIdLessThan(String value) {
            addCriterion("CUST_ID <", value, "custId");
            return (Criteria) this;
        }

        public Criteria andCustIdLessThanOrEqualTo(String value) {
            addCriterion("CUST_ID <=", value, "custId");
            return (Criteria) this;
        }

        public Criteria andCustIdLike(String value) {
            addCriterion("CUST_ID like", value, "custId");
            return (Criteria) this;
        }

        public Criteria andCustIdNotLike(String value) {
            addCriterion("CUST_ID not like", value, "custId");
            return (Criteria) this;
        }

        public Criteria andCustIdIn(List<String> values) {
            addCriterion("CUST_ID in", values, "custId");
            return (Criteria) this;
        }

        public Criteria andCustIdNotIn(List<String> values) {
            addCriterion("CUST_ID not in", values, "custId");
            return (Criteria) this;
        }

        public Criteria andCustIdBetween(String value1, String value2) {
            addCriterion("CUST_ID between", value1, value2, "custId");
            return (Criteria) this;
        }

        public Criteria andCustIdNotBetween(String value1, String value2) {
            addCriterion("CUST_ID not between", value1, value2, "custId");
            return (Criteria) this;
        }

        public Criteria andCertiTypeIsNull() {
            addCriterion("CERTI_TYPE is null");
            return (Criteria) this;
        }

        public Criteria andCertiTypeIsNotNull() {
            addCriterion("CERTI_TYPE is not null");
            return (Criteria) this;
        }

        public Criteria andCertiTypeEqualTo(Short value) {
            addCriterion("CERTI_TYPE =", value, "certiType");
            return (Criteria) this;
        }

        public Criteria andCertiTypeNotEqualTo(Short value) {
            addCriterion("CERTI_TYPE <>", value, "certiType");
            return (Criteria) this;
        }

        public Criteria andCertiTypeGreaterThan(Short value) {
            addCriterion("CERTI_TYPE >", value, "certiType");
            return (Criteria) this;
        }

        public Criteria andCertiTypeGreaterThanOrEqualTo(Short value) {
            addCriterion("CERTI_TYPE >=", value, "certiType");
            return (Criteria) this;
        }

        public Criteria andCertiTypeLessThan(Short value) {
            addCriterion("CERTI_TYPE <", value, "certiType");
            return (Criteria) this;
        }

        public Criteria andCertiTypeLessThanOrEqualTo(Short value) {
            addCriterion("CERTI_TYPE <=", value, "certiType");
            return (Criteria) this;
        }

        public Criteria andCertiTypeIn(List<Short> values) {
            addCriterion("CERTI_TYPE in", values, "certiType");
            return (Criteria) this;
        }

        public Criteria andCertiTypeNotIn(List<Short> values) {
            addCriterion("CERTI_TYPE not in", values, "certiType");
            return (Criteria) this;
        }

        public Criteria andCertiTypeBetween(Short value1, Short value2) {
            addCriterion("CERTI_TYPE between", value1, value2, "certiType");
            return (Criteria) this;
        }

        public Criteria andCertiTypeNotBetween(Short value1, Short value2) {
            addCriterion("CERTI_TYPE not between", value1, value2, "certiType");
            return (Criteria) this;
        }

        public Criteria andCertiNbrIsNull() {
            addCriterion("CERTI_NBR is null");
            return (Criteria) this;
        }

        public Criteria andCertiNbrIsNotNull() {
            addCriterion("CERTI_NBR is not null");
            return (Criteria) this;
        }

        public Criteria andCertiNbrEqualTo(String value) {
            addCriterion("CERTI_NBR =", value, "certiNbr");
            return (Criteria) this;
        }

        public Criteria andCertiNbrNotEqualTo(String value) {
            addCriterion("CERTI_NBR <>", value, "certiNbr");
            return (Criteria) this;
        }

        public Criteria andCertiNbrGreaterThan(String value) {
            addCriterion("CERTI_NBR >", value, "certiNbr");
            return (Criteria) this;
        }

        public Criteria andCertiNbrGreaterThanOrEqualTo(String value) {
            addCriterion("CERTI_NBR >=", value, "certiNbr");
            return (Criteria) this;
        }

        public Criteria andCertiNbrLessThan(String value) {
            addCriterion("CERTI_NBR <", value, "certiNbr");
            return (Criteria) this;
        }

        public Criteria andCertiNbrLessThanOrEqualTo(String value) {
            addCriterion("CERTI_NBR <=", value, "certiNbr");
            return (Criteria) this;
        }

        public Criteria andCertiNbrLike(String value) {
            addCriterion("CERTI_NBR like", value, "certiNbr");
            return (Criteria) this;
        }

        public Criteria andCertiNbrNotLike(String value) {
            addCriterion("CERTI_NBR not like", value, "certiNbr");
            return (Criteria) this;
        }

        public Criteria andCertiNbrIn(List<String> values) {
            addCriterion("CERTI_NBR in", values, "certiNbr");
            return (Criteria) this;
        }

        public Criteria andCertiNbrNotIn(List<String> values) {
            addCriterion("CERTI_NBR not in", values, "certiNbr");
            return (Criteria) this;
        }

        public Criteria andCertiNbrBetween(String value1, String value2) {
            addCriterion("CERTI_NBR between", value1, value2, "certiNbr");
            return (Criteria) this;
        }

        public Criteria andCertiNbrNotBetween(String value1, String value2) {
            addCriterion("CERTI_NBR not between", value1, value2, "certiNbr");
            return (Criteria) this;
        }

        public Criteria andCertiAddrIsNull() {
            addCriterion("CERTI_ADDR is null");
            return (Criteria) this;
        }

        public Criteria andCertiAddrIsNotNull() {
            addCriterion("CERTI_ADDR is not null");
            return (Criteria) this;
        }

        public Criteria andCertiAddrEqualTo(String value) {
            addCriterion("CERTI_ADDR =", value, "certiAddr");
            return (Criteria) this;
        }

        public Criteria andCertiAddrNotEqualTo(String value) {
            addCriterion("CERTI_ADDR <>", value, "certiAddr");
            return (Criteria) this;
        }

        public Criteria andCertiAddrGreaterThan(String value) {
            addCriterion("CERTI_ADDR >", value, "certiAddr");
            return (Criteria) this;
        }

        public Criteria andCertiAddrGreaterThanOrEqualTo(String value) {
            addCriterion("CERTI_ADDR >=", value, "certiAddr");
            return (Criteria) this;
        }

        public Criteria andCertiAddrLessThan(String value) {
            addCriterion("CERTI_ADDR <", value, "certiAddr");
            return (Criteria) this;
        }

        public Criteria andCertiAddrLessThanOrEqualTo(String value) {
            addCriterion("CERTI_ADDR <=", value, "certiAddr");
            return (Criteria) this;
        }

        public Criteria andCertiAddrLike(String value) {
            addCriterion("CERTI_ADDR like", value, "certiAddr");
            return (Criteria) this;
        }

        public Criteria andCertiAddrNotLike(String value) {
            addCriterion("CERTI_ADDR not like", value, "certiAddr");
            return (Criteria) this;
        }

        public Criteria andCertiAddrIn(List<String> values) {
            addCriterion("CERTI_ADDR in", values, "certiAddr");
            return (Criteria) this;
        }

        public Criteria andCertiAddrNotIn(List<String> values) {
            addCriterion("CERTI_ADDR not in", values, "certiAddr");
            return (Criteria) this;
        }

        public Criteria andCertiAddrBetween(String value1, String value2) {
            addCriterion("CERTI_ADDR between", value1, value2, "certiAddr");
            return (Criteria) this;
        }

        public Criteria andCertiAddrNotBetween(String value1, String value2) {
            addCriterion("CERTI_ADDR not between", value1, value2, "certiAddr");
            return (Criteria) this;
        }

        public Criteria andStatusCdIsNull() {
            addCriterion("STATUS_CD is null");
            return (Criteria) this;
        }

        public Criteria andStatusCdIsNotNull() {
            addCriterion("STATUS_CD is not null");
            return (Criteria) this;
        }

        public Criteria andStatusCdEqualTo(String value) {
            addCriterion("STATUS_CD =", value, "statusCd");
            return (Criteria) this;
        }

        public Criteria andStatusCdNotEqualTo(String value) {
            addCriterion("STATUS_CD <>", value, "statusCd");
            return (Criteria) this;
        }

        public Criteria andStatusCdGreaterThan(String value) {
            addCriterion("STATUS_CD >", value, "statusCd");
            return (Criteria) this;
        }

        public Criteria andStatusCdGreaterThanOrEqualTo(String value) {
            addCriterion("STATUS_CD >=", value, "statusCd");
            return (Criteria) this;
        }

        public Criteria andStatusCdLessThan(String value) {
            addCriterion("STATUS_CD <", value, "statusCd");
            return (Criteria) this;
        }

        public Criteria andStatusCdLessThanOrEqualTo(String value) {
            addCriterion("STATUS_CD <=", value, "statusCd");
            return (Criteria) this;
        }

        public Criteria andStatusCdLike(String value) {
            addCriterion("STATUS_CD like", value, "statusCd");
            return (Criteria) this;
        }

        public Criteria andStatusCdNotLike(String value) {
            addCriterion("STATUS_CD not like", value, "statusCd");
            return (Criteria) this;
        }

        public Criteria andStatusCdIn(List<String> values) {
            addCriterion("STATUS_CD in", values, "statusCd");
            return (Criteria) this;
        }

        public Criteria andStatusCdNotIn(List<String> values) {
            addCriterion("STATUS_CD not in", values, "statusCd");
            return (Criteria) this;
        }

        public Criteria andStatusCdBetween(String value1, String value2) {
            addCriterion("STATUS_CD between", value1, value2, "statusCd");
            return (Criteria) this;
        }

        public Criteria andStatusCdNotBetween(String value1, String value2) {
            addCriterion("STATUS_CD not between", value1, value2, "statusCd");
            return (Criteria) this;
        }

        public Criteria andAuthFlagIsNull() {
            addCriterion("AUTH_FLAG is null");
            return (Criteria) this;
        }

        public Criteria andAuthFlagIsNotNull() {
            addCriterion("AUTH_FLAG is not null");
            return (Criteria) this;
        }

        public Criteria andAuthFlagEqualTo(String value) {
            addCriterion("AUTH_FLAG =", value, "authFlag");
            return (Criteria) this;
        }

        public Criteria andAuthFlagNotEqualTo(String value) {
            addCriterion("AUTH_FLAG <>", value, "authFlag");
            return (Criteria) this;
        }

        public Criteria andAuthFlagGreaterThan(String value) {
            addCriterion("AUTH_FLAG >", value, "authFlag");
            return (Criteria) this;
        }

        public Criteria andAuthFlagGreaterThanOrEqualTo(String value) {
            addCriterion("AUTH_FLAG >=", value, "authFlag");
            return (Criteria) this;
        }

        public Criteria andAuthFlagLessThan(String value) {
            addCriterion("AUTH_FLAG <", value, "authFlag");
            return (Criteria) this;
        }

        public Criteria andAuthFlagLessThanOrEqualTo(String value) {
            addCriterion("AUTH_FLAG <=", value, "authFlag");
            return (Criteria) this;
        }

        public Criteria andAuthFlagLike(String value) {
            addCriterion("AUTH_FLAG like", value, "authFlag");
            return (Criteria) this;
        }

        public Criteria andAuthFlagNotLike(String value) {
            addCriterion("AUTH_FLAG not like", value, "authFlag");
            return (Criteria) this;
        }

        public Criteria andAuthFlagIn(List<String> values) {
            addCriterion("AUTH_FLAG in", values, "authFlag");
            return (Criteria) this;
        }

        public Criteria andAuthFlagNotIn(List<String> values) {
            addCriterion("AUTH_FLAG not in", values, "authFlag");
            return (Criteria) this;
        }

        public Criteria andAuthFlagBetween(String value1, String value2) {
            addCriterion("AUTH_FLAG between", value1, value2, "authFlag");
            return (Criteria) this;
        }

        public Criteria andAuthFlagNotBetween(String value1, String value2) {
            addCriterion("AUTH_FLAG not between", value1, value2, "authFlag");
            return (Criteria) this;
        }

        public Criteria andCrtDateIsNull() {
            addCriterion("CRT_DATE is null");
            return (Criteria) this;
        }

        public Criteria andCrtDateIsNotNull() {
            addCriterion("CRT_DATE is not null");
            return (Criteria) this;
        }

        public Criteria andCrtDateEqualTo(Date value) {
            addCriterionForJDBCDate("CRT_DATE =", value, "crtDate");
            return (Criteria) this;
        }

        public Criteria andCrtDateNotEqualTo(Date value) {
            addCriterionForJDBCDate("CRT_DATE <>", value, "crtDate");
            return (Criteria) this;
        }

        public Criteria andCrtDateGreaterThan(Date value) {
            addCriterionForJDBCDate("CRT_DATE >", value, "crtDate");
            return (Criteria) this;
        }

        public Criteria andCrtDateGreaterThanOrEqualTo(Date value) {
            addCriterionForJDBCDate("CRT_DATE >=", value, "crtDate");
            return (Criteria) this;
        }

        public Criteria andCrtDateLessThan(Date value) {
            addCriterionForJDBCDate("CRT_DATE <", value, "crtDate");
            return (Criteria) this;
        }

        public Criteria andCrtDateLessThanOrEqualTo(Date value) {
            addCriterionForJDBCDate("CRT_DATE <=", value, "crtDate");
            return (Criteria) this;
        }

        public Criteria andCrtDateIn(List<Date> values) {
            addCriterionForJDBCDate("CRT_DATE in", values, "crtDate");
            return (Criteria) this;
        }

        public Criteria andCrtDateNotIn(List<Date> values) {
            addCriterionForJDBCDate("CRT_DATE not in", values, "crtDate");
            return (Criteria) this;
        }

        public Criteria andCrtDateBetween(Date value1, Date value2) {
            addCriterionForJDBCDate("CRT_DATE between", value1, value2, "crtDate");
            return (Criteria) this;
        }

        public Criteria andCrtDateNotBetween(Date value1, Date value2) {
            addCriterionForJDBCDate("CRT_DATE not between", value1, value2, "crtDate");
            return (Criteria) this;
        }

        public Criteria andAuthDateIsNull() {
            addCriterion("AUTH_DATE is null");
            return (Criteria) this;
        }

        public Criteria andAuthDateIsNotNull() {
            addCriterion("AUTH_DATE is not null");
            return (Criteria) this;
        }

        public Criteria andAuthDateEqualTo(Date value) {
            addCriterionForJDBCDate("AUTH_DATE =", value, "authDate");
            return (Criteria) this;
        }

        public Criteria andAuthDateNotEqualTo(Date value) {
            addCriterionForJDBCDate("AUTH_DATE <>", value, "authDate");
            return (Criteria) this;
        }

        public Criteria andAuthDateGreaterThan(Date value) {
            addCriterionForJDBCDate("AUTH_DATE >", value, "authDate");
            return (Criteria) this;
        }

        public Criteria andAuthDateGreaterThanOrEqualTo(Date value) {
            addCriterionForJDBCDate("AUTH_DATE >=", value, "authDate");
            return (Criteria) this;
        }

        public Criteria andAuthDateLessThan(Date value) {
            addCriterionForJDBCDate("AUTH_DATE <", value, "authDate");
            return (Criteria) this;
        }

        public Criteria andAuthDateLessThanOrEqualTo(Date value) {
            addCriterionForJDBCDate("AUTH_DATE <=", value, "authDate");
            return (Criteria) this;
        }

        public Criteria andAuthDateIn(List<Date> values) {
            addCriterionForJDBCDate("AUTH_DATE in", values, "authDate");
            return (Criteria) this;
        }

        public Criteria andAuthDateNotIn(List<Date> values) {
            addCriterionForJDBCDate("AUTH_DATE not in", values, "authDate");
            return (Criteria) this;
        }

        public Criteria andAuthDateBetween(Date value1, Date value2) {
            addCriterionForJDBCDate("AUTH_DATE between", value1, value2, "authDate");
            return (Criteria) this;
        }

        public Criteria andAuthDateNotBetween(Date value1, Date value2) {
            addCriterionForJDBCDate("AUTH_DATE not between", value1, value2, "authDate");
            return (Criteria) this;
        }

        public Criteria andModDateIsNull() {
            addCriterion("MOD_DATE is null");
            return (Criteria) this;
        }

        public Criteria andModDateIsNotNull() {
            addCriterion("MOD_DATE is not null");
            return (Criteria) this;
        }

        public Criteria andModDateEqualTo(Date value) {
            addCriterionForJDBCDate("MOD_DATE =", value, "modDate");
            return (Criteria) this;
        }

        public Criteria andModDateNotEqualTo(Date value) {
            addCriterionForJDBCDate("MOD_DATE <>", value, "modDate");
            return (Criteria) this;
        }

        public Criteria andModDateGreaterThan(Date value) {
            addCriterionForJDBCDate("MOD_DATE >", value, "modDate");
            return (Criteria) this;
        }

        public Criteria andModDateGreaterThanOrEqualTo(Date value) {
            addCriterionForJDBCDate("MOD_DATE >=", value, "modDate");
            return (Criteria) this;
        }

        public Criteria andModDateLessThan(Date value) {
            addCriterionForJDBCDate("MOD_DATE <", value, "modDate");
            return (Criteria) this;
        }

        public Criteria andModDateLessThanOrEqualTo(Date value) {
            addCriterionForJDBCDate("MOD_DATE <=", value, "modDate");
            return (Criteria) this;
        }

        public Criteria andModDateIn(List<Date> values) {
            addCriterionForJDBCDate("MOD_DATE in", values, "modDate");
            return (Criteria) this;
        }

        public Criteria andModDateNotIn(List<Date> values) {
            addCriterionForJDBCDate("MOD_DATE not in", values, "modDate");
            return (Criteria) this;
        }

        public Criteria andModDateBetween(Date value1, Date value2) {
            addCriterionForJDBCDate("MOD_DATE between", value1, value2, "modDate");
            return (Criteria) this;
        }

        public Criteria andModDateNotBetween(Date value1, Date value2) {
            addCriterionForJDBCDate("MOD_DATE not between", value1, value2, "modDate");
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