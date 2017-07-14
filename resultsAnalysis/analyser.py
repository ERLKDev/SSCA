import argparse
import sys

import pandas as pd
import math
import matplotlib.pyplot as plt
import numpy as np
import statsmodels.api as sm
import statsmodels.formula.api as smf
import os
import regression as reg
import tablegen as tg
import re

class Analyser:

	def __init__(self, args):
		self.seperationLine = ''.join(["-" for _ in range(80)]) + "\n"
		self.dependentKey = "faults"
		self.dependentVar = "dependentVar"
		self.faultTreshold = 0.0
		self.args = args

	def wavg(self, group):
		r = []
		for x in group.keys():
			if str(x) == self.dependentKey:
				r.append(group[self.dependentKey].sum())
			elif x == "path" or x =="commit":
				r.append(group[x])
			else:
				r.append(group[x].median())
		return pd.Series(r, index=group.keys())

	def getNumTypes(self, df):
		numtypes = list(df.select_dtypes(include=['float64', 'int64', 'int', 'float']).keys())
		numtypes.remove(self.dependentKey)
		return numtypes


	def storePlt(self, metric, name, fig):
		if args.png or args.all:
			fig.savefig(args.destination + "/" + metric + "/" + name + '.png', format='png')
		if args.eps or args.all:
			fig.savefig(args.destination + "/" + metric + "/" + name + '.eps', format='eps')
		if args.pdf or args.all:
			fig.savefig(args.destination + "/" + metric + "/" + name + '.pdf', format='pdf')

	def validate(self, df):
		if self.args.holdout:
			testSize = int(len(df) * (1.0 - self.args.holdout))
			return df.iloc[testSize:, :], df.iloc[:testSize, :]
		elif self.args.cross:
			if (self.args.columns != None):
				df_test = pd.concat(pd.read_csv(self.args.cross, usecols=self.args.columns, chunksize=1000, iterator=True), ignore_index=True)
			else:
				df_test = pd.concat(pd.read_csv(self.args.cross, chunksize=1000, iterator=True), ignore_index=True)
			df_test = df_test.groupby(['path']).apply(self.wavg)
			if self.args.ols:
				df_test[self.dependentVar] = df_test[self.dependentKey]
			else:
				df_test[self.dependentVar] = df_test[self.dependentKey].map(lambda x: 1 if x > self.faultTreshold else 0)
		
			return df, df_test
		else:
			return df, df

	def standardizing(self, df):
		for x in self.getNumTypes(df):
			if x != self.dependentKey:
				df[x] = (df[x] - df[x].mean()) / df[x].std()

		return df

	def descriptive(self, df):
		numtypes = self.getNumTypes(df)

		# Descriptive statistics
		result = df[numtypes].describe()
		a = np.vstack([np.asarray(map(re.escape, result.index)), np.asarray(map(lambda x: map(lambda y: format(y, '.2f'), x), result.as_matrix())).T]).T
		b = np.vstack([np.append([""], np.asarray(result.keys())), a]).T

		tg.createTable(b, file=open(self.args.destination + "/" +"descriptve-table.txt", 'w'), caption="Descriptive statistics")


		# Fault descriptive statistics
		result = df[df[self.dependentKey] > 0][numtypes].describe()
		a = np.vstack([np.asarray(map(re.escape, result.index)), np.asarray(map(lambda x: map(lambda y: format(y, '.2f'), x), result.as_matrix())).T]).T
		b = np.vstack([np.append([""], np.asarray(result.keys())), a]).T

		tg.createTable(b, file=open(self.args.destination + "/" +"fault-descriptve-table.txt", 'w'),caption="Fault Descriptive statistics")			


	def correlation(self, df):
		# Correlations
		numtypes = self.getNumTypes(df)
		result = df[numtypes].corr()

		a = np.vstack([np.asarray(map(re.escape, result.index)), np.asarray(map(lambda x: map(lambda y: format(y, '.6f'), x), result.as_matrix())).T]).T
		b = np.vstack([np.append([""], np.asarray(result.keys())), a])

		tg.createTable(b, file=open(self.args.destination + "/" +"correlations-table.txt", 'w'), caption="Metric correlation")


	def distribution(self, df):
		size = 20

		df1 = df.copy()
		df1 = df.groupby(['path']).apply(self.wavg)
		df2 = df1[(df1[self.dependentKey] > 0.0)].copy()
		for a in self.getNumTypes(df):
			
			fig1, ax1 = plt.subplots()
			df1[a].hist(ax=ax1)
			ax1.set_title(a + "-Distribution")

			fig2, ax2 = plt.subplots()
			df2[a].hist(ax=ax2)
			ax2.set_title(a + "-Fault-Distribution")


			self.storePlt(a, a + "-Distribution", fig1)
			self.storePlt(a, a + "-Fault-Distribution", fig2)

			plt.close(fig1)
			plt.close(fig2)


	def unRegression(self, df):
		# Makes a copy of the dataframe
		df = df.copy()
		df = df.groupby(['path']).apply(self.wavg)

		numtypes = self.getNumTypes(df)
		tableData = [["Metric", "Constant", "Coefficient", "P-value", "R^2", "Completeness", "Correctness"]]

		# Prepares the dataframe for ols or logit regression
		if self.args.ols:
			df[self.dependentVar] = df[self.dependentKey]
			regFunc = reg.olsRegression
		else:
			df[self.dependentVar] = df[self.dependentKey].map(lambda x: 1 if x > self.faultTreshold else 0)
			regFunc = reg.logitRegression


		df_train, df_test = self.validate(df)

		for a in numtypes:

			# Get the regression results
			result = regFunc(df_train[a], df_train[self.dependentVar])

			# Get correctness and completeness
			predTable = reg.genPredTable(result, df_test, a, self.dependentKey, self.dependentVar, threshold=0.5)
			comp = reg.completeness(predTable.astype(float))
			corr = reg.correctness(predTable.astype(float))

			# Get the regression measurements
			const = format(result.params[0], '.4f')
			coef = format(result.params[1], '.4f')
			rsquared = format(result.rsquared if self.args.ols else result.prsquared, '.4f')
			pvalue = format(result.pvalues[1], '.4f') if result.pvalues[1] > self.args.sigThreshold else "\\textbf{" + format(result.pvalues[1], '.4f') + "}"
			comp = format(comp * 100, '.2f') + "\\%"
			corr = format(corr * 100, '.2f') + "\\%"

			tableData = np.vstack([tableData, [a, const, coef, pvalue, rsquared, comp, corr]])

			# Create plots
			fig, ax = reg.plotLogisticRegression(df_test, result, a, self.dependentVar)
			fig2, ax2 = reg.createComCorGraph(result, df_test, a, self.dependentVar, self.dependentVar)

			self.storePlt(a, a + "-LogitRegression", fig)
			self.storePlt(a, a + "-LogitRegressionCompCorr", fig2)

			plt.close(fig)
			plt.close(fig2)

		# Create the table
		tg.createTable(tableData, file=open(self.args.destination + "/" +"univariate-regression-table.txt", 'w'), caption="Univariate regression")
			


	def runMultiReg(self, df_train, numtypes, formula=None):
		# Get the regression results
		if (self.args.select):
			if self.args.ols:
				return smf.ols(formula, df_train, missing='drop').fit_regularized()
			else:
				return smf.logit(formula, df_train, missing='drop').fit_regularized()
		else:
			if self.args.ols:
				return reg.olsRegression(df_train[numtypes], df_train[self.dependentVar])
			else:
				return reg.logitRegression(df_train[numtypes], df_train[self.dependentVar])

	def multiRegression(self, df):
		# Makes a copy of the dataframe
		df = df.copy()
		df = df.groupby(['path']).apply(self.wavg)

		numtypes = self.getNumTypes(df)

		if self.args.standardizing:
			df = self.standardizing(df)

		# Prepares the dataframe for ols or logit regression
		if self.args.ols:
			df[self.dependentVar] = df[self.dependentKey]
		else:
			df[self.dependentVar] = df[self.dependentKey].map(lambda x: 1 if x > self.faultTreshold else 0)

		formula = None
		if (self.args.select):
			if self.args.ols:
				formula = reg.forward_selected(df[numtypes + [self.dependentVar]], self.dependentVar, smf.ols)
			else:
				formula = reg.forward_selected(df[numtypes + [self.dependentVar]], self.dependentVar, smf.logit)

		if self.args.kfold:
			print "Kfold started"
			chunksize = int(math.ceil(len(df) / float(self.args.kfold)))
			xpts = np.linspace(0, 1, 100)

			totalPredTable = []
			predTable = None

			for x in range(self.args.kfold):
				df_train = None
				for i in range(self.args.kfold):
					chunk = df.iloc[i*chunksize:i*chunksize + chunksize, :]
					if i == x:
						df_test = chunk
					else:
						if df_train is not None:
							df_train = pd.concat([df_train, chunk])
						else:
							df_train = chunk

				result = self.runMultiReg(df_train, numtypes, formula)


				table_tmp = []
				for x in xpts:
					table_tmp.append(reg.genPredTable(result, df_test, numtypes, self.dependentKey, self.dependentVar, threshold=x))

				table = reg.genPredTable(result, df_test, numtypes, self.dependentKey, self.dependentVar, threshold=0.5)
				if predTable is None:
					predTable = table
					totalPredTable = table_tmp
				else:
					predTable = np.add(predTable, table)
					totalPredTable = np.add(totalPredTable, table_tmp)

			comp_data = []
			corr_data = []
			for x in totalPredTable:
				comp_data.append(reg.completeness(x.astype(float)))
				corr_data.append(reg.correctness(x.astype(float)))

		else:
			df_train, df_test = self.validate(df)
			result = self.runMultiReg(df_train, numtypes, formula)

			predTable = reg.genPredTable(result, df_test, numtypes, self.dependentKey, self.dependentVar, threshold=0.5)

		comp = reg.completeness(predTable.astype(float))
		corr = reg.correctness(predTable.astype(float))
	


		tableData = [["Metric", "Coefficient", "P-value"]]
		for x in range(len(result.params)):
			pvalue = lambda x: format(result.pvalues[x], '.4f') if result.pvalues[x] > self.args.sigThreshold else "\\textbf{" + format(result.pvalues[x], '.4f') + "}"
			tableData = np.vstack([tableData, [result.params.keys()[x], format(result.params[x], '.4f'), pvalue(x)]])

		tg.createTable(tableData, file=open(self.args.destination + "/" +"multi-regression-table.txt", 'w'), caption="Multivariate regression")


		# Create faulty not faulty table
		predTable = np.vstack([predTable[0, :], map(lambda x: ("{} ({})").format(predTable[1, x], predTable[2, x]), range(2))])

		a = np.vstack([["Not Faulty", "Faulty"], predTable.T]).T
		b = np.vstack([["", "Not Faulty", "Faulty"], a])
		tg.createTable(b, file=open(self.args.destination + "/" +"faulty-nonFaulty-table.txt", 'w'), caption="Prediction table")

		# Create completeness and correctness table

		tableOutput = np.array([["", "Completeness", "Correctness"], ["Multi. reg.", format(comp * 100, '.2f') + "\\%", format(corr * 100, '.2f') + "\\%"]])
		tg.createTable(tableOutput, file=open(self.args.destination + "/" +"completeness-correctness-table.txt", 'w'), caption="Multivariate regression: Completeness and correctness")

		#  Create completeness and correctness plot
		if self.args.kfold:
			fig, ax = reg.createComCorGraphData(comp_data, corr_data)
		else:
			fig, ax = reg.createComCorGraph(result, df_test, numtypes, self.dependentKey, self.dependentVar)

		self.storePlt("", "LogitRegressionCompCorr", fig)
		plt.close(fig)


	def getStatistics(self):
		np.random.seed()
		df = None

		# Get the dataframe
		if (self.args.columns != None):
			for x in self.args.input:
				if df is None:
					df = pd.concat(pd.read_csv(x, usecols=self.args.columns, chunksize=1000, iterator=True), ignore_index=True)
				else:
					df = pd.concat([df, pd.concat(pd.read_csv(x, usecols=self.args.columns, chunksize=1000, iterator=True), ignore_index=True)], ignore_index=True)
		else:
			for x in self.args.input:
				if df is None:
					df = pd.concat(pd.read_csv(x, chunksize=1000, iterator=True), ignore_index=True)
				else:
					df = pd.concat([df, pd.concat(pd.read_csv(x, chunksize=1000, iterator=True), ignore_index=True)], ignore_index=True)

		# shuffel the dataframe indices
		df = df.reindex(np.random.permutation(df.index))

		# Create output dirs
		os.makedirs(self.args.destination)
		for x in self.getNumTypes(df):
			os.makedirs(self.args.destination + "/" + x)

		if not self.args.multireg:
			self.descriptive(df)

			self.correlation(df)

			self.distribution(df)

			self.unRegression(df)

		self.multiRegression(df)

if __name__ == "__main__":
	parser = argparse.ArgumentParser()
	parser.add_argument("-i", "--input", help="The input file", nargs='+', dest="input", required=True)
	parser.add_argument("-c", "--columns", help="The columns", nargs='+', dest="columns", type=int)
	parser.add_argument("-m", "--multireg", help="Multi regression only", action="store_true", dest="multireg")
	parser.add_argument("-d", "--destination" , help="The output path destination", dest="destination", default="./output")
	parser.add_argument("-a", "--all" , help="Use all image types", dest="all", action="store_true")
	parser.add_argument("--png" , help="Use image type png", dest="png", action="store_true")
	parser.add_argument("--eps" , help="Use image type eps", dest="eps", action="store_true")
	parser.add_argument("--pdf" , help="Use image type pdf", dest="pdf", action="store_true")
	parser.add_argument("--ols" , help="Use ols instead of logit", dest="ols", action="store_true")
	parser.add_argument("--select" , help="Use stepwise selection", dest="select", action="store_true")
	parser.add_argument("--hold", help="Holdout training size", dest="holdout", type=float)
	parser.add_argument("--kfold", help="K-fold cross validation", dest="kfold", type=int)
	parser.add_argument("--cross", help="The file to be cross-system validated", dest="cross")
	parser.add_argument("-t", "--threshold", help="Significant threshold", dest="sigThreshold", type=float, default=0.05)
	parser.add_argument("--stand", help="Standardizing", dest="standardizing", action="store_true")

	args = parser.parse_args()

	analysis = Analyser(args)
	analysis.getStatistics()