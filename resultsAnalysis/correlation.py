
import pandas as pd

def getCorrelation(path, cols):
	df = pd.read_csv(path, usecols=cols)
	return df.corr()

if __name__ == "__main__":
	print getCorrelation('faultOutput.csv', [3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17])