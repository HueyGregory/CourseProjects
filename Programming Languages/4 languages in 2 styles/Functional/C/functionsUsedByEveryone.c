//
// Created by yaeav on 12/31/2018.
//
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>
#include "functionsUsedByEveryone.h"

/*void debug_print_content(char** content) {
    int i = 0;
    while(content[i] != NULL) {
        printf("line %d: %s\n", i, content[i]);
        i++;
    }
}
*/
long getSizeOfFile(FILE* theFile) {
    fseek(theFile, 0, SEEK_END);
    return ftell(theFile);
}

char** readFromDisk(char* fileName) {
    FILE* theFile = fopen(fileName, "r");
    long sizeOfFile = getSizeOfFile(theFile);
    char** content = calloc(8, (size_t) sizeOfFile);
    char* line_content = calloc(sizeof(char), (size_t) sizeOfFile);
    // set file position to beginning of file
    fseek(theFile, 0, SEEK_SET);
    int indexInContent = 0;
    while((fgets(line_content, sizeOfFile, theFile)) != NULL) {
        line_content = realloc(line_content, strlen(line_content) + 1);

        // Got to change around the strcpy and/or the array of pointers - content
        content[indexInContent++] = line_content;
        line_content = calloc(1, (size_t) sizeOfFile);
    }
    content = realloc(content, (size_t) 8*(indexInContent + 1));
    content[indexInContent] = NULL;
    if (fclose(theFile) == EOF) {
        perror ("Error closing file\n");
        return NULL;
    }
    return content;
}

char* convertStringToLower(char* input) {
    char* strLower = calloc(sizeof(char), strlen(input) + 8);
    for (int i = 0; i < strlen(input); i++) {
        strLower[i] = tolower(input[i]);
    }
    return strLower;
}

void freeCharStarStar(char** input) {
    int i = 0;
    while(input[i] != NULL) {
        free(input[i++]);
    }
    free(input[i]);
    free(input);
}


void print_result(char** result) {
    int i = 0;
    while(result[i] != NULL) {
        printf("%s\n", result[i++]);
    }
    freeCharStarStar(result);
}