package com.example.administrator.androidserver.Test;


import java.util.Map;

public class WebServerAPI {

	//无参数

	//http://192.168.0.70:8080/MobileService/Json/Test
	public String Test() {

		return "qwer";
	}

	//有请求参数

//	http://192.168.0.18:8080/MobileService/Json/TestParams?id=4
	public String TestParams(Map<String, String> params) {
		int id = Integer.parseInt(params.get("id"));
		return "请求的id为"+id;
	}

}
