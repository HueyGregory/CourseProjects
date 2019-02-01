//
// Created by yaeav on 12/31/2018.
//
#include <stddef.h>
#ifndef C_FUNCTIONSUSEDBYEVERYONE_H
#define C_FUNCTIONSUSEDBYEVERYONE_H

#endif //C_FUNCTIONSUSEDBYEVERYONE_H

extern char** globalFileContent;
extern size_t numRowsInGlobalFileContent;

extern void freeCharStarStar(char** input);

extern char* convertStringToLower(char* input);

extern void readFromDisk(char* fileName);

extern void print_result();

extern void debug_print_content(char** content);