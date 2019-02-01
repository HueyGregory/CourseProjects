#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include "functionsUsedByEveryone.h"

#define POINTER_SIZE 8

void lineFilterer(char* wordToSearchFor) {
    char* lowerWordsToSearchFor = convertStringToLower(wordToSearchFor);
    int indexInFileContent = 0;
    size_t indexInLinesToReturn = 0;
    size_t amountToAllocate = 5*POINTER_SIZE;
    char** linesToReturn = calloc(1, amountToAllocate);
    while(globalFileContent[indexInFileContent] != NULL) {
        char* lowerFileContent = convertStringToLower(globalFileContent[indexInFileContent]);
        if (strstr(lowerFileContent, lowerWordsToSearchFor) != NULL) {
            char* line_content = calloc(1, (size_t) strlen(globalFileContent[indexInFileContent]));
            strcpy(line_content, globalFileContent[indexInFileContent]);
            linesToReturn[indexInLinesToReturn++] = line_content;
            if(indexInLinesToReturn*POINTER_SIZE >= amountToAllocate) {
                amountToAllocate = 2*amountToAllocate;
                linesToReturn = realloc(linesToReturn, amountToAllocate);
            }
        }
        indexInFileContent++;
        free(lowerFileContent);
    }

    linesToReturn[indexInLinesToReturn++] = NULL;
    char** tempLinesToReturn = realloc(linesToReturn, indexInLinesToReturn*POINTER_SIZE);
    if(tempLinesToReturn != NULL) {
        linesToReturn = tempLinesToReturn;
    }
    free(lowerWordsToSearchFor);

    numRowsInGlobalFileContent = indexInLinesToReturn;
    freeCharStarStar(globalFileContent);
    globalFileContent = linesToReturn;
}

void caseConverter() {
    for (int indexInFileContent = 0; indexInFileContent < numRowsInGlobalFileContent; indexInFileContent++) {
        char* tempString = globalFileContent[indexInFileContent];
        globalFileContent[indexInFileContent] = convertStringToLower(tempString);
        free(tempString);
    }
}

void wordFinder() {

    size_t amountToAllocate = 20*POINTER_SIZE;
    size_t indexInFileContent = 0;
    size_t indexInWordArray = 0;
    char** individualWordsFileContent = calloc(sizeof(char), amountToAllocate);
    while(globalFileContent[indexInFileContent] != NULL) {
        // split the string into individual words
        char* tempString = calloc(sizeof(char), strlen(globalFileContent[indexInFileContent]) + 1);

        strcpy(tempString, globalFileContent[indexInFileContent]);
        char* splitWord = strtok(tempString, " ");
        while(splitWord != NULL) {
            char* word = calloc(sizeof(char), strlen(splitWord) + 1);
            strcpy(word, splitWord);
            individualWordsFileContent[indexInWordArray] = word;
            indexInWordArray++;
            if(indexInWordArray*POINTER_SIZE > amountToAllocate - POINTER_SIZE) {
                amountToAllocate = 2*amountToAllocate;
                if((individualWordsFileContent = realloc(individualWordsFileContent, amountToAllocate)) == NULL) {
                    printf("realloc failed\n");
                    perror("realloc failed");

                }
            }
            splitWord = strtok(NULL, " ");
        }
        indexInFileContent++;
        free(tempString);

    }

    individualWordsFileContent[indexInWordArray++] = NULL;
    if((individualWordsFileContent = realloc(individualWordsFileContent, POINTER_SIZE*(indexInWordArray))) == NULL) {
        printf("realloc failed\n");
        perror("realloc failed");
    }
    numRowsInGlobalFileContent = indexInWordArray;
    freeCharStarStar(globalFileContent);
    globalFileContent = individualWordsFileContent;
}

static char* filterWord(char* input) {
    if (input == NULL) {
        return NULL;
    }
    char* word = calloc(sizeof(char), strlen(input) + 1);
    int indexInWord = 0;
    for (int i = 0; i < strlen(input); i++) {
        if (isalnum(input[i])) {
            word[indexInWord] = input[i];
            indexInWord++;
        }
    }
    word[indexInWord++] = '\0';
    word = realloc(word, (size_t) indexInWord);
    return word;
}

void nonABCFilterer() {
    for (int i = 0; i < numRowsInGlobalFileContent; i++) {
        char* tempWord = globalFileContent[i];
        globalFileContent[i] = filterWord(tempWord);
        free(tempWord);
    }
}

struct count {
    char* word;
    int numTimes;
};

void wordCounter() {
    size_t amountToAllocate = 10;
    size_t indexInFileContent = 0;
    size_t numWordsInWordArray = 0;
    struct count* countedWordsFileContent = calloc(amountToAllocate, sizeof(struct count) + 8);
    while(globalFileContent[indexInFileContent] != NULL) {
        char* word = calloc(sizeof(char), strlen(globalFileContent[indexInFileContent]) + 1);
        strcpy(word, globalFileContent[indexInFileContent]);
        strcat(word, "\0");
        size_t tempIndexInFileContent = indexInFileContent;
        int alreadyThere = 0;
        for (int i = 0; i < numWordsInWordArray; i++) {
//            printf("Entered for loop; i == %d\n", i);
            if(strcmp(countedWordsFileContent[i].word, word) == 0) {
                alreadyThere = 1;
                countedWordsFileContent[i].numTimes = countedWordsFileContent[i].numTimes + 1;
                break;
            }
        }
        if (alreadyThere == 0) {
            // add word to array
            struct count *newCount = calloc(1, sizeof(struct count) + 8);
            newCount->word = word;
            newCount->numTimes = 1;
            countedWordsFileContent[numWordsInWordArray] = *newCount;
            numWordsInWordArray++;
        }
        indexInFileContent++;
        if(indexInFileContent > amountToAllocate - POINTER_SIZE) {
            amountToAllocate = 2*amountToAllocate;
            struct count* tempCountedWordsFileContent = realloc(countedWordsFileContent, amountToAllocate*sizeof(struct count) + 8);
            if (tempCountedWordsFileContent != NULL) {
                countedWordsFileContent = tempCountedWordsFileContent;
            }
        }
    }
    char** convertToCharStarStar = calloc(numWordsInWordArray, POINTER_SIZE + 1);
    for (int i = 0; i < numWordsInWordArray; i++) {
        char* numStr = calloc(sizeof(char), 10);
        strcpy(numStr, ": ");
        char* tempStr = calloc(sizeof(char), 10);
        sprintf(tempStr, "%d", countedWordsFileContent[i].numTimes);
        strcat(numStr, tempStr);
        free(tempStr);
        strcat(numStr, "\0");
        char* wordWithNum = calloc(1, strlen(countedWordsFileContent[i].word) + strlen(numStr) + 8);
        strcpy(wordWithNum, countedWordsFileContent[i].word);
        strcat(wordWithNum, numStr);
        free(numStr);
        free(countedWordsFileContent[i].word);
        convertToCharStarStar[i] = wordWithNum;
    }
    convertToCharStarStar[numWordsInWordArray] = NULL;
    freeCharStarStar(globalFileContent);
    free(countedWordsFileContent);

    globalFileContent = convertToCharStarStar;
}

int main(int argc, char** argv) {
    if(argc == 4) {
        readFromDisk(argv[3]);
        lineFilterer(argv[2]);
        print_result();
     //   print_result(lineFilterer(argv[2], readFromDisk(argv[3])));
    }
    else if(argc == 3) {
        readFromDisk(argv[2]);
        caseConverter();
        wordFinder();
        nonABCFilterer();
        wordCounter();
        print_result();
    }
    else if (argc == 6) {
        readFromDisk(argv[3]);
        lineFilterer(argv[2]);
        caseConverter();
        wordFinder();
        nonABCFilterer();
        wordCounter();
        print_result();
    }

    return 0;
}