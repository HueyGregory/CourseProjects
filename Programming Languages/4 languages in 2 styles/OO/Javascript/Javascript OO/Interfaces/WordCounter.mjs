export class WordCounter {
    process(fileContentList){

    }
}

export class WordCounterImpl extends WordCounter {
    process (fileContentList) {
        let fileContentMap = new Map();
        for (let i = 0; i < fileContentList.length; i++) {
            let numOfTimes = fileContentMap.get(fileContentList[i]);
            if ((numOfTimes === undefined) || (numOfTimes === "NAN")) {
                fileContentMap.set(fileContentList[i], 1);
            }
            else {
                fileContentMap.set(fileContentList[i] + "", numOfTimes + 1);
            }
        }
        return fileContentMap;
    }

}