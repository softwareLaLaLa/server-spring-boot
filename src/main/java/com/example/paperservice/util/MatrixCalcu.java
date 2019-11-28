package com.example.paperservice.util;

import java.io.IOException;
import java.lang.ProcessBuilder;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MatrixCalcu
{
	//����˷�����
	public static List<Float> multiple(List<Float> m1, List<List<Float>> m2){
		// �������
		List<Float> res = new ArrayList<>();

		// ��ȡ��������
		String length1 = String.valueOf(m1.size());
		String length2 = String.valueOf(m2.get(0).size());

		// ǰ��������Ϊ�����С
		String commandStr = new String("python matrixCalcu.py " + length1 + " " + length2);

		//����һ����������ݼ��뵽������
		for (Float i : m1) {
			String s = String.valueOf(i);
			commandStr = commandStr + " " + s;
		}

		//���ڶ�����������ݼ��뵽������
		for(List<Float> i : m2) {
			for(Float j : i) {
				String s = String.valueOf(j);
				commandStr = commandStr + " " + s;
			}
		}
		try {
			Process pr = Runtime.getRuntime().exec(commandStr);
			BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			String line = null;
			//�����ÿһ���Ǿ����һ�����֣���ȡ�����뵽�������
			while ((line = in.readLine()) != null) {
				Float f = Float.parseFloat(line);
				//System.out.println(f);
				res.add(f);
			}
			in.close();
		}catch (Exception e){
			e.printStackTrace();
		}
		return res;
	}

}