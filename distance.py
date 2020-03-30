import numpy as np
import sys
import os

if __name__ == '__main__':
    tagDataFile = open('distanceData.txt', 'r')
    tagData = tagDataFile.read()
    tagInfos = tagData.split(" ")
    #print(tagInfos)

    tagNum = int(tagInfos[0])
    paperNum = int(tagInfos[1])
    groupNum = int(tagInfos[2])

    paperTagData = np.empty(shape=[0, tagNum])
    i=3
    for pNum in range(0,paperNum):
        paperTag = np.zeros(tagNum)
        for tNum in range(0,tagNum):
            paperTag[tNum] = float(tagInfos[i])
            i += 1
        paperTagData = np.append(paperTagData, [paperTag], axis=0)
    #print(paperTagData)
    
    groupTagData = np.empty(shape=[0, tagNum])
    for gNum in range(0, groupNum):
        groupTag = np.zeros(tagNum)
        for tNum in range(0,tagNum):
            groupTag[tNum] = float(tagInfos[i])
            i += 1
        groupTagData = np.append(groupTagData, [groupTag], axis=0)
    #print(groupTagData)
    
    result = []
    for pNum in range(0, paperNum):
        paperTag = paperTagData[pNum]
        minNum = 0
        minDistance = np.linalg.norm(groupTagData[0]-paperTag)
        for gNum in range(0, groupNum):
            distance = np.linalg.norm(groupTagData[gNum]-paperTag)
            #print("distance=")
            #print(distance)
            if(distance < minDistance):
                minNum = gNum
                minDistance = distance
        result.append(minNum)
    for r in result:
        print(r, end=" ")
    print("")
    
