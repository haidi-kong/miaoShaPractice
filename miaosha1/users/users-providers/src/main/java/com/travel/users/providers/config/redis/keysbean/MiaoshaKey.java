package com.travel.users.providers.config.redis.keysbean;


import com.travel.common.config.redis.BasePrefix;

public class MiaoshaKey extends BasePrefix {

	private MiaoshaKey(int expireSeconds, String prefix) {
		super(expireSeconds, prefix);
	}
	public static MiaoshaKey isGoodsOver = new MiaoshaKey(0, "go");
	public static MiaoshaKey getMiaoshaPath = new MiaoshaKey(60, "mp");
	public static MiaoshaKey getMiaoshaVerifyCode = new MiaoshaKey(300, "vc");
	public static MiaoshaKey getMiaoshaVerifyCodeRegister = new MiaoshaKey(300, "register");

}
