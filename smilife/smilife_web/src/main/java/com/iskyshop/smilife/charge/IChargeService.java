package com.iskyshop.smilife.charge;
import com.iskyshop.foundation.domain.User;
import com.iskyshop.smilife.common.Result;

/**
 * 充值接口
 * @author chuzhisheng
 * @version 1.0
 * @date 2016年3月22日 上午10:42:19
 */
public interface IChargeService {
    /**
     * 账户充值
     * @author chuzhisheng
     * @version 1.0
     * @date 2016年3月22日 下午1:28:50
     * @param id
     * @param amount
     * @param type
     * @param remark
     * @param gateid
     * @return
     */
	public Result charge(User user,String amount,String custId,String remark,String url);

}
