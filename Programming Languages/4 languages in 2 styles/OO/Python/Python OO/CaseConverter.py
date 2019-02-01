class CaseConverter:
    def __init__(self):
        pass


class CaseConverterImpl(CaseConverter):

    @staticmethod
    def process(file_content):
        return file_content.lower()
