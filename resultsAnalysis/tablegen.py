import numpy as np
import pandas as pd

def createTable(data, method="np", header=None, caption="My caption", label="my-label"):
	if method == "pd":
		header = np.asarray(data.keys())
		data = data.as_matrix()

	headerData = data[0, :] if header is None else header
	headerSize = len(headerData)
	headerRow = ' & '.join(map(str, headerData)) + "\\\\"

	colls = "".join(["l" for _ in range(headerSize)])

	bodyData = data[1:, :] if header is None else data
	bodyRows = "\\\\ \n".join([" & ".join(map(str, x)) for x in bodyData]) + "\\\\"


	print "\\begin{table}[]"
	print "\\centering"
	print "\\begin{tabular}{@{}" + colls + "@{}}"
	print "\\toprule"
	print headerRow
	print "\\midrule"
	print bodyRows
	print "\\bottomrule"
	print "\\end{tabular}"
	print "\\caption{" + caption + "}"
	print "\\label{" + label + "}"
	print "\\end{table}"
