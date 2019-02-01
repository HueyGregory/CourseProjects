import DocumentProcessorBuilder


def parseCMDArgs(listOfCMDArgs):

    def parse_commands(listOfCMDArgs):
        second_command = None;
        if len(listOfCMDArgs) > 5 :
            second_command = listOfCMDArgs[5]
        elif len(listOfCMDArgs) < 3:
            raise RuntimeError("too few arguments")

        def fillObjectsToReturn (first_command, second_command):
            return_object = DocumentProcessorBuilder.DocumentProcessorBuilder()
            if first_command == "grep":
                return_object.set_line_filterer()
            if first_command == "wc" or second_command == "wc":
                return_object.set_case_converter()
                return_object.set_word_finder()
                return_object.set_non_abc_filterer()
                return_object.set_word_counter()
            return return_object

        return fillObjectsToReturn(listOfCMDArgs[1], second_command)

    def parse_arguments(listOfCMDArgs, dpb):
        file_name = listOfCMDArgs[2]
        if hasattr(dpb, 'line_filterer'):
            file_name = listOfCMDArgs[3]
            words_to_search_for = listOfCMDArgs[2]
            return {'file_name': file_name, 'words_to_search_for': words_to_search_for, 'dpb': dpb}
        dictionary_to_return = {'file_name': file_name, 'dpb':dpb}
        return dictionary_to_return

    dpb = parse_commands(listOfCMDArgs)

    return parse_arguments(listOfCMDArgs, dpb)
