package com.travel.users.providers.utils;

import com.travel.users.apis.entity.MiaoShaUser;
import com.travel.users.providers.entity.MiaoshaUserAccount;

import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserPaymentUtil {

	private static void createUser(int count) throws Exception {
		List<MiaoshaUserAccount> users = new ArrayList<>(count);
		//生成用户
		for(int i=0;i<count;i++) {
			MiaoshaUserAccount userAccount = new MiaoshaUserAccount();
			userAccount.setUserId(13000000000L+i);
			userAccount.setBalanceAmount(new BigDecimal(100));
			userAccount.setUpdateTime(new Date());
			userAccount.setCreateTime(new Date());
			users.add(userAccount);
		}
		System.out.println("create userAcount");
//		//插入数据库
//		Connection conn = DBUtil.getConn();
//		String sql = "insert into miaosha_account(balance_amount, user_id, create_time, update_time)values(?,?,?,?)";
//		PreparedStatement pstmt = conn.prepareStatement(sql);
//		for(int i=0;i<users.size();i++) {
//			MiaoshaUserAccount user = users.get(i);
//			pstmt.setBigDecimal(1, user.getBalanceAmount());
//			pstmt.setLong(2, user.getUserId());
//			pstmt.setTimestamp(3, new Timestamp(user.getCreateTime().getTime()));
//			pstmt.setTimestamp(4, new Timestamp(user.getCreateTime().getTime()));
//			pstmt.addBatch();
//		}
//		pstmt.executeBatch();
//		pstmt.close();
//		conn.close();
//		System.out.println("insert to db");
		//登录，生成token

		File file = new File("D:/tokens2.txt");
		if(file.exists()) {
			file.delete();
		}
		RandomAccessFile raf = new RandomAccessFile(file, "rw");
		file.createNewFile();
		raf.seek(0);
		for(int i=0;i<users.size();i++) {
			MiaoshaUserAccount user = users.get(i);

//			JSONObject jo = JSON.parseObject(response);
//			String token = jo.getString("data");
			System.out.println("create orderId : " + user.getUserId());
			String orderId = getOrderId(user.getUserId().toString());
			String token = UserUtil.getToken(user.getUserId().toString());
			String row = user.getUserId()+","+token+","+orderId;
			raf.seek(raf.length());
			raf.write(row.getBytes());
			raf.write("\r\n".getBytes());
			System.out.println("write to file : " + user.getId());
		}
		raf.close();

		System.out.println("over");
	}

	public static String getOrderId(String userID) throws Exception {
		String urlString = "http://localhost:9082/order/getOrderId";
		URL url = new URL(urlString);
		HttpURLConnection co = (HttpURLConnection)url.openConnection();
		co.setRequestMethod("POST");
		co.setDoOutput(true);
		OutputStream out = co.getOutputStream();
		String params = "mobile="+userID+"&password=123456";
		out.write(params.getBytes());
		out.flush();
		InputStream inputStream = co.getInputStream();
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		byte buff[] = new byte[1024];
		int len = 0;
		while((len = inputStream.read(buff)) >= 0) {
			bout.write(buff, 0 ,len);
		}
		inputStream.close();
		bout.close();
		String response = new String(bout.toByteArray());
		return response;
	}


	public static void main(String[] args)throws Exception {
		createUser(30);
	}
}
