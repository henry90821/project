package com.iskyshop.core.constant;

/**
 * 系统常量类，这里的常量是系统默认值，可以在系统中进行用户定制
 *
 */
public final class Globals {
	private Globals() {
	}
	/**
	 * 系统默认名称
	 */
	public static final String DEFAULT_SYSTEM_TITLE = "ISkyShop B2B2C商城系统";
	/**
	 * 是否允许单点登录，店铺二级域名开启
	 */
	public static final boolean SSO_SIGN = false;
	/**
	 * 软件发布小版本号
	 */
	public static final int DEFAULT_SHOP_VERSION = 20140601;
	/**
	 * 软件大版本号
	 */
	public static final String DEFAULT_SHOP_OUT_VERSION = "V1.0";
	/**
	 * 软件名称
	 */
	public static final String DEFAULT_WBESITE_NAME = "ISkyShop";
	/**
	 * 系统关闭原因默认值
	 */
	public static final String DEFAULT_CLOSE_REASON = "系统维护中...";
	/**
	 * 系统管理后台默认风格
	 */
	public static final String DEFAULT_THEME = "blue";
	/**
	 * 用户各种报表模板
	 */
	public static final String DERAULT_USER_TEMPLATE = "user_templates";
	/**
	 * 默认文件上传路径
	 */
	public static final String UPLOAD_FILE_PATH = "upload";
	/**
	 * 默认系统语言为简体中文
	 */
	public static final String DEFAULT_SYSTEM_LANGUAGE = "zh_cn";
	/**
	 * 默认系统页面根路径
	 */
	public static final String DEFAULT_SYSTEM_PAGE_ROOT = "WEB-INF/templates/";
	/**
	 * 默认系统后台页面路径为
	 */
	public static final String SYSTEM_MANAGE_PAGE_PATH = "WEB-INF/templates/zh_cn/system/";
	/**
	 * 默认系统页面前台路径
	 */
	public static final String SYSTEM_FORNT_PAGE_PATH = "WEB-INF/templates/zh_cn/shop/";
	/**
	 * 系统数据备份默认路径
	 */
	public static final String SYSTEM_DATA_BACKUP_PATH = "data";
	/**
	 * 系统是否需要升级,预留字段
	 */
	public static final Boolean SYSTEM_UPDATE = false;
	/**
	 * 是否记录日志
	 */
	public static final boolean SAVE_LOG = true;
	/**
	 * 默认验证码类型
	 */
	public static final String SECURITY_CODE_TYPE = "normal";
	/**
	 * 是否可以申请开店
	 */
	public static final boolean STORE_ALLOW = true;
	/**
	 * 邮箱默认开启
	 */
	public static final boolean EAMIL_ENABLE = true;
	/**
	 * 默认图片存储路径格式
	 */
	public static final String DEFAULT_IMAGESAVETYPE = "sidImg";
	/**
	 * 默认上传图片最大尺寸,单位为KB
	 */
	public static final int DEFAULT_IMAGE_SIZE = 1024;
	/**
	 * 默认上传图片扩展名
	 */
	public static final String DEFAULT_IMAGE_SUFFIX = "gif|jpg|jpeg|bmp|png|tbi";
	/**
	 * 默认商城小图片宽度
	 */
	public static final int DEFAULT_IMAGE_SMALL_WIDTH = 160;
	/**
	 * 默认商城小图片高度
	 */
	public static final int DEFAULT_IMAGE_SMALL_HEIGH = 160;
	/**
	 * 默认商城中图片宽度
	 */
	public static final int DEFAULT_IMAGE_MIDDLE_WIDTH = 300;
	/**
	 * 默认商城中图片高度	
	 */
	public static final int DEFAULT_IMAGE_MIDDLE_HEIGH = 300;
	/**
	 * 默认商城大图片宽度
	 */
	public static final int DEFAULT_IMAGE_BIG_WIDTH = 1024;
	/**
	 * 默认商城大图片高度
	 */
	public static final int DEFAULT_IMAGE_BIG_HEIGH = 1024;
	/**
	 * 默认投诉时效时间
	 */
	public static final int DEFAULT_COMPLAINT_TIME = 30;
	/**
	 * 默认表前缀名
	 */
	public static final String DEFAULT_TABLE_SUFFIX = "iskyshop_";
	/**
	 * 第三方账号登录的前缀
	 */
	public static final String THIRD_ACCOUNT_LOGIN = "iskyshop_thid_login_";
	/**hi
	 * 暂时使用第三方，以后公司会接入自己的接口
	 */
	public static final String DEFAULT_SMS_URL = "http://service.winic.org/sys_port/gateway/";
	
	/**
	 * 字符集字符串常量定义
	 */
	public static final class CharSet {
		/**
		 * ISO 拉丁字母表 No.1，也叫作 ISO-LATIN-1
		 */
		public static final String ISO_8859_1 = "ISO-8859-1";
		/**
		 * 8 位 UCS 转换格式
		 */
		public static final String UTF_8 = "UTF-8";
		/**
		 * 16 位 UCS 转换格式，字节顺序由可选的字节顺序标记来标识
		 */
		public static final String UTF_16 = "UTF-16";
		/**
		 * 中文超大字符集
		 */
		public static final String GBK = "GBK";
		/**
		 * GB2312
		 */
		public static final String GB2312 = "GB2312";
	}
	
	/**
	 * 日期格式常量定义
	 */
	public static final class DateFormat {
		/**
		 * 短时间格式 yyyy-MM-dd
		 */
		public static final String DATE_FORMAT_SHORT = "yyyy-MM-dd";
		/**
		 * 长时间格式 yyyy-MM-dd HH:mm:ss
		 */
		public static final String DATE_FORMAT_LONG = "yyyy-MM-dd HH:mm:ss";

		/**
		 * 精确到毫秒的时间字符串 yyyyMMddHHmmssSSS
		 */
		public static final String DATE_FORMAT_MILLISECOND = "yyyyMMddHHmmssSSS";
		
		/**
		 * 精确到秒的时间字符串 yyyyMMddHHmmss
		 */
		public static final String DATE_FORMAT_SECOND = "yyyyMMddHHmmss";
		/**
		 * 中文日期格式 yyyy年MM月dd日 HH时mm分ss秒
		 */
		public static final String DATE_FORMAT_CHINESE = "yyyy年MM月dd日 HH时mm分ss秒";
	}
	
	/**
	 * 一级商品分类
	 */
	public static final Long GOODS_CLASS_LEVEL_FIRST = 0L;
	
	/**
	 * 二级商品分类
	 */
	public static final Long GOODS_CLASS_LEVEL_SECOND = 1L;
	
	/**
	 * 三级商品分类
	 */
	public static final Long GOODS_CLASS_LEVEL_THRID = 2L;
}
