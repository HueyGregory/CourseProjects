export class NonABCFilterer {
    process(fileContentList){

    }
}

export class NonABCFiltererImpl extends NonABCFilterer {

    process(fileContentList) {
        for (let i = 0; i < fileContentList.length; i++) {
            fileContentList[i] = fileContentList[i].replace(/[^0-9a-z]/gi, '');
        }
        return fileContentList;
    }
}