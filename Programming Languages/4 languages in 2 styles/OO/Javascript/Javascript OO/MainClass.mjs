import {parseCMDArgs} from "./ProcessCMDArgs";
import {DocumentProcessor} from "./DocumentProcessor";
import * as fs from 'fs';

console.log("Starting Program");

function theMyAppFunction(cmdArgs) {

    function build(objectWithDPBAndParsedArgs) {
        return new DocumentProcessor(objectWithDPBAndParsedArgs.wordsToSearchFor, objectWithDPBAndParsedArgs.DocumentProcessorBuilder);
    }
    function getFileContent(fileName) {
        return fs.readFileSync(fileName);
    }
    function printResult(result) {
        if (result instanceof Map) {
            let iteratorMap = result.entries();
            let entry = iteratorMap.next();
            while(!entry.done) {
                console.log(entry.value);
                entry = iteratorMap.next();
            }
        }
        else {
            console.log("The result is: " + result);
        }
    }
    
    let objectWithDPBAndParsedArgs = parseCMDArgs(cmdArgs);

    let DP = build(objectWithDPBAndParsedArgs);
    let fileContent = getFileContent(objectWithDPBAndParsedArgs.fileName);
    let result = DP.process(fileContent);
    
    printResult(result);
}

const cmdArgs = process.argv;
theMyAppFunction(cmdArgs);

console.log("Ending Program")
