import argparse
import sys

import pandas as pd
import math
import matplotlib.pyplot as plt
import numpy as np
import statsmodels.api as sm
import os
import Regression as reg

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

		df = df.groupby(['path']).apply(self.wavg)
		df[self.dependantKey] = df[self.dependantKey].map(lambda x: 1 if x > self.faultTreshold else 0)

		for a in numtypes:
			if self.args.store:
				sys.stdout = open(self.args.destination + "/" + a + "/" +"output.txt", 'w')


			result = reg.logitRegression(df[a], df[self.dependantKey])

			print result.summary()
			print ""
			print reg.printResultMatrix(result)

			fig, ax = reg.plotLogisticRegression(df, result, a, self.dependantKey)

			if (self.args.store):
				self.storePlt(a, a + "-LogitRegression", fig)
			else:
				plt.show()
				plt.close(fig)

		sys.stdout.flush()
		sys.stdout = tmp

		if not self.args.store:
			print "\n" + self.seperationLine


	def multiRegression(self, df):
		print self.seperationLine
		print "Multivariate Regressions\n\n"

		numtypes = self.getNumTypes(df)

		df = df.groupby(['path']).apply(self.wavg)
		df[self.dependantKey] = df[self.dependantKey].map(lambda x: 1 if x > self.faultTreshold else 0)

		result = reg.logitRegression(df[numtypes], df[self.dependantKey])

		print result.summary()
		print ""
		print reg.printResultMatrix(result, threshold=0.5)

		print "\n" + self.seperationLine


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
			df = pd.concat(pd.read_csv(self.args.input, usecols=self.args.columns, chunksize=1000, iterator=True), ignore_index=True)
		else:
			df = pd.concat(pd.read_csv(self.args.input, chunksize=1000, iterator=True), ignore_index=True)


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

			self.distribution(df)
			sys.stdout.flush()

		if self.standardizing:
			for x in self.getNumTypes(df):
				df[x] = (df[x] - df[x].mean()) / df[x].std()

		if (not self.args.multireg):
			self.unRegression(df)
			sys.stdout.flush()

		self.multiRegression(df)
		sys.stdout.flush()


if __name__ == "__main__":
	parser = argparse.ArgumentParser()
	parser.add_argument("-i", "--input", help="The input file", dest="input", required=True)
	parser.add_argument("-c", "--columns", help="The columns", nargs='+', dest="columns", type=int)
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
	