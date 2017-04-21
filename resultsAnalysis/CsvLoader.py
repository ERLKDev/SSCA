import numpy as np
import scipy.stats as stats
import pylab as pl
import csv


reader = csv.reader(open('../../tmp/gitAkkaAkka1OutputOld/fullOutput.csv', "rb"), delimiter=',')

header = reader.next()

data = []

for row in reader:
	data.append(row)
	


commitData = [x for x in data if x[0] == "1b81b1991ac4ecd1437a52ca5578e3b2cf2fde57"]

commitData = np.array(commitData)
commitData = np.delete(commitData, 0, 1)
commitData = np.delete(commitData, 1, 1)

commitData = commitData.astype(float)

h = sorted(commitData[:, 9])  #sorted

pl.hist(h, bins=100)      #use this to draw histogram of your data

pl.show()                   #use may also need add this 