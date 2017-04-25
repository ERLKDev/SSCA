
import pandas as pd
import math
import matplotlib.pyplot as plt
import numpy as np
from sklearn import linear_model

df = pd.read_csv('fullOutput.csv', usecols=[1, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17])
size = int(math.ceil(len(df) *0.30))

X_test= df.as_matrix(' CC')[-size:]
y_test = df.as_matrix(' faults')[-size:]

print X_test
X_train= df.as_matrix(' CC')[:-size]
y_train =df.as_matrix(' faults')[:-size]

print len(X_train), len(X_test)
# Create linear regression object
regr = linear_model.LinearRegression()

# Train the model using the training sets
regr.fit(X_train, y_train)

# The coefficients
print('Coefficients: \n', regr.coef_)

# The mean squared error
print("Mean squared error: %.2f"
      % np.mean((regr.predict(X_test) - y_test) ** 2))
# Explained variance score: 1 is perfect prediction
print('Variance score: %.2f' % regr.score(X_test, y_test))

# Plot outputs

plt.scatter(X_test, y_test,  color='black')
plt.plot(X_test, regr.predict(X_test), color='blue',
         linewidth=3)

plt.xticks(())
plt.yticks(())

plt.show()