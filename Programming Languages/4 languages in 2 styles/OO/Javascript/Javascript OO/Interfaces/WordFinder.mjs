export class WordFinder {
    process(fileContent){

    }
}

export class WordFinderImpl extends WordFinder {

    process (fileContent) {
        fileContent = fileContent + '';
        let fileContentList = fileContent.split(" ");
        return fileContentList;

    }
}