
import pandas as pd

def getDescriptive(path, cols):
	df = pd.read_csv(path, usecols=cols)
	return df.describe()

if __name__ == "__main__":
	print getDescriptive('fullOutput2.csv', [3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17])
   