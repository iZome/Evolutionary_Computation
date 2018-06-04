from matplotlib import pyplot as plt
import numpy as np
import os
from operator import itemgetter

relativePath = os.path.dirname(os.path.realpath(__file__))

def loadFile(path):
    loadedFile = np.genfromtxt(path, delimiter = ",")
    return loadedFile

def getMaxMinAvg(data):
    minRun = data[0][-1];
    maxRun = data[0][-1];
    avgRun = np.zeros(len(data[0]))
    count = 0;

    for run in data:
        if(run[-1] < minRun):
            minRun = count;
        if(run[-1] > maxRun):
            maxRun = count;
        avgRun += run
        count += 1

    avgRun = avgRun/len(data)
    maxMinAvg = np.array([data[maxRun], data[minRun], avgRun])
    return maxMinAvg



def plotProgress(data):

    colour_list = ["red", "yellow", "white"]
    plt.style.use("dark_background")
    for plot,colour in zip(data, colour_list):
        plt.plot(plot, color = colour)

    plt.annotate(str(data[2][-1]), xy=(99, data[2][-1]), xytext=(88, data[2][-1] + 1000),
            arrowprops=dict(facecolor='white', shrink=0.05),
            )

    plt.legend(["Worst", "Best", "Average"])
    plt.ylabel("Distance for best route")
    plt.xlabel("Generation")
    plt.savefig(relativePath + "/plottedRuns.png")
    plt.show()


def main():
    path = relativePath + "/../progress.out"
    loadedFile = loadFile(path)
    plotRuns = getMaxMinAvg(loadedFile)
    plotProgress(plotRuns)

if __name__ == "__main__":
    main()
