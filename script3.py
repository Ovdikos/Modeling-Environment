CONS = [0] * len(PKB)
for i in range(len(PKB)):
    CONS[i] = (KI[i] + KS[i]) / PKB[i]
print(f"CONS\t{'\t'.join([f'{x:.3f}' for x in CONS])}")