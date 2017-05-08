import matplotlib.pyplot as plt
import numpy as np
import math
import pandas as pd
import statsmodels.api as sm

def correctness(predTable):
	return predTable[1, 1] / (predTable[0, 1] + predTable[1, 1])

def completeness(predTable):
	return predTable[1, 1] / (predTable[1, 0] + predTable[1, 1])


def printResultMatrix(result, threshold=0.5):
	predTable = result.pred_table(threshold=threshold)
	print "Predicted\tNo Fault\tFault"
	print "Actual"
	print "No Fault\t{}\t\t{}".format(int(predTable[0, 0]), int(predTable[0, 1]))
	print "Fault\t\t{}\t\t{}".format(int(predTable[1, 0]), int(predTable[1, 1]))
	print ""
	print "Completeness: {}".format(completeness(predTable))
	print "Correctness: {}".format(correctness(predTable))
	print ""
	print predTable


def plotLogisticRegression(df, result, x_label, y_label):
	params = result.params
	intercept = params["const"]
	dep = params[x_label]

	fig, ax = plt.subplots()

	yhat = lambda x: 1.0 / (1.0 + math.e**(-(intercept + dep * x)))

	xpts = np.linspace(0, df[x_label].max(), 1000)
	ax.plot(xpts, map(yhat, xpts))

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

	df = pd.concat(pd.read_csv("fullOutput3.csv", usecols=[1, 2, 7], chunksize=1000, iterator=True), ignore_index=True)

	a = "WMCcc"
	b = "faults"


	df = df.groupby(['path']).apply(wavg)
	df[b] = df[b].map(lambda x: 1 if x > 0.0 else 0)


	result = logisticRegression(df[a], df[b])
	print result.summary()

	printResultMatrix(result)

	plotLogisticRegression(result, a, b)
