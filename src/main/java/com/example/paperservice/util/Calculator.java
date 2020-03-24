package com.example.paperservice.util;

import com.example.paperservice.DataProcess.GroupData;
import com.example.paperservice.DataProcess.TagRela;

import java.io.IOException;
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
		String commandStr = new String("python divideGroup.py " + length);
		for (Float f : valueList) {
			commandStr = commandStr + " " + f;
		}
		try {
			System.out.println("divide group commander:"+commandStr);
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
		float level = (float) 0.1;
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
		float level = (float) 0.1;
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
			result.add(temp);
		}
		System.out.println("转化List数据结果："+result);
		return result;
	}

	public static GroupData Cluster(List<List<Float>> allPaperTags) {
		String tagNum = String.valueOf(allPaperTags.get(0).size()); // ��ǩ������
		String paperNum = String.valueOf(allPaperTags.size()); // ��������

		int clusterNums = 15;
		String clusterNum = String.valueOf(clusterNums); // ����group��
		String baseCommand = "python KMeans.py ";
		String commandStr = new String(
				clusterNum + " " + tagNum + " " + paperNum);
		// �����ĵ�tag���ӵ�������
		for (List<Float> paperTags : allPaperTags) {
			for (float tag : paperTags) {
				String s = String.valueOf(tag);
				commandStr = commandStr + " " + s;
			}
		}

		System.out.println("Have created the command, paperNums = " + paperNum + " tagNums = " + tagNum
				+ " clusterNums = " + clusterNum);
		String filePath = "clusterData.txt";
		try{
			File file = new File(filePath);
			PrintStream ps = new PrintStream(new FileOutputStream(file));
			ps.println(commandStr);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		//commandStr = baseCommand + commandStr;
		Process pr = null;
		try {
			pr = Runtime.getRuntime().exec(baseCommand);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
//		BufferedReader stdError = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
		String line = null;
//		String errorLine = null;
//		//   ȡgroup
//		try {
//			while((errorLine = stdError.readLine())!=null) {
//
//				System.out.println(errorLine);
//			}
//		} catch (IOException e1) {
//			// TODO 自动生成的 catch 块
//			e1.printStackTrace();
//		}
		List<List<Integer>> groupPapers = new ArrayList<>();
		System.out.println("Now cluster the papers into groups");
		for (int i = 0; i < clusterNums; i++) {
			System.out.println("Group" + i + ": ");
			try {
				System.out.println("获取聚类算法输出");
				line = in.readLine();
				System.out.println("获取成功，line="+line);
				List<Integer> aGroup = new ArrayList<>();
				String[] papers = line.split(" ");
				System.out.println("Group" + i + "对应paper个数" + papers.length);
				for (String paper : papers) {
					System.out.print(paper + " ");
					aGroup.add(Integer.valueOf(paper));
				}
				groupPapers.add(aGroup);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("");
		// ��ȡgroup������tag��ض�
		List<List<Float>> groupTags = new ArrayList<>();
		System.out.println("Now cluster the tags into groups");
		for (int i = 0; i < clusterNums; i++) {
			System.out.println("Group" + String.valueOf(i) + ": ");
			try {
				line = in.readLine();
				ArrayList<Float> aGroup = new ArrayList<>();
				String[] tags = line.split(" ");
				for (String tag : tags) {
					System.out.print(tag + " ");
					aGroup.add(Float.valueOf(tag));
				}
				groupTags.add(aGroup);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("");
		// ��ȡÿ��tag���ڵ�group
		List<List<Integer>> tagInGroup = new ArrayList<>();
		System.out.println("Now see the tags in which groups");
		int tagNums = allPaperTags.get(0).size();
		for (int i = 0; i < tagNums; i++) {
			System.out.println("Tag" + String.valueOf(i) + ": ");
			try {
				line = in.readLine();
				ArrayList<Integer> aTag = new ArrayList<>();
				String[] groups = line.split(" ");
				for (String group : groups) {
					System.out.print(group + " ");
					aTag.add(Integer.valueOf(group));
				}
				tagInGroup.add(aTag);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("");
		// System.out.println(line);
		try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		pr.destroy();

		GroupData groupData = new GroupData(groupPapers, groupTags, tagInGroup);
		return groupData;
	}

	public static List<Integer> distance(List<List<Float>> paperData, List<List<Float>> groupData) throws IOException {
		int tagNum = paperData.get(0).size();
		int paperNum = paperData.size();
		int groupNum = groupData.size();

		String baseCommand = "python lalala.py ";
		String commandStr = tagNum + " " + paperNum + " " + groupNum;
		for(List<Float> paperTagData: paperData){
			for(Float relation: paperTagData){
				commandStr += " " + relation;
			}
		}
		for(List<Float> groupTagData: groupData){
			for(Float relation: groupTagData){
				commandStr += " " + relation;
			}
		}

		String filePath = "distanceData.txt";
		try{
			File file = new File(filePath);
			PrintStream ps = new PrintStream(new FileOutputStream(file));
			ps.println(commandStr);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		//commandStr = baseCommand + commandStr;
		Process pr = null;
		try {
			pr = Runtime.getRuntime().exec(baseCommand);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
//		BufferedReader stdError = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
		String line = null;
		line = in.readLine();
		String[] groupNums = line.split(" ");
		List<Integer> result = new ArrayList<>();
		for(String gNum: groupNums){
			result.add(Integer.parseInt(gNum));
		}
		return result;
 	}
}