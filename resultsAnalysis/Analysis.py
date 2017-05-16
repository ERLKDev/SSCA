import argparse
import sys

import pandas as pd
import math
import matplotlib.pyplot as plt
import numpy as np
import statsmodels.api as sm
import os
import Regression as reg
import tablegen as tg

class Analysis:

	def __init__(self, args):
		self.seperationLine = ''.join(["-" for _ in range(80)]) + "\n"
		self.dependantKey = "faults"
		self.faultTreshold = 0.0
		self.args = args
		self.standardizing = True


	def descriptive(self, df):
		# Prints the descriptive statistics
		print self.seperationLine
		print "Descriptive statistics\n\n"
		print df.select_dtypes(include=['float64', 'int64', 'int', 'float']).describe()
		print "\n" + self.seperationLine

		print self.seperationLine
		print "Descriptive statistics faults\n\n"
		print df[df[self.dependantKey] > 0].select_dtypes(include=['float64', 'int64', 'int', 'float']).describe()
		print "\n" + self.seperationLine


	def correlation(self, df):
		# Prints the correlations
		print self.seperationLine
		print "Correlations\n\n"
		print df.select_dtypes(include=['float64', 'int64', 'int', 'float']).corr()
		print "\n" + self.seperationLine


	def unRegression(self, df):
		tmp = sys.stdout

		if not self.args.store:
			print self.seperationLine
			print "Univariate Regressions\n\n"

		numtypes = self.getNumTypes(df)
		tableData = [["Metric", "Constant", "Coefficient", "P-value", "R^2", "Completeness", "Correctness"]]# np.zeros([0, 8])

		# df = df.groupby(['path']).apply(self.wavg)
		df[self.dependantKey] = df[self.dependantKey].map(lambda x: 1 if x > self.faultTreshold else 0)

		for a in numtypes:
			if self.args.store:
				sys.stdout = open(self.args.destination + "/" + a + "/" +"output.txt", 'w')


			result = reg.logitRegression(df[a], df[self.dependantKey])

			print result.summary()
			print ""
			print reg.printResultMatrix(result, df, a, self.dependantKey, threshold=0.5)
			print "\n\n"

			predTable = reg.genPredTable(result, df, a, self.dependantKey, threshold=0.5)
			comp = reg.completeness(predTable.astype(float))
			corr = reg.correctness(predTable.astype(float))

			tableData = np.vstack([tableData, [a, format(result.params[0], '.4f'), format(result.params[1], '.4f'), 
				format(result.pvalues[1], '.4f'), format(result.prsquared, '.4f'), format(comp * 100, '.2f') + "\\%", format(corr * 100, '.2f') + "\\%"]])



			fig, ax = reg.plotLogisticRegression(df, result, a, self.dependantKey)
			fig2, ax2 = reg.createComCorGraph(result, df, a, self.dependantKey)

			if (self.args.store):
				self.storePlt(a, a + "-LogitRegression", fig)
				self.storePlt(a, a + "-LogitRegressionCompCorr", fig2)
			else:
				plt.show()
			plt.close(fig)
			plt.close(fig2)

		sys.stdout.flush()
		sys.stdout = tmp

		print tg.createTable(tableData)
		sys.stdout.flush()

		print "\n" + self.seperationLine


	def multiRegression(self, df):
		print self.seperationLine
		print "Multivariate Regressions\n\n"

		numtypes = self.getNumTypes(df)

		# df = df.groupby(['path']).apply(self.wavg)
		# df[self.dependantKey] = df[self.dependantKey].map(lambda x: 1 if x > self.faultTreshold else 0)
		testSize = int(len(df) * 0.20)
		df_test = df#.iloc[:testSize, :]
		df_train = df#.iloc[testSize:, :]

		result = reg.logitRegression(df_train[numtypes], df_train[self.dependantKey])

		print result.summary()
		print ""
		print reg.printResultMatrix(result, df_test, numtypes, self.dependantKey, threshold=0.4)

		tableData = [["Metric", "Coefficient", "P-value"]]
		for x in range(len(numtypes) + 1):
			name = "Constant"
			if x > 0:
				name = numtypes[x - 1]

			tableData = np.vstack([tableData, [name, format(result.params[x], '.4f'), format(result.pvalues[x], '.4f')]])

		print tg.createTable(tableData)

		fig, ax = reg.createComCorGraph(result, df_test, numtypes, self.dependantKey)

		if (self.args.store):
			self.storePlt("", "LogitRegressionCompCorr", fig)
		else:
			plt.show()
		plt.close(fig)

		print "\n" + self.seperationLine
		return result


	def wavg(self, group):
		r = []
		for x in group.keys():
			if str(x) == self.dependantKey:
				r.append(group[self.dependantKey].sum())
			elif x == "path":
				r.append(group[x])
			else:
				r.append(group[x].mean())
		return pd.Series(r, index=group.keys())


	def distribution(self, df):
		size = 20

		for a in self.getNumTypes(df):
			df1 = df.copy()
			binarray = [df1[a].quantile(.0) + (df1[a].quantile(1.0) / float(size)) * x for x in range(size + 1)]
			df1[self.dependantKey] = df1[self.dependantKey].map(lambda x: 1 if x > self.faultTreshold else 0)

			df2 = df1[(df1[self.dependantKey] > 0.0)].copy()

			df2["bucket"] = pd.cut(df2[a], bins=binarray)
			df1["bucket"] = pd.cut(df1[a], bins=binarray)

			newdf = df1[['bucket',a]].groupby('bucket').count()
			newdf2 = df2[['bucket',a]].groupby('bucket').count()

			newdf3 = newdf2[a] / newdf[a]

			fig1, ax1 = plt.subplots()
			newdf.plot(kind='bar', ax=ax1)
			ax1.set_title(a + "-Distribution")

			fig2, ax2 = plt.subplots()
			newdf2.plot(kind='bar', ax=ax2)
			ax2.set_title(a + "-Fault-Distribution")

			fig3, ax3 = plt.subplots()
			newdf3.plot(kind='bar', ax=ax3)
			ax3.set_title(a + "-Weighted-Fault-Distribution")

			if (self.args.store):
				self.storePlt(a, a + "-Distribution", fig1)
				self.storePlt(a, a + "-Fault-Distribution", fig2)
				self.storePlt(a, a + "-Weighted-Fault-Distribution", fig3)
			else:
				plt.show()
			plt.close(fig1)
			plt.close(fig2)
			plt.close(fig3)


	def storePlt(self, metric, name, fig):
		if args.png or args.all:
			fig.savefig(args.destination + "/" + metric + "/" + name + '.png', format='png')
		if args.eps or args.all:
			fig.savefig(args.destination + "/" + metric + "/" + name + '.eps', format='eps')
		if args.pdf or args.all:
			fig.savefig(args.destination + "/" + metric + "/" + name + '.pdf', format='pdf')


	def getNumTypes(self, df):
		numtypes = list(df.select_dtypes(include=['float64', 'int64', 'int', 'float']).keys())
		numtypes.remove(self.dependantKey)
		return numtypes


	def getStatistics(self):
		df = None

		if (self.args.columns != None):
			for x in self.args.input:
				if df is None:
					df = pd.concat(pd.read_csv(x, usecols=self.args.columns, chunksize=1000, iterator=True), ignore_index=True)
				else:
					df = pd.concat([df, pd.concat(pd.read_csv(x, usecols=self.args.columns, chunksize=1000, iterator=True), ignore_index=True)], ignore_index=True)
		else:
			df = pd.concat(pd.read_csv(self.args.input, chunksize=1000, iterator=True), ignore_index=True)

		# self.dfTotal = df
		# df = df.sample(frac=1)
		# df = pd.concat([df[df[self.dependantKey] == 0].sample(n=int(len(df[df[self.dependantKey] == 0]) * 1.0)), df[df[self.dependantKey] > 0]], ignore_index=True)
		df = df.reindex(np.random.permutation(df.index))

		if (self.args.store):
			os.makedirs(self.args.destination)
			for x in self.getNumTypes(df):
				os.makedirs(self.args.destination + "/" + x)
			sys.stdout = open(self.args.destination + "/output.txt", 'w')

		if (not self.args.multireg):
			self.descriptive(df)
			sys.stdout.flush()

			self.correlation(df)
			sys.stdout.flush()

			# self.distribution(df)
			# sys.stdout.flush()

		if self.standardizing:
			for x in self.getNumTypes(df):
				df[x] = (df[x] - df[x].mean()) / df[x].std()

		if (not self.args.multireg):
			self.unRegression(df)
			sys.stdout.flush()


		result = self.multiRegression(df)
		sys.stdout.flush()

		if self.args.cross is not None:
			for x in self.args.cross:
				df = pd.concat(pd.read_csv(x, usecols=self.args.columns, chunksize=1000, iterator=True), ignore_index=True)

				print self.seperationLine
				print "Cross validation --" + x + "\n\n"

				numtypes = self.getNumTypes(df)
				print reg.printResultMatrix(result, df, numtypes, self.dependantKey, threshold=0.5)
				print "\n\n"

				fig, ax = reg.createComCorGraph(result, df, numtypes, self.dependantKey)

				if (self.args.store):
					self.storePlt("", x + "-CrossLogitRegressionCompCorr", fig)
				else:
					plt.show()
				plt.close(fig)
	


if __name__ == "__main__":
	parser = argparse.ArgumentParser()
	parser.add_argument("-i", "--input", help="The input file", nargs='+', dest="input", required=True)
	parser.add_argument("-c", "--columns", help="The columns", nargs='+', dest="columns", type=int)
	parser.add_argument("--cross", help="The files to be cross-system validated", nargs='+', dest="cross")
	parser.add_argument("-m", "--multireg", help="Multi regression only", action="store_true", dest="multireg")
	parser.add_argument("-s", "--store", help="Store the results", action="store_true", dest="store")
	parser.add_argument("-d", "--destination" , help="The output path destination", dest="destination", default="./output")
	parser.add_argument("-a", "--all" , help="Use all image types", dest="all", action="store_true")
	parser.add_argument("--png" , help="Use image type png", dest="png", action="store_true")
	parser.add_argument("--eps" , help="Use image type eps", dest="eps", action="store_true")
	parser.add_argument("--pdf" , help="Use image type pdf", dest="pdf", action="store_true")
	args = parser.parse_args()

	analysis = Analysis(args)
	analysis.getStatistics()
	