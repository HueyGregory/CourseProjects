#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include "functionsUsedByEveryone.h"

#define POINTER_SIZE 8

char** lineFilterer(char* wordToSearchFor, char** fileContent) {
    char* lowerWordsToSearchFor = convertStringToLower(wordToSearchFor);
    int indexInFileContent = 0;
    size_t indexInLinesToReturn = 0;
    size_t amountToAllocate = 5*POINTER_SIZE;
    char** linesToReturn = calloc(1, amountToAllocate);
    while(fileContent[indexInFileContent] != NULL) {
        char* lowerFileContent = convertStringToLower(fileContent[indexInFileContent]);
        if (strstr(lowerFileContent, lowerWordsToSearchFor) != NULL) {
            char* line_content = calloc(1, (size_t) strlen(fileContent[indexInFileContent]));
            strcpy(line_content, fileContent[indexInFileContent]);
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
    linesToReturn = realloc(linesToReturn, indexInLinesToReturn*8);
    free(lowerWordsToSearchFor);
    freeCharStarStar(fileContent);
    return linesToReturn;
}

char** caseConverter(char** fileContent) {
    size_t amountToAllocate = 5*POINTER_SIZE;
    size_t indexInFileContent = 0;
    char** lowerCaseFileContent = calloc(1, amountToAllocate);
    while(fileContent[indexInFileContent] != NULL) {
        char* newInput = convertStringToLower(fileContent[indexInFileContent]);
        lowerCaseFileContent[indexInFileContent] = newInput;
        indexInFileContent++;
        if(indexInFileContent*POINTER_SIZE > amountToAllocate) {
            amountToAllocate = 2*amountToAllocate;
            lowerCaseFileContent = realloc(lowerCaseFileContent, amountToAllocate);
        }

    }
    lowerCaseFileContent = realloc(lowerCaseFileContent, POINTER_SIZE*(indexInFileContent + 1));
    lowerCaseFileContent[indexInFileContent] = NULL;
    freeCharStarStar(fileContent);
   return lowerCaseFileContent;
}

char** wordFinder(char** fileContent) {

    size_t amountToAllocate = 20*POINTER_SIZE;
    size_t indexInFileContent = 0;
    size_t indexInWordArray = 0;
    char** individualWordsFileContent = calloc(sizeof(char), amountToAllocate);
    while(fileContent[indexInFileContent] != NULL) {
        // split the string into individual words
        char* tempString = calloc(sizeof(char), strlen(fileContent[indexInFileContent]) + 1);

        strcpy(tempString, fileContent[indexInFileContent]);
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


    freeCharStarStar(fileContent);

    return individualWordsFileContent;
}

static char* filterWord(char* input) {
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

char** nonABCFilterer(char** fileContent) {
    size_t amountToAllocate = 10*POINTER_SIZE;
    size_t indexInFileContent = 0;
    size_t indexInWordArray = 0;
    char** filteredWordsFileContent = calloc(1, amountToAllocate);
    while(fileContent[indexInFileContent] != NULL) {
        char* word = filterWord(fileContent[indexInFileContent]);
        filteredWordsFileContent[indexInWordArray] = word;
        indexInWordArray++;
        indexInFileContent++;
        if(indexInFileContent*POINTER_SIZE > amountToAllocate - POINTER_SIZE) {
            amountToAllocate = 2*amountToAllocate;
            filteredWordsFileContent = realloc(filteredWordsFileContent, amountToAllocate);
        }

    }
    filteredWordsFileContent[indexInWordArray++] = NULL;
    filteredWordsFileContent = realloc(filteredWordsFileContent, POINTER_SIZE*(indexInWordArray + 1));
    freeCharStarStar(fileContent);
    return filteredWordsFileContent;
}

struct count {
    char* word;
    int numTimes;
};

char** wordCounter(char** fileContent) {
    size_t amountToAllocate = 10;
    size_t indexInFileContent = 0;
    size_t numWordsInWordArray = 0;
    struct count* countedWordsFileContent = calloc(amountToAllocate, sizeof(struct count) + 8);
    while(fileContent[indexInFileContent] != NULL) {
        char* word = calloc(sizeof(char), strlen(fileContent[indexInFileContent]) + 1);
        strcpy(word, fileContent[indexInFileContent]);
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
    freeCharStarStar(fileContent);
    free(countedWordsFileContent);

    return convertToCharStarStar;
}

int main(int argc, char** argv) {
    if(argc == 4) {
        print_result(lineFilterer(argv[2], readFromDisk(argv[3])));
    }
    else if(argc == 3) {
        print_result(wordCounter(nonABCFilterer(wordFinder(caseConverter(readFromDisk(argv[2]))))));
    }
    else if (argc == 6) {
        print_result(wordCounter(nonABCFilterer(wordFinder(caseConverter(lineFilterer(argv[2], readFromDisk(argv[3])))))));
    }


    return 0;
}