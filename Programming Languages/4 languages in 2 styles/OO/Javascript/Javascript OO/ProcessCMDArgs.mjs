import {DocumentProcessorBuilder} from "./DocumentProcessorBuilder";

export function parseCMDArgs(cmdArgs) {

    function parseCommands(cmdArgs) {
        let secondCommand;
        if ((cmdArgs.length > 6)) {
            secondCommand = cmdArgs[6];
        }
        else if ((cmdArgs.length < 4)) {
            throw "too few arguments";
        }

        function fillObjectsToReturn(firstCommand, secondCommand) {
            const returnObject = new DocumentProcessorBuilder();
            if ((firstCommand === "grep")) {
                returnObject.setLineFilterer();
            }
            if (firstCommand === "wc" || secondCommand === "wc") {
                returnObject.setCaseConverter();
                returnObject.setWordFinder();
                returnObject.setNonABCFilterer();
                returnObject.setWordCounter();
            }
            return returnObject;
        }

        return fillObjectsToReturn(cmdArgs[2], secondCommand);
    }

    function parseArguments(cmdArgs, DPB) {
        let objectToReturn = [];
        objectToReturn.fileName = cmdArgs[3];
        if (DPB.LineFilterer !== undefined && DPB.LineFilterer != null) {
            objectToReturn.fileName = cmdArgs[4];
            objectToReturn.wordsToSearchFor = cmdArgs[3];
        }
        objectToReturn.DocumentProcessorBuilder = DPB;
        return objectToReturn;
    }

    const DPB = parseCommands(cmdArgs);
    return parseArguments(cmdArgs, DPB);
    
}

