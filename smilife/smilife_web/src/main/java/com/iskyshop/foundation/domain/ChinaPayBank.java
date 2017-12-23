package com.iskyshop.foundation.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.iskyshop.core.constant.Globals;
import com.iskyshop.core.domain.IdEntity;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@org.hibernate.annotations.Entity(dynamicUpdate = true)
@Table(name = Globals.DEFAULT_TABLE_SUFFIX + "chinapaybank")
public class ChinaPayBank extends IdEntity {
	private String bank_name;//银行名称
	private String bank_gate_id;//银行网关号
	private String bank_img_url;//银行图片路径
	@Column(columnDefinition = "int default 0")
	private int bank_position;//银行位置
	@Column(columnDefinition = "int default 0")
	private int bank_index;//银行位置索引

	public String getBank_name() {
		return bank_name;
	}

	public void setBank_name(String bank_name) {
		this.bank_name = bank_name;
	}

	public String getBank_gate_id() {
		return bank_gate_id;
	}

	public void setBank_gate_id(String bank_gate_id) {
		this.bank_gate_id = bank_gate_id;
	}

	public String getBank_img_url() {
		return bank_img_url;
	}

	public void setBank_img_url(String bank_img_url) {
		this.bank_img_url = bank_img_url;
	}

	public int getBank_position() {
		return bank_position;
	}

	public void setBank_position(int bank_position) {
		this.bank_position = bank_position;
	}

	public int getBank_index() {
		return bank_index;
	}

	public void setBank_index(int bank_index) {
		this.bank_index = bank_index;
	}

}
