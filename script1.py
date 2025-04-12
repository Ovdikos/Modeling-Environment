ZDEKS = [0] * len(PKB)
for i in range(len(PKB)):
    ZDEKS[i] = (EKS[i] / PKB[i])
print(f"ZDEKS\t{'\t'.join([f'{x:.3f}' for x in ZDEKS])}")