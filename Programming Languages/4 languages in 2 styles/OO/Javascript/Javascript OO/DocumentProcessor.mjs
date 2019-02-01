import {LineFiltererImpl} from "./Interfaces/LineFilterer";
import {CaseConverterImpl} from "./Interfaces/CaseConverter";
import {NonABCFiltererImpl} from "./Interfaces/NonABCFilterer";
import {WordCounterImpl} from "./Interfaces/WordCounter";
import {WordFinderImpl} from "./Interfaces/WordFinder";

export class DocumentProcessor {
    constructor (wordsToSearchFor, DPB) {
        if ((DPB.LineFilterer !== undefined) && (DPB.LineFilterer != null)) {
            this.LineFilterer = new LineFiltererImpl(wordsToSearchFor);
        }
        if ((DPB.CaseConverter !== undefined) && (DPB.NonABCFilterer !== undefined) && (DPB.WordCounter !== undefined) && (DPB.WordFinder !== undefined)) {
            this.CaseConverter = new CaseConverterImpl();
            this.NonABCFilterer = new NonABCFiltererImpl();
            this.WordCounter = new WordCounterImpl();
            this.WordFinder = new WordFinderImpl();

        }
    }

    process(fileContent) {
        if ((this.LineFilterer !== undefined) && (this.LineFilterer != null)) {
            fileContent = this.LineFilterer.process(fileContent);
        }
        if ((this.CaseConverter !== undefined) && (this.NonABCFilterer !== undefined) && (this.WordCounter !== undefined) && (this.WordFinder !== undefined)) {
            fileContent = this.CaseConverter.process(fileContent);
            let fileContentList = this.WordFinder.process(fileContent);
            fileContentList = this.NonABCFilterer.process(fileContentList);
            let mapResult = this.WordCounter.process(fileContentList);
            return mapResult;
        }
        else {
            return fileContent;
        }
    }
}
