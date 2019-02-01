export class LineFilterer {
    constructor(wordsToSearchFor) {
        this.wordsToSearchFor = wordsToSearchFor;
    }
    process(fileContent){

    }
}

export class LineFiltererImpl extends LineFilterer {
    constructor(wordsToSearchFor) {
        super(wordsToSearchFor);
    }

    process(fileContent) {
        fileContent = fileContent + '';
        const lines = fileContent.split('\r\n');
        let contentThatMeetsRequirement = "";
        for (let i = 0; i < lines.length; i++) {
            if ((lines[i].toLowerCase().includes(this.wordsToSearchFor.toLowerCase()))) {
                let tempVar = lines[i].substring(0, lines[i].length);
                contentThatMeetsRequirement = contentThatMeetsRequirement + "\r\n" + tempVar;
            }

        }
        return contentThatMeetsRequirement;

    }
}