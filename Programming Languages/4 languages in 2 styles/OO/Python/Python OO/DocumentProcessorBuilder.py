import LineFilterer, CaseConverter, WordFinder, NonABCFilterer, WordCounter


class DocumentProcessorBuilder:

    def set_line_filterer(self):
        self.line_filterer = LineFilterer.LineFilterer()

    def set_case_converter(self):
        self.case_converter = CaseConverter.CaseConverter()


    def set_word_finder(self):
        self.word_finder = WordFinder.WordFinder()

    def set_non_abc_filterer(self):
        self.non_abc_filterer = NonABCFilterer.NonABCFilterer()

    def set_word_counter(self):
        self.word_counter = WordCounter.WordCounter()
