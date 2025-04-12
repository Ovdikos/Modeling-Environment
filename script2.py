ZIMP = [0] * len(PKB)
for i in range(len(PKB)):
    ZIMP[i] = (IMP[i] / PKB[i])
print(f"ZIMP\t{'\t'.join([f'{x:.3f}' for x in ZIMP])}")