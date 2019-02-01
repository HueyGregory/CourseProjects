
var fs = require('fs');

function getFileContent(fileName) {
    content = fs.readFileSync(fileName, "utf8");
    split_content = content.split("\r\n");
    return split_content;
}

function caseConverter(line) {
    return line.toLowerCase();
}

function word_finder(line) {
    let allTogether;
    line.reduce((allTogether, line) => {
            let words = line.split(" ");
            words.forEach(word => allTogether.push(word));
            return allTogether;
        }, allTogether = []
    );
    return allTogether;
}

function non_ABCFilterer(line) {
    return line.replace(/[^0-9a-z]/gi, '');
}

function wordCounter(line) {
    let mapCounter;
    line.reduce((mapCounter, line) => {
        for (let obj of mapCounter) {
            if (obj.key === line) {
                let numTimes = obj.count;
                obj.count = numTimes + 1;
                return mapCounter;
            }
        }
        mapCounter.push( {
            key: line,
            count: 1
        });
        return mapCounter;

    }, mapCounter = []);
    return mapCounter;

}

function printResult(result) {
    result.forEach(function(element) {
        console.log(element);
    });
}

function theMyAppFunction(cmdArgs) {

    function lineFilterer(line) {
        return (line.toLowerCase().includes(cmdArgs[3].toLowerCase()));
    }

    if (cmdArgs.length === 4) {
        // only wc
        printResult(wordCounter(word_finder(getFileContent(cmdArgs[3]).map(caseConverter)).map(non_ABCFilterer)));
    } else if (cmdArgs.length === 5) {
        // only grep
        printResult(getFileContent(cmdArgs[4]).filter(lineFilterer));
    } else if (cmdArgs.length === 7) {
        // both grep and wc
        printResult(wordCounter(word_finder(getFileContent(cmdArgs[4]).filter(lineFilterer).map(caseConverter)).map(non_ABCFilterer)));
    } else {
        throw "error with number of arguments"
    }

}

const cmdArgs = process.argv;
theMyAppFunction(cmdArgs);