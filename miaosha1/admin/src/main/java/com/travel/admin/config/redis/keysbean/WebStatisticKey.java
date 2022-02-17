package com.travel.admin.config.redis.keysbean;


import com.travel.common.config.redis.BasePrefix;

/**
 * @author luo
 */
public class WebStatisticKey extends BasePrefix {

	private WebStatisticKey(int expireSeconds, String prefix) {
		super(expireSeconds, prefix);
	}
	
	public static WebStatisticKey withExpire(int expireSeconds) {
		return new WebStatisticKey(expireSeconds, "statistic");
	}
	
}
