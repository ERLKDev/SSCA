import matplotlib.pyplot as plt
import numpy as np
import math
import pandas as pd
import statsmodels.api as sm

def correctness(predTable):
	return predTable[1, 1] / (predTable[0, 1] + predTable[1, 1])

def completeness(predTable):
	return predTable[1, 1] / (predTable[1, 0] + predTable[1, 1])


def printResultMatrix(result, df, x_label, y_label, threshold=0.5):
	predTable = result.pred_table(threshold=threshold)

	predictions = map(lambda x: 1 if x > threshold else 0, result.predict(sm.add_constant(df[x_label])))
	real_pred = np.vstack((df[y_label], predictions)).T

	nofaultNofault = len(filter(lambda x: x[0] == 0 and x[1] == 0, real_pred))
	nofaultFault = len(filter(lambda x: x[0] == 0 and x[1] == 1, real_pred))
	faultNofault = len(filter(lambda x: x[0] == 1 and x[1] == 0, real_pred))
	faultFault = len(filter(lambda x: x[0] == 1 and x[1] == 1, real_pred))

	predTable = np.asarray([[nofaultNofault, nofaultFault], [faultNofault, faultFault]])

	print predTable.astype(float)[1, 1]

	print "Predicted\tNo Fault\tFault"
	print "Actual"
	print "No Fault\t{}\t\t{}".format(nofaultNofault, nofaultFault)
	print "Fault\t\t{}\t\t{}".format(faultNofault, faultFault)
	print ""
	print "Completeness: {}".format(completeness(predTable.astype(float)))
	print "Correctness: {}".format(correctness(predTable.astype(float)))
	print ""




def plotLogisticRegression(df, result, x_label, y_label):
	fig, ax = plt.subplots()


	xpts = np.linspace(0, df[x_label].max(), 1000).reshape((-1, 1))
	ax.plot(xpts, result.predict(sm.add_constant(xpts)))

	ax.plot(df[x_label], df[y_label], "o")

	return fig, ax


def logitRegression(x, y):
	module = sm.Logit(y, sm.add_constant(x))
	return module.fit()


def oslRegression(x, y):
	module = sm.OLS(y, sm.add_constant(x))
	return module.fit()



if __name__ == '__main__':	
	def wavg(group):
		r = []
		for x in group.keys():
			if str(x) == b:
				r.append(group[b].sum())
			elif x == "path" or x == "commit":
				r.append(group[x])
			else:
				r.append(group[x].mean())
		return pd.Series(r, index=group.keys())

	df = pd.concat(pd.read_csv("fullOutputSHA.csv", usecols=[1, 2, 7], chunksize=1000, iterator=True), ignore_index=True)

	a = "WMCcc"
	b = "faults"


	# df = df.groupby(['path']).apply(wavg)
	df[b] = df[b].map(lambda x: 1 if x > 0.0 else 0)


	result = logitRegression(df[a], df[b])
	print result.summary()

	printResultMatrix(result, df, a, b)

	plotLogisticRegression(df, result, a, b)
	plt.show()
