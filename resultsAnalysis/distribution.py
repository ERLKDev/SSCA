
import pandas as pd
import matplotlib.pyplot as plt
import math

df = pd.read_csv('faultOutputSmall.csv', usecols=[3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17])
df2 = pd.read_csv('fullOutputSmall.csv', usecols=[3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17])

# ax1 = df[' CC'].plot(kind='kde')
# ax2 = df2[' CC'].plot(kind='kde')

a = 'WMCcc'
divider = 0.001
size = int(math.ceil(df[a].max() * divider))

df['bucket'] = pd.cut(df[a], bins=[x/divider for x in range(0, size)])
newdf = df[['bucket',a]].groupby('bucket').sum()

df2['bucket'] = pd.cut(df2[a], bins=[x/divider for x in range(0, size)])
newdf2 = df2[['bucket',a]].groupby('bucket').sum()

newdf3 = newdf[a] / newdf2[a]

newdf3.plot(kind='bar')

# print ax1.get_children()[2]._x
# ax1.get_children()[2]._y

# print ax2.get_children()[2]._x
# ax2.get_children()[2]._y

plt.show()



   