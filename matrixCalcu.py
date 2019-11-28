import sys
import numpy as np

if __name__ == '__main__':
    # 第一个数组的列数
    size1 = int(sys.argv[1])
    # 第二个数组的列数
    size2 = int(sys.argv[2])

    # 第一个矩阵
    list1 = []
    # 生成第一个矩阵
    for i in range(1, size1 + 1):
        list1.append(float(sys.argv[i + 2]))

    # 第二个矩阵
    list2 = []
    # 生成第二个矩阵
    for i in range(0, size1):
        # 生成每一列
        temp = []
        for j in range(1, size2 + 1):
            temp.append(float(sys.argv[size1 + 2 + i * size2 + j]))
        list2.append(temp)

    # 计算矩阵相乘
    m1 = np.array(list1)
    m2 = np.array(list2)

    res = np.matmul(m1, m2)

    for i in res:
        print(i)
