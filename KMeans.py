from sklearn.cluster import KMeans
import numpy as np
import sys
import os

# 问题1：顺序有影响
# 问题2：标签数要相同
# 问题3：数值本身会影响
# 问题4：维度

# 方法：聚类时每篇论文包含所有标签，只对相关度进行聚类，对于本来没有的标签相关度置0
# 传入论文数据时，传入总标签数n，然后对每个论文都生成一个等长的标签相关度列表(长度=n)，因为传入时每个论文的标签数不同，
# 所以每篇论文之间要有分隔的依据，假定有m篇论文，则最终会生成一个m*n的数组，然后对该数组聚类
#

# 传入的参数依次为：聚类数，标签总数，论文标签(每篇论文都是一个"tag_id+relation"的序列)，论文之间可以用特殊字符隔开
if __name__ == '__main__':

    paperTags = open('clusterData.txt', 'r')  # 读取文件
    allDatas = paperTags.read()  # 读取文件全部内容
    tagInfos = allDatas.split(" ")  # 将文件内容中数字分开
    # 聚类中心数
    center_num = int(tagInfos[0])
    # 标签总数
    all_tags_num = int(tagInfos[1])
    # 论文总数
    paper_num = int(tagInfos[2])
    all_paper_tags = np.empty(shape=[0, all_tags_num])  # 保存全部的论文标签列表
    i = 3  # 循环变量
    for paper in range(0, paper_num):
        paper_tags = np.zeros(all_tags_num)
        for tag in range(0, all_tags_num):
            paper_tags[tag] = float(tagInfos[i])
            i = i + 1
        all_paper_tags = np.append(all_paper_tags, [paper_tags], axis=0)

    # first_tag_id = 1  # 第一个tag的id
    # while i < len(tagInfos):
    #     paper_tags = np.zeros(all_tags_num)  # 一个论文的标签列表
    #     while(tagInfos[i] != '#':
    #         paper_tags(tagInfos[i] - first_tag_id] = int(tagInfos[i + 1])
    #         i = i + 2
    #     i = i + 1
    #     np.append(all_paper_tags, [paper_tags], axis=0)

    kmeans = KMeans(n_clusters=center_num)
    kmeans.fit(all_paper_tags)

    # 每个分组包含的论文
    # print("每个分组包含的论文")
    paper_id = 0
    groups_paper = []
    for i in range(0, center_num):
        groups_paper.append([])
    for i in kmeans.labels_:
        groups_paper[i].append(paper_id)
        paper_id = paper_id + 1

    # 每个分组包含的tag
    # print("每个分组包含的tag")
    for i in groups_paper:
        for j in i:
            print(j, end=" ")
        print("")
    for i in kmeans.cluster_centers_:
        for j in i:
            print(j, end=" ")
        print("")

    # 每个tag在的分组
    tag_groups = []
    for i in range(0, all_tags_num):
        tag_groups.append([])

    for i in range(0, all_tags_num):
        for j in range(0, center_num):
            if kmeans.cluster_centers_[j][i] != 0:
                tag_groups[i].append(j)
        for k in tag_groups[i]:
            print(k, end=" ")
        print("")

# print(kmeans.cluster_centers_)

# X = np.array([[1, 2], [1, 4], [1, 0], [4, 2], [4, 4], [4, 0]])  # 此处要进行np的import  import numpy as np
# Y = np.array([np.array([[1, 0.5], [2, 0.7], [3, 0.8]]), np.array([[4, 0.6], [5, 0.9], [6, 1.0]]),
#               np.array([[2, 0.8], [3, 0.9], [4, 0.3]]),
#               np.array([[5, 0.7], [6, 0.8], [1, 0.4]])])
# Z = np.array([[1, 0.5, 2, 0.7, 3, 0.8], [4, 0.6, 5, 0.9, 6, 1.0], [2, 0.8, 3, 0.9, 4, 0.3], [5, 0.7, 6, 0.8, 1, 0.4]])
# ZZ = np.array([['1', 0.5, '2', 0.7, '3', 0.8], ['4', 0.6, '5', 0.9, '6', 1.0], ['2', 0.8, '3', 0.9, '4', 0.3],
#                ['5', 0.7, '6', 0.8, '1', 0.4]])
# ZZZ = [['1', 0.5, '2', 0.7, '3', 0.8], ['4', 0.6, '5', 0.9, '6', 1.0], ['2', 0.8, '3', 0.9, '4', 0.3],
#        ['5', 0.7, '6', 0.8, '1', 0.4]]
# print(ZZZ)
# kmeans = KMeans(n_clusters=2, random_state=0)  # 新建KMeans对象，并传入参数
# kmeans.fit(ZZZ)  # 进行训练
# print(kmeans.labels_)
# print(kmeans.predict([[0, 0], [4, 4]]))

# print(kmeans.cluster_centers_)
