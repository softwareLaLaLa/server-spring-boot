import sys
import numpy as np

if __name__ == '__main__':
    # 数组大小
    size = int(sys.argv[1])
    list = []
    for i in range(2, size + 2):
        list.append(float(sys.argv[i]))
    list.sort()
    border = size / 2
    minVar = np.var(list)
    result = minVar
    while True:
        minVar = result
        var1 = np.var(list[0:border])
        var2 = np.var(list[border:size])
        result = var1 + var2
        if result >= minVar:
            break
        if var2 > var1:
            ++border
        else:
            --border
    print(list[border])
