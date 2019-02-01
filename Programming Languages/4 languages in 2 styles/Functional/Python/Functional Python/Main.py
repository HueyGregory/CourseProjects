import sys
import re
import itertools
from collections import Counter
import pprint


def get_file_content(file_name):
    file = open(file_name, "r")
    content = file.readlines()
    file.close()
    return iter(content)


def print_result(result):
    if isinstance(result, Counter):
        for word, times in result.items():
            print(word, times)
    else:
        for line in result:
            print(line)


def logicOfProgram (args):
    def line_filterer(line):
        return line.lower().__contains__(args[2].lower())

    def case_converter(line):
        return line.lower()

    def word_finder(line):
        return line.split(" ")

    def non_abc_filterer(word):
        return re.sub('[^0-9a-z]+', '', word)

    if len(args) == 3:
        # only wc
        """
        The Counter (subclass of dictionary) counts the number of times each word appears.
        """
        print_result(Counter(
            (map(non_abc_filterer,
                (itertools.chain.from_iterable(map(word_finder,
                    (map(case_converter, get_file_content(args[2]))))))))))
        pass

    elif len(args) == 4:
        # only grep
        print_result(filter(line_filterer,
                            get_file_content(args[3])))

    elif len(args) == 6:
        # both grep and wc
        print_result(Counter(
            (map(non_abc_filterer,
                (itertools.chain.from_iterable(map(word_finder,
                    (map(case_converter, (filter(line_filterer,
                        get_file_content(args[3]))))))))))))

    else:
        raise RuntimeError("too few arguments")


logicOfProgram(sys.argv)
