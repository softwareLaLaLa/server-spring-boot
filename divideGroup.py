import sys
import numpy as np

if __name__ == '__main__':
    # 数组大小
    size = int(sys.argv[1])
    list = []
    for i in range(2, size + 2):
        list.append(float(sys.argv[i]))
    list.sort()
    border = int(size / 2)
    #print("最小方差：",np.var(list))

    v1 = np.var(list[0:border])
    v2 = np.var(list[border:size])
    minVar = v1 + v2
    #print("左边方差：", v1)
    #print("右边方差：", v2)
    #print("初始方差：", minVar)
    result = minVar
    flag = 0
    if v1 > v2:
        flag = -1
    else:
        flag = 1

    while flag != 0:
        border += flag
        var1 = np.var(list[0:border])
        var2 = np.var(list[border:size])
        result = var1 + var2
        #print("左边方差：", var1)
        #print("右边方差：", var2)
        #print("border为", border)
        #print("方差合为：", result)
        if ((var1-var2)*flag)>0:
            break

    print(list[border])
