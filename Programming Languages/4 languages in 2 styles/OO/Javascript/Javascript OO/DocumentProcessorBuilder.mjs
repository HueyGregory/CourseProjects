import {LineFilterer} from "./Interfaces/LineFilterer";
import {CaseConverter} from "./Interfaces/CaseConverter";
import {NonABCFilterer} from "./Interfaces/NonABCFilterer";
import {WordCounter} from "./Interfaces/WordCounter";
import {WordFinder} from "./Interfaces/WordFinder";

export class DocumentProcessorBuilder {


    constructor() {
       // Array;
    }

    setLineFilterer() {
        this.LineFilterer = new LineFilterer();
    }

    setCaseConverter() {
        this.CaseConverter = new CaseConverter();
    }

    setNonABCFilterer() {
        this.NonABCFilterer = new NonABCFilterer();
    }

    setWordCounter() {
        this.WordCounter = new WordCounter();
    }

    setWordFinder() {
        this.WordFinder = new WordFinder();
    }


}
