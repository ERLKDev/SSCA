
import pandas as pd
import math
import matplotlib.pyplot as plt
import numpy as np
import statsmodels.api as sm
from sklearn import datasets, linear_model
from scipy import stats
import LinearRegression as lr
import LogisticRegression as ls

df = pd.concat(pd.read_csv("fullOutput.csv", chunksize=1000, iterator=True), ignore_index=True)

a = " DIT"
b = "faults"


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


# numtypes = list(df.select_dtypes(include=['float64', 'int64', 'int', 'float']).keys())
# numtypes.remove(b)

df = df.groupby(['path']).apply(wavg)
df[b] = df[b].map(lambda x: 1 if x > 38.0 else 0)

# df = df.groupby(['path']).agg({b: np.sum, a: np.mean})
# # df[a] = df[a].map(lambda x: x / 100.0)
# df[b] = df[b].map(lambda x: 1 if x > 20 else 0)
# df = df.groupby(['path'])
# df[a] = df[a].mean()

# X = df.as_matrix([a])
# y = df.as_matrix([b])

# lm = linear_model.LinearRegression()
# lm.fit(X,y)
# params = np.append(lm.intercept_,lm.coef_)
# predictions = lm.predict(X)

# newX = pd.DataFrame({"Constant":np.ones(len(X))}).join(pd.DataFrame(X))
# MSE = (sum((y-predictions)**2))/(len(newX)-len(newX.columns))

# # Note if you don't want to use a DataFrame replace the two lines above with
# # newX = np.append(np.ones((len(X),1)), X, axis=1)
# # MSE = (sum((y-predictions)**2))/(len(newX)-len(newX[0]))

# var_b = MSE*(np.linalg.inv(np.dot(newX.T,newX)).diagonal())
# sd_b = np.sqrt(var_b)
# ts_b = params/ sd_b

# p_values =[2*(1-stats.t.cdf(np.abs(i),(len(newX)-1))) for i in ts_b]

# sd_b = np.round(sd_b,3)
# ts_b = np.round(ts_b,3)
# p_values = np.round(p_values,3)
# params = np.round(params,4)

# myDF3 = pd.DataFrame()
# myDF3["Coefficients"],myDF3["Standard Errors"],myDF3["t values"],myDF3["Probabilites"] = [params,sd_b,ts_b,p_values]
# print(myDF3)

# plt.plot(X, predictions, color='blue', linewidth=3)

# Xf1 = df.loc[df[b] == 0.0]
# Xf2 = df.loc[df[b] > 0.0]
# plt.scatter(Xf1[a], Xf1[b], color='black')
# plt.scatter(Xf2[a], Xf2[b], color='red')
# plt.show()

# yY = map(lambda x: [1] if x[0] > 0 else [0], df.groupby(['path']).sum()[[b]])
# m = np.asarray(xX)
# n = np.asarray(yY)

# d = {b: n[:,0], a: m[:,0]}
# df = pd.DataFrame(data=d, index=df.groupby(['path'])['path'])
# print df
# print len(filter(lambda y: y[0] > 0, bB))

size = int(math.ceil(len(df) *0.30))
# X = df.as_matrix([a])
# Y = df.as_matrix([b])
X_test = df.as_matrix([a])[-size:]
y_test = df.as_matrix([b])[-size:]
X_train = df.as_matrix([a])[:-size]
y_train = df.as_matrix([b])[:-size]

# # df = pd.DataFrame({a :xX, b :yY})



# # print X_test
# # print len(X_train), len(X_test)


result = sm.OLS(df[b], sm.add_constant(df[a])).fit()
print result.summary()

# result = sm.GLS(df[b], df[a]).fit()
# print result.summary()


# result = sm.WLS(df[b], df[a]).fit()
# print result.summary()

# result = sm.GLSAR(df[b], df[a]).fit()
# print result.summary()

result = sm.Logit(df[b], sm.add_constant(df[a])).fit()
print result.summary()










# a = numtypes
# res = sm.Logit(df[b], df[a]).fit()
# print res.summary()

# predictres = res.predict(df[a])

# tre = 0.5

# t_t = 0
# t_f = 0
# f_t = 0
# f_f = 0
# for i in range(len(df[b])):

# 	if (bool(df[b].iloc[i]) and predictres[i] >= tre):
# 		t_t += 1
# 	elif (bool(df[b].iloc[i]) and predictres[i] < tre): 
# 		t_f += 1
# 	elif (not bool(df[b].iloc[i]) and predictres[i] >= tre):
# 		f_t += 1
# 	else:
# 		f_f += 1

# print "Fault and a fault predicted = " + str(t_t)
# print "No fault and no fault predicted = " + str(f_f)
# print "Fault and a  no fault predicted = " + str(t_f)
# print "No fault and a fault predicted = " + str(f_t)

# xv = np.reshape(np.arange(0.0, df[a].max(), df[a].max()/10000.0), (-1, 1))

# plt.plot(df[a], df[b], 'ob', label="Data")
# plt.plot(xv, res.predict(xv), 'or', label="OLS prediction")

# plt.show()












# print pd.crosstab(df[b], df[a], rownames=[b])

# df.hist()
# plt.show()

















# Create linear regression object
regr = lr.LinearRegression()

# Train the model using the training sets
regr.fit(sm.add_constant(X_train), y_train)

# The coefficients
print('Coefficients: \n', regr.coef_)
print('P: \n', regr.p)

# The mean squared error
print("Mean squared error: %f"
      % np.mean((regr.predict(sm.add_constant(X_test)) - y_test) ** 2))
# Explained variance score: 1 is perfect prediction
print('Variance score: %f' % regr.score(sm.add_constant(X_test), y_test))

# Plot outputs
println(X_test)
plt.scatter(X_test, y_test,  color='black')
plt.plot(X_test, regr.predict(sm.add_constant(X_test)), color='blue', linewidth=3)


# plt.xticks(())
# plt.yticks(())
# plt.savefig('destination_path.eps', format='eps')
# plt.savefig('destination_path.pdf', format='pdf')
# plt.savefig('destination_path.png', format='png')
plt.show()