package com.travel.users.providers.utils;

import com.travel.common.utils.MD5Util;
import com.travel.users.apis.entity.MiaoShaUser;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserUtil {

	private static void createUser(int count) throws Exception {
		List<MiaoShaUser> users = new ArrayList<>(count);
		//生成用户
		for(int i=0;i<count;i++) {
			MiaoShaUser user = new MiaoShaUser();
			user.setId(100L+i);
			user.setLoginCount(1);
			user.setNickname(String.valueOf(13000000000L+i));
			user.setRegisterDate(new Date());
			user.setSalt("1a2b3c4d");
			user.setPassword("123456");
			users.add(user);
		}
//		System.out.println("create user");
//		//插入数据库
//		Connection conn = DBUtil.getConn();
//		String sql = "insert into miaosha_user(login_count, nickname, register_date, salt, password, id)values(?,?,?,?,?,?)";
//		PreparedStatement pstmt = conn.prepareStatement(sql);
//		for(int i=0;i<users.size();i++) {
//			MiaoShaUser user = users.get(i);
//			pstmt.setInt(1, user.getLoginCount());
//			pstmt.setString(2, user.getNickname());
//			pstmt.setTimestamp(3, new Timestamp(user.getRegisterDate().getTime()));
//			pstmt.setString(4, user.getSalt());
//			pstmt.setString(5, user.getPassword());
//			pstmt.setLong(6, user.getId());
//			pstmt.addBatch();
//		}
//		pstmt.executeBatch();
//		pstmt.close();
//		conn.close();
//		System.out.println("insert to db");
		//登录，生成token

		File file = new File("D:/tokens.txt");
		if(file.exists()) {
			file.delete();
		}
		RandomAccessFile raf = new RandomAccessFile(file, "rw");
		file.createNewFile();
		raf.seek(0);
		for(int i=0;i<users.size();i++) {
			MiaoShaUser user = users.get(i);

//			JSONObject jo = JSON.parseObject(response);
//			String token = jo.getString("data");
			System.out.println("create token : " + user.getId());
			String row = user.getNickname()+","+getToken(user.getNickname());
			raf.seek(raf.length());
			raf.write(row.getBytes());
			raf.write("\r\n".getBytes());
			System.out.println("write to file : " + user.getId());
		}
		raf.close();
		
		System.out.println("over");
	}

	public static String getToken(String userId) throws Exception {
		String urlString = "http://localhost:9082/login/create_token";
		URL url = new URL(urlString);
		HttpURLConnection co = (HttpURLConnection)url.openConnection();
		co.setRequestMethod("POST");
		co.setDoOutput(true);
		OutputStream out = co.getOutputStream();
		String params = "mobile="+userId+"&password=123456";
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
