package com.hospital.s1m.lib_base.utils;



public class UUID {

	public static String getUUID(){
		
		java.util.UUID uuid = java.util.UUID.randomUUID();
		return uuid.toString().replaceAll("-", "");
	}
}
