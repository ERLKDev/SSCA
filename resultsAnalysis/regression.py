
import pandas as pd
import math
import matplotlib.pyplot as plt
import numpy as np
from pandas.stats.api import ols
import statsmodels.api as sm
from sklearn import datasets, linear_model

df = pd.read_csv('fullOutput.csv', usecols=[1, 2, 3, 4, 8, 9, 11, 12])
print df.keys()
a = " DIT"
b = "faults"


df = df.groupby(['path']).agg({b: np.sum, a: np.mean})
df[a] = df[a].map(lambda x: x / 100.0)
df[b] = df[b].map(lambda x: 1 if x > 0.0 else 0)
# df = df.groupby(['path'])
# df[a] = df[a].mean()


# yY = map(lambda x: [1] if x[0] > 0 else [0], df.groupby(['path']).sum()[[b]])
# m = np.asarray(xX)
# n = np.asarray(yY)

# d = {b: n[:,0], a: m[:,0]}
# df = pd.DataFrame(data=d, index=df.groupby(['path'])['path'])
# print df
# print len(filter(lambda y: y[0] > 0, bB))

size = int(math.ceil(len(df) *0.30))
X = df.as_matrix([a])
Y = df.as_matrix([b])
X_test = df.as_matrix([a])[-size:]
y_test = df.as_matrix([b])[-size:]
X_train = df.as_matrix([a])[:-size]
y_train = df.as_matrix([b])[:-size]

# df = pd.DataFrame({a :xX, b :yY})



# print X_test
# print len(X_train), len(X_test)


# 
result = sm.OLS(df[b], df[a]).fit()
print result.summary()


res = ols(y=df[b], x=df[a])
print res

# Create linear regression object
regr = linear_model.LinearRegression()

# Train the model using the training sets
regr.fit(X_train, y_train)

# The coefficients
print('Coefficients: \n', regr.coef_)
# The mean squared error
print("Mean squared error: %f"
      % np.mean((regr.predict(X_test) - y_test) ** 2))
# Explained variance score: 1 is perfect prediction
print('Variance score: %f' % regr.score(X_test, y_test))

# Plot outputs
plt.scatter(X_test, y_test,  color='black')
plt.plot(X_test, regr.predict(X_test), color='blue',
         linewidth=3)

plt.xticks(())
plt.yticks(())

plt.show()