import sys
import ProcessCMDArgs
import DocumentProcessor


def logicOfProgram (listOfCMDArgs):
    def build(dict_dpb_parsed_args):
        dpb = dict_dpb_parsed_args.get('dpb')
        dp = DocumentProcessor.DocumentProcessor(dpb);
        if 'words_to_search_for' in dict_dpb_parsed_args:
            search = dict_dpb_parsed_args.get('words_to_search_for')
            dp.setWordsToSearchFor(search)
        return dp

    def get_file_content (file_name):
        file = open(file_name, "r")
        content = file.read()
        file.close()
        return content

    def print_result(result):
        if isinstance(result, str):
            print(result)
        else:
            for x, y in result.items():
                print(x, y)

    dict_dpb_parsed_args = ProcessCMDArgs.parseCMDArgs(listOfCMDArgs)
    dp = build(dict_dpb_parsed_args)
    file_content = get_file_content(dict_dpb_parsed_args['file_name'])
    result = dp.process(file_content)

    print_result(result)


logicOfProgram(sys.argv);
