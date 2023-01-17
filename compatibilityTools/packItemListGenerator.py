import os,sys

folder = sys.argv[1]
filesToCheck = []
filesToOutput = []

for file_name in os.listdir(folder):
    if file_name.endswith('.json'):
        filesToCheck.append(file_name)

for file in filesToCheck:
    with open(f"{folder}/{file}", "r") as fileCandidate:
        contence = fileCandidate.read()
        if not contence.__contains__('"parent": "minecraft:block'):
            filesToOutput.append(file.replace(".json", "") + "\n")
outFile = open("nonGeneratedItems.txt", "w")
outFile.writelines(filesToOutput)