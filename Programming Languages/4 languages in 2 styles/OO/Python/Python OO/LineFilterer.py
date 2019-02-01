class LineFilterer:
    def __init__(self):
        pass


class LineFiltererImpl(LineFilterer):
    def __init__(self, words_to_search_for):
        super().__init__()
        self.words_to_search_for = words_to_search_for

    def process(self, file_content):
        file_content_with_words = ""
        file_content = file_content.split("\n")
        for line in file_content:
            if self.words_to_search_for.lower() in line.lower():
                file_content_with_words += (line + "\n")
        return file_content_with_words
