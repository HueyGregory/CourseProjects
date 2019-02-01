class WordFinder:
    def __init__(self):
        pass


class WordFinderImpl(WordFinder):

    def process(self, file_content):
        file_content = file_content + "";
        file_content_list = file_content.split(" ")
        return file_content_list
