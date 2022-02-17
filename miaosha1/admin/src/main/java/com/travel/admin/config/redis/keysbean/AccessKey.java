package com.travel.admin.config.redis.keysbean;


import com.travel.common.config.redis.BasePrefix;

/**
 * @author luo
 */
public class AccessKey extends BasePrefix {

	private AccessKey( int expireSeconds, String prefix) {
		super(expireSeconds, prefix);
	}
	
	public static AccessKey withExpire(int expireSeconds) {
		return new AccessKey(expireSeconds, "access");
	}
	
}
