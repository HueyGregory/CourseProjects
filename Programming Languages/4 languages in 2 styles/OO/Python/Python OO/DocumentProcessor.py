from LineFilterer import LineFiltererImpl
from CaseConverter import CaseConverterImpl
from WordFinder import WordFinderImpl
from NonABCFilterer import NonABCFiltererImpl
from WordCounter import WordCounterImpl


class DocumentProcessor:
    """
    def __init__(self, words_to_search_for, dpb):
        if hasattr(dpb, 'line_filterer'):
            self.line_filterer = LineFiltererImpl(words_to_search_for)
        if hasattr(dpb, 'case_converter') and hasattr(dpb, 'word_finder') and hasattr(dpb, 'non_abc_filterer') and hasattr(dpb, 'word_counter'):
            self.case_converter = CaseConverterImpl()
            self.word_finder = WordFinderImpl()
            self.non_abc_filterer = NonABCFiltererImpl()
            self.word_counter = WordCounterImpl()
            """

    def __init__(self, dpb):
        if hasattr(dpb, 'case_converter') and hasattr(dpb, 'word_finder') and hasattr(dpb, 'non_abc_filterer') and hasattr(dpb, 'word_counter'):
            self.case_converter = CaseConverterImpl()
            self.word_finder = WordFinderImpl()
            self.non_abc_filterer = NonABCFiltererImpl()
            self.word_counter = WordCounterImpl()

    def setWordsToSearchFor(self, words):
        self.line_filterer = LineFiltererImpl(words)

    def process(self, file_content):
        if hasattr(self, 'line_filterer'):
            file_content = self.line_filterer.process(file_content)
        if hasattr(self, 'case_converter') and hasattr(self, 'word_finder') and hasattr(self, 'non_abc_filterer') and hasattr(self, 'word_counter'):
            file_content = self.case_converter.process(file_content)
            word_list = self.word_finder.process(file_content)
            word_list = self.non_abc_filterer.process(word_list)
            result_set_map = self.word_counter.process(word_list)
            return result_set_map
        return file_content
