ZIMQS = [0] * len(PKB)
for i in range(len(PKB)):
    ZIMQS[i] = (IMP[i] / PKB[i])
print(f"ZIMQS\t{'\t'.join([f'{x:.3f}' for x in ZIMQS])}")