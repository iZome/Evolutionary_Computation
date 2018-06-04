from scipy.stats import ks_2samp
from scipy.stats import ttest_ind
import scipy
import numpy as np
from matplotlib import pyplot as plt
import sys

args = [arg for arg in sys.argv]


def normalDistribution(x, sigma, mu):
    return (1.0 / (sigma * np.sqrt(2 * np.pi)) * np.exp(- (x - mu)**2 / (2 * sigma**2)))


def readData(filename):
    data = []
    for line in reversed(open(filename).readlines()):
        print(line)
        if ("Run Stats for experiment at:" in line):
            break
        try:
            isdigit = float(line.strip("\n"))
            data.append(isdigit)
        except:
            raise Exception("Error converting to flaot")
    return(data[::-1])


def normaltest(data):
    k2, p = scipy.stats.normaltest(data)
    alpha = 1E-3

    if p < alpha:
        print("The sample is most likely not normal")
        return False
    else:
        print("The sample is most likely normal")
        return True


if ('--normal' in args and len(args) == 3):
    data = readData(args[1])
    normaltest(data)

    sigma, mu = np.std(data), np.mean(data)
    fig = plt.figure()
    count, bins, ignored = plt.hist(data, 20, normed=True)
    #
    plt.plot(bins, normalDistribution(bins, sigma, mu), linewidth=2, color='r')
    plt.show()

if ('--normal' in args and len(args) == 4):
    data1 = readData(args[1])
    data2 = readData(args[2])

    sigma1, mu1 = np.std(data1), np.mean(data1)
    sigma2, mu2 = np.std(data2), np.mean(data2)

    data1_normal = normaltest(data1)
    data2_normal = normaltest(data2)

    alpha = 1E-3

    if (data1_normal and data2_normal):
        value, p = ttest_ind(data1, data2, equal_var=False)

        if p < alpha:
            print('Samples are likely drawn from different \ndistributions,' +
                  ' i.e. there is a significant difference in the two samples')
        else:
            print('Samples are likely drawn from the same distributions, i.e. there\n' +
                  'is no significant difference in the two samples')

if ("--ks-test" in args):
    data_crossover = readData(args[1])
    data_onepoint = readData(args[2])

    ks, p = ks_2samp(data_crossover, data_onepoint)
    alpha = 1E-3

    if p < alpha:
        print('Samples are likely drawn from different \ndistributions,' +
              ' i.e. there is a significantly difference in the two samples')
    else:
        print('Samples are likely drawn from the same distributions, i.e. there\n' +
        'is no significantly difference in the two samples')
