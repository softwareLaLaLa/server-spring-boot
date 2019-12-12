package com.example.paperservice.util;

import com.example.paperservice.Entity.TagRela;

import java.io.IOException;
import java.lang.ProcessBuilder;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Calculator
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

	public static float divide(List<Float> valueList){
		float result = -1;
		int length = valueList.size();
		String commandStr = new String("python matrixCalcu.py " + length);
		for (Float f : valueList) {
			commandStr = commandStr + " " + f;
		}
		try {
			Process pr = Runtime.getRuntime().exec(commandStr);
			BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
			String line = null;
			while ((line = in.readLine()) != null) {
				result = Float.parseFloat(line);
			}
			in.close();
		}catch (Exception e){
			e.printStackTrace();
		}
		return result;
	}

	//生成矩阵数据
	//行为用户， 列为tag
	public static List<List<Float>> getMatrixData(List<Map<Integer, TagRela>> mapList, Set<Integer> relativeSet){
		float level = (float) 1;
		System.out.println("关联tag："+relativeSet);
		List<List<Float>> result = new ArrayList<>();
		for(Map<Integer, TagRela> tagData: mapList){
			System.out.println("论文Tag信息："+tagData);
			List<Float> temp = new ArrayList<>();
			for(Integer i: relativeSet){
				if(tagData.containsKey(i)){
					TagRela tagRela = tagData.get(i);
					if(tagRela.getCorrelation()>level) {
						temp.add(tagRela.getCorrelation());
						continue;
					}
					else{
						System.out.println("tag:"+i+"相关值过低！");
						temp.add((float) 0);
					}
				}
				System.out.println("paper tag:"+i+"相关数据为空");
				temp.add((float) 0);
			}
			result.add(temp);
		}
		System.out.println("转化List数据结果："+result);
		return result;
	}

	//行为用户， 列为group
	public static List<List<Float>> getMatrixData2(List<Map<Integer, Float>> list, Set<Integer> relativeSet){
		float level = (float) 1;
		System.out.println("关联group："+relativeSet);
		List<List<Float>> result = new ArrayList<>();
		for(Map<Integer, Float> tagGroupData:list){
			System.out.println("tagGroup信息："+tagGroupData);
			List<Float> temp = new ArrayList<>();
			for(Integer i: relativeSet){
				if(tagGroupData.containsKey(i)){
					Float value = tagGroupData.get(i);
					if(value > level) {
						temp.add(value);
					}else{
						System.out.println("tag:"+i+"相关值过低！");
						temp.add((float)0);
					}
					continue;
				}
				System.out.println("group tag:"+i+"相关数据为空");
				temp.add((float) 0);
			}
		}
		System.out.println("转化List数据结果："+result);
		return result;
	}
}