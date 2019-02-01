export class CaseConverter {
    process(fileContent){

    }
}

export class CaseConverterImpl extends CaseConverter {
    process(fileContent) {
        fileContent = fileContent + '';
        return fileContent.toLowerCase();
    }
}